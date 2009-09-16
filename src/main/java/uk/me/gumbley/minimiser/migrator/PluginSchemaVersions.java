package uk.me.gumbley.minimiser.migrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.HashCodeBuilder;

import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.version.ComparableVersion;

/**
 * Stores a map of plugin names to schema versions, and allows
 * comparison between this map and another instance.
 * 
 * @author matt
 *
 */
public final class PluginSchemaVersions implements Comparable<PluginSchemaVersions> {
    private final Map<String, ComparableVersion> mPluginNameToSchemaVersion;
    
    /**
     * Construct the map of plugin-schema versions 
     */
    public PluginSchemaVersions() {
        mPluginNameToSchemaVersion = new HashMap<String, ComparableVersion>();
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo(final PluginSchemaVersions o) {
        // It doesn't matter from which side we compare, only
        // plugin names that are present on both sides are
        // compared.
        // We find -1 first across all plugins, then 1, otherwise,
        // it's 0.
        boolean anyMinusOne = false;
        boolean anyOne = false;
        for (final String pluginName : mPluginNameToSchemaVersion.keySet()) {
            if (o.mPluginNameToSchemaVersion.containsKey(pluginName)) {
                final ComparableVersion mySchemaVersion = mPluginNameToSchemaVersion.get(pluginName);
                final ComparableVersion theirSchemaVersion = o.mPluginNameToSchemaVersion.get(pluginName);
                final int cmp = mySchemaVersion.compareTo(theirSchemaVersion);
                if (cmp == -1) {
                    anyMinusOne = true;
                } else if (cmp == 1) {
                    anyOne = true;
                }
            }
        }
        if (anyMinusOne) {
            return -1;
        } else if (anyOne) {
            return 1;
        } else {
            // all plugins present in both this and o are the same
            // or there are none, or they're both dissimilar
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final List<String> versions = new ArrayList<String>();
        final Set<Entry<String, ComparableVersion>> entrySet = mPluginNameToSchemaVersion.entrySet();
        for (Entry<String, ComparableVersion> entry : entrySet) {
            final StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue().getVersion());
            versions.add(sb.toString());
        }
        return StringUtils.join(versions, ", ");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return compareTo((PluginSchemaVersions) obj) == 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // TODO: check this - not sure about hashCodes for collections
        // pick 2 hard-coded, odd, >0 ints as args
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(1, 31);
        final Set<Entry<String, ComparableVersion>> entrySet = mPluginNameToSchemaVersion.entrySet();
        for (Entry<String, ComparableVersion> entry : entrySet) {
            hashCodeBuilder
                .append(entry.getKey())
                .append(entry.getValue());
        }
        return hashCodeBuilder.toHashCode();
    }

    /**
     * Add a plugin name and its schema version to the map.
     * @param pluginName the name of the plugin
     * @param schemaVersion the version of the schema
     */
    public void addPluginSchemaVersion(final String pluginName, final String schemaVersion) {
        mPluginNameToSchemaVersion.put(pluginName, new ComparableVersion(schemaVersion));
    }
}
