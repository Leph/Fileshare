/**
 * Classe regroupant quelques fontions
 * utilitaires accessibles en static
 */

class Tools
{
    private static byte[] longToBytes(long n)
    {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(n);
        return bb.array();
    }

    private static byte[] stringToBytes(String s)
    {
        return s.getBytes();
    }

    private static long bytesToLong(byte[] b)
    {
        if (b.length != 8) {
            throw new IllegalArgumentException();
        }

        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.put(b);
        return bb.getLong();
    }

    private static String bytesToString(byte[] b)
    {
        return String(b);
    }
}

