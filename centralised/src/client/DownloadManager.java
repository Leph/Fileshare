/**
 * Gère et commande le partage de fichier
 */

import java.io.*;

class DownloadManager
{
    /**
     * Créer le DownloadManager
     */
    public DownloadManager()
    {
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
     * Démarre les threads de téléchargement des fichiers
     */
    public void initDownloads()
    {
        FileShared[] files = App.files.getTmpFiles();
        for (int i=0;i<files.length;i++) {
            ClientDownloadThread client = new ClientDownloadThread(files[i]);
            client.start();
        }
    }
}

