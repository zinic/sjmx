package net.jps.sjmx.plugin;

/**
 *
 * @author zinic
 */
public interface ObjectFactory<T> {

    T createObject(String className) throws ClassNotFoundException;
}
