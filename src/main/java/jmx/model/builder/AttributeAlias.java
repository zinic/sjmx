package jmx.model.builder;

/**
 *
 * @author zinic
 */
public class AttributeAlias {

   private final String attributeName, alias;

   public AttributeAlias(String attributeName, String alias) {
      this.attributeName = attributeName;
      this.alias = alias;
   }

   public String getAlias() {
      return alias;
   }

   public String getAttributeName() {
      return attributeName;
   }
}
