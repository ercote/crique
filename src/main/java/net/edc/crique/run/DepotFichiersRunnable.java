package net.edc.crique.run;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author coteeri
 *
 * Représente une téche permettant d'étre exécutée é
 * l'intérieur d'un DepotFichiersRunner.
 *
 */
public abstract class DepotFichiersRunnable implements Runnable {
  FileNotFoundException 	fnfe;
  IOException 			ioe;

  String name;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setIOException(IOException ioe) {
    this.ioe = ioe;
  }
  public void setFileNotFoundException(FileNotFoundException fnfe) {
    this.fnfe = fnfe;
  }
  public FileNotFoundException getFileNotFoundException() {
    return fnfe;
  }
  public IOException getIOException() {
    return ioe;
  }

  abstract public void run();
}
