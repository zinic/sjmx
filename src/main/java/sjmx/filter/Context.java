package sjmx.filter;

import java.util.List;
import jmx.model.proxy.ProxyManagementBeanBuilder;
import jmx.model.info.ManagementDomainInfo;

/**
 *
 * @author zinic
 */
public interface Context {

    ProxyManagementBeanBuilder builder();

    List<ManagementDomainInfo> jmxInfo();
}
