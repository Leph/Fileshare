/**
 * Représente un pair
 */

import java.net.*;
import java.io.IOException;

class Peer
{
    /**
     * Ip du pair
     */
    private String _ip;

    /**
     * Port d'écoute du pair
     */
    private int _port;

    /**
     * Connexion cliente vers ce pair
     */
    public Protocol socket;

    /**
     * Créer et initialise de pair
     */
    public Peer(String ip, int port) 
    {
        _ip = ip;
        _port = port;
    }

    /**
     * Connecte ou reconnecte la socket interne
     * Renvoi true si la connexion est établie, false sinon
     */
    public boolean connect()
    {
        try {
            socket = new Protocol(_ip, _port);
            return true;
        }
        catch (Exception e) {
            System.out.println("Unable to contact peer : " + 
                this.getHash()
            );
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Renvoi la chaine permettant d'identifer
     * de manière unique le pair
     */
    public String getHash()
    {
        return Peer.computeHash(_ip, _port);
    }

    /**
     * Retourne le hash unique correspondant
     * à l'adresse ip et au port donné
     */
    public static String computeHash(String ip, int port)
    {
        return ip + ":" + port;
    }

    /**
     * Indique si la conexion intenre est active
     */
    public boolean isConnected()
    {
        return (socket != null) && 
            socket.isConnected() && 
            !socket.isInputShutdown() && 
            !socket.isOutputShutdown() &&
            !socket.isClosed();
    }

    /**
     * Ferme la connexion
     */
    public void closeConnection()
    {
        try {
            this.socket.close();
        }
        catch (Exception e) {
            System.out.println("Unable to close connection : " + this.getHash());
            e.printStackTrace();
        }
    }

    /**
     * Renvoi l'adresse ip
     */
    public String getIP()
    {
        return _ip;
    }

    /**
     * Renvoi le port
     */
    public int getPort()
    {
        return _port;
    }
}

