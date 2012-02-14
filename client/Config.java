/**
 * Contient la configuration actuelle du programme
 * Gère le fichier de configuration
 * Pattern Singleton
 */

import java.util.*;

public class Config
{
    /**
     * Liste des paramètres
     */
    private Map<String, Object> _config;

    /**
     * Instance du singleton
     */
    private static Config _instance;

    /**
     * Constructeur : initialisation
     * des valeurs par défaut
     */
    private Config()
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

    public void set(String param, Object value)
    {
        _config.put(param, value);
    }

    /**
     * Renvoi l'instance de configuration
     */
    public static Config config()
    {
        if (_instance == null) {
            _instance = new Config();
        }
        return _instance;
    }
}

