
/**
 * Etend ServerSocket pour renvoyer
 * une socket Protocol
 */

import java.io.IOException;
import java.net.ServerSocket;

class ListenServerSocket extends ServerSocket
{
    /**
     * Créer une socket server
     * @param port : port d'écoute
     */
    ListenServerSocket(int port) throws IOException
    {
        super(port);
    }

    /**
     * Attend et retourne une connexion entrante
     * @return Protocol
     */
    public Protocol accept() throws IOException
    {
        Protocol socket = new Protocol();
        this.implAccept(socket);
        socket.setSoTimeout((Integer)App.config.get("socketServerTimeout"));

        return socket;
    }
}

