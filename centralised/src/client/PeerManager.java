/**
 * Gère les connexions clientes vers
 * les autres pairs
 */

import java.util.*;

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
        if (peer.socket.isConnected()) {
            return _peers.get(hash);
        }
        else {
            _peers.remove(peer.getHash());
            return null;
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
        try {
            String hash = Peer.computeHash(ip, port);
            if (_peers.get(hash) == null) {
                Peer peer = new Peer(ip, port);
                _peers.put(peer.getHash(), peer);
            }
            return hash;
        }
        catch (Exception e) {
            System.out.println("Unable to contact peer : " + ip);
            e.printStackTrace();
            return null;
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

