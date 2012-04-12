/**
 * Représente un fichier en cours de 
 * partage
 */

import java.io.*;
import java.util.*;
import java.security.*;
import java.nio.channels.FileChannel;

class FileShared extends File
{
	// pour eclipse numeroter les classes, pas de effet direct ici
	private static final long serialVersionUID = 1L;

	/**
     * Clef du fichier
     */
    private String _key;

    /**
     * Taille du fichier
     */
    private int _size;

    /**
     * Taille des pieces
     */
    private int _piecesize;

   /**
    * Buffermap du fichier
    */
    private Buffermap _buffermap;

    /**
     * Status du fichier
     */
    private boolean _iscomplete;

    /**
     * Ensemble de pair possédant le fichier
     * La clef représente le hash distinctif du pair
     * dans le PeerManager
     * La valeur représente le buffermap du pair pour
     * ce fichier
     */
    public Map<String, Buffermap> peers;

    /**
     * Créer un nouveau fichier déjà connue
     * Il peut etre complet ou en cours de téléchargement
     *
     * La distinction entre fichier complet ou pas est faite 
     * à l'aide de son extension
     *
     * @param name : le nom du fichier tel qu'il est dans 
     * le système de fichier
     */
    public FileShared(String name)
    {
        super(
            name.endsWith((String)App.config.get("tmpExtension")) ? 
            App.config.get("tmpDir") + File.separator + name : 
            App.config.get("downloadDir") + File.separator + name
        );
        
        if (!this.exists() || !this.isFile()) {
            System.out.println("Invalid file : " + name);
            throw new IllegalArgumentException();
        }

        this.peers = new HashMap<String, Buffermap>();

        if (name.endsWith((String)App.config.get("tmpExtension"))) {
            _iscomplete = false;
            this.readHeaderTmpFile();
        }
        else {
            _iscomplete = true;
            this.readInfoCompleteFile();
        }
    }

    /**
     * Créer un nouveau fichier non présent
     * dans le système de fichier
     * Créer un fichier temporaire correspondant
     * 
     * @param name : le nom du fichier (sans extension tmp)
     * @param key : sa clef
     * @param size : sa taille
     * @param piecesize : la taiile de ses pieces
     */
    public FileShared(String name, String key, int size, int piecesize)
    {
        super(
            App.config.get("tmpDir") + 
            File.separator + 
            name + 
            App.config.get("tmpExtension")
        );

        if (this.exists()) {
            throw new IllegalArgumentException("File already exists : " + name);
        }

        assert key.length() == 32;

        _key = key;
        _size = size;
        _piecesize = piecesize;
        _iscomplete = false;
        
        int nbpieces = _size / _piecesize;
        if ((_size % _piecesize) > 0) nbpieces++;
        _buffermap = new Buffermap(nbpieces, false);

        this.peers = new HashMap<String, Buffermap>();

        this.initHeaderTmpFile();
    }

    /**
     * Renvoi la clef du fichier
     */
    public String getKey()
    {
        return _key;
    }

    /**
     * Renvoi la taille total
     */
    public int getSize()
    {
        return _size;
    }

    /**
     * Renvoi la taille des pièces
     */
    public int getPieceSize()
    {
        return _piecesize;
    }

    /**
     * Renvoi le nombre de pieces manquantes
     */
    public int nbMissingPieces()
    {
        if (_iscomplete) {
            return 0;
        }
        else {
            return _buffermap.getNbMissingPieces();
        }
    }

    /**
     * Renvoi l'état du fichier
     */
    public boolean isComplete()
    {
        return _iscomplete;
    }

    /**
     * Renvoi la taille du buffermap
     * en octets
     */
    public int buffermapSize()
    {
        return _buffermap.getBufferSize();
    }

    /**
     * Renvoi le nombre de pièce du fichier
     */
    public int nbPieces()
    {
        int nb = _size / _piecesize;
        if ((_size % _piecesize) == 0) {
            return nb;
        }
        else {
            return nb+1;
        }
    }

    /**
     * Renvoi le buffermap brute sous forme
     * d'array d'octets
     */
    public byte[] getRawBuffermap()
    {
        return _buffermap.rawBuffer();
    }

    /**
     * Renvoi le buffermap du fichier
     */
    public Buffermap getBuffermap()
    {
        return _buffermap;
    }

    /**
     * Indique si le fichier possède la piece
     * numeros num
     */
    public boolean hasPiece(int num)
    {
        if (_iscomplete) {
            return true;
        }
        else {
            return _buffermap.getBit(num);
        }
    }

    /**
     * Ecrit dans le fichier la pièce piece
     * de numeros num
     */
    public void writePiece(byte[] piece, int num)
    {
        if (_iscomplete) {
            throw new IllegalArgumentException();
        }
        else {
            this.writePieceTmpFile(piece, num);
            _buffermap.setBit(num, true);
        }
    }
    
    /**
     * Lis et retourne  dans le fichier la pièce
     * de numeros num
     */
    public byte[] readPiece(int num)
    {
        if (_iscomplete) {
            return this.readPieceCompleteFile(num);
        }
        else {
            return this.readPieceTmpFile(num);
        }
    }

    /**
     * Structure du header des fichiers temporaires :
     * int   : taille de la clef en octets
     * string : key
     * int   : size
     * int   : piece size
     * //buffermap : le num correspond à la position de la piece dans le fichier tmp
     * // -1 : non présente
     * int   : num piece 1
     * int   : num piece 2
     * ***
     * int   : num piece n
     * //DATA
     * [piece num 1]
     * [piece num 2]
     * ***
     */

    /**
     * Renvoi la taille du header du fichier temporaire
     */
    private int headerSize()
    {
        return 4 + _key.length() + 4 + 4 + 4*this.nbPieces();
    }

    /**
     * Initialise le header du fichier temporaire
     */
    private void initHeaderTmpFile()
    {
        try {
            FileOutputStream writer_tmp = new FileOutputStream(this);
            FileChannel writer = writer_tmp.getChannel();

            int offset = 0;

            //Taille de la clef
            Tools.write(writer, offset, _key.length());
            offset += 4;
            //Clef
            Tools.write(writer, offset, _key);
            offset += _key.length();
            //Size
            Tools.write(writer, offset, _size);
            offset += 4;
            //piecesize
            Tools.write(writer, offset, _piecesize);
            offset += 4;
            //Buffermap
            int i;
            for (i=0;i<this.nbPieces();i++) {
                Tools.write(writer, offset, -1);
                offset += 4;
            }

            writer.force(true);
            writer_tmp.close();
        }
        catch (Exception e) {
            System.out.println("Unable to create a new tmp file");
            e.printStackTrace();
        }
    }

    /**
     * Lis le header du fichier temporaire 
     * et charge ses informations
     */
    private void readHeaderTmpFile()
    {
        try {
            FileInputStream reader_tmp = new FileInputStream(this);
            FileChannel reader = reader_tmp.getChannel();

            int key_size = 0;
            int offset = 0;

            //Taile de le clef
            key_size = Tools.readInt(reader, offset);
            offset += 4;
            //Clef
            _key = Tools.readString(reader, offset, key_size);
            offset += key_size;
            //Size
            _size = Tools.readInt(reader, offset);
            offset += 4;
            //piecesize
            _piecesize = Tools.readInt(reader, offset);
            offset += 4;
            //Buffermap
            _buffermap = new Buffermap(this.nbPieces(), false);
            int i;
            for (i=0;i<this.nbPieces();i++) {
                int index = Tools.readInt(reader, offset);
                if (index >= 0) {
                    _buffermap.setBit(i, true);
                }
                else {
                    _buffermap.setBit(i, false);
                }
                offset += 4;
            }
            
            reader_tmp.close();
        }
        catch(Exception e) {
            System.out.println("Unable to read tmp file header");
            e.printStackTrace();
        }
    }

    /**
     * Lis et charge l'objet a partir du fichier
     * complet sur le disque
     */
    private void readInfoCompleteFile()
    {
        _size = (int)this.length();
        _piecesize = (Integer)App.config.get("pieceSize");
        _key = this.computeHash();
        assert _key.length() == 32;
        _buffermap = new Buffermap(this.nbPieces(), true);
    }

    /**
     * Renvoi la chaine correspondant au hash
     * md5 du fichier
     */
    private String computeHash()
    {
        try {
            FileInputStream reader_tmp = new FileInputStream(this);
            BufferedInputStream reader = new BufferedInputStream(reader_tmp);

            byte[] buffer = new byte[2048];
            MessageDigest hash = MessageDigest.getInstance("MD5");
            int count;
            do {
                count = reader.read(buffer);
                if (count > 0) {
                    hash.update(buffer, 0, count);
                }

            }
            while (count != -1);
            byte[] b = hash.digest();

            reader.close();

            String result = "";
            for (int i=0;i<b.length;i++) {
                result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
            }
        
            return result;
        }
        catch (Exception e) {
            System.out.println("Unable to compute key for complete file");
            e.printStackTrace();
        }
        return new String();
    }

    /**
     * Lis et retourne une pièce depuis le fichier 
     * complet sur le disque
     * @param num : numéros de la piece
     */
    private byte[] readPieceCompleteFile(int num)
    {
        if (num < 0 || num >= this.nbPieces()) {
            throw new IllegalArgumentException();
        }
        
        try {
            FileInputStream reader_tmp = new FileInputStream(this);
            FileChannel reader = reader_tmp.getChannel();

            int size = _piecesize;
            if (num == this.nbPieces()-1) {
                size = _size - _piecesize*(this.nbPieces()-1);
            }

            byte[] piece = Tools.readBytes(reader, _piecesize*num, size);
            reader_tmp.close();
        
            return piece;
        }
        catch (Exception e) {
            System.out.println("Unable to read complete file piece");
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * Lis et retourne une piece depuis le fichier
     * temporaire sur le disque (la piece doit exister)
     * @param num : numéros de la piece
     */
    synchronized private byte[] readPieceTmpFile(int num)
    {
        if (num < 0 || num >= this.nbPieces()) {
            throw new IllegalArgumentException();
        }

        try {
            FileInputStream reader_tmp = new FileInputStream(this);
            FileChannel reader = reader_tmp.getChannel();

            int index_piece = Tools.readInt(reader, 4 + _key.length() + 4 + 4 + 4*num);
            if (index_piece < 0) {
                throw new IllegalArgumentException();
            }

            int size = _piecesize;
            if (num == this.nbPieces()-1) {
                size = _size - _piecesize*(this.nbPieces()-1);
            }

            byte[] piece = Tools.readBytes(reader, this.headerSize() + _piecesize*index_piece, size);
            reader_tmp.close();

            return piece;
        }
        catch (Exception e) {
            System.out.println("Unable to read tmp file piece");
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * Ecrit la piece donnée dans le fichier
     * temporaire sur le disque
     * @param piece : pièce à écrire
     * @param num : numéros de la pièce
     */
    synchronized private void writePieceTmpFile(byte[] piece, int num)
    {
        if (num < 0 || num >= this.nbPieces()) {
            throw new IllegalArgumentException();
        }
        if (piece.length > _piecesize) {
            throw new IllegalArgumentException();
        }
       
        try {
            RandomAccessFile writer_tmp = new RandomAccessFile(this, "rw");
            FileChannel writer = writer_tmp.getChannel();

            int index_piece = ((int)this.length() - this.headerSize()) / _piecesize;

            if (piece.length < _piecesize) {
                piece = Arrays.copyOf(piece, _piecesize);
            }
            Tools.write(writer, 4 + _key.length() + 4 + 4 + 4*num, index_piece);
            Tools.write(writer, this.headerSize() + _piecesize*index_piece, piece);
            
            writer.force(true);
            writer_tmp.close();
        }
        catch (Exception e) {
            System.out.println("Unable to write tmp file piece");
            e.printStackTrace();
        }
    }

    /**
     * Créer le fichier complet correspondant
     * Copie et réassemble les données
     * Supprime le fichier temporaire et renvoi le nouveau fichier
     */
    synchronized public FileShared tmpToComplete()
    {
        String name = this.getName();
        name = name.substring(
            0, 
            name.length() - ((String)App.config.get("tmpExtension")).length()
        );

        File complete = new File(App.config.get("downloadDir") + File.separator + name);
        if (complete.exists()) {
            throw new IllegalArgumentException();
        }

        try {
            FileOutputStream writer_tmp = new FileOutputStream(complete, true);
            FileChannel writer = writer_tmp.getChannel();

            int i;
            for (i=0;i<this.nbPieces();i++) {
                byte[] piece = this.readPieceTmpFile(i);
                Tools.write(writer, 0, piece);
            }

            writer.force(true);
            writer_tmp.close();
        }
        catch (Exception e) {
            System.out.println("Unable to write complete file");
            e.printStackTrace();
        }

        this.delete();

        return new FileShared(name);
    }
}

