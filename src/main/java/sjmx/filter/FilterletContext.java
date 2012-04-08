package sjmx.filter;

import java.util.LinkedList;
import java.util.List;
import jmx.model.proxy.ProxyManagementBeanBuilder;
import jmx.model.info.ManagementDomainInfo;

/**
 *
 * @author zinic
 */
public class FilterletContext implements Context {

    private final ProxyManagementBeanBuilder managementBeanBuilder;
    private final List<ManagementDomainInfo> managementDomains;

    public FilterletContext(ProxyManagementBeanBuilder managementBeanBuilder, List<ManagementDomainInfo> managementDomains) {
        this.managementBeanBuilder = managementBeanBuilder;
        this.managementDomains = new LinkedList<ManagementDomainInfo>(managementDomains);
    }

    @Override
    public List<ManagementDomainInfo> jmxInfo() {
        return managementDomains;
    }

    @Override
    public ProxyManagementBeanBuilder builder() {
        return managementBeanBuilder;
    }
}
