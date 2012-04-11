/**
 * Gère et commande le partage de fichier
 */

import java.io.*;

class DownloadManager
{
    /**
     * Connexion vers le traker
     */
    public static Protocol tracker;

    /**
     * Créer le DownloadManager
     */
    public DownloadManager()
    {
        tracker = new Protocol();
    }

    /**
     * Démarre le thread serveur d'écoute
     */
    public void initServer()
    {
        ServerListenThread server = new ServerListenThread();
        server.start();
    }

    /**
     * Initie la connexion avec le tracker
     * Démarre les threads de téléchargement des fichiers
     */
    public void initDownloads()
    {
        System.out.println("Contacting the tracker");
        try {
            String ip = (String)App.config.get("trackerIP");
            int port = (Integer)App.config.get("trackerPort");
            App.downloads.tracker = new Protocol(ip, port);
        }
        catch (Exception e) {
            System.out.println("Unable to contact tracker");
            e.printStackTrace();
        }

        FileShared[] files = App.files.getTmpFiles();
        for (int i=0;i<files.length;i++) {
            ClientDownloadThread client = new ClientDownloadThread(files[i]);
            client.start();
        }
    }
}

