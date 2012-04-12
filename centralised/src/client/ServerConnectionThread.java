/**
 * Représente le thread d'une connexion serveur 
 * du pair
 */

class ServerConnectionThread extends Thread
{
    /**
     * Socket de communication
     * implémentant le protocol
     */
    Protocol _socket;

    /**
     * Créer le thread gérant une connexion serveur
     */
    ServerConnectionThread(Protocol socket)
    {
        super();
        _socket = socket;
    }

    /**
     * Fonction principale du thread
     *
     * Traite les messages entrants
     */
    public void run()
    {
        try {
            while (true) {
                _socket.serverReadAndDispatch();
            }
        }
        catch (Exception e)
        {
            /*TODO fermer la socket */
            System.out.println("End server connection :");
            e.printStackTrace();
        }
    }
}
