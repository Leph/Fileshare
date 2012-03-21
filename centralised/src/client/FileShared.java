/**
 * Représente un fichier en cours de 
 * partage
 */

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class FileShared extends File
{
    /**
     * Clef du fichier
     */
    private String _key;

    /**
     * Taille du fichier
     */
    private long _size;

    /**
     * Taille des pieces
     */
    private long _piecesize;

   /**
    * Buffermap du fichier
    */
    private Buffermap _buffermap;

    /**
     * Status du fichier
     */
    private boolean _iscomplete;

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
        String path;
        if (name.endWith(App.config.get("tmpExtension"))) {
            _iscomplete = false;
            path = App.config.get("tmpDir") + File.pathSeparator + name;
        }
        else {
            _iscomplet = true;
            path = App.config.get("downloadDir") + File.pathSeparator + name;
        }
        super(path);

        if (!this.exists()) {
            throw new IllegalArgumentException();
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
    public FileShared(String name, String key, long size, long piecesize)
    {
        String path = App.config.get("tmpDir") + File.pathSeparator + name + App.config.get("tmpExtension");
        super(path);

        if (this.exits()) {
            throw new IllegalArgumentException();
        }

        _key = key;
        _size = size;
        _piecesize = piecesize;
        _iscomplete = false;
        
        long buffersize = _size / _piecesize;
        if ((_size % _piecesize) > 0) buffersize++;
        if ((buffersize % 8) == 0) {
            buffersize = buffersize / 8;
        }
        else {
            buffersize = buffersize / 8 + 1;
        }
        _buffermap = new Buffermap(buffersize);

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
    public long getSize()
    {
        return _size;
    }

    /**
     * Renvoi la taille des pièces
     */
    public long getPieceSize()
    {
        return _piecesize;
    }

    /**
     * Renvoi l'état du fichier
     */
    public boolean isComplete()
    {
        return _iscomplete;
    }

    /**
     * Renvoi le nombre de pièce du fichier
     */
    public long nbPieces()
    {
        long nb = _size / _piecesize;
        if ((_size % _piecesize) == 0) {
            return nb;
        }
        else {
            return nb+1;
        }
    }

    /**
     * Structure du header des fichiers temporaires :
     * long   : taille de la clef en octets
     * string : key
     * long   : size
     * long   : piece size
     * //buffermap : le num correspond à la position de la piece dans le fichier tmp
     * // -1 : non présente
     * long   : num piece 1
     * long   : num piece 2
     * ***
     * long   : num piece n
     * //DATA
     * [piece num 1]
     * [piece num 2]
     * ***
     */

    /**
     * Renvoi la taille du header du fichier temporaire
     */
    private long headerSize()
    {
        return 8 + _key.length + 8 + 8  + 8*this.nbPieces();
    }

    /**
     * Initialise le header du fichier temporaire
     */
    private void initHeaderTmpFile()
    {
        FileOutputStream writer_tmp = new FileOutputStream(this);
        BufferedOutputStream writer = new BufferedOutputStream(writer_tmp);

        long offset = 0;

        //Taille de la clef
        writer.write(Tools.longToBytes(_key.length), offset, 8);
        offset += 8;
        //Clef
        writer.write(Tools.stringToBytes(_key), offset, _key.length);
        offset += _key.length;
        //Size
        writer.write(Tools.longToBytes(_size), offset, 8);
        offset += 8;
        //piecesize
        writer.write(Tools.longToBytes(_piecesize), offset, 8);
        offset += 8;
        //Buffermap
        long i;
        for (i=0;i<this.nbPieces();i++) {
            writer.write(Tools.longToBytes(-1), offset, 8);
            offset += 8;
        }

        writer.flush();
        writer.close();
    }

    /**
     * Lis le header du fichier temporaire 
     * et charge ses informations
     */
    private void readHeaderTmpFile()
    {
        FileInputStream reader_tmp = new FileInputStream(this);
        BufferedInputStream reader = new BufferedInputStream(reader_tmp);

        byte[] tmp = new byte[8];
        long key_size = 0;
        long offset = 0;

        //Taile de le clef
        reader.read(tmp, offset, 8);
        key_size = Tools.bytesToLong(tmp);
        offset += 8;
        //Clef
        byte[] key = new byte[key_size];
        reader.read(key, offset, key_size);
        _key = Tools.bytesToString(key);
        offset += key_size;
        //Size
        reader.read(tmp, offset, 8);
        _size = Tools.bytesToLong(tmp);
        offset += 8;
        //piecesize
        reader.read(tmp, offset, 8);
        _piecesize = Tools.bytesToLong(tmp);
        offset += 8;
        //Buffermap
        long buffer_size = this.nbPieces();
        if ((buffer_size % 8) == 0) {
            buffer_size = buffer_size / 8;
        }
        else {
            buffer_size = buffer_size / 8 + 1;
        }
        _buffermap = Buffermap(buffer_size);
        long i;
        for (i=0;i<this.nbPieces();i++) {
            reader.read(tmp, offset, 8);
            if (Tools.bytesToLong(tmp) >= 0) {
                _buffermap.setBit(i, 1);
            }
            else {
                _buffermap.setBit(i, 0);
            }
            offset += 8;
        }
        
        reader.close();
    }

    /**
     * Lis et charge l'objet a partir du fichier
     * complet sur le disque
     */
    private void readInfoCompleteFile()
    {
        _size = this.length();
        _piecesize = App.config.get("pieceSize");

        /* ! BUFFERMAP INUTILE SI FICHIER COMPLET ! */
        /*
        long buffersize = _size / _piecesize;
        if ((_size % _piecesize) > 0) buffersize++;
        if ((buffersize % 8) == 0) {
            buffersize = buffersize / 8;
        }
        else {
            buffersize = buffersize / 8 + 1;
        }
        _buffermap = new Buffermap(buffersize);
        */

        _key = this.computeHash();
    }

    /**
     * Renvoi la chaine correspondant au hash
     * md5 du fichier
     */
    private String computeHash()
    {
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

    /**
     * Lis et retourne une pièce depuis le fichier 
     * complet sur le disque
     * @param num : numéros de la piece
     */
    private byte[] readPieceCompleteFile(long num)
    {
        if (num < 0 || num >= this.nbPiece()) {
            throw new IllegalArgumentException();
        }
        
        FileInputStream reader_tmp = new FileInputStream(this);
        BufferedInputStream reader = new BufferedInputStream(reader_tmp);

        byte[] piece = new byte[_piecesize];
        reader.read(piece, _piecesize*num, _piecesize); 
        reader.close();

        return piece;
    }

    /**
     * Lis et retourne une piece depuis le fichier
     * temporaire sur le disque (la piece doit exister)
     * @param num : numéros de la piece
     */
    private byte[] readPieceTmpFile(long num)
    {
        if (num < 0 || num >= this.nbPiece()) {
            throw new IllegalArgumentException();
        }

        FileInputStream reader_tmp = new FileInputStream(this);
        BufferedInputStream reader = new BufferedInputStream(reader_tmp);

        byte[] tmp = new byte[8];
        reader.read(tmp, 8 + _key.length + 8 + 8 + 8*num, 8);
        long index_piece = Tools.bytesToLong(tmp);
        if (index_piece < 0) {
            throw new IllegalArgumentException();
        }

        byte[] piece = new byte[_piecesize];
        reader.read(piece, this.headerSize() + _piecesize*num, _piecesize); 
        reader.close();

        return piece;
    }

    /**
     * Ecrit la piece donnée dans le fichier
     * temporaire sur le disque
     * @param piece : pièce à écrire
     * @param num : numéros de la pièce
     */
    private void writePieceTmpFile(byte[] piece, long num)
    {
        if (num < 0 || num >= this.nbPiece()) {
            throw new IllegalArgumentException();
        }
        if (piece.length > _piecesize) {
            throw new IllegalArgumentException();
        }
        
        FileOutputStream writer_tmp = new FileOutputStream(this);
        BufferedOutputStream writer = new BufferedOutputStream(writer_tmp);

        long index_piece = (this.length() - this.headerSize()) / _piecesize;

        if (piece.length < _piecesize) {
            byte[] tmp = new byte[_piecesize];
            piece = Array.copyOf(piece, _piecesize);
        }
        writer.write(Tools.longToBytes(index_piece), 8 + _key.length + 8 + 8 + 8*num, 8);
        writer.write(piece, this.headerSize() + _piecesize*index_piece, _piecesize);
        
        writer.flush();
        writer.close();
    }
}

