package org.activiti.editor.rest.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
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
	  public Deployment deployByKey() throws JsonProcessingException, IOException
{
    	  
    	  System.out.println("--------------- ModelDeployByKeyRest deployByKey--------------------");  
        
            
            String modelKey = (String) getRequest().getAttributes().get("modelKey");
            
            System.out.println("modelKey: " + modelKey);
	        
            
            Model modelData = repositoryService.createModelQuery().modelKey(modelKey).singleResult();
            	
            System.out.println("Model data id: " + modelData.getId());
	         
	         
			ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
			
			byte[] bpmnBytes = null;

			BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
			bpmnBytes = new BpmnXMLConverter().convertToXML(model);

			String processName = modelData.getName() + ".bpmn20.xml";
			Deployment deployment = repositoryService.createDeployment()
					.name(modelData.getName())
					.addString(processName, new String(bpmnBytes)).deploy();
			
			
			// save new deployment to modelData key
			
			modelData.setKey(deployment.getId());
			repositoryService.saveModel(modelData);
			
			
			return deployment;
            
      }


	  
	  
	  // unused please delete
	  
		public Model convertToEditableModel() throws UnsupportedEncodingException, XMLStreamException {


				System.out.println("--------------- convertToEditableModel  --------------------");

	            String modelKey = (String) getRequest().getAttributes().get("modelKey");
	            System.out.println("modelKey: " + modelKey);


				Model modelData = repositoryService.createModelQuery().modelKey(modelKey).singleResult();

				// if there is model
				// then return existing model id
				if (modelData != null)
					return modelData;
				ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(modelKey).singleResult();

				System.out.println("processDefinition.getDeploymentId(): " + processDefinition.getDeploymentId() + " processDefinition.getResourceName(): "
						+ processDefinition.getResourceName());

				InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
				XMLInputFactory xif = XMLInputFactory.newInstance();
				InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
				XMLStreamReader xtr = xif.createXMLStreamReader(in);
				BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

				if (bpmnModel.getMainProcess() == null || bpmnModel.getMainProcess().getId() == null) {
					System.out.println("error main process is null");
					return null;
				}
				if (bpmnModel.getLocationMap().size() == 0) {
					System.out.println("location map is null");
					return null;
				}

				ObjectNode modelNode = null;

				modelData = repositoryService.newModel();
				modelData.setKey(modelKey);

				BpmnJsonConverter converter = new BpmnJsonConverter();
				modelNode = converter.convertToJson(bpmnModel);

				ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
				modelObjectNode.put(MODEL_NAME, processDefinition.getName());
				modelObjectNode.put(MODEL_REVISION, 1);
				modelObjectNode.put(MODEL_DESCRIPTION, processDefinition.getDescription());
				modelData.setMetaInfo(modelObjectNode.toString());

				repositoryService.saveModel(modelData);
				repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

				return modelData;


		}
			  
}
