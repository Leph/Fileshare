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
     * Fonction main
     */
    public static void main(String args[])
    {
        App.config.load("config");
        App.config.print();

        test();
    }

    /**
     * Fonction de debug/tests
     */
    public static void test()
    {
    }
}
