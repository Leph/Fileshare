/**
 * Une meilleur version du BufferedInputStream
 * Permet un accès en lecture aléatoire au buffer interne
 */

import java.io.*;
import java.util.*;

class RandomInputStream
{
    /**
     * Stream d'entrée
     */
    private InputStream _input;

    /**
     * Buffer interne
     */
    private byte[] _buffer;

    /**
     * Position du curseur
     * tout les bytes du buffer inférieur sont valides
     */
    private int _pos;

    /**
     * Construit BufferedInputStream
     */
    public RandomInputStream(InputStream input)
    {
        _input = input;
        _buffer = new byte[2048];
        _pos = 0;
    }

    /**
     * Renvoi la taille des données disponibles
     */
    public int size() throws IOException {
        this.fillAvailable();
        return _pos;
    }

    /**
     * Renvoi le buffer à la position offset
     * et le longueur len
     * Attend si nécessaire
     */
    public byte[] read(int offset, int len) throws IOException
    {
        this.fillAvailable();
        if (offset >= _pos) {
            throw new IllegalArgumentException();
        }
        else {
            while (_pos < offset + len) {
                if (this.getData()) {
                    throw new IOException("Protocol error");
                }
            }
            return Arrays.copyOfRange(_buffer, offset, offset + len);
        }
    }

    /**
     * Lis tout le buffer diponible à partir de offset
     */
    public byte[] read(int offset) throws IOException
    {
        this.fillAvailable();
        if (offset > _pos) {
            throw new IllegalArgumentException();
        }
        else {
            return Arrays.copyOfRange(_buffer, offset, _pos);
        }
    }

    /**
     * Lis la socket pour obtenir plus de données
     * Renvoi true si la fin du flux est atteint
     * false sinon
     */
    public boolean getData() throws IOException
    {
        this.fillAvailable();
        int data = _input.read();
        if (data == -1) {
            return true;
        }
        _buffer[_pos] = (byte)data;
        _pos++;
        this.fillAvailable();
        return false;
    }

    /**
     * Lis les données disponibles du flux
     */
    private void fillAvailable() throws IOException
    {
        int len = _input.available();
        if (_buffer.length - _pos <= len + 1) {
            _buffer = Arrays.copyOf(_buffer, _buffer.length*2);
        }
        if (len > 0) {
            int read = _input.read(_buffer, _pos, len);
            if (read != -1) {
                _pos += read;
            }
        }
    }
}

