package net.edc.crique;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import net.edc.crique.parser.DepotParser;
import net.edc.crique.parser.RawParser;

public class DepotConfig {

    String src, listener, selector;

    public static int NB_SECONDS_MIN = 30;

    int monitoringInterval;

    boolean live;

    Map<String,DepotFile> mapFiles;

    public DepotConfig() {
        src = null;
        selector = null;
        monitoringInterval = NB_SECONDS_MIN;
        live = false;
        mapFiles = new HashMap<String,DepotFile>();
    }

    public void clear() {
        mapFiles.clear();
    }

    public DepotFile getDepotFile(String key) {
        return mapFiles.get(key);
    }

    public DepotFile getDepotFileFromPath(String relpath) {
        Collection<DepotFile> files = mapFiles.values();
        for (DepotFile file : files) {
            if (file.match(relpath)) {
                return file;
            }
        }
        return null;
    }

    public Collection<DepotFile> getDepotFiles() {
        return mapFiles.values();
    }

    public void addDepotFile(String path, String key, String parser) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        DepotParser<?> depotParser = null;
        if (parser != null && parser.trim().length()>0) {
            depotParser = (DepotParser<?>)Class.forName(parser, true, Thread.currentThread().getContextClassLoader()).newInstance();
        } else {
            depotParser = RawParser.class.newInstance();
        }
        mapFiles.put(key, new DepotFile(path, depotParser, key));
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public int getMonitoringInterval() {
        return monitoringInterval;
    }

    public void setMonitoringInterval(int monitoringInterval) {
        this.monitoringInterval = monitoringInterval < NB_SECONDS_MIN ? NB_SECONDS_MIN : monitoringInterval;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = DepotUtils.standardizePath(src);
        if (this.src.charAt(this.src.length()-1) != File.separatorChar) {
            this.src += File.separatorChar;
        }
    }

    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }
}
