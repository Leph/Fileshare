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
    static final private Pattern _announce = Pattern.compile("ok");
    
    /**
     * Look protocol
     */
    static final private Pattern _look_begin = Pattern.compile("list \\[");
    static final private Pattern _look_repeat = Pattern.compile("([^\\s]+) (\\d+) (\\d+) (\\w+)[ ]?");
    static final private Pattern _look_end = Pattern.compile("\\]");

    /**
     * Getfile protocole
     */
    static final private Pattern _getfile_begin = Pattern.compile("peers (\\w+) \\[");
    static final private Pattern _getfile_repeat = Pattern.compile("(\\d{1,3}+\\.\\d{1,3}+\\.\\d{1,3}+\\.\\d{1,3}+):(\\d+)[ ]?");
    static final private Pattern _getfile_end = Pattern.compile("\\]");

    /**
     * Interested protocol
     */
    static final private Pattern _interested = Pattern.compile("have (\\w+) ");

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

        InputStream reader_tmp = this.getInputStream();
        RandomInputStream reader = new RandomInputStream(reader_tmp);

        readBytesToPattern(reader, 0, _announce, null, null);
    }

    /**
     * Implémente le message de recherche de fichier
     * vers le tracker
     * @param filename : nom du fichier a rechercher
     * @return String[]
     * [0] : filename
     * [1] : size
     * [2] : piecesize
     * [3] : key
     * ...
     */
    public String[] look(String filename) throws IOException
    {
        String query = "look [";
        query += "filename=\"" + filename + "\"";
        query += "]";
        this.writeBytes(query.getBytes());
        
        InputStream reader_tmp = this.getInputStream();
        RandomInputStream reader = new RandomInputStream(reader_tmp);

        ArrayList<String> groups = new ArrayList<String>();
    
        int offset = readBytesToPattern(reader, 0, _look_begin, null, groups);
        do {
            int tmp = readBytesToPattern(reader, offset, _look_repeat, _look_end, groups);
            if (tmp == -1) {
                break;
            }
            offset += tmp;
        } while (true);
        
        return groups.toArray(new String[0]);
    }

    /**
     * Implémente le message de récupération des pairs
     * possédant un fichier auprès du tracker
     * @param key : la clef du fichier
     * @return String[]
     * [0] : ip
     * [1] : port
     * ...
     */
    public String[] getFile(String key) throws IOException
    {
        String query = "getfile " + key;
        this.writeBytes(query.getBytes());
        
        InputStream reader_tmp = this.getInputStream();
        RandomInputStream reader = new RandomInputStream(reader_tmp);
        
        ArrayList<String> groups = new ArrayList<String>();
    
        int offset = readBytesToPattern(reader, 0, _getfile_begin, null, groups);
        if (!groups.get(0).equals(key)) {
            throw new IOException("Protocol error");
        }
        groups.remove(0);
        do {
            int tmp = readBytesToPattern(reader, offset, _getfile_repeat, _getfile_end, groups);
            if (tmp == -1) {
                break;
            }
            offset += tmp;
        } while (true);
        
        return groups.toArray(new String[0]);
    }

    /**
     * Implémente le message de d'interogation d'un pair
     * pour connaitre son buffermap d'un fichier
     * @param key : la clef du fichier
     */
    public void interested(String key) throws IOException
    {
        String query = "interested " + key;
        this.writeBytes(query.getBytes());
        
        InputStream reader_tmp = this.getInputStream();
        RandomInputStream reader = new RandomInputStream(reader_tmp);
        
        ArrayList<String> groups = new ArrayList<String>();
    
        int offset = readBytesToPattern(reader, 0, _interested, null, groups);
        if (!groups.get(0).equals(key)) {
            throw new IOException("Protocol error");
        }

        FileShared file = App.files.getByKey(key);
        int buffermap_size = file.buffermapSize();
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
    private int readBytesToPattern(RandomInputStream reader, int offset, Pattern pattern, Pattern reject, ArrayList<String> groups) 
        throws IOException
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

