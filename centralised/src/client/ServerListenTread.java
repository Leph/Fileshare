/**
 * Représente le thread serveur d'écoute 
 * du pair
 */

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
        try {
            ListenServerSocket server = new ListenServerSocket(port);
            while (true) {
                try {
                    Protocol socket = server.accept();
                    ServerConnectionThread connection = new ServerConnectionThread(socket);
                    connection.start();
                }
                catch (IOException e) {
                    System.out.println("Error accept connection :");
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error listen server :");
            e.printStackTrace();
        }
    }
}

