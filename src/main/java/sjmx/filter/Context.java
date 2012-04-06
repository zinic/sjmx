package sjmx.filter;

import java.util.List;
import java.util.Map;
import jmx.model.builder.ManagementBeanBuilder;
import jmx.model.info.ManagementBeanInfo;

/**
 *
 * @author zinic
 */
public interface Context {

    ManagementBeanBuilder builder();

    Map<String, List<ManagementBeanInfo>> jmxInfo();
}
