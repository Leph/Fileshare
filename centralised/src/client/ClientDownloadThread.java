/**
 * Réprésente le thread gérant le téléchargement 
 * d'un fichier
 */

import java.io.*;
import java.util.*;
import java.lang.*;

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
        assert _file.nbMissingPieces() > 0;
    }

    /**
     * Récupère des pairs possédant le fichier
     * auprès du tracker et les enregistres
     */
    public void retrievePeers()
    {
        System.out.println("Retrieve peers : " + _file.getName());
        try {
            String[] data = App.downloads.tracker.getFile(_file.getKey());
            for (int i=0;i<data.length;i+=2) {
                String hash = App.peers.add(
                    data[i], 
                    Integer.parseInt(data[i+1])
                );
                System.out.println("get : " + hash);
                if (hash != null && _file.peers.get(hash) == null) {
                    System.out.println("save : " + hash);
                    _file.peers.put(hash, new Buffermap(_file.nbPieces(), false));
                }
            }
        }
        catch (Exception e) {
            System.out.println("Unable to retrieve peers : " + _file.getName());
            e.printStackTrace();
        }
    }

    /**
     * Récupère et met a jours le buffermap
     * de tous les pairs connus
     */
    public void retrieveBuffermap()
    {
        System.out.println("Retrieve buffermap : " + _file.getName());
        Set keys = _file.peers.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String hash = (String)it.next();
            Peer peer = App.peers.getByHash(hash);
            try {
                Buffermap buffermap = peer.socket.interested(_file.getKey());
                _file.peers.put(hash, buffermap);
                System.out.println("get from : " + hash);
                buffermap.print();
            }
            catch (Exception e) {
                System.out.println("Unable to retrieve buffermap : " + hash);
                e.printStackTrace();
            }
        }
    }

    /**
     * Récupère des pièces du fichier
     * Renvoi true si au moins une piece 
     * à été téléchargée false sinon
     */
    public boolean retrievePieces()
    {
        System.out.println("-- New Retrieve piece : " + _file.getName());
        _file.getBuffermap().print();
        int max = (Integer)App.config.get("maxDownloadedPieces");
        
        Set keys = _file.peers.keySet();
        Iterator it = keys.iterator();
        try {
            while (it.hasNext()) {
                String hash = (String)it.next();
                System.out.println("try dl from peer : " + hash);
                Buffermap buffermap = _file.peers.get(hash);
                buffermap.print();
                int[] indexes = _file.getBuffermap().getDownloadPieces(buffermap, max);
                System.out.println("possibles piece : " + indexes.length);
                for (int i=0;i<indexes.length;i++) {
                    System.out.println("> : " + indexes[i]);
                }

                if (indexes.length > 0) {
                    Peer peer = App.peers.getByHash(hash);
                    System.out.println("try download!");
                    byte[][] data = peer.socket.getPieces(_file.getKey(), indexes);
                    System.out.println("Donnée dl : " + data.length);
                    for (int i=0;i<indexes.length;i++) {
                        System.out.println("write piece " + indexes[i]);
                        _file.writePiece(data[i], indexes[i]);
                    }
                    _file.getBuffermap().print();
                    return true;
                }
                System.out.println("pas de pièce");
            }
        }
        catch (Exception e) {
            System.out.println("Unable to download pieces : " + _file.getName());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fonction principale du thread
     *
     */
    public void run()
    {
        System.out.println("Starting download : " + _file.getName());

        this.retrievePeers();
        this.retrieveBuffermap();
        while (_file.nbMissingPieces() > 0) {
            if (!this.retrievePieces()) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.retrievePeers();
                this.retrieveBuffermap();
            }
        }
        System.out.println("Complete : " + _file.getName());
        App.files.transformToComplete(_file.getKey());
    }
}

