/**
 * Représente le thread serveur d'écoute 
 * du pair
 */

import java.io.*;
import java.lang.*;
import java.net.*;
import java.io.IOException;

class ServerListenThread extends Thread
{
    /**
     * Créer le thread serveur principal d'écoute
     */
    ServerListenThread()
    {
        super();
    }

    /**
     * Fonction principale du thread
     *
     * Réceptionne les connexions entrantes
     * et lance des threads pour gérer ses connexions
     */
    public void run()
    {
        int port = (Integer)App.config.get("listenPort");
        System.out.println("Starting listening port : " + port);
        while (true) {
            try {
                ListenServerSocket server = new ListenServerSocket(port);
                Protocol socket = server.accept();
            }
            catch (IOException e) {
                System.out.println("Error listen server :");
                e.printStackTrace();
            }
        }
    }
}

