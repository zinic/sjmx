package jmx.model.builder;

import jmx.model.info.ManagementBeanInfo;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zinic
 */
public class ManagementBeanBuilderImpl implements ManagementBeanBuilder {

   private final Map<ManagementBeanInfo, List<AttributeAlias>> aliasMap;
   private final ManagementBeanInfo info;

   public ManagementBeanBuilderImpl() {
      aliasMap = new HashMap<ManagementBeanInfo, List<AttributeAlias>>();
      info = new ManagementBeanInfo();
   }

   @Override
   public void setType(String type) {
      info.setType(type);
   }

   @Override
   public void setName(String name) {
      info.setName(name);
   }

   @Override
   public void setDescription(String description) {
      info.setDescription(description);
   }

   @Override
   public void alias(ManagementBeanInfo monitorInfo, String attributeName, String alias) {
      List<AttributeAlias> attributeAliases = aliasMap.get(monitorInfo);
      
      if (attributeAliases == null) {
         attributeAliases = new LinkedList<AttributeAlias>();
         aliasMap.put(monitorInfo, attributeAliases);
      }
      
      attributeAliases.add(new AttributeAlias(attributeName, alias));
   }
}
