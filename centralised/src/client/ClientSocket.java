/**
 * Représente une connexion en mode client
 * client -- requète --> serveur
 */

import java.io.*;
import java.net.*;

class ClientSocket extends Socket
{
    /**
     * Initialise la connexion avec
     * IP, port
     */
    public ClientSocket(String ip, int port) throws UnknownHostException, IOException
    {
        super(ip, port);

        try {
            this.setSoTimeout((Integer)App.config.get("socketTimeout"));
        }
        catch (SocketException e) {
            System.out.println("Unable to set timeout");
            e.printStackTrace();
        }
    }

    /**
     * Implémente le message d'annonce au tracker
     */
    public void announce()
    {
        FileShared[] completefiles = App.files.getCompleteFiles();
        FileShared[] tmpfiles = App.files.getTmpFiles();

        String query = "announce listen "+(String)App.config.get("listenPort")+" ";
        query += "seed [";
        for (int i=0;i<completefiles.length;i++) {
            query += completefiles[i].getName()+" ";
            query += completefiles[i].getSize()+" ";
            query += completefiles[i].getPieceSize()+" ";
            query += completefiles[i].getKey();
            if (i != completefiles.length-1) {
                query += " ";
            }
        }
        query += "] leech [";
        for (int i=0;i<tmpfiles.length;i++) {
            query += completefiles[i].getKey();
            if (i != tmpfiles.length-1) {
                query += " ";
            }
        }
        query += "]";
    }
}

