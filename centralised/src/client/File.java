/**
 * Représente un fichier
 */

class File 
{
    /**
     * Etat du fichier
     * 0 = looked : le fichier vient d'être recherché
     *              on ne connais que son nom, taille, taille
     *              des pieces et sa clef; on ne possède rien du fichier
     * 1 = peered : on connais des pairs pour ce fichier le fichier est en réception
     * 2 = upload : Le fichier est complet et est en cours ou attente d'envoi
     */
    private int _status;

    /**
     * buffermap du fichier
     */
    private long _buffermap;

    /**
     * Taille du fichier
     */
    private long _size;

    /**
     * Taille des pièces du fichier
     */
    private long _piecesize;

    /**
     * Clef du fichier
     */
    private String _key;

    /**
     * Nom du fichier
     */
    private String _name;

    /**
     * Renvoi la taille total du fichier
     */
    public long getSize()
    {
        return _size;
    }

    /**
     * Renvoi le buffermap
     */
    public long getBuffermap()
    {
        return _buffermap;
    }

    /**
     * Renvoi la taille des pieces
     */
    public long getPieceSize()
    {
        return _piecesize;
    }

    /**
     * Renvoi la clef
     */
    public String getKey()
    {
        return _key;
    }

    /**
     * Renvoi le nom du fichier
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Renvoi nombre de pieces total
     */
    public long nbPieces()
    {
        long nb =  _size / _piecesize;
        if ((_size % _piecesize) > 0 ) {
            return nb + 1;
        }
        else {
            return nb;
        }
    }

    /**
     * Renvoi la taille du buffermap
     * (nombre d'octets à lire)
     */
    public long buffermapSize()
    {
        long nb = this.nbPiece();
        if ((nb % 8) > 0) {
            return (nb / 8) + 1;
        }
        else {
            return nb / 8;
        }
    }
}

