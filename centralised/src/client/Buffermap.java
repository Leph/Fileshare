/**
 * Représente et implémente un buffermap
 */

class Buffermap
{
    /**
     * buffermap : tableau d'octets
     */
    private byte[] _buffer;

    /**
     * Contruit le buffermap à partir
     * d'un tableau de bit
     */
    public Buffermap(byte[] buffer)
    {
        _buffer = buffer;
    }

    /**
     * Contruit le buffermap connaissant sa taille
     * Initialisation de toutes les pieces à 0
     */
    public Buffermap(long size)
    {
        _buffer = new byte[size];
        int i;
        for (i=0;i<_buffer.length;i++) {
            _buffer[i] = 0x0;
        }
    }

    /**
     * Renvoi la taille du buffermap
     */
    public long size()
    {
        return _buffer.length;
    }

    /**
     * Renvoi le nombre de bits du buffermap
     */
    public long bitSize()
    {
        return size() * 8;
    }

    /**
     * Renvoi la valeur du bit demandé
     * la numérotation commence à 0
     */
    public boolean getBit(long n)
    {
        if (n < 0 || n >= bitSize()) {
            throw new IndexOutOfBoundsException();
        }

        long numByte = n / 8;
        long numBit = n % 8;
        byte mask = 0x1;

        return (boolean)((mask << numBit) & _buffer[numByte]);
    }

    /**
     * Enregistre une valeur pour un bit donné
     */
    public void setBit(long n, boolean val)
    {
        if (n < 0 || n >= bitSize()) {
            throw new IndexOutOfBoundsException();
        }
        
        
        long numByte = n / 8;
        long numBit = n % 8;

        if (val) {
            byte mask = 0x1;
            _buffer[numByte] = (mask << numBit) | _buffer[numByte];
        }
        else {
            byte mask = 0x1;
            _buffer[numByte] = (~(mask << numBit)) & _buffer[numByte];
        }
    }
}

