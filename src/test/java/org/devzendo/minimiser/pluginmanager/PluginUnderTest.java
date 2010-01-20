/**
 * 
 */
package org.devzendo.minimiser.pluginmanager;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation set at a type level that declares a set of plugins
 * to be loaded before tests in subclasses of PluginUnittestCase
 * run.
 * 
 * @author matt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginUnderTest {
    String[] value();
}
