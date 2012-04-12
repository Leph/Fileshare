/**
 * Classe permettant de manipuler
 * les fichiers complets et temporaires
 */

import java.io.*;
import java.util.*;

class FileManager
{
    /**
     * Conteneur des fichiers
     */
    private Map<String, FileShared> _files;

    /**
     * Construit le manager
     */
    public FileManager()
    {
        _files = new HashMap<String, FileShared>();
    }

    /**
     * Initialise le manager
     */
    public void init()
    {
        this.scanCompleteDir();
        this.scanTmpDir();
    }

    /**
     * Lis et insert les fichiers complets dans le container
     */
    private void scanCompleteDir()
    {
        System.out.println("Looking for completes files : " + (String)App.config.get("downloadDir"));
        File dir = new File((String)App.config.get("downloadDir"));
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Unable to read directory : " + 
                (String)App.config.get("downloadDir"));
            throw new IllegalArgumentException();
        }
        File[] scan = dir.listFiles();

        for (int i=0;i<scan.length;i++) {
            FileShared f = new FileShared(scan[i].getName());
            _files.put(f.getKey(), f);
        }
    }

    /**
     * Lis et insert les fichiers temporaires dans le container
     */
    private void scanTmpDir()
    {
        System.out.println("Looking for tmp files : " + (String)App.config.get("tmpDir"));
        File dir = new File((String)App.config.get("tmpDir"));
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Unable to read directory : " + 
                (String)App.config.get("tmpDir"));
            throw new IllegalArgumentException();
        }
        File[] scan = dir.listFiles();

        for (int i=0;i<scan.length;i++) {
            FileShared f = new FileShared(scan[i].getName());
            _files.put(f.getKey(), f);
        }
    }

    /**
     * Renvoi le nombre de fichiers contenu
     */
    public int getSize()
    {
        return _files.size();
    }

    /**
     * Retourne un fichier par sa clef
     */
    public FileShared getByKey(String key)
    {
        if (_files.get(key) == null) {
            throw new IllegalArgumentException("Unknown key");
        }
        else {
            return _files.get(key);
        }
    }

    /**
     * Retourne tout les fichiers contenus
     */
    public FileShared[] getAllFiles()
    {
        FileShared[] files = new FileShared[_files.size()];
        int i = 0;

        Set<String> keys = _files.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()){
            String key = (String)it.next();
            FileShared f = _files.get(key);
            files[i] = f;
            i++;
        }

        return files;
    }

    /**
     * Retourne tout les fichiers complets
     */
    public FileShared[] getCompleteFiles()
    {
        FileShared[] files = this.getAllFiles();
        int nb = 0;

        for (int i=0;i<files.length;i++) {
            if (files[i].isComplete()) {
                nb++;
            }
        }

        FileShared[] filesComplete = new FileShared[nb];
        int j = 0;
        for (int i=0;i<files.length;i++) {
            if (files[i].isComplete()) {
                filesComplete[j] = files[i];
                j++;
            }
        }

        return filesComplete;
    }
    
    /**
     * Retourne tout les fichiers temporaires
     */
    public FileShared[] getTmpFiles()
    {
        FileShared[] files = this.getAllFiles();
        int nb = 0;

        for (int i=0;i<files.length;i++) {
            if (!files[i].isComplete()) {
                nb++;
            }
        }

        FileShared[] filesTmp = new FileShared[nb];
        int j = 0;
        for (int i=0;i<files.length;i++) {
            if (!files[i].isComplete()) {
                filesTmp[j] = files[i];
                j++;
            }
        }

        return filesTmp;
    }

    /**
     * Converti le fichier spécifié par sa clef
     * en fichier complet
     */
    public void transformToComplete(String key)
    {
        FileShared tmp = this.getByKey(key);
        if (tmp.nbMissingPieces() != 0) {
            throw new IllegalArgumentException("File not complete : " + tmp.getName());
        }

        FileShared complete = tmp.tmpToComplete();
        _files.put(key, complete);
    }

    /**
     * Affiche l'état du manager
     * (debugage)
     */
    public void print()
    {
        System.out.println("Size : " + this.getSize());
        FileShared[] files = this.getAllFiles();
        for (int i=0;i<files.length;i++) {
            System.out.println(
                files[i].getKey()+" => "+
                files[i].getName()+" -- "+
                files[i].getSize()+" -- "+
                files[i].isComplete()
            );
        }
    }

    /**
     * Ajoute le fichier donné au manager
     */
    public void addFile(FileShared file)
    {
        _files.put(file.getKey(), file);
    }
}

