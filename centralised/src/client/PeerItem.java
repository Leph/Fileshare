/**
 * Représente un pair pour le téléchargement
 * d'un fichier donné
 */

import java.io.*;

class PeerItem
{
    /**
     * Le hash unique du pair permettant
     * de le retrouver dans le manager
     */
    public String hash;

    /**
     * Le buffermap du pair pour ce fichier
     */
    public Buffermap buffermap;

    /**
     * Constructeur
     */
    public PeerItem(String h, Buffermap b)
    {
        hash = h;
        buffermap = b;
    }
}

