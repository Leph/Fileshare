/**
 * Gère les connexions clientes vers
 * les autres pairs
 */

import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.*;
import java.io.IOException;

class PeerManager
{
    /**
     * HashMap des pairs
     */
    private Map<String, Peer> _peers;

    /**
     * Créer et initialise le manager
     */
    public PeerManager()
    {
        _peers = new HashMap<String, Peer>();
    }

    /**
     * Retourne le pair demander par son hash (obtenu par Peer.getHash())
     * Test si la connexion est toujours valide
     * @return Peer ou null si le pair demandé n'est pas joignable
     */
    public Peer getByHash(String hash)
    {
        Peer peer = _peers.get(hash);
        if (peer.isConnected()) {
            return peer;
        }
        else {
            if (this.canConnectPeer() && peer.connect()) {
                return peer;
            }
            else {
                return null;
            }
        }
    }

    /**
     * Ajoute, créer un pair au manager et
     * tente d'établir une connexion si le pair
     * n'est pas présent
     * @return String contenant le hash du Peer
     * si succès, renvoi null sinon
     */
    public String add(String ip, int port)
    {
        String hash = Peer.computeHash(ip, port);
        if (_peers.get(hash) == null) {
            Peer peer = new Peer(ip, port);
            _peers.put(peer.getHash(), peer);
        }

        return hash;
    }

    /**
     * Retourne tout les pairs connus
     */
    public Peer[] getAllPeers()
    {
        Peer[] peers = new Peer[_peers.size()];
        int i = 0;

        Set keys = _peers.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()){
            String hash = (String)it.next();
            peers[i] = _peers.get(hash);
            i++;
        }

        return peers;
    }

    /**
     * Retourne le nombre connexion actuellement ouverte
     * vers d'autres pairs en mode client
     */
    public int nbConnectedPeers()
    {
        Peer[] peers = this.getAllPeers();
        int count = 0;

        for (int i=0;i<peers.length;i++) {
            if (peers[i].isConnected()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Renvoi true si le nombre maximum de pairs
     * connecté n'est pas atteint, false sinon
     */
    private boolean canConnectPeer()
    {
        int max = (Integer)App.config.get("maxPeerConnections");
        int nb = this.nbConnectedPeers();

        if (nb < max) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Supprime un pair du manager par son hash
     */
    public void del(String hash)
    {
        _peers.remove(hash);
    }
}

