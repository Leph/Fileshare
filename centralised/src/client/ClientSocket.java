/**
 * Représente une connexion cliente vers un hôte : 
 * Tracker ou autres Peer
 * Implémente le protocole réseau
 */

import java.io.*;
import java.net.*;

class ClientSocket extends Socket
{
    /**
     * Créér la socket
     * @param addr : l'adresse ip sous forme pointée
     * @param port : le numéros du port a contacter
     */
    public ClientSocket(String addr, int port)
    {
        super(addr, port);
        this.setSoTimeout(App.config.get("SocketTimeOut"));
    }

    public String query(byte[] query, long len)
    {
        BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(this.getOutputStream())
        );
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(this.getInputStream())
        );

        writer.write(query, 0, len);
        writer.flush();

        
    }

}

