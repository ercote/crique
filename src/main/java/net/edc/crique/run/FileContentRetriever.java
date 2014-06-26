package net.edc.crique.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.edc.crique.DepotUtils;
import net.edc.crique.run.DepotFichiersRunnable;

public class FileContentRetriever extends DepotFichiersRunnable {
  File file;
  byte[] bytes;
  public FileContentRetriever(File file) {
    this.file = file;
    this.setName(FileContentRetriever.class.getName());
  }
  public byte[] getBytes() {
    return bytes;
  }
  public void run() {
    try {
      bytes = DepotUtils.getFileContent(file);
    } catch(FileNotFoundException fnfe){
      setFileNotFoundException(fnfe);
    } catch(IOException ioe) {
      setIOException(ioe);
    }
  }
}
