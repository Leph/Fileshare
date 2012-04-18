/**
 * Représente un compteur de débit
 */

import java.io.*;

class RateCounter
{
    /**
     * date de la dernière réinitialisation
     */
    private long _timestart;

    /**
     * Compteur de débit
     */
    private long _counter;

    /**
     * Valeur du débit
     */
    private float _rate;

    /**
     * Créé et initialide le compteur
     */
    public RateCounter()
    {
        _timestart = System.currentTimeMillis();
        _counter = 0;
        _rate = 0;
    }

    /**
     * Founit au compteur le débit échangé
     * @param : la quantité de donnée échangée
     */
    synchronized public void tick(int datasize)
    {
        long current = System.currentTimeMillis();
        int mean = (Integer)App.config.get("rateMeanTime");

        if (current - _timestart > mean) {
            if (current - _timestart > 2*mean) {
                _rate = 0;
            }
            else {
                _rate = (float)Math.floor(100*(float)_counter/(float)mean)/100;
            }
            _timestart = current;
            _counter = 0;
        }

        _counter += datasize;
    }

    /**
     * Renvoi la valeur actuelle du débit
     * en Ko/s
     */
    synchronized public float getRate()
    {
        long current = System.currentTimeMillis();
        int mean = (Integer)App.config.get("rateMeanTime");

        if (current - _timestart > 2*mean) {
            return 0;
        }
        else {
            return _rate;
        }
    }
}

