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
    public Peer(String ip, int port) throws UnknownHostException, IOException
    {
        _ip = ip;
        _port = port;
        socket = new Protocol(ip, port);
    }

    /**
     * Renvoi la chaine permettant d'identifer
     * de manière unique le pair
     */
    public String getHash()
    {
        return _ip+":"+_port;
    }
}

