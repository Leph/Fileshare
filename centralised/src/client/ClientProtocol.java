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
    static Pattern _look_begin = Pattern.compile("list \\[");
    static Pattern _look_repeat = Pattern.compile("([^\\s]+) (\\d+) (\\d+) (\\w+)[ ]?");
    static Pattern _look_end = Pattern.compile("\\]");

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
        
        InputStream reader_tmp = this.getInputStream();
        BufferedInputStream reader = new BufferedInputStream(reader_tmp);
        reader.mark(512);

        ArrayList<String> groups = new ArrayList<String>();
        int offset = 0;
    
        offset += readBytesToPattern(reader, offset, _look_begin, null, groups);
        System.out.println(offset);
        System.out.println(groups.size());
        for (int i=0;i<groups.size();i++) {
            System.out.println(groups.get(i));
        }

        int tmp = 0;
        do {
            System.out.println(">"+offset);
            tmp = readBytesToPattern(reader, offset, _look_repeat, _look_end, groups);
            if (tmp != -1) {
                offset += tmp;
            }
            System.out.println(offset);
            System.out.println(groups.size());
            for (int i=0;i<groups.size();i++) {
                System.out.println(groups.get(i));
            }
        } while (tmp != -1);
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
    private int readBytesToPattern(BufferedInputStream reader, int offset, Pattern pattern, Pattern reject, ArrayList<String> groups) throws IOException
    {
        reader.reset();
        reader.skip(offset);
        int size = 1024;
        byte[] buffer = new byte[size];
        int len = 0;
        while (true) {
            int read = reader.read(buffer, offset, size-offset);
            len += read;
            offset += read;
            System.out.println(">>>>" + new String(buffer, 0, len));
            if (reject != null) {
                Matcher matcher = reject.matcher(new String(buffer, 0, len));
                if (matcher.lookingAt()) {
                    return -1;
                }
            }
            Matcher matcher = pattern.matcher(new String(buffer, 0, len));
            if (matcher.lookingAt()) {
                int count = matcher.groupCount();
                for (int i=0;i<count;i++) {
                    groups.add(matcher.group(i+1));
                }
                return matcher.end();
            }
            if (read == size-offset) {
                size *= 2;
                buffer = Arrays.copyOf(buffer, size);
            }
        }
    }

    /**
     *
     */
    /*
    private int readBytesToPatternIterate(BufferedInputStream reader, int offset, Pattern pattern, Pattern reject, ArrayList groups) throws IOException
    {
        if (reject != null) {
            throw new IllegalArgumentException();
        }
        Object[] result = new Object[2];
        result[0] = (Object)(new String[0]);
        result[1] = (Object)offset;
        while (true) {
            Object[] r = readBytesToPattern(reader, (Integer)result[1], pattern, reject);
            if (r == null) {
                break;
            }
            String[] tmp = new String[((String[])result[0]).length + ((String[])r[0]).length];
            System.arraycopy((String[])result[0], 0, tmp, 0, ((String[])result[0]).length);
            System.arraycopy((String[])r[0], 0, tmp, ((String[])result[0]).length, ((String[])r[0]).length);
            result[0] = (Object)tmp;
            result[1] = (Object)((Integer)result[1] + (Integer)r[1]);
        }
        
        return result;
    }
    */
}

