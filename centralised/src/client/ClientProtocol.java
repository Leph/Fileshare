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
     * Look protocol
     */
    static Pattern _look = Pattern.compile("list \\[([^\\s]+) (\\d+) (\\d+) (\\w+)\\]");

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
        
        this.writeBytes(query.getBytes());

        /**/
        InputStream reader_tmp = this.getInputStream();
        BufferedInputStream reader = new BufferedInputStream(reader_tmp);
        int size = 1024;
        byte[] buffer = new byte[size];
        int offset = 0;
        while (true) {
            int read = reader.read(buffer, offset, size-offset);
            offset += read;
            Matcher matcher = _announce.matcher(new String(buffer, 0, offset)); 
            if (matcher.lookingAt()) {
                return;
            }
            if (read == size-offset) {
                size *= 2;
                buffer = Arrays.copyOf(buffer, size);
            }
        }
        /**/
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
        
        /**/
        InputStream reader_tmp = this.getInputStream();
        BufferedInputStream reader = new BufferedInputStream(reader_tmp);

        String[] groups = new String[0];
        int offset = readBytesToPattern(reader, 0, _look, groups);
        System.out.println(offset);
        System.out.println(groups.length);
        for (int i=0;i<groups.length;i++) {
            System.out.println(groups[i]);
        }
        /**/
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

    /**
     * Lis la socket selon le pattern spécifié
     */
    private int readBytesToPattern(BufferedInputStream reader, int offset, Pattern pattern, String[] groups) throws IOException
    {
        int size = 1024;
        byte[] buffer = new byte[size];
        int len = 0;
        while (true) {
            int read = reader.read(buffer, offset, size-offset);
            len += read;
            offset += read;
            Matcher matcher = pattern.matcher(new String(buffer, 0, len)); 
            if (matcher.lookingAt()) {
                int count = matcher.groupCount();
                groups = new String[count];
                for (int i=0;i<count;i++) {
                    groups[i] = matcher.group(i+1);
                }
                System.out.println(groups.length);
                return matcher.end();
            }
            if (read == size-offset) {
                size *= 2;
                buffer = Arrays.copyOf(buffer, size);
            }
        }
    }
}

