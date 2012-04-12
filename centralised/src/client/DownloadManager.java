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
            DownloadManager.tracker = new Protocol(ip, port);
            DownloadManager.tracker.announce();
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
     * Recherche et retourne les informations
     * le client fait une recherche mais pas forcement telecharge tous les fichiers
     * dans le resultat de recherche
     * @param filename : nom du fichier à rechercher
     */
    public String[] search(String filename)
    {
    	try {
			return DownloadManager.tracker.look(filename);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * commence le telechargement
     * 
     * @param	un tableau de taille 4 qui contient les informations
     * d'un fichier a telecharger
     */
    public void download(String[] data){
    	 String name = data[0];
         int size = Integer.parseInt(data[1]);
         int piecesize = Integer.parseInt(data[2]);
         String key = data[3];
         
         FileShared file = new FileShared(name, key, size, piecesize);
         App.files.addFile(file);
         
         ClientDownloadThread client = new ClientDownloadThread(file);
         client.start();
    }
}

