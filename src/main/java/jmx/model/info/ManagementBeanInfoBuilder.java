package jmx.model.info;

import java.util.LinkedList;
import java.util.List;
import javax.management.*;

/**
 *
 * @author zinic
 */
public final class ManagementBeanInfoBuilder {

   private final ObjectName objectName;
   private final MBeanInfo mBeanInfo;

   public ManagementBeanInfoBuilder(ObjectName objectName, MBeanInfo mBeanInfo) {
      this.objectName = objectName;
      this.mBeanInfo = mBeanInfo;
   }

   public ManagementBeanInfo build() {
      final ManagementBeanInfo model = new ManagementBeanInfo();
      model.setName(objectName.getKeyProperty("name"));
      model.setType(objectName.getKeyProperty("type"));
      model.setDescription(mBeanInfo.getDescription());

      model.getAttributes().addAll(attributeList());
      model.getOperations().addAll(operationList());

      return model;
   }

   private List<AttributeInfo> attributeList() {
      final List<AttributeInfo> attributeInfoList = new LinkedList<AttributeInfo>();

      for (MBeanAttributeInfo attrInfo : mBeanInfo.getAttributes()) {
         final AttributeInfo attrModel = new AttributeInfo();
         attrModel.setName(attrInfo.getName());
         attrModel.setDescription(attrInfo.getDescription());
         attrModel.setType(attrInfo.getType());
         attrModel.setReadable(attrInfo.isReadable());
         attrModel.setWritable(attrInfo.isWritable());
         attrModel.setIs(attrInfo.isIs());

         attributeInfoList.add(attrModel);
      }

      return attributeInfoList;
   }

   private List<OperationInfo> operationList() {
      final List<OperationInfo> operationInfoList = new LinkedList<OperationInfo>();

      for (MBeanOperationInfo operationInfo : mBeanInfo.getOperations()) {
         final OperationInfo operationModel = new OperationInfo();
         
         operationModel.setName(operationInfo.getName());
         operationModel.setType(operationInfo.getReturnType());
         operationModel.setDescription(operationInfo.getDescription());

         operationModel.getOperationParameters().addAll(operationParameterInfo(operationInfo));
         
         operationInfoList.add(operationModel);
      }

      return operationInfoList;
   }

   private List<OperationParameterInfo> operationParameterInfo(MBeanOperationInfo operationInfo) {

      final List<OperationParameterInfo> operationInfoList = new LinkedList<OperationParameterInfo>();
      
      for (MBeanParameterInfo paramInfo : operationInfo.getSignature()) {
         final OperationParameterInfo paramModel = new OperationParameterInfo();

         paramModel.setName(paramInfo.getName());
         paramModel.setType(paramInfo.getType());
         paramModel.setDescription(paramInfo.getDescription());

         operationInfoList.add(paramModel);
      }
      return operationInfoList;
   }
}
