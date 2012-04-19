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
     * Recontact le serveur
     */
    public void connectTracker()
    {
        if (
            App.downloads.tracker == null ||
            !App.downloads.tracker.isConnected() ||
            App.downloads.tracker.isInputShutdown() ||
            App.downloads.tracker.isOutputShutdown() ||
            App.downloads.tracker.isClosed()
        ) {
            try {
                String ip = (String)App.config.get("trackerIP");
                int port = (Integer)App.config.get("trackerPort");
                App.downloads.tracker = new Protocol(ip, port);
            }
            catch (Exception e) {
                System.out.println("Unable to Recontact tracker");
                e.printStackTrace();
            }
        }
    }

    /**
     * Initie la connexion avec le tracker
     * Démarre les threads de téléchargement des fichiers
     * et de mise à jour
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

        UpdateThread update = new UpdateThread();
        update.start();
    }

    /**
     * Recherche un fichier auprès du tracker
     * @param filename : nom du fichier à rechercher
     */
    public String[] search(String filename)
    {
        try {
            App.downloads.connectTracker();
            String[] data = App.downloads.tracker.look(filename);
            return data;
        }
        catch (Exception e) {
            System.out.println("Unable to search for file : " + filename);
            e.printStackTrace();
            return new String[0];
        }

    }

    /**
     * Commence le téléchargement du fichier donné
     */
    public void startDownload(String name, String key, int size, int piecesize)
    {
        FileShared file = new FileShared(name, key, size, piecesize);
        App.files.addFile(file);
        ClientDownloadThread client = new ClientDownloadThread(file);
        client.start();
    }
}

