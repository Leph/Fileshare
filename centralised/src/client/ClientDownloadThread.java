/**
 * Réprésente le thread gérant le téléchargement 
 * d'un fichier
 */

import java.io.*;

class ClientDownloadThread extends Thread
{
    /**
     * Le fichier en cours de téléchargement
     */
    private FileShared _file;

    /**
     * Créer le thread de téléchargement
     * @param : le fichier a télécharger
     */
    ClientDownloadThread(FileShared file)
    {
        super();
        _file = file;
        assert !_file.isComplete();
    }

    /**
     * Récupère des pairs possédant le fichier
     * auprès du tracker et les enregistres
     */
    public void retrievePeers()
    {
        try {
            String[] data = App.downloads.tracker.getFile(_file.getKey());
            for (int i=0;i<data.length;i+=2) {
                String hash = App.peers.add(
                    data[i], 
                    Integer.parseInt(data[i+1])
                );
                if (hash != null && _file.peers.get(hash) == null) {
                    //_file.peers.put(hash, );
                }
            }

        }
        catch (Exception e) {
            System.out.println("Unable to retrieve peers : " + _file.getName());
            e.printStackTrace();
        }
    }

    /**
     * Fonction principale du thread
     *
     */
    public void run()
    {
        System.out.println("Starting download : " + _file.getName());
    }
}

