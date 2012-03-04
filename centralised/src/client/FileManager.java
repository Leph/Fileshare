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
	 * list de fichiers locaux + virtuels (en train de t��l��charger)
	 */
	ArrayList<FileWithInfo> fileList;
	// repertoire contenant les fichiers locaux
	private String fileDir;
	// repertoire contenant les pi��ces de fichiers qui sont en train de t��l��charger 
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
	 * Cr��e une instance de fichier virtuel  (fichier �� t��l��charger)
	 * Cette m��thode doit ��tre appel��e avant de faire getData ou savePiece sur ce fichier
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
	 * Trouve l'instance de fichier avec le nom sp��cifi�� 
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
	 * R��cup��re un morceau de donn��es dans un fichier
	 * 
	 * @param filename		un fichier existant
	 * @param index			index de buffermap correspondant
	 * @return des donn��es de longueur au plus $PieceSize$ dans le fichier filename commen�0�4ant par la position (index*$PieceSize$)
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
			// si f est un fichier dans le disque, lire dans ce fichier
			FileInputStream fis = new FileInputStream(f);
			fis.skip(index * pieceSize);
			fis.read(buffer);
			fis.close();
			return buffer;
		}else{
			// sinon, f est sous forme pi��ces, r��cup��rer directement "tmp/filename.$index"
			FileInputStream fis = new FileInputStream(tmpDir+filename+".$"+index);
			fis.read(buffer);
			return buffer;
		}
	}
	
	/**
	 * Enregistre une pi��ce d'un fichier virtuel (en train de t��l��charger)
	 * 
	 * @param filename		nom de fichier virtuel
	 * @param index			index de buffermap correspondant 
	 * @param data			des donn��es �� stocker 
	 * @return 				faux si la pi��ce de ce fichier existe d��j�� (��viter les donn��es dupliqu��es) 
	 * @throws IOException	normalement, �0�4a ne doit pas se passer 
	 */
	public boolean savePiece(String filename, int index, byte[] data) throws IOException{
		// Enregister les donn��es dans un nouveau fichier "tmp/filename.$index"
		// Si cela existe d��j�� ,retourne false
		File tmpFile = new File(tmpDir+filename+".$"+index);
		if (tmpFile.exists())
			return false;
		else{
			tmpFile.createNewFile();
			// debug
			System.out.println("file: " + tmpFile + " created");
			//��crit donn��es 
			FileOutputStream fout = new FileOutputStream(tmpFile);
			fout.write(data);
			fout.flush();
			fout.close();
			// maj buffermap
			FileWithInfo f = findFile(filename);
			f.buffermap[index] = true;
			f.rest--;
			// tester si le fichier est completement t��l��charg�� 
			if (f.rest == 0)
				fusionFile(f);
			return true;
		}
	
	}
	
	/**
	 * Fusionne des pieces d'un fichier s'il est termin�� de t��l��charger
	 * 
	 * @param file			l'instance de fichier virtuel
	 * @throws IOException	si le fichier virtuel existe d��j�� dans le disque. 
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
	 * Informe une liste de cl��s des fichiers locaux 
	 * 
	 * @return	une cha�0�6ne de caract��re qui contient la liste de cl��s des fichiers locaux 
	 */
	public String listSeedKey(){
		StringBuffer buffer = new StringBuffer("");
		for (int i = 0 ; i < fileList.size() ; i++)
			if (fileList.get(i).rest == 0)
				buffer.append(fileList.get(i).key + " ");
		return buffer.toString();
	}
	
	/**
	 * Informe une liste de cl��s des fichiers en train de t��l��charger  
	 * 
	 * @return	une cha�0�6ne de caract��re qui contient la liste de cl��s des fichiers en train de t��l��charger
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
	 * Servira �� envoyer des informations au tracker au d��but  
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
