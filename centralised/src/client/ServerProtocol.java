/**
 * Représente une connexion en mode serveur
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class ServerProtocol extends ServerSocket
{
    /**
     * Initialise la connexion en mode serveur
     * @param port : port d'écoute
     */
    public ServerProtocol(int port) throws IOException
    {
        super(port);
    }

    
}

