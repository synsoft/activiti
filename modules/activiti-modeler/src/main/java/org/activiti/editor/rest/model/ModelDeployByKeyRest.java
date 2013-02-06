package org.activiti.editor.rest.model;

import java.io.IOException;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelDeployByKeyRest  extends ServerResource implements ModelDataJsonConstants {
	
	
	  private RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
	  protected static final Logger LOGGER = LoggerFactory.getLogger(ModelDeployByKeyRest.class);
	  private ObjectMapper objectMapper = new ObjectMapper();
	  
	  @Get
	  public Model deployByKey()
{
    	  
    	  System.out.println("--------------- ModelDeployByKeyRest deployByKey--------------------");  
        
        
        try {
        
            
            String modelKey = (String) getRequest().getAttributes().get("modelKey");
            
            
            return deployModel(modelKey);
            
        } catch(Exception e) {
        	
        	e.printStackTrace();
        	
        }
        return null;
      }


	    
		public Model deployModel(String modelKey) throws JsonProcessingException, IOException {
				
		         Model modelData = repositoryService.createModelQuery().modelKey(modelKey).singleResult();

				ObjectNode modelNode = (ObjectNode) new ObjectMapper()
						.readTree(repositoryService.getModelEditorSource(modelData
								.getId()));
				byte[] bpmnBytes = null;

				BpmnModel model = new BpmnJsonConverter()
						.convertToBpmnModel(modelNode);
				bpmnBytes = new BpmnXMLConverter().convertToXML(model);

				String processName = modelData.getName() + ".bpmn20.xml";
				Deployment deployment = repositoryService.createDeployment()
						.name(modelData.getName())
						.addString(processName, new String(bpmnBytes)).deploy();
				
				return modelData;

		}
		
		
		  
		/*	  protected void deployModel() {
				    try {
				      
				      ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
				      byte[] bpmnBytes = null;
				      
				      
				      // this is for table editor that we don't use 
				      if (SimpleTableEditorConstants.TABLE_EDITOR_CATEGORY.equals(modelData.getCategory())) {
				        JsonConverter jsonConverter = new JsonConverter();
				        WorkflowDefinition workflowDefinition = jsonConverter.convertFromJson(modelNode);
				        
				        WorkflowDefinitionConversion conversion = 
				                ExplorerApp.get().getWorkflowDefinitionConversionFactory().createWorkflowDefinitionConversion(workflowDefinition);
				        bpmnBytes = conversion.getbpm20Xml().getBytes("utf-8");
				      } else {
				        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
				        bpmnBytes = new BpmnXMLConverter().convertToXML(model);
				      }

				      String processName = modelData.getName() + ".bpmn20.xml";
				      Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes)).deploy();

				      ExplorerApp.get().getViewManager().showDeploymentPage(deployment.getId());

				    } catch (Exception e) {
				      e.printStackTrace();
				      ExplorerApp.get().getNotificationManager().showErrorNotification(Messages.PROCESS_TOXML_FAILED, e);
				    }
				  }
			  */
			  
}
