/**
 * @author csong
 * 
 * Cette classe s'occupe de la gestion de fichiers
 */

package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FileManager {
	
	/**
	 * list de fichiers locaux + virtuels (en train de telecharger)
	 */
	ArrayList<FileWithInfo> fileList;
	// repertoire contenant les fichiers locaux
	private String fileDir;
	// repertoire contenant les pieces de fichiers qui sont en train de telecharger 
	private String tmpDir;
	private long pieceSize;
	private static FileManager instance = null;
	
	public FileManager(){
		fileDir = (String)Config.config().get("fileDir");
		File f = new File(fileDir);
		if (!f.exists() && !f.isDirectory())
			f.mkdir();
			
		tmpDir = (String)Config.config().get("tmpDir");
		f = new File(tmpDir);
		if (!f.exists())
			f.mkdir();
		pieceSize = ((Long)Config.config().get("PieceSize")).longValue();
		String[] fileNameList = new File(fileDir).list();
		fileList = new ArrayList<FileWithInfo>(fileNameList.length);
		try {
			for (int i = 0 ; i < fileNameList.length ; i++)
				fileList.add(new FileWithInfo(fileDir + fileNameList[i]));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		instance = this;
	}
	
	public static FileManager filemanager(){
		return instance;
	}
	
	/**
	 * Cree une instance de fichier virtuel  (fichier a charger)
	 * Cette methode doit etre appelee avant de faire getData ou savePiece sur ce fichier
	 * 
	 * @param filename
	 * @param key
	 * @param fileSize
	 * @return
	 */
	public FileWithInfo newInterestedFile(String filename, String key, long fileSize){
		FileWithInfo f = new FileWithInfo(fileDir+filename, key, fileSize);
		fileList.add(f);
		return f;
	}
	
	/**
	 * Trouve l'instance de fichier avec le nom specifie 
	 * 
	 * @param filename		nom de fichier 
	 * @return	l'instance de fichier (existant ou virtuel)
	 * @throws FileNotFoundException	s'il n'y pas de fichier de tel nom
	 */
	public FileWithInfo findFile(String filename) throws FileNotFoundException{
		for (FileWithInfo f : fileList)
			if (f.getName().equals(filename))
				return f;
		throw new FileNotFoundException();
	}
	
	public String keyToName(String key) throws KeyNotFoundException{
		for (FileWithInfo f : fileList)
			if (f.key.equals(key))
				return f.getName();
		throw new KeyNotFoundException("Key not exist");
	}
	
	/**
	 * Recupere un morceau de donnees dans un fichier
	 * 
	 * @param filename		un fichier existant
	 * @param index			index de buffermap correspondant
	 * @return des donnees de longueur au plus $PieceSize$ dans le fichier filename commencant par la position (index*$PieceSize$)
	 * @throws FileNotFoundException	s'il n'y a pas de fichier de tel nom
	 * @throws IOException 				si l'utilisateur n'a pas de droit de lire ce fichier
	 */
	public byte[] getData(String filename, int index) throws FileNotFoundException,IOException{
		FileWithInfo f = findFile(filename);
		byte[] buffer;
		long l = f.length() - pieceSize * index;
		if (l < pieceSize)
			buffer = new byte[(int)l];
		else
			buffer = new byte[(int)pieceSize];
		if (f.rest == 0){
			// si f est un fichier dans le disque, lit dans ce fichier
			FileInputStream fis = new FileInputStream(f);
			fis.skip(index * pieceSize);
			fis.read(buffer);
			fis.close();
			return buffer;
		}else{
			// sinon, f est sous forme pieces, recupere directement "tmp/filename.$index"
			FileInputStream fis = new FileInputStream(tmpDir+filename+".$"+index);
			fis.read(buffer);
			return buffer;
		}
	}
	
	/**
	 * Enregistre une piece d'un fichier virtuel (en train de telecharger)
	 * 
	 * @param filename		nom de fichier virtuel
	 * @param index			index de buffermap correspondant 
	 * @param data			des donnees a stocker 
	 * @return  faux si la piece de ce fichier existe deja (eviter les donnees dupliquees) 
	 * @throws IOException	normalement, cela ne doit pas se passer 
	 */
	public boolean savePiece(String filename, int index, byte[] data) throws IOException{
		// Enregister les donnees dans un nouveau fichier "tmp/filename.$index"
		// Si cela existe deja ,retourne false
		File tmpFile = new File(tmpDir+filename+".$"+index);
		if (tmpFile.exists())
			return false;
		else{
			tmpFile.createNewFile();
			// debug
			System.out.println("file: " + tmpFile + " created");
			// ecrit donnees 
			FileOutputStream fout = new FileOutputStream(tmpFile);
			fout.write(data);
			fout.flush();
			fout.close();
			// maj buffermap
			FileWithInfo f = findFile(filename);
			f.buffermap[index] = true;
			f.rest--;
			// tester si le fichier est completement telecharge 
			if (f.rest == 0)
				fusionFile(f);
			return true;
		}
	
	}
	
	/**
	 * Fusionne des pieces d'un fichier s'il est termine de telecharger
	 * 
	 * @param file			l'instance de fichier virtuel
	 * @throws IOException	si le fichier virtuel existe deja dans le disque. 
	 */
	private void fusionFile(FileWithInfo file) throws IOException{
		file.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		FileInputStream in;
		File filePiece;
		byte[] buffer = new byte[(int)pieceSize];
		for (int i = 0 ; i < file.buffermap.length ; i++){
			filePiece = new File(tmpDir+file.getName()+".$"+i);
			in = new FileInputStream(filePiece);
			int l = in.read(buffer);
			out.write(buffer,0,l);
			in.close();
			filePiece.delete();
			// debug
			System.out.println("file: " + filePiece + " deleted");
		}
		out.close();
		// debug
		System.out.println("file: " + file.getPath() + " created");
	}
	
	/**
	 * Informe une liste de cles des fichiers locaux 
	 * 
	 * @return	une chaine de caractere qui contient la liste de cles des fichiers locaux 
	 */
	public String listSeedKey(){
		StringBuffer buffer = new StringBuffer("");
		for (int i = 0 ; i < fileList.size() ; i++)
			if (fileList.get(i).rest == 0)
				buffer.append(fileList.get(i).key + " ");
		return buffer.toString();
	}
	
	/**
	 * Informe une liste de cles des fichiers en train de telecharger  
	 * 
	 * @return	une chaine de caractere qui contient la liste de cles des fichiers en train de telecharger
	 */
	public String listLeechKey(){
		StringBuffer buffer = new StringBuffer("");
		for (int i = 0 ; i < fileList.size() ; i++)
			if (fileList.get(i).rest > 0)
				buffer.append(fileList.get(i).key + " ");
		return buffer.toString();
	}
	
	/**
	 * Affichier les infomations des fichiers locaux
	 * Servira a envoyer des informations au tracker au debut  
	 */
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer("");
		for (int i = 0 ; i < fileList.size() ; i++)
			if (fileList.get(i).rest == 0)
				buffer.append(fileList.get(i).toString() + " ");
		return buffer.toString();
	}
	
}
