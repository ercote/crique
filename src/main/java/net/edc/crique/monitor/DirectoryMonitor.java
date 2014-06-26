package com.lq.monitor;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * @author coteeri
 *
 * Moniteur permettant de surveiller toutes les modifications survenues
 * sur les fichiers situés sous un répertoire donné.
 *
 * Il tient compte de la possibilité que les états des fichiers doivent
 * être synchronisés. Donc, si le moniteur détecte une modification
 * d'un des fichiers du répertoire, il reprendra une autre vérification
 * de son contenu, ainsi de suite jusqu'à ce qu'aucune modification ne soit
 * repérée durant tout un cycle.
 *
 * Implémentation des niveaux de log trace et debug log4j.
 * debug: pour obtenir les notifications de début et de fin des vérifications
 * 		  sur le répertoire racine.
 * trace: pour obtenir les notifications de tous les changements détectés dans
 * 		  le répertoire racine.
 *
 */
public class DirectoryMonitor {

    protected static Logger log = Logger.getLogger(DirectoryMonitor.class);

    Timer timer;
    File src;
    FileFilter filter;
    Map<File,Long> mapFiles;
    List<WeakReference<DirectoryListener>> listeners;
    DirectorySelector selector;
    long pollingInterval;

    /**
     * Construit nouvelle instance du moniteur.
     * La tâche de vérification du répertoire n'est pas démarrée
     * à ce moment-ci.
     *
     * @params
     *  src : le répertoire source à monitorer
     *  pollingInterval : l'interval de l'exécution de la tâche
     *  recursive : true pour monitorer tous les sous-répertoires
     *  filter : permet de filtrer les fichiers à monitorer dans le répertoire.
     */

    public DirectoryMonitor(File src, long pollingInterval, FileFilter filter) {
        this(src, pollingInterval, filter, null);
    }

    public DirectoryMonitor(File src, long pollingInterval, FileFilter filter, DirectorySelector selector) {
        this.src = src;
        this.filter = filter;
        this.selector = selector != null ? selector : defaultSelector();
        this.pollingInterval = pollingInterval;

        /*
         * On doit synchroniser cette liste puisque l'invocation
         * de ses méthodes peut être faite dans le thread local ou
         * dans le TimerTask.
         */
        listeners = Collections.synchronizedList(new ArrayList<WeakReference<DirectoryListener>>());

        mapFiles = new HashMap<File,Long>();
    }

    public void start() {
        start(true);
    }

    public void start(boolean startInContext) {
        if (startInContext) {
            monitor();
        }
        timer = new Timer(true);
        timer.schedule(new DirectoryNotifier(), pollingInterval, pollingInterval);
    }

    /**
     * Ajouter un listener afin d'obtenir les notifications du moniteur
     * pour chacune des modifications apportées aux fichiers monitorés.
     * Le moniteur est démarré à l'ajout du premier listener pour s'assurer
     * qu'un listener a été défini lors de la fabrication initiale de la liste
     * de fichiers.
     *
     * Si des fichiers ont déjà été monitorés, le listener sera notifié
     * de chacun de ces fichiers dans sa méthode fileAdded.
     *
     * @params DirectoryListener
     */
    public void addListener(DirectoryListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(new WeakReference<DirectoryListener>(listener));
            for(File f : mapFiles.keySet()) {
                listener.fileAdded(f);
            }
        }
    }

    /**
     * Retrait d'un listener du moniteur. Si plus aucun listener, la tâche
     * est arrêtée. Elle peut être redémarrée en ajoutant un autre listener.
     *
     * @params DirectoryListener
     */
    public void removeListener(DirectoryListener listener) {
        if (listener != null)
            listeners.remove(listener);
    }

    /**
     * Termine la tâche du moniteur.
     */
    public void stop() {
        timer.cancel();
    }

    public Long getLastModified(File file) {
        return mapFiles.get(file);
    }

    /**
     * Méthode récursive permettant d'obtenir un mapping de
     * la liste des fichiers et de leur date de dernière
     * modification.
     *
     * @param dir
     * @return ArrayList<File>
     */
    Map<File,Long> mapFiles(File dir) {
        List<File> files = selector.listFiles(dir);
        HashMap<File,Long> map = new HashMap<File,Long>();
        if (files != null) {
            for(File f : files) {
                if (f.isFile() && filter.accept(f)) {
                    map.put(f, f.lastModified());
                }
            }
        }
        return map;
    }

    void monitor() {
        if (!src.canRead()) {
            log.error(" --- ");
            log.error(DirectoryMonitor.class.getName() + " ERREUR : ");
            log.error("  Le répertoire [" + src.getAbsolutePath() + "] n'est pas accessible...");
            log.error(" --- ");
            return;
        }
        boolean modif = false;
        do {
            long exec = System.currentTimeMillis();

            log.debug(DirectoryMonitor.class.getName() + " : Début de la vérifications des fichiers sous le répertoire [" + src.getAbsolutePath() + "].");

            Map<File,Long> newMapFiles = mapFiles( src );
            modif = !newMapFiles.equals(mapFiles);
            if (modif) {
                for (File f : mapFiles.keySet()) {
                    if (!newMapFiles.containsKey(f))
                        notifyDeleted(f); // suppression
                }
                for (Entry<File,Long> entry : newMapFiles.entrySet()) {
                    Long old = mapFiles.get(entry.getKey());
                    if (old == null) {
                        notifyAdded(entry.getKey()); // ajout
                    } else if (old.longValue() != entry.getValue().longValue()) {
                        notifyChanged(entry.getKey()); // modification
                    }
                }

                mapFiles = Collections.unmodifiableMap( newMapFiles );

                log.debug(DirectoryMonitor.class.getName() + " : Changements détectés sous [" + src.getAbsolutePath() + "], relancer la vérification.");
            } else {
                log.debug(DirectoryMonitor.class.getName() + " : Aucun changement détecté sous [" + src.getAbsolutePath() + "].");
            }
            log.debug(DirectoryMonitor.class.getName() + ".run() - Temps de routine: " + (System.currentTimeMillis() - exec) + " millisecondes.");
        } while (modif); /* Si on détecte une modification, on relance le moniteur... */
    }

    void notifyChanged(File f) {
        log.trace(DirectoryMonitor.class.getName() + " : Fichier modifié [" + f.getAbsolutePath() + "]");
        for (WeakReference<DirectoryListener> ref : listeners) {
            DirectoryListener listener = ref.get();
            if (listener != null)
                listener.fileChanged(f);
        }
    }

    void notifyDeleted(File f) {
        log.trace(DirectoryMonitor.class.getName() + " : Fichier supprimé [" + f.getAbsolutePath() + "]");
        for (WeakReference<DirectoryListener> ref : listeners) {
            DirectoryListener listener = ref.get();
            if (listener != null)
                listener.fileDeleted(f);
        }
    }

    void notifyAdded(File f) {
        log.trace(DirectoryMonitor.class.getName() + " : Fichier ajouté [" + f.getAbsolutePath() + "]");
        for (WeakReference<DirectoryListener> ref : listeners) {
            DirectoryListener listener = ref.get();
            if (listener != null)
                listener.fileAdded(f);
        }
    }

    DirectorySelector defaultSelector() {
        return new DirectorySelector() {
            public List<File> listFiles(File dir) {
                File[] files = dir.listFiles();
                if (files == null)
                    return null;
                List<File> list = new LinkedList<File>();
                for(File f : files) {
                    if (f.isFile()) {
                        list.add(f);
                    } else if (f.isDirectory()) {
                        List<File> dirFiles = listFiles(f);
                        if (dirFiles != null)
                            list.addAll( dirFiles );
                    }
                }
                return list;
            }
        };
    }


    class DirectoryNotifier extends TimerTask {
        public void run() {
            monitor();
        }
    }
}
