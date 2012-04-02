/**
 * Représente une connexion en mode client
 * client -- requète -- serveur
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.IOException;
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
        RandomInputStream reader = new RandomInputStream(reader_tmp);

        ArrayList<String> groups = new ArrayList<String>();
    
        int offset = 0;
        offset += readBytesToPattern(reader, offset, _look_begin, null, groups);

        int tmp = 0;
        do {
            tmp = readBytesToPattern(reader, offset, _look_repeat, _look_end, groups);
            if (tmp != -1) {
                offset += tmp;
            }
        } while (tmp != -1);
            
        System.out.println(offset);
        System.out.println(groups.size());
        for (int i=0;i<groups.size();i++) {
            System.out.println(groups.get(i));
        }
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
     * Lis la socket selon le pattern spécifié et enregistre les groups trouvés
     * @param reader : le RandomInputStream représentant le flux d'entré
     * @param offset : l'offset de lecture dans le flux d'entré
     * @param pattern : le pattern à matcher
     * @param reject : le pattern d'arrêt de lecture optionnel (peut être null)
     * @param groups : les groups trouvé sont ajouté dans l'ArrayList
     * Retourne l'offset du dernier caractère lus si le pattern est matcher
     * Retourne -1 si le pattern de rejet est matcher
     * Si le pattern n'est pas reconnu, lecture jusqu'à ce que la socket timeout (exception)
     */
    private int readBytesToPattern(RandomInputStream reader, int offset, Pattern pattern, Pattern reject, ArrayList<String> groups) throws IOException
    {
        do {
            byte[] data = reader.read(offset);
            if (data.length > 0) {
                String str = new String(data);
                if (reject != null) {
                    Matcher matcher = reject.matcher(str);
                    if (matcher.lookingAt()) {
                        return -1;
                    }
                }
                Matcher matcher = pattern.matcher(str);
                if (matcher.lookingAt()) {
                    int count = matcher.groupCount();
                    for (int i=0;i<count;i++) {
                        groups.add(matcher.group(i+1));
                    }
                    return matcher.end();
                }
            }
            if (reader.getData()) {
                throw new IOException("Connection closed");
            }
        } while (true);
    }
}

