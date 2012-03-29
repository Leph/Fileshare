/**
 * Représente une connexion en mode client
 * client -- requète --> serveur
 */

import java.io.*;
import java.net.*;

class ClientSocket extends Socket
{
    /**
     * Initialise la connexion avec
     * IP, port
     */
    public ClientSocket(String ip, int port) throws UnknownHostException, IOException
    {
        super(ip, port);

        try {
            this.setSoTimeout((Integer)App.config.get("socketTimeout"));
        }
        catch (SocketException e) {
            System.out.println("Unable to set timeout");
            e.printStackTrace();
        }
    }

    /**
     * Implémente le message d'annonce au tracker
     */
    public void announce() throws IOException
    {
        FileShared[] completefiles = App.files.getCompleteFiles();
        FileShared[] tmpfiles = App.files.getTmpFiles();

        String query = "announce ";
        query += "listen " + (Integer)App.config.get("listenPort") + " ";
        query += "seed [";
        for (int i=0;i<completefiles.length;i++) {
            query += completefiles[i].getName() + " ";
            query += completefiles[i].getSize() + " ";
            query += completefiles[i].getPieceSize() + " ";
            query += completefiles[i].getKey();
            if (i != completefiles.length-1) {
                query += " ";
            }
        }
        query += "] leech [";
        for (int i=0;i<tmpfiles.length;i++) {
            query += completefiles[i].getKey();
            if (i != tmpfiles.length-1) {
                query += " ";
            }
        }
        query += "]";

        InputStream reader_tmp = this.getInputStream();
        BufferedInputStream reader = new BufferedInputStream(reader_tmp);

        this.writeBytes(query.getBytes());

        int data1 = reader.read();
        if ((byte)data1 != (byte)'o') {
            throw new IOException("Protocol error");
        }
        int data2 = reader.read();
        if ((byte)data2 != (byte)'k') {
            throw new IOException("Protocol error");
        }
    }

    /**
     * Implémente le message de recherche de fichier
     * vers le tracker
     * @param filename : nom du fichier a rechercher
     */
    public void look(String filename) throws IOException
    {
        String query = "look [";
        query += "filename=\"" + filename + "\"";
        query += "]";

        this.writeBytes(query.getBytes());
    }

    /**
     * Ecrit le buffer spécifié dasn la socket
     */
    private void writeBytes(byte[] buffer) throws IOException
    {
        OutputStream writer_tmp = this.getOutputStream();
        BufferedOutputStream writer = new BufferedOutputStream(writer_tmp);
        writer.write(buffer, 0, buffer.length);
        writer.flush();
    }
}

