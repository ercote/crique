package net.edc.crique;

import java.io.File;

import java.io.FileFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.edc.crique.parser.DepotParser;
import com.lq.monitor.DirectoryListener;
import com.lq.monitor.DirectoryMonitor;
import com.lq.monitor.DirectorySelector;

public class Depot {

    protected static Logger log = Logger.getLogger(Depot.class);

    protected Map<String,Object> mapObjects;

    protected DirectoryMonitor monitor;
    protected DepotConfig config;
    protected DirectoryListener listener;
    protected DirectorySelector dirSelector;
    protected DepotListener depotListener;

    protected Depot(DepotConfig config) throws Exception {
        this.config = config;
        mapObjects = Collections.synchronizedMap(new HashMap<String,Object>());
        dirSelector = (DirectorySelector)instantiate(config.getSelector());
        monitor = new DirectoryMonitor(new File(config.getSrc()), (long)config.getMonitoringInterval()*1000, getFileFilter(), dirSelector);
        listener = (DirectoryListener)instantiate(config.getListener());
        if (listener != null) {
            monitor.addListener(listener);
        }
        depotListener = new DepotListener();
        monitor.addListener(depotListener);
    }

    public void init() {
        log.debug("DEBUG net.edc.crique");
        log.debug(" Depot [" + config.getSrc() + "]");
        log.debug("   Monitor interval [" + config.getMonitoringInterval() + " seconds]");
        if (dirSelector != null) {
            log.debug("   Selector [" + dirSelector.getClass().getName() + "]");
        }
        log.debug("   Parsers:");
        for (DepotFile f : config.getDepotFiles()) {
            log.debug("     " + f.getPath() + " --> " + f.getParser().getClass().getName());
        }
        monitor.start();
    }

    static Object instantiate(String clazz) throws Exception {
        Object o = null;
        if (clazz != null && clazz.length() > 0) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            o = Class.forName(clazz, true, loader).newInstance();
        }
        return o;
    }

    public void destroy() {
        monitor.stop();
        mapObjects.clear();
        config.clear();
    }

    public DepotConfig getConfig() {
        return config;
    }

    public DirectorySelector getSelector() {
        return dirSelector;
    }

    public DirectoryListener getListener() {
        return listener;
    }

    FileFilter getFileFilter() {
        return new FileFilter() {
            public boolean accept(File file) {
                return config.getDepotFileFromPath(getRelPath(file)) != null;
            }
        };
    }

    public byte[] getContent(String key) {
        return (byte[])getObject(key);
    }

    public byte[] getContent(String key, String val) {
        return (byte[])getObject(key, val);
    }

    public Object getObject(String key) {
        return getObject(key, null);
    }

    public Object getObject(String key, String val) {
        DepotFile f = config.getDepotFile(key);
        if (f != null) {
            if (val == null) {
                return getMapObject(f.getPath());
            } else {
                return getMapObject(f.getPath(DepotUtils.standardizePath(val)));
            }
        }
        return null;
    }

    Object getMapObject(String path) {
        return mapObjects.get(path);
    }

    String getRelPath(File file) {
        String path = file.getAbsolutePath().substring(config.getSrc().length());
        return path.toLowerCase();
    }

    protected void supprimer(File file) {
        mapObjects.remove(getRelPath(file));
    }

    protected void modif(File file) {
        String path = getRelPath(file);
        DepotFile depotFile = config.getDepotFileFromPath(path);
        if (depotFile == null)
            return;
        try {
            DepotParser<?> parser = depotFile.getParser();
            Object o = parser.parse(file, DepotUtils.getFileContent(file));
            mapObjects.put(path, o);
        } catch(Exception e) {
            log.error(this.getClass().getName(), e);
        }
    }

    protected class DepotListener implements DirectoryListener {
        public void fileAdded(File file) {
            fileChanged(file);
        }

        public void fileDeleted(File file) {
            supprimer(file);
        }

        public void fileChanged(File file) {
            modif(file);
        }
    }
}
