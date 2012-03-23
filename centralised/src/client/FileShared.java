/**
 * Représente un fichier en cours de 
 * partage
 */

import java.io.*;
import java.util.*;
import java.lang.*;
import java.security.*;

class FileShared extends File
{
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
            App.config.get("tmpDir") + File.pathSeparator + name : 
            App.config.get("downloadDir") + File.pathSeparator + name
        );

        if (name.endsWith((String)App.config.get("tmpExtension"))) {
            _iscomplete = false;
        }
        else {
            _iscomplete = true;
        }

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
    public FileShared(String name, String key, int size, int piecesize)
    {
        super(
            App.config.get("tmpDir") + 
            File.pathSeparator + 
            name + 
            App.config.get("tmpExtension")
        );

        if (this.exists()) {
            throw new IllegalArgumentException();
        }

        _key = key;
        _size = size;
        _piecesize = piecesize;
        _iscomplete = false;
        
        int buffersize = _size / _piecesize;
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
     * Renvoi l'état du fichier
     */
    public boolean isComplete()
    {
        return _iscomplete;
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
        return 4 + _key.length() + 4 + 4  + 4*this.nbPieces();
    }

    /**
     * Initialise le header du fichier temporaire
     */
    private void initHeaderTmpFile()
    {
        try {
            FileOutputStream writer_tmp = new FileOutputStream(this);
            BufferedOutputStream writer = new BufferedOutputStream(writer_tmp);

            int offset = 0;

            //Taille de la clef
            writer.write(Tools.intToBytes(_key.length()), offset, 4);
            offset += 4;
            //Clef
            writer.write(Tools.stringToBytes(_key), offset, _key.length());
            offset += _key.length();
            //Size
            writer.write(Tools.intToBytes(_size), offset, 4);
            offset += 4;
            //piecesize
            writer.write(Tools.intToBytes(_piecesize), offset, 4);
            offset += 4;
            //Buffermap
            int i;
            for (i=0;i<this.nbPieces();i++) {
                writer.write(Tools.intToBytes(-1), offset, 4);
                offset += 4;
            }

            writer.flush();
            writer.close();
        }
        catch (Exception e) {
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
            BufferedInputStream reader = new BufferedInputStream(reader_tmp);

            byte[] tmp = new byte[4];
            int key_size = 0;
            int offset = 0;

            //Taile de le clef
            reader.read(tmp, offset, 4);
            key_size = Tools.bytesToInt(tmp);
            offset += 4;
            //Clef
            byte[] key = new byte[key_size];
            reader.read(key, offset, key_size);
            _key = Tools.bytesToString(key);
            offset += key_size;
            //Size
            reader.read(tmp, offset, 4);
            _size = Tools.bytesToInt(tmp);
            offset += 4;
            //piecesize
            reader.read(tmp, offset, 4);
            _piecesize = Tools.bytesToInt(tmp);
            offset += 4;
            //Buffermap
            int buffer_size = this.nbPieces();
            if ((buffer_size % 8) == 0) {
                buffer_size = buffer_size / 8;
            }
            else {
                buffer_size = buffer_size / 8 + 1;
            }
            _buffermap = new Buffermap(buffer_size);
            int i;
            for (i=0;i<this.nbPieces();i++) {
                reader.read(tmp, offset, 4);
                if (Tools.bytesToInt(tmp) >= 0) {
                    _buffermap.setBit(i, true);
                }
                else {
                    _buffermap.setBit(i, false);
                }
                offset += 4;
            }
            
            reader.close();
        }
        catch(Exception e) {
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

        /* ! BUFFERMAP INUTILE SI FICHIER COMPLET ! */
        /*
        int buffersize = _size / _piecesize;
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
            BufferedInputStream reader = new BufferedInputStream(reader_tmp);

            byte[] piece = new byte[_piecesize];
            reader.read(piece, _piecesize*num, _piecesize); 
            reader.close();
        
            return piece;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * Lis et retourne une piece depuis le fichier
     * temporaire sur le disque (la piece doit exister)
     * @param num : numéros de la piece
     */
    private byte[] readPieceTmpFile(int num)
    {
        if (num < 0 || num >= this.nbPieces()) {
            throw new IllegalArgumentException();
        }

        try {
            FileInputStream reader_tmp = new FileInputStream(this);
            BufferedInputStream reader = new BufferedInputStream(reader_tmp);

            byte[] tmp = new byte[4];
            reader.read(tmp, 4 + _key.length() + 4 + 4 + 4*num, 4);
            int index_piece = Tools.bytesToInt(tmp);
            if (index_piece < 0) {
                throw new IllegalArgumentException();
            }

            byte[] piece = new byte[_piecesize];
            reader.read(piece, this.headerSize() + _piecesize*num, _piecesize); 
            reader.close();

            return piece;
        }
        catch (Exception e) {
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
    private void writePieceTmpFile(byte[] piece, int num)
    {
        if (num < 0 || num >= this.nbPieces()) {
            throw new IllegalArgumentException();
        }
        if (piece.length > _piecesize) {
            throw new IllegalArgumentException();
        }
       
        try {
            FileOutputStream writer_tmp = new FileOutputStream(this);
            BufferedOutputStream writer = new BufferedOutputStream(writer_tmp);

            int index_piece = ((int)this.length() - this.headerSize()) / _piecesize;

            if (piece.length < _piecesize) {
                byte[] tmp = new byte[_piecesize];
                piece = Arrays.copyOf(piece, _piecesize);
            }
            writer.write(Tools.intToBytes(index_piece), 4 + _key.length() + 4 + 4 + 4*num, 4);
            writer.write(piece, this.headerSize() + _piecesize*index_piece, _piecesize);
            
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

