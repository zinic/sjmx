package net.jps.sjmx.command.jmx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import javax.management.*;
import javax.management.remote.JMXConnector;
import net.jps.jx.JsonWriter;
import net.jps.jx.jackson.JacksonJsonWriter;
import net.jps.jx.mapping.reflection.StaticFieldMapper;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.config.ConfigurationReader;
import jmx.model.Attribute;
import jmx.model.ManagementBean;
import org.codehaus.jackson.JsonFactory;

/**
 *
 * @author zinic
 */
public class ReadCommand extends AbstractJmxCommand {

    public ReadCommand(ConfigurationReader configurationManager) {
        super(configurationManager);
    }

    @Override
    public String getCommandDescription() {
        return "Reads all readable attributes that are available on the requested MBean.";
    }

    @Override
    public String getCommandToken() {
        return "read";
    }

    @Override
    public CommandResult perform(String[] arguments) {
        if (arguments.length != 1) {
            return new InvalidArguments("Expecting MBean full-name or MBean search string.");
        }

        return readMBean(arguments[0]);
    }

    private CommandResult readMBean(String mbeanName) {
        try {
            final JMXConnector jmxConnector = connect();
            final CommandResult result = readMBean(mbeanName, jmxConnector.getMBeanServerConnection());

            jmxConnector.close();

            return result;
        } catch (Exception ex) {
            return new ExceptionResult(ex);
        }
    }

    private CommandResult readMBean(String mbeanName, MBeanServerConnection mBeanServerConnection) throws IOException {
        try {
            final Set<ObjectName> foundObjectNames = mBeanServerConnection.queryNames(ObjectName.getInstance(mbeanName), null);

            if (!foundObjectNames.isEmpty()) {
                final ObjectName first = foundObjectNames.iterator().next();

                return readMBeanAttributres(first, mBeanServerConnection);
            } else {
                return new MessageResult("Unable to locate any MBean bound to full-name or query string: " + mbeanName);
            }
        } catch (Exception exception) {
            return new ExceptionResult(exception);
        }
    }

    private CommandResult readMBeanAttributres(ObjectName name, MBeanServerConnection mBeanServerConnection) {
        final StringBuilder stringBuilder = new StringBuilder();
        final JsonWriter<ManagementBean> mbeanJsonWriter = new JacksonJsonWriter<ManagementBean>(new JsonFactory(), StaticFieldMapper.getInstance());

        try {
            final MBeanInfo mbeanInfo = mBeanServerConnection.getMBeanInfo(name);
            final AttributeList attrList = mBeanServerConnection.getAttributes(name, getAttributeNames(mbeanInfo));

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mbeanJsonWriter.write(buildModel(name, attrList), baos);

            stringBuilder.append(new String(baos.toByteArray()));
        } catch (Exception exception) {
            return new ExceptionResult(exception);
        }

        return new MessageResult(stringBuilder.toString());
    }

    private String[] getAttributeNames(MBeanInfo mBeanInfo) {
        final String[] attributeNames = new String[mBeanInfo.getAttributes().length];

        int index = 0;
        for (MBeanAttributeInfo attrInfo : mBeanInfo.getAttributes()) {
            attributeNames[index++] = attrInfo.getName();
        }

        return attributeNames;
    }

    private ManagementBean buildModel(ObjectName name, final AttributeList attrList) throws NullPointerException {
        final ManagementBean model = new ManagementBean();
        model.setName(name.getKeyProperty("name"));
        model.setType(name.getKeyProperty("type"));

        for (javax.management.Attribute attr : attrList.asList()) {
            final Attribute attrModel = new Attribute();
            attrModel.setName(attr.getName());
            attrModel.setValue(attr.getValue());

            model.getAttributes().add(attrModel);
        }

        return model;
    }
}
