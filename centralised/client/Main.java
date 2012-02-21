/**
 * Classe principale
 */

public class Main
{
    public static void main(String args[])
    {
        Config.config().load("config");
        Config.config().print();
    }
}

