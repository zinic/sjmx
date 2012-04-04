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
import net.jps.sjmx.config.ConfigurationManager;
import net.jps.sjmx.jmx.model.MBeanAttributeInfoModel;
import net.jps.sjmx.jmx.model.MBeanModel;
import org.codehaus.jackson.JsonFactory;

/**
 *
 * @author zinic
 */
public class DescribeCommand extends AbstractJmxCommand {

    public DescribeCommand(ConfigurationManager configurationManager) {
        super(configurationManager);
    }

    @Override
    public String getCommandDescription() {
        return "Describes readable attributes that are available on the requested MBean.";
    }

    @Override
    public String getCommandToken() {
        return "describe";
    }

    @Override
    public CommandResult perform(String[] arguments) {
        if (arguments.length != 1) {
            return new InvalidArguments("Expecting MBean full-name or MBean search string.");
        }

        return describeMBean(arguments[0]);
    }

    private CommandResult describeMBean(String mbeanName) {
        try {
            final JMXConnector jmxConnector = connect();
            final CommandResult result = describeMBean(mbeanName, jmxConnector.getMBeanServerConnection());

            jmxConnector.close();

            return result;
        } catch (Exception ex) {
            return new ExceptionResult(ex);
        }
    }

    private CommandResult describeMBean(String mbeanName, MBeanServerConnection mBeanServerConnection) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        final JsonWriter<MBeanModel> mbeanJsonWriter = new JacksonJsonWriter<MBeanModel>(new JsonFactory(), StaticFieldMapper.getInstance());

        try {
            final Set<ObjectName> foundObjectNames = mBeanServerConnection.queryNames(ObjectName.getInstance(mbeanName), null);
            
            if (!foundObjectNames.isEmpty()) {
                final ObjectName first = foundObjectNames.iterator().next();
                final MBeanInfo mbeanInfo = mBeanServerConnection.getMBeanInfo(first);
                
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mbeanJsonWriter.write(mbeanInfoToModel(first, mbeanInfo), baos);
                
                stringBuilder.append(new String(baos.toByteArray()));
            } else {
                return new MessageResult("Unable to locate any MBean bound to full-name or query string: " + mbeanName);
            }
        } catch (Exception exception) {
            return new ExceptionResult(exception);
        }

        return new MessageResult(stringBuilder.toString());
    }
    
    private MBeanModel mbeanInfoToModel(ObjectName name, MBeanInfo mBeanInfo) {
        final MBeanModel model = new MBeanModel();
        model.setName(name.getKeyProperty("name"));
        model.setType(name.getKeyProperty("type"));
        model.setClassName(mBeanInfo.getClassName());
        model.setDescription(mBeanInfo.getDescription());
        
        for (MBeanAttributeInfo attrInfo : mBeanInfo.getAttributes()) {
            final MBeanAttributeInfoModel attrModel = new MBeanAttributeInfoModel();
            attrModel.setName(attrInfo.getName());
            attrModel.setDescription(attrInfo.getDescription());
            attrModel.setType(attrInfo.getType());
            attrModel.setReadable(attrInfo.isReadable());
            attrModel.setWritable(attrInfo.isWritable());
            
            model.getAttributes().add(attrModel);
        }
        
        return model;
    }
}
