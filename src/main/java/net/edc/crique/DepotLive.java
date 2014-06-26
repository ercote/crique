package net.edc.crique;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.edc.crique.run.DepotFichiersRunner;
import net.edc.crique.run.DepotFichiersRunnable;

public class DepotLive extends Depot {

  Map<File,Long> map;

  public DepotLive(DepotConfig config) throws Exception {
    super(config);
    map = Collections.synchronizedMap(new HashMap<File,Long>());
  }

  public void init() {
    depotListener = new DepotLiveListener();
    monitor.addListener(depotListener);
    logDebug();
    monitor.start(false);
  }

  public byte[] getContenu(String relativePath) {
    return (byte[])getObject(relativePath);
  }

    public byte[] getContenuParCle(String cle) {
        return (byte[])getObjectParCle(cle);
    }

    public byte[] getContenuParCle(String cle, String valeur) {
        return (byte[])getObjectParCle(cle, valeur);
    }

  public Object getObject(String relativePath) {
    File file = getFile(relativePath);
    checkUpdate(file);
    return super.getObject(relativePath);
  }

    public Object getObjectParCle(String cle) {
        return getObjectParCle(cle, null);
    }

    public Object getObjectParCle(String cle, String valeur) {
        File file = getFileParCle(cle, valeur);
        checkUpdate(file);
        return super.getObjectParCle(cle, valeur);
    }

    File getFileParCle(String cle) {
        return getFileParCle(cle, null);
    }

    File getFileParCle(String cle, String valeur) {
        Fichier f = config.getFichierParCle(cle);
        if (f != null) {
            if (valeur != null) {
                return getFile(f.construireChemin(valeur));
            } else {
                return getFile(f.getChemin());
            }
        }
        return null;
    }

  void checkUpdate(File file) {
    Updater updater = new Updater(file);
    DepotFichiersRunner runner = new DepotFichiersRunner(5000);
    try {
      runner.run(updater);
    } catch(IOException ioe) {
        log.error(DepotLive.class.getName(), ioe);
    }
  }

    class DepotLiveListener extends Depot.DepotListener {
        public void fileAdded(File file) {
            fileChanged(file);
        }

        public void fileDeleted(File file) {
            super.fileDeleted(file);
            map.remove(file);
        }

        public void fileChanged(File file) {
            String chemin = getCheminRelatif(file);
            if (mapObjects.get(chemin) != null) {
                super.fileChanged(file);
                map.remove(file);
            }
        }
    }


  class Updater extends DepotFichiersRunnable {
    File file;
    public Updater(File file) {
      this.file = file;
      this.setName(Updater.class.getName());
    }
    public void run() {
      log.debug("Vérification modification au fichier [" + file.getAbsolutePath() + "]");
      long lastModified = file.lastModified();
      Long lastModifiedMonitor = monitor.getLastModified(file);
      if (lastModifiedMonitor == null || lastModified != lastModifiedMonitor) {
        Long lastModifiedIndex = map.get(file);
        if (lastModifiedIndex == null || lastModified != lastModifiedIndex) {
          modif(file);
          if (listener != null) {
            if (lastModifiedMonitor == null && lastModifiedIndex == null) {
                listener.fileAdded(file);
            } else {
                listener.fileChanged(file);
            }
          }
          map.put(file, lastModified);
        }
      } else {
        map.remove(file);
      }
    }
  }
}
