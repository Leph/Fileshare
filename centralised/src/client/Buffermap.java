/**
 * Représente et implémente un buffermap
 */

class Buffermap
{
    /**
     * buffermap : tableau de bits
     */
    private boolean[] _buffer;

    /**
     * Nombre de pièces manquantes
     */
    private int _missingpieces;

    /**
     * Contruit le buffermap à partir
     * d'un tableau d'octets et du nombre
     * de pièces total
     */
    public Buffermap(byte[] buffer, int nbpieces)
    {
        if (buffer.length*8 < nbpieces || (buffer.length-1)*8 > nbpieces) {
            throw new IllegalArgumentException();
        }

        _buffer = new boolean[nbpieces];
        _missingpieces = 0;

        for (int i=0;i<nbpieces;i++) {
            int numByte = i/8;
            int numBit = i%8;
            byte mask = (byte)((byte)0x01 << (byte)numBit);
            _buffer[i] = (byte)(mask & buffer[numByte]) > 0 ? true : false;
            if (!_buffer[i]) {
                _missingpieces++;
            }
        }
    }

    /**
     * Contruit le buffermap connaissant le nombre
     * de pièces
     * Initialisation de toutes les pieces à val
     */
    public Buffermap(int nbpieces, boolean val)
    {
        _buffer = new boolean[nbpieces];
        _missingpieces = nbpieces;

        for (int i=0;i<nbpieces;i++) {
            _buffer[i] = val;
        }
    }

    /**
     * Renvoi le nombre de pieces du buffermap
     */
    public int getNbPieces()
    {
        return _buffer.length;
    }

    /**
     * Renvoi la taille du buffer d'octets
     * représentant le buffermap
     */
    public int getBufferSize()
    {
        if (_buffer.length % 8 == 0) {
            return _buffer.length / 8;
        }
        else {
            return _buffer.length / 8 + 1;
        }
    }

    /**
     * Renvoi le nombre de pièces manquantes
     */
    public int getNbMissingPieces()
    {
        return _missingpieces;
    }

    /**
     * Renvoi le buffer d'octet représentant
     * le buffermap
     */
    public byte[] rawBuffer()
    {
        int size = this.getBufferSize();
        byte[] buffer = new byte[size];

        for (int i=0;i<size;i++) {
            byte tmp = 0x00;
            for (int j=0;j<8;j++) {
                if (i*8+j < _buffer.length) {
                    if (_buffer[i*8+j]) tmp += 1;
                }
                tmp = (byte)(tmp << (byte)1);
            }
            buffer[i] = tmp;
        }

        return buffer;
    }

    /**
     * Renvoi la valeur du bit demandé
     * la numérotation commence à 0
     */
    public boolean getBit(int n)
    {
        if (n < 0 || n >= _buffer.length) {
            throw new IndexOutOfBoundsException();
        }

        return _buffer[n];
    }

    /**
     * Enregistre une valeur pour un bit donné
     */
    public void setBit(int n, boolean val)
    {
        if (n < 0 || n >= _buffer.length) {
            throw new IndexOutOfBoundsException();
        }
        
        if (val && !_buffer[n]) {
            _missingpieces--;
        }
        if (!val && _buffer[n]) {
            _missingpieces++;
        }

        _buffer[n] = val;
    }

    /**
     * Renvoi au maximum nb index de
     * pièces manquantes
     */
    public int[] getMissingPieces(int nb)
    {
        int min = Math.min(nb, _missingpieces);
        int[] indexes = new int[min];

        int k = 0;
        for (int i=0;i<_buffer.length;i++) {
            if (!_buffer[i]) {
                indexes[k] = i;
                k++;
            }
            if (k >= min) {
                break;
            }
        }

        return indexes;
    }

    /**
     * Renvoi au maximum nb pièces que le buffermap 
     * ne possède pas et que pair possède
     */
    public int[] getDownloadPieces(Buffermap pair, int nb)
    {
        if (_buffer.length != pair._buffer.length) {
            throw new IllegalArgumentException("Buffermap size not the same");
        }

        int[] indexes = new int[nb];

        int k = 0;
        for (int i=0;i<_buffer.length;i++) {
            if (!_buffer[i] && pair._buffer[i]) {
                indexes[k] = i;
                k++;
            }
            if (k >= nb) {
                break;
            }
        }
        if (k < nb) {
            int[] tmp = new int[k];
            for (int i=0;i<k;i++) {
                tmp[i] = indexes[i];
            }
            indexes = tmp;
        }

        return indexes;
    }
}

