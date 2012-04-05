package net.jps.sjmx.config;

import java.io.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.ObjectFactory;

/**
 *
 * @author zinic
 */
public class FileConfigurationManager implements ConfigurationManager {

    public static final String CFG_NAME = "sjmx.cfg.xml";
    private final File configurationDirectory, configurationFile;
    private final ObjectFactory objectFactory;
    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;

    /**
     * Defaults to "user home directory" + File.separatorChar + ".sjmx"
     */
    public FileConfigurationManager(Marshaller marshaller, Unmarshaller unmarshaller, ObjectFactory objectFactory) {
        this(marshaller, unmarshaller, objectFactory, new File(System.getProperty("user.home") + File.separatorChar + ".sjmx"));
    }

    public FileConfigurationManager(Marshaller marshaller, Unmarshaller unmarshaller, ObjectFactory objectFactory, File configurationDirectory) {
        this.configurationDirectory = configurationDirectory;
        this.configurationFile = new File(configurationDirectory, CFG_NAME);

        this.objectFactory = objectFactory;
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    @Override
    public synchronized ConfigurationHandler readConfiguration() throws ConfigurationException {
        return new ConfigurationHandler(this, readConfigurationFile());
    }

    private Configuration readConfigurationFile() throws ConfigurationException {
        if (!configurationDirectory.exists() && !configurationDirectory.mkdir()) {
            throw new ConfigurationException("Unable to make configuration directory: " + configurationDirectory.getAbsolutePath());
        }
        
        if (!configurationFile.exists() || configurationFile.length() == 0) {
            return new Configuration();
        }
        
        try {
            final InputStream fin = new FileInputStream(configurationFile);
            final Configuration newConfiguration = read(fin);
            
            fin.close();
            
            return newConfiguration;
        } catch (IOException ioe) {
            throw new ConfigurationException(ioe.getMessage(), ioe);
        }
    }

    private Configuration read(InputStream in) throws ConfigurationException {
        try {
            final Object o = unmarshaller.unmarshal(in);
            return o instanceof JAXBElement ? ((JAXBElement<Configuration>) o).getValue() : (Configuration) o;
        } catch (JAXBException jaxbe) {
            throw new ConfigurationException(jaxbe.getLinkedException().getMessage(), jaxbe);
        }
    }

    @Override
    public synchronized void write(Configuration configuration) throws ConfigurationException {
        try {
            final FileOutputStream fout = new FileOutputStream(configurationFile);
            write(configuration, fout);
            
            fout.close();
        } catch (IOException ioe) {
            throw new ConfigurationException(ioe.getMessage(), ioe);
        }
    }
    
    private void write(Configuration configuration, OutputStream out) throws ConfigurationException {
        try {
            marshaller.marshal(objectFactory.createSjmx(configuration), out);
        } catch(JAXBException jaxbe) {
            throw new ConfigurationException(jaxbe.getLinkedException().getMessage(), jaxbe);
        }
    }
}
