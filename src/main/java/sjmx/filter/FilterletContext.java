package sjmx.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jmx.model.builder.ManagementBeanBuilder;
import jmx.model.info.ManagementBeanInfo;

/**
 *
 * @author zinic
 */
public class FilterletContext implements Context {

    private final ManagementBeanBuilder managementBeanBuilder;
    private final Map<String, List<ManagementBeanInfo>> managementBeans;

    public FilterletContext(ManagementBeanBuilder managementBeanBuilder, Map<String, List<ManagementBeanInfo>> managementBeans) {
        this.managementBeanBuilder = managementBeanBuilder;
        this.managementBeans = new HashMap<String, List<ManagementBeanInfo>>(managementBeans);
    }

    @Override
    public Map<String, List<ManagementBeanInfo>> jmxInfo() {
        return managementBeans;
    }

    @Override
    public ManagementBeanBuilder builder() {
        return managementBeanBuilder;
    }
}
