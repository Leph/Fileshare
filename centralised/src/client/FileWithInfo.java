/**
 * Cette classe contient les information necessaires d'un fichier
 */

import java.io.File;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileWithInfo extends File {
	private static final long serialVersionUID = 1L;
	
	private static final char md5Chars[] = {'0','1','2','3','4','5','6','7','8',
		'9','a','b','c','d','e','f'};
	
	String key;
	boolean[] buffermap;
	int rest; // Nb de pieces manquants.
	
	/**
	 * Creation d'un fichier existant
	 * 
	 * @param pathname			nom de path
	 * @throws NoSuchAlgorithmException		si l'algo md5 n'est pas supporte par la machine 
	 */
	public FileWithInfo(String pathname) throws NoSuchAlgorithmException {
		super(pathname);
		this.key = getKey(this.getName());
		initBuffer(length(),true);
	}
	
	public FileWithInfo(String parent, String child) throws NoSuchAlgorithmException{
		super(parent, child);
		this.key = getKey(this.getName()); 
		initBuffer(length(),true);
	}
	
	public FileWithInfo(File parent, String child) throws NoSuchAlgorithmException{
		super(parent, child);
		this.key = getKey(this.getName());
		initBuffer(length(),true);
	}
	
	public FileWithInfo(URI uri) throws NoSuchAlgorithmException{
		super(uri);
		this.key = getKey(this.getName());
		initBuffer(length(),true);
	}
	
	/**
	 * Creation d'un fichier a telecharger 
	 * 
	 * @param pathname		nom de path
	 * @param key			clef md5
	 * @param fileSize		taille de fichier
	 */
	public FileWithInfo(String pathname, String key, long fileSize){
		super(pathname);
		this.key = key;
		initBuffer(fileSize,false);
	}
	
	/**
	 * Genener une clef pour un nom de fichier en entree en utilisant l'algorithme md5 
	 * 
	 * @param filename		le nom d'un fichier
	 * @return  			une chaine de bits qui est la clef generee
	 * @throws NoSuchAlgorithmException 	si l'algo md5 n'est pas supporte par la machine 
	 */
	public static String getKey(String filename) throws NoSuchAlgorithmException{
		MessageDigest m=MessageDigest.getInstance("MD5");
	    m.update(filename.getBytes(),0,filename.getBytes().length);
	    byte[] bytes = m.digest();
	    StringBuffer buffer = new StringBuffer("");
	    for (int i = 0 ; i < bytes.length ; i++){
	    	buffer.append(md5Chars[(bytes[i] & 0xf0)>>4]);
	    	buffer.append(md5Chars[bytes[i] & 0xf]);
	    }
	    return buffer.toString();
	}
	
	/**
	 * Initialisation de buffermap
	 * 
	 * @param fileSize		Taille de fichier
	 * @param value			la valeur initiale de chaque case de tableau
	 */
	private final void initBuffer(long fileSize, boolean value){
		long pieceSize =((Long)Config.config().get("PieceSize")).longValue();
		buffermap = new boolean[(int) (( fileSize + pieceSize -1) / pieceSize)];
		for (int i = 0 ; i < buffermap.length ; i++)
			buffermap[i] = value;
		
		if (value)
			rest = 0;
		else
			rest = buffermap.length;
	}
	
	@Override
	public String toString(){
		return getName() + " " + length() + " " + (Long)Config.config().get("PieceSize") + 
			" " + new String(key);	
	}
}
