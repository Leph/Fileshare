/**
 * Objet principal contenant le main
 * Conteneur des autres classe globales
 */

class App
{
    /**
     * Configuration de l'application
     */
    static public Config config = new Config();

    /**
     * FileManager
     */
    static public FileManager files = new FileManager();

    /**
     * PeerManager
     */
    static public PeerManager peers = new PeerManager();

    /**
     * DownloadManager
     */
    static public DownloadManager downloads = new DownloadManager();

    /**
     * Fonction main
     */
    public static void main(String args[])
    {
	try{        
		App.config.load("config");
	       	App.files.init();
	        App.downloads.initServer();
	        App.downloads.initDownloads();

	        //testConfig();
	        //testFileManager();
	        //testBuffermap();
	        //testFileShared();

		new Interface();
	}catch(Exception ex){
		ex.printStackTrace();
	}
    }

    /**
     * Test de Config
     */
    public static void testConfig()
    {
        System.out.println("**** TEST Config");
        App.config.set("test", "test");
        assert(App.config.get("test") == "test");
        App.config.print();
    }

    /**
     * Test de FileManager
     */
    public static void testFileManager()
    {
        System.out.println("**** TEST FileManager");
        App.files.print();
    }

    /**
     * Test de Buffermap
     */
    public static void testBuffermap()
    {
        System.out.println("**** TEST Buffermap");

        Buffermap b1 = new Buffermap(8, false);
        assert b1.getNbPieces() == 8;
        assert b1.getBit(0) == false;
        assert b1.getBit(7) == false;
        assert b1.getBit(4) == false;
        b1.setBit(4, true);
        assert b1.getBit(4) == true;

        byte [] bb = new byte[1];
        bb[0] = 0x02;
        Buffermap b2 = new Buffermap(bb, 8);
        assert b2.getNbPieces() == 8;
        assert b2.getBit(0) == false;
        assert b2.getBit(2) == false;
        assert b2.getBit(1) == true;
        b2.setBit(1, false);
        assert b2.getBit(1) == false;
    }

    /**
     * Test de FileShared
     */
    public static void testFileShared()
    {
        System.out.println("**** TEST FileShared");

        FileShared f = new FileShared("test", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 10, 3);
        assert f.getName().equals("test"+App.config.get("tmpExtension"));
        assert f.getKey().equals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        assert f.getSize() == 10;
        assert f.getPieceSize() == 3;
        assert f.isComplete() == false;
        assert f.nbPieces() == 4;

        assert f.hasPiece(2) == false;

        byte[] piece1 = new byte[3];
        piece1[0] = 0;
        piece1[1] = 1;
        piece1[2] = 2;
        byte[] piece2 = new byte[3];
        piece2[0] = 3;
        piece2[1] = 4;
        piece2[2] = 5;
        f.writePiece(piece1, 2);
        f.writePiece(piece2, 0);
        
        assert f.hasPiece(2) == true;

        byte[] piece3 = f.readPiece(2);
        assert piece3[0] == 0;
        assert piece3[1] == 1;
        assert piece3[2] == 2;
        byte[] piece4 = f.readPiece(0);
        assert piece4[0] == 3;
        assert piece4[1] == 4;
        assert piece4[2] == 5;

        FileShared f2 = new FileShared("test.tmp");
        assert f2.getName().equals("test"+App.config.get("tmpExtension"));
        assert f2.getKey().equals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        assert f2.getSize() == 10;
        assert f2.getPieceSize() == 3;
        assert f2.isComplete() == false;
        assert f2.nbPieces() == 4;

        assert f2.hasPiece(2) == true;

        byte[] piece5 = f2.readPiece(2);
        assert piece5[0] == 0;
        assert piece5[1] == 1;
        assert piece5[2] == 2;
        
        byte[] piece7 = new byte[1];
        piece7[0] = 9;
        f2.writePiece(piece7, 3);
        byte[] piece6 = new byte[3];
        piece6[0] = 6;
        piece6[1] = 7;
        piece6[2] = 8;
        f2.writePiece(piece6, 1);

        byte[] piece8 = f2.readPiece(3);
        assert piece8.length == 1;
        assert piece8[0] == 9;

        FileShared f3 = f2.tmpToComplete();
        assert f3.getName().equals("test");
        assert f3.getSize() == 10;
        assert f3.getPieceSize() == (Integer)App.config.get("pieceSize");
        assert f3.isComplete() == true;
        assert f3.nbPieces() == 1;

        byte[] piece9 = f3.readPiece(0);
        assert piece9.length == 10;
        assert piece9[0] == 3;
        assert piece9[9] == 9;

	f3.delete();
    }

}
