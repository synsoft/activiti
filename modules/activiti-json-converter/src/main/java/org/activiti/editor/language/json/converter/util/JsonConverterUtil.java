package org.activiti.editor.language.json.converter.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activiti.bpmn.model.FieldExtension;
import org.activiti.editor.constants.EditorJsonConstants;
import org.activiti.editor.constants.StencilConstants;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;

public class JsonConverterUtil implements EditorJsonConstants, StencilConstants {
  
  public static String getPropertyValueAsString(String name, JsonNode objectNode) {
    String propertyValue = null;
    JsonNode propertyNode = getProperty(name, objectNode);
    if (propertyNode != null && "null".equalsIgnoreCase(propertyNode.asText()) == false) {
      propertyValue = propertyNode.asText();
    }
    return propertyValue;
  }
  
  public static boolean getPropertyValueAsBoolean(String name, JsonNode objectNode) {
    boolean result = false;
    String stringValue = getPropertyValueAsString(name, objectNode);
    if (PROPERTY_VALUE_YES.equalsIgnoreCase(stringValue)) {
      result = true;
    }
    return result;
  }
  
  public static List<String> getPropertyValueAsList(String name, JsonNode objectNode) {
    List<String> resultList = new ArrayList<String>();
    JsonNode propertyNode = getProperty(name, objectNode);
    if (propertyNode != null && "null".equalsIgnoreCase(propertyNode.asText()) == false) {
      String propertyValue = propertyNode.asText();
      String[] valueList = propertyValue.split(",");
      for (String value : valueList) {
        resultList.add(value.trim());
      }
    }
    return resultList;
  }
  
  public static List<String> getItemsValueAsList(String name, JsonNode objectNode) {
	    List<String> resultList = new ArrayList<String>();
	    JsonNode propertyNode = getProperty(name, objectNode);
	    JsonNode itemsArrayNode = propertyNode.get(EDITOR_PROPERTIES_GENERAL_ITEMS);
	      if (itemsArrayNode != null) {
	        for (JsonNode itemNode : itemsArrayNode) {
	          JsonNode nameNode = itemNode.get("event");
	          if (nameNode != null && StringUtils.isNotEmpty(nameNode.asText())) {
	        	  resultList.add(nameNode.asText());
//	            FieldExtension field = new FieldExtension();
//	            field.setFieldName(nameNode.asText());
//	            if (StringUtils.isNotEmpty(getValueAsString(PROPERTY_SERVICETASK_FIELD_VALUE, itemNode))) {
//	              field.setStringValue(getValueAsString(PROPERTY_SERVICETASK_FIELD_VALUE, itemNode));
//	            } else if (StringUtils.isNotEmpty(getValueAsString(PROPERTY_SERVICETASK_FIELD_EXPRESSION, itemNode))) {
//	              field.setExpression(getValueAsString(PROPERTY_SERVICETASK_FIELD_EXPRESSION, itemNode));
//	            }
//	            task.getFieldExtensions().add(field);
	          }
	        }
	    }
	    return resultList;
  }
  
  public static JsonNode getProperty(String name, JsonNode objectNode) {
    JsonNode propertyNode = null;
    if (objectNode.get(EDITOR_SHAPE_PROPERTIES) != null) {
      JsonNode propertiesNode = objectNode.get(EDITOR_SHAPE_PROPERTIES);
      propertyNode = propertiesNode.get(name);
    }
    return propertyNode;
  }

}
