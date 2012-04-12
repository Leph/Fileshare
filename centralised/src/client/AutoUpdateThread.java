/**
 * @author csong
 *
 *	thread qui envoie les informations de ses fichiers dispos chaque "timeslice" seconde 
 */

public class AutoUpdateThread extends Thread {
	@Override
	  public void run(){
		Protocol s;
		try {
			s = new Protocol((String)App.config.get("trackerIP"),
				(Integer)App.config.get("trackerPort"));
			FileShared[] tmpfiles = App.files.getTmpFiles();
			while (true){	
				wait(((Integer)(App.config.get("timeslice"))).intValue()*1000);
				for (int i=0;i<tmpfiles.length;i++)
					s.have(tmpfiles[i].getKey());
				s.update();
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

}
