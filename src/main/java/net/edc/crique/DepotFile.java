package net.edc.crique;

import net.edc.crique.parser.DepotParser;

public class DepotFile {
    String path, key;
    DepotParser<?> parser;

    int wildcardIndex;

    public DepotFile(String path, DepotParser<?> parser, String key) {
        this.path = DepotUtils.standardizePath(path.trim()).toLowerCase();
        this.wildcardIndex = path.indexOf("*");
        this.key = key;
        this.parser = parser;
    }

    public DepotParser<?> getParser() {
        return parser;
    }

    public String getPath() {
        return path;
    }

    public String getKey() {
        return key;
    }

    public boolean hasWildcard() {
        return wildcardIndex > -1;
    }

    public String getPath(String valeur) {
        if (!hasWildcard()) {
            return path;
        }
        String prefix = path.substring(0, wildcardIndex);
        String suffix = path.substring(wildcardIndex+1);
        return prefix+valeur.toLowerCase()+suffix;
    }

    public boolean match(String endPath) {
        if (hasWildcard()) {
            String prefix = path.substring(0, wildcardIndex);
            String suffix = path.substring(wildcardIndex+1);
            return  endPath.startsWith(prefix) && endPath.endsWith(suffix) &&
                    endPath.length() >= path.length()-1;
        } else {
            return endPath.equals(path);
        }
    }
}
