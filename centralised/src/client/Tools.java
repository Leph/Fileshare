/**
 * Classe regroupant quelques fontions
 * utilitaires accessibles en static
 */

import java.nio.*;

class Tools
{
    public static byte[] intToBytes(int n)
    {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putInt(n);
        return bb.array();
    }

    public static byte[] stringToBytes(String s)
    {
        return s.getBytes();
    }

    public static int bytesToInt(byte[] b)
    {
        if (b.length != 8) {
            throw new IllegalArgumentException();
        }

        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.put(b);
        return bb.getInt();
    }

    public static String bytesToString(byte[] b)
    {
        return new String(b);
    }
}

