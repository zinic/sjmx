package jmx.model.info;

/**
 *
 * @author zinic
 */
public final class AttributeInfo extends ManagementBeanComponentInfo {

    private boolean readable, writable, is;

    public AttributeInfo() {
    }

    /**
     * Copy constructor
     *
     * @param attributeInfoToCopy
     */
    public AttributeInfo(AttributeInfo attributeInfoToCopy) {
        setName(attributeInfoToCopy.getName());
        setType(attributeInfoToCopy.getType());
        setDescription(attributeInfoToCopy.getDescription());
        setWritable(attributeInfoToCopy.isWritable());
        setReadable(attributeInfoToCopy.isReadable());
    }

    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public void setIs(boolean is) {
        this.is = is;
    }

    public boolean isIs() {
        return is;
    }
}
