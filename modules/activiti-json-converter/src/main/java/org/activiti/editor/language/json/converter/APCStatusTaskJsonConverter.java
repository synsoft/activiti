/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.editor.language.json.converter;

import java.util.Map;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

/**
 * @author Tijs Rademakers
 */
public class APCStatusTaskJsonConverter extends BaseBpmnJsonConverter {

  public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap,
      Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
    
    fillJsonTypes(convertersToBpmnMap);
    fillBpmnTypes(convertersToJsonMap);
  }
  
  public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
    convertersToBpmnMap.put(STENCIL_TASK_STATUS, APCStatusTaskJsonConverter.class);
  }
  
  public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
    convertersToJsonMap.put(ServiceTask.class, APCStatusTaskJsonConverter.class);
  }
  
  protected String getStencilId(FlowElement flowElement) {
    return STENCIL_TASK_STATUS;
  }
  
  protected void convertElementToJson(ObjectNode propertiesNode, FlowElement flowElement) {
  	ServiceTask serviceTask = (ServiceTask) flowElement;
  	
  	if ("mail".equalsIgnoreCase(serviceTask.getType())) {
  	  
  	  setPropertyFieldValue(PROPERTY_MAILTASK_TO, serviceTask, propertiesNode);
  	  setPropertyFieldValue(PROPERTY_MAILTASK_FROM, serviceTask, propertiesNode);
  	  setPropertyFieldValue(PROPERTY_MAILTASK_SUBJECT, serviceTask, propertiesNode);
  	  setPropertyFieldValue(PROPERTY_MAILTASK_CC, serviceTask, propertiesNode);
  	  setPropertyFieldValue(PROPERTY_MAILTASK_BCC, serviceTask, propertiesNode);
  	  setPropertyFieldValue(PROPERTY_MAILTASK_TEXT, serviceTask, propertiesNode);
  	  setPropertyFieldValue(PROPERTY_MAILTASK_HTML, serviceTask, propertiesNode);
  	  setPropertyFieldValue(PROPERTY_MAILTASK_CHARSET, serviceTask, propertiesNode);
  	  
  	} else {
  	
    	if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(serviceTask.getImplementationType())) {
    	  propertiesNode.put(PROPERTY_SERVICETASK_CLASS, serviceTask.getImplementation());
    	} else if (ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(serviceTask.getImplementationType())) {
        propertiesNode.put(PROPERTY_SERVICETASK_EXPRESSION, serviceTask.getImplementation());
      } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(serviceTask.getImplementationType())) {
        propertiesNode.put(PROPERTY_SERVICETASK_DELEGATE_EXPRESSION, serviceTask.getImplementation());
      }
    	
    	addFieldExtensions(serviceTask.getFieldExtensions(), propertiesNode);
  	}
  }
  
  protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
    ServiceTask task = new ServiceTask();
    if (StringUtils.isNotEmpty(getPropertyValueAsString(PROPERTY_SERVICETASK_CLASS, elementNode))) {
      task.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
      task.setImplementation(getPropertyValueAsString(PROPERTY_SERVICETASK_CLASS, elementNode));
      
    } else if (StringUtils.isNotEmpty(getPropertyValueAsString(PROPERTY_SERVICETASK_EXPRESSION, elementNode))) {
      task.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
      task.setImplementation(getPropertyValueAsString(PROPERTY_SERVICETASK_EXPRESSION, elementNode));
      
    } else if (StringUtils.isNotEmpty(getPropertyValueAsString(PROPERTY_SERVICETASK_DELEGATE_EXPRESSION, elementNode))) {
      task.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
      task.setImplementation(getPropertyValueAsString(PROPERTY_SERVICETASK_DELEGATE_EXPRESSION, elementNode));
    }
    
    JsonNode fieldsNode = getProperty(PROPERTY_SERVICETASK_FIELDS, elementNode);
    System.out.println("I am here...");
    if (fieldsNode != null) {
      JsonNode itemsArrayNode = fieldsNode.get(EDITOR_PROPERTIES_GENERAL_ITEMS);
      if (itemsArrayNode != null) {
        for (JsonNode itemNode : itemsArrayNode) {
          JsonNode nameNode = itemNode.get(PROPERTY_SERVICETASK_FIELD_NAME);
          if (nameNode != null && StringUtils.isNotEmpty(nameNode.asText())) {
            
            FieldExtension field = new FieldExtension();
            field.setFieldName(nameNode.asText());
            if (StringUtils.isNotEmpty(getValueAsString(PROPERTY_SERVICETASK_FIELD_VALUE, itemNode))) {
              field.setStringValue(getValueAsString(PROPERTY_SERVICETASK_FIELD_VALUE, itemNode));
            } else if (StringUtils.isNotEmpty(getValueAsString(PROPERTY_SERVICETASK_FIELD_EXPRESSION, itemNode))) {
              field.setExpression(getValueAsString(PROPERTY_SERVICETASK_FIELD_EXPRESSION, itemNode));
            }
            task.getFieldExtensions().add(field);
          }
        }
      } else {
    	  System.out.println("Choice type...");
          FieldExtension field = new FieldExtension();
          field.setFieldName("status");
          field.setStringValue(fieldsNode.asText());
          task.getFieldExtensions().add(field);
      }
    }
    
    return task;
  }
  
  protected void setPropertyFieldValue(String name, ServiceTask task, ObjectNode propertiesNode) {
    for (FieldExtension extension : task.getFieldExtensions()) {
      if (name.substring(8).equalsIgnoreCase(extension.getFieldName())) {
        if (StringUtils.isNotEmpty(extension.getStringValue())) {
          setPropertyValue(name, extension.getStringValue(), propertiesNode);
        } else if (StringUtils.isNotEmpty(extension.getExpression())) {
          setPropertyValue(name, extension.getExpression(), propertiesNode);
        }
      }
    }
  }
}
