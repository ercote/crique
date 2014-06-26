package net.edc.crique;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class DepotFactory {

    public static Depot getInstance(InputStream is) throws Exception {
        DepotConfig depotConfig = parse( is );
        return depotConfig.isLive() ? new DepotLive(depotConfig) : new Depot(depotConfig);
    }

    static DepotConfig parse(InputStream is) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);
        digester.addObjectCreate("depotConfig", DepotConfig.class);
        digester.addSetProperties("depotConfig");
        digester.addBeanPropertySetter("depotConfig/monitoringInterval");
        digester.addBeanPropertySetter("depotConfig/live");
        digester.addBeanPropertySetter("depotConfig/selector");
        digester.addBeanPropertySetter("depotConfig/listener");
        digester.addBeanPropertySetter("depotConfig/useContextClassLoader");
        digester.addCallMethod("depotConfig/files/file", "addDepotFile", 3);
        digester.addCallParam("depotConfig/files/file", 0, "path");
        digester.addCallParam("depotConfig/files/file", 1, "key");
        digester.addCallParam("depotConfig/files/file", 2);
        DepotConfig config = (DepotConfig)digester.parse(is);
        digester.clear();
        return config;
    }
}
