package org.activiti.editor.rest.model;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelCreateRestResource  extends ServerResource implements ModelDataJsonConstants {
	
	
	  private RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
	  protected static final Logger LOGGER = LoggerFactory.getLogger(ModelCreateRestResource.class);
	  private ObjectMapper objectMapper = new ObjectMapper();
	  
/*	  @Put
	  public ObjectNode getEditorJson() {
		  
	    ObjectNode modelNode = null;
	    String modelId = (String) getRequest().getAttributes().get("modelId");
	    
	    if(NumberUtils.isNumber(modelId)) {
	      RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
	      Model model = repositoryService.getModel(modelId);
	      
	      if (model != null) {
	        try {
	          modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
	          modelNode.put(MODEL_ID, model.getId());
	          ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
	          modelNode.put("model", editorJsonNode);
	          
	        } catch(Exception e) {
	          LOGGER.error("Error creating model JSON", e);
	          setStatus(Status.SERVER_ERROR_INTERNAL);
	        }
	      }
	    }
	    return modelNode;
	  }*/
	  
	  @Get
	  public String newModel()
{
    	  
    	  System.out.println("--------------- ModelCreateRestResource --------------------");  
        
        
        try {
          ObjectMapper objectMapper = new ObjectMapper();
          ObjectNode editorNode = objectMapper.createObjectNode();
          editorNode.put("id", "canvas");
          editorNode.put("resourceId", "canvas");
          ObjectNode stencilSetNode = objectMapper.createObjectNode();
          stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
          editorNode.put("stencilset", stencilSetNode);
          Model modelData = repositoryService.newModel();
          
          String deploymentId = (String) getRequest().getAttributes().get("deploymentId");
          
          System.out.println("deploymentId " +deploymentId);
          
          ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
          
          //repositoryService.get
          
          ObjectNode modelObjectNode = objectMapper.createObjectNode();
          modelObjectNode.put(MODEL_NAME, (String) "test_model_name");
          modelObjectNode.put(MODEL_REVISION, 1);
          String description = null;
          if (StringUtils.isNotEmpty((String) "test")) {
            description = (String) "test1";
          } else {
            description = "";
          }
          modelObjectNode.put(MODEL_DESCRIPTION, description);
          modelData.setMetaInfo(modelObjectNode.toString());
          modelData.setName((String) "test_name");
          
          repositoryService.saveModel(modelData);
          
          repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
          
          return modelData.getId();
        } catch(Exception e) {
        	e.printStackTrace();
        }
        return "";
      }

}
