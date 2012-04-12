/**
 * Réprésente le thread gérant le téléchargement 
 * d'un fichier
 */

import java.util.*;

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
        try {
            String[] data = DownloadManager.tracker.getFile(_file.getKey());
            for (int i=0;i<data.length;i+=2) {
                String hash = App.peers.add(
                    data[i], 
                    Integer.parseInt(data[i+1])
                );
                if (hash != null && _file.peers.get(hash) == null) {
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
        Set<String> keys = _file.peers.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String hash = (String)it.next();
            Peer peer = App.peers.getByHash(hash);
            try {
                Buffermap buffermap = peer.socket.interested(_file.getKey());
                _file.peers.put(hash, buffermap);
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
        int max = (Integer)App.config.get("maxDownloadedPieces");
        
        Set<String> keys = _file.peers.keySet();
        Iterator<String> it = keys.iterator();
        try {
            while (it.hasNext()) {
                String hash = (String)it.next();
                Buffermap buffermap = _file.peers.get(hash);
                int[] indexes = _file.getBuffermap().getDownloadPieces(buffermap, max);

                if (indexes.length > 0) {
                    Peer peer = App.peers.getByHash(hash);
                    byte[][] data = peer.socket.getPieces(_file.getKey(), indexes);
                    for (int i=0;i<indexes.length;i++) {
                        _file.writePiece(data[i], indexes[i]);
                    }
                    return true;
                }
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
                this.retrievePeers();
                this.retrieveBuffermap();
            }
        }
    
        App.files.transformToComplete(_file.getKey());
    }
}

