package jmx.model.info.builder;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import jmx.model.info.AttributeInfo;
import jmx.model.info.ManagementBeanInfo;

/**
 *
 * @author zinic
 */
public class MBeanInfoBuilder {

    private final ManagementBeanInfo managementBean;

    public MBeanInfoBuilder(ManagementBeanInfo managementBean) {
        this.managementBean = managementBean;
    }

    public MBeanInfo toMBeanInfo(String className, String description) {
        return new MBeanInfo(className, description, attributeInfo(), new MBeanConstructorInfo[0], new MBeanOperationInfo[0], new MBeanNotificationInfo[0]);
    }

    private MBeanAttributeInfo[] attributeInfo() {
        final MBeanAttributeInfo[] mBeanAttributes = new MBeanAttributeInfo[managementBean.getAttributes().size()];

        int index = 0;
        for (AttributeInfo attributeInfo : managementBean.getAttributes()) {
            mBeanAttributes[index++] = new MBeanAttributeInfo(attributeInfo.getName(), attributeInfo.getType(), attributeInfo.getDescription(),
                    attributeInfo.isReadable(), attributeInfo.isWritable(), attributeInfo.isIs());
        }

        return mBeanAttributes;
    }
}
