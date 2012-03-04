/**
 * Classe principale
 */

package client;

public class Main
{
    public static void main(String args[])
    {
        Config.config().load("config");
        
        Config.config().print();
        testFileManager();
    }
    
    //test methode
    private static void testFileManager(){
    	String newFileName = "test_photo.png";
    	String oldFileName = "icon.png";
    	
    	System.out.println("=====================FileManager=====================");
    	FileManager fm = new FileManager();
    	System.out.println("file info: " + fm);
    	System.out.println("seed: " + fm.listSeedKey());
    	System.out.println("leech: " + fm.listLeechKey());
    	System.out.println("*****************************************************");
    	try {
    		FileWithInfo nf = fm.newInterestedFile(newFileName, 
    				"qwertyuiopasdfhjk1234567890", fm.findFile(oldFileName).length());
    		System.out.println("seed: " + fm.listSeedKey());
        	System.out.println("leech: " + fm.listLeechKey());
    		for (int i = 0; i < nf.buffermap.length; i++)
    			fm.savePiece(newFileName, i, fm.getData(oldFileName, i));
    		System.out.println("*****************************************************");
    		System.out.println("seed: " + fm.listSeedKey());
        	System.out.println("leech: " + fm.listLeechKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}

