package jmx.model.info;

/**
 *
 * @author zinic
 */
public abstract class ManagementBeanComponentInfo {

   private String name, description, type;
   
   public final String getDescription() {
      return description;
   }

   public final void setDescription(String description) {
      this.description = description;
   }

   public final String getName() {
      return name;
   }

   public final void setName(String name) {
      this.name = name;
   }

   public final String getType() {
      return type;
   }

   public final void setType(String type) {
      this.type = type;
   }
}
