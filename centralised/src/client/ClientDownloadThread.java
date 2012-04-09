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
     * Fonction principale du thread
     *
     */
    public void run()
    {
        System.out.println("Starting download : " + _file.getName());
    }
}

