/**
 * Représente le thread de mise à jour
 * régulière du tracker et des pairs
 */

import java.io.*;
import java.util.*;
import java.lang.*;

class UpdateThread extends Thread
{
    /**
     * Contruit et initialise le thread
     */
    public UpdateThread() {
        super();
    }

    /**
     * Fonction principale du thread
     * Mise à jour régulière du tracker
     */
    public void run()
    {
        do {
            try {
                int delay = (Integer)App.config.get("updateTime");
                Thread.sleep(delay);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                App.downloads.tracker.update();
            } 
            catch (Exception e)
            {
                System.out.println("Unable to update tracker");
                e.printStackTrace();
            }
        } while (true);
    }
}

