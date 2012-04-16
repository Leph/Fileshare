/**
 * Command Line Interface
 * Représente l'interface utilisateur fonctionnant
 * au sein d'un thread
 */

import java.io.*;
import java.util.*;
import java.lang.*;
import java.io.IOException;

class CLI extends Thread
{
    /**
     * Créé une nouvelle interface
     */
    private CLI()
    {
        super();
    }

    /**
     * Lis une ligne et la retourne
     */
    private String readInput()
    {
        try {
            InputStreamReader reader_tmp = new InputStreamReader(System.in); 
            BufferedReader reader = new BufferedReader(reader_tmp);
            return reader.readLine();
        }
        catch (IOException e) {
            System.out.println("Unable to read user input");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Affiche l'aide
     */
    private void help()
    {
        System.out.println("Available commands :");
        System.out.println("files              Show all known files");
        System.out.println("help               Display this message");
        System.out.println("search filename    Search for file filename");
        System.out.println("exit               Quit");
    }

    /**
     * Affiche tous les fichiers
     */
    private void files()
    {
        FileShared[] files = App.files.getAllFiles();
        if (files.length > 0) {
            System.out.println("Files ("+files.length+") :");
        }
        else {
            System.out.println("No file");
        }

        for (int i=0;i<files.length;i++) {
            System.out.println(
                "["+i+"] "+
                files[i].getName()+" "+
                files[i].getSize()+" "+
                files[i].getKey()+" "+
                ((files[i].isComplete()) ? "[complete]" : "[tmp]")
            );
        }
    }

    /**
     * Recherche le fichier donné
     */
    private void search(String filename)
    {
        String[] data = App.downloads.search(filename);
        
        if (data.length == 0) {
            System.out.println("No file found");
            return;
        }

        for (int i=0;i<data.length;i+=4) {
            String name = data[i];
            int size = Integer.parseInt(data[i+1]);
            int piecesize = Integer.parseInt(data[i+2]);
            String key = data[i+3];
            System.out.println(
                "["+((i/4)+1)+"] "+
                name+" "+
                size+" "+
                key+" "+
                piecesize
            );
        }
        System.out.println("[0] RETURN");

        System.out.println("Download file ?");

        while (true) {
            System.out.print("#? ");
            String command = this.readInput();
            try {
                int choice = Integer.parseInt(command);
                if (choice == 0) return;
                else if (choice > 0 && choice <= data.length/4) {
                    int i = (choice-1)*4;
                    String name = data[i];
                    int size = Integer.parseInt(data[i+1]);
                    int piecesize = Integer.parseInt(data[i+2]);
                    String key = data[i+3];
                    App.downloads.startDownload(name, key, size, piecesize);
                    return;
                }
            }
            catch (NumberFormatException e) {
            }
            System.out.println("Invalid input : " + command);
        }
    }

    /**
     * Fonction principale du thread
     */
    public void run()
    {
        System.out.println("Starting CLI");

        while (true) {
            System.out.print("> ");
            String command = this.readInput();
            String[] args = command.split(" ");

            int argc = args.length;
            if (argc == 0 || command.length() == 0) {
                continue;
            }

            if (argc == 1 && args[0].equals("files")) this.files();
            else if (argc == 1 && args[0].equals("help")) this.help();
            else if (argc == 2 && args[0].equals("search")) this.search(args[1]);
            else if (argc == 1 && args[0].equals("exit")) break;
            else {
                System.out.println("Unknown command : " + command);
                System.out.println("Try help");
            }
        }

        System.out.println("Exiting");
    }

    /**
     * Initialise l'interface utilisateur
     */
    static public void init()
    {
        CLI cli = new CLI();
        cli.start();
    }
}
