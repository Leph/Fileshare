/**
 * Contient la configuration actuelle du programme
 * Gère le fichier de configuration
 */

import java.util.*;
import java.io.*;

public class Config
{
    /**
     * Liste des paramètres
     */
    private Map<String, Object> _config;

    /**
     * Constructeur : initialisation
     * des valeurs par défaut
     */
    public Config()
    {
        _config = new HashMap<String, Object>();
    }

    /**
     * Renvoi la valeur du paramètre spécifié
     */
    public Object get(String param)
    {
        return _config.get(param);
    }

    /**
     * Créer ou modifie un paramètre de configuration
     */
    public void set(String param, Object value)
    {
        _config.put(param, value);
    }

    /**
     * Charge les paramètres depuis le fichier
     * de configuration
     */
    public void load(String filename)
    {
        try {
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                if (strLine.length() == 0 || 
                        strLine.charAt(0) == '#' || 
                        strLine.charAt(0) == ' ') 
                {
                    continue;
                }
                String left = "";
                String right = "";
                int i = 0;
                while (i < strLine.length()) {
                    if (strLine.charAt(i) == ' ' || strLine.charAt(i) == '=') {
                        break;
                    }
                    left += strLine.charAt(i);
                    i++;
                }
                while (i < strLine.length()) {
                    if (strLine.charAt(i) != ' ' && strLine.charAt(i) != '=') {
                        break;
                    }
                    i++;
                }
                while (i < strLine.length()) {
                    if (strLine.charAt(i) == ' ') {
                        break;
                    }
                    right += strLine.charAt(i);
                    i++;
                }
                try {
                    long valLong = Long.parseLong(right);
                    set(left, valLong);
                }
                catch (Exception e) {
                    try {
                        double valDouble = Double.parseDouble(right);
                        set(left, valDouble);
                    }
                    catch (Exception ee) {
                        set(left, right);
                    }
                }
            }
            in.close();
        }
        catch (Exception e) {
            System.err.println("Config file error : " + e.getMessage());
        }
    }

    /**
     * Affiche toutes les configurations
     */
    public void print()
    {
        for (String mapKey : _config.keySet()) {
            System.out.println(mapKey+" = "+_config.get(mapKey));
        }
    }
}

