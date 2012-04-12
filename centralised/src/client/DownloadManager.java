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
            App.downloads.tracker.announce();
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

    /**
     * Recherche et initie le téléchargement
     * de fichier
     * @param filename : nom du fichier à rechercher
     */
    public void search(String filename)
    {
        try {
            String[] data = App.downloads.tracker.look(filename);

            for (int i=0;i<data.length;i+=4) {
                String name = data[i];
                int size = Integer.parseInt(data[i+1]);
                int piecesize = Integer.parseInt(data[i+2]);
                String key = data[i+3];

                FileShared file = new FileShared(name, key, size, piecesize);
                App.files.addFile(file);
                
                ClientDownloadThread client = new ClientDownloadThread(file);
                client.start();
            }
        }
        catch (Exception e) {
            System.out.println("Unable to search for file : " + filename);
            e.printStackTrace();
        }

    }
}

