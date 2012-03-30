/**
 * Représente une connexion en mode client
 * client -- requète -- serveur
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class ClientProtocol extends Socket
{
    /**
     * Announce protocol
     */
    static Pattern _announce = Pattern.compile("ok");

    /**
     * Initialise la connexion avec
     * IP, port
     */
    public ClientProtocol(String ip, int port) throws UnknownHostException, IOException
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

        int size = 1024;
        byte[] buffer = new byte[size];
        int offset = 0;

        while (true) {
            int read = reader.read(buffer, offset, size-offset);
            offset += read;
            Matcher matcher = _announce.matcher(new String(buffer, 0, offset));
            System.out.println("read: "+read);
            System.out.println("offset: "+offset);
            System.out.println("size: "+size);
            System.out.println(new String(buffer, 0, offset));
            if (matcher.lookingAt()) {
                System.out.println("COOL");
                return;
            }
            try {
                int last = matcher.end();
                System.out.println("last: "+last);
                if (last != offset) {
                    throw new IOException("Protocol error");
                }
            }
            catch (IllegalStateException e) {
                e.printStackTrace();
                throw new IOException("Protocol error");
            }
            if (read == size-offset) {
                size *= 2;
                buffer = Arrays.copyOf(buffer, size);
            }
        }

        /*
        int data1 = reader.read();
        if ((byte)data1 != (byte)'o') {
            throw new IOException("Protocol error");
        }
        int data2 = reader.read();
        if ((byte)data2 != (byte)'k') {
            throw new IOException("Protocol error");
        }
        */
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

