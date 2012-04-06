/**
 * Classe regroupant quelques fontions
 * utilitaires accessibles en static
 */

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;

class Tools
{
    /**
     * Ecrit l'entier val à la position offset
     * dans le Channel writer spécifié
     */
    public static void write(FileChannel writer, int offset, int val) throws IOException
    {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(val);
        b.rewind();

        writer.position(offset);
        int written = writer.write(b);
        if (written != 4) {
            throw new IOException("Write failed");
        }
    }
    
    /**
     * Ecrit la chaine s à la position offset
     * dans le Channel writer spécifié
     */
    public static void write(FileChannel writer, int offset, String s) throws IOException
    {
        ByteBuffer b = ByteBuffer.wrap(s.getBytes()); 

        writer.position(offset);
        int written = writer.write(b);
        if (written != s.length()) {
            throw new IOException("Write failed");
        }
    }

    /**
     * Ecrit le buffer binaire bb à la position offset
     * dans le Channel writer spécifié
     */
    public static void write(FileChannel writer, int offset, byte[] bb) throws IOException
    {
        ByteBuffer b = ByteBuffer.wrap(bb); 

        writer.position(offset);
        int written = writer.write(b);
        if (written != bb.length) {
            throw new IOException("Write failed");
        }
    }

    /**
     * Lit et retourne en entier à la position offset
     * dans le Channel reader
     */
    public static int readInt(FileChannel reader, int offset) throws IOException
    {
        ByteBuffer b = ByteBuffer.allocate(4);
        
        reader.position(offset);
        int read = reader.read(b);

        if (read != 4) {
            throw new IOException("Write failed");
        }

        b.rewind();
        return b.getInt();
    }

    /**
     * Lit et retourne une chaine de taille size 
     * à la position offset dans le Channel reader
     */
    public static String readString(FileChannel reader, int offset, int size) throws IOException
    {
        ByteBuffer b = ByteBuffer.allocate(size);
        
        reader.position(offset);
        int read = reader.read(b);
        if (read != size) {
            throw new IOException("Write failed");
        }
    
        String s = new String(b.array());
        assert s.length() == size;
        return s;
    }
    
    /**
     * Lit et retourne un buffer binaire de taille size 
     * à la position offset dans le Channel reader
     */
    public static byte[] readBytes(FileChannel reader, int offset, int size) throws IOException
    {
        ByteBuffer b = ByteBuffer.allocate(size);
        
        reader.position(offset);
        int read = reader.read(b);

        if (read != size) {
            throw new IOException("Write failed");
        }
    
        byte[] bb = b.array();
        assert bb.length == size;
        return bb;
    }

    /**
     * Concatène deux buffer d'octets et renvoi le résultat
     */
    public static byte[] concatBytes(byte[] b1, byte[] b2)
    {
        byte[] buffer = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, buffer, 0, b1.length);
        System.arraycopy(b2, 0, buffer, b1.length, b2.length);

        return buffer;
    }
}

