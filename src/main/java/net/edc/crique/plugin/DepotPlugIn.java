package net.edc.crique.plugin;


import net.edc.crique.Depot;
import net.edc.crique.DepotFactory;

public class DepotPlugIn {

    protected Depot depot;
    protected String reference;
    protected String config;

/*
    public void init(ServletContext context) throws Exception {
        depot = DepotFactory.getInstance(context.getResourceAsStream(config));
        depot.init();
        context.setAttribute(DepotPlugIn.class.getName() + "." + reference, depot);
    }

    public static Depot getDepot(ServletContext context, String reference) {
        return (Depot)context.getAttribute(DepotPlugIn.class.getName() + "." + reference);
    }
*/
    public void destroy() {
        depot.destroy();
    }


    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference){
        this.reference = reference;
    }
}
