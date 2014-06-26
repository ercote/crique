package net.edc.crique.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import org.apache.log4j.Logger;
import junit.framework.Assert;
import org.junit.Test;

import net.edc.crique.Depot;
import net.edc.crique.DepotFactory;

public class DepotTest {

    protected static Logger log = Logger.getLogger(Test.class);

    @Test
    public void testConfig() throws Exception {
        String config = System.getProperty("config");
        Assert.assertTrue( conf.exists() && conf.canRead() );
        Depot depot = DepotFactory.getInstance( config );
        depot.init();
        byte[] content = depot.getContent("notes");
        Assert.assertNotNull( content );
        content = depot.getContent("text", "Notes");
        Assert.assertNotNull( content );
        depot.destroy();
    }
}
