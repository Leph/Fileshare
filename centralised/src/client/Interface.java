import java.io.InputStreamReader;
import java.io.BufferedReader;

class Interface{
	
    public Interface(){
    	AutoUpdateThread autoUp = new AutoUpdateThread();
		// listen thread
		DownloadManager dm = new DownloadManager();
		dm.initServer();
		dm.initDownloads();
		
		
		System.out.println("Connection OK.....");
		System.out.println("Welcome");
		autoUp.start();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try{
			while (true){
				System.out.println("Search a file? (y/n)");
				line = reader.readLine();
				if (line.compareTo("n") == 0)
					break;
				else if (line.compareTo("y") == 0){
					System.out.println("Search a file with its name:");
					line = reader.readLine();
					if (line != null ){
						String[] data = dm.search(line);
						// afficher le resultat de recherche
						printInfo(data);
						// laisser client faire son choix
						System.out.println("Please choose a number of which the " +
								"file will start to be download. (0 for return)");
						int choice;
						while (true){
							line = reader.readLine();
							try{
								choice = Integer.parseInt(line);
								if (choice >= 0 && choice <= data.length/4)
									break;
							}catch(NumberFormatException ex){
								ex.printStackTrace();
							}finally{
								System.out.println("Please entre a correct number");
							}
						}
						
						// construire le data de fichier a telecharger 
						String[] dl = new String[4];
						for (int i = 0 ; i < 4 ; i++){
							dl[i] = data[(choice-1)*4+i];
						}
						// commencer telechargement
						dm.download(dl);
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		  
    }
    
    private void printInfo(String[] data){
    	for (int i = 0 ; i < data.length; i+=4){
    		String name = data[i];
            int size = Integer.parseInt(data[i+1]);
            int piecesize = Integer.parseInt(data[i+2]);
            String key = data[i+3];
            
            System.out.print(i/4 + 1 );
            System.out.println(" name: "+name+" size: "+size
            		+" piecesize: "+piecesize+" key: "+key);
    	}
    }
}
