/**
 * Une meilleur version du BufferedInputStream
 * Permet un accès en lecture aléatoire au buffer interne
 */

import java.io.*;

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
        _buffer = new byte[1024];
        _pos = 0;
    }
}

