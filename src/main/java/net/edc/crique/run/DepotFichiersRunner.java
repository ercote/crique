package net.edc.crique.run;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * @author coteeri
 *
 * Permet d'exécuter une téche DepotFichiersRunnable
 * é l'intérieur d'un Thread afin de limiter le temps
 * d'exécution.
 *
 * Malgré que cette classe contienne une méthode run,
 * on n'implémente pas Runnable afin de pouvoir lancer
 * des exceptions provenant du systéme de fichiers.
 *
 */
public class DepotFichiersRunner {

  protected static Logger log = Logger.getLogger(DepotFichiersRunner.class);

  long checkTime;

  /**
   * Constructeur.
   * @param checkTime : temps d'exécution permis en millisecondes.
   *
   */
  public DepotFichiersRunner(long checkTime) {
    this.checkTime = checkTime;
  }

  /**
   * Méthode permettant d'exécuter une téche DepotFichiersRunnable.
   * Si le temps limite de l'exécution est atteint, une exception
   * IOException sera lancée.
   *
   * Il est préférable de spécifier un nom pour la téche afin de mieux
   * cibler le probléme.
   *
   * @param runnable
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void run(DepotFichiersRunnable runnable) throws FileNotFoundException, IOException {
    Thread t = new Thread(runnable);
    t.start();
    try {
      t.join(checkTime);
    } catch (InterruptedException ie) {
        log.error(runnable.getName() != null ?
            DepotFichiersRunner.class.getName() + " - Téche [" + runnable.getName() + "]" :
              DepotFichiersRunner.class.getName(), ie);
    }
    if (t.isAlive()) {
      t.interrupt();
      throw new IOException(DepotFichiersRunnable.class.getName() +
          " : La téche " + (runnable.getName() != null ? "[" + runnable.getName() + "] " : "") +
          "n'a pas été complétée dans le temps alloué [" + checkTime + " millisecondes].");
    }

    if (runnable.getFileNotFoundException() != null)
      throw runnable.getFileNotFoundException();
    else if (runnable.getIOException() != null)
      throw runnable.getIOException();
  }
}
