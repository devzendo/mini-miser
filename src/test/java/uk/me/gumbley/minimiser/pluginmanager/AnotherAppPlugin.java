package uk.me.gumbley.minimiser.pluginmanager;

public class AnotherAppPlugin extends AbstractPlugin implements ApplicationPlugin {
    public String getName() {
        return "Application";
    }

    public String getVersion() {
        return "1.0.0";
    }
}
