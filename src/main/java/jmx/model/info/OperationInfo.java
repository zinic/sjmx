package jmx.model.info;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author zinic
 */
public final class OperationInfo extends ManagementBeanComponentInfo {

   private List<OperationParameterInfo> operationParameters;

   public List<OperationParameterInfo> getOperationParameters() {
      if (operationParameters == null) {
         operationParameters = new LinkedList<OperationParameterInfo>();
      }

      return operationParameters;
   }
}
