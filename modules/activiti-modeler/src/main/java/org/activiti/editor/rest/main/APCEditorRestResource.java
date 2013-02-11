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
package org.activiti.editor.rest.main;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author Tijs Rademakers
 */
public class APCEditorRestResource extends ServerResource implements ModelDataJsonConstants {

	private RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();

	@Get
	public InputRepresentation getEditorPage() throws UnsupportedEncodingException, XMLStreamException {

		
		String modelId = convertToEditableModel();
		redirectPermanent("/activiti-webapp-explorer2/service/editor?id=" + modelId);

		InputStream editorStream = this.getClass().getClassLoader().getResourceAsStream("editor.html");
		InputRepresentation editorResultRepresentation = new InputRepresentation(editorStream);
		editorResultRepresentation.setMediaType(MediaType.APPLICATION_XHTML);
		return editorResultRepresentation;
	}

	public String convertToEditableModel() throws UnsupportedEncodingException, XMLStreamException {

			System.out.println("--------------- convertToEditableModel  --------------------");

			String deploymentId = (String) getRequest().getAttributes().get("deploymentId");

			System.out.println("deploymentId " + deploymentId);

			Model modelData = repositoryService.createModelQuery().modelKey(deploymentId).singleResult();

			// if there is model
			// then return existing model id
			if (modelData != null){
				System.out.println("there is model, so we are returning existing model with id " + modelData.getId());
				return modelData.getId();
			}
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();

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
			modelData.setKey(deploymentId);

			BpmnJsonConverter converter = new BpmnJsonConverter();
			modelNode = converter.convertToJson(bpmnModel);

			ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
			modelObjectNode.put(MODEL_NAME, processDefinition.getName());
			modelObjectNode.put(MODEL_REVISION, 1);
			modelObjectNode.put(MODEL_DESCRIPTION, processDefinition.getDescription());
			modelData.setMetaInfo(modelObjectNode.toString());

			repositoryService.saveModel(modelData);
			repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

			return modelData.getId();

	}

	protected void deployModel() {
		try {
			Model modelData = null;

			ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
			byte[] bpmnBytes = null;

			BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
			bpmnBytes = new BpmnXMLConverter().convertToXML(model);

			String processName = modelData.getName() + ".bpmn20.xml";
			Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes)).deploy();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public String newModel() {

		System.out.println("--------------- CREATE NEW MODEL --------------------");

		try {
			String deploymentId = (String) getRequest().getAttributes().get("deploymentId");

			System.out.println("deploymentId " + deploymentId);

			ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();

			// List<Model> modelList =
			// repositoryService.createModelQuery().deploymentId(deploymentId).list();

			List<Model> modelList = repositoryService.createModelQuery().modelKey(deploymentId).list();
			if (!modelList.isEmpty()) {
				System.out.println("MODEL LIST SIZES " + modelList.size());
				return modelList.get(0).getId();
			} else
				System.out.println("MODEL DONT EXISTS");

			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode editorNode = objectMapper.createObjectNode();
			editorNode.put("id", "canvas");
			editorNode.put("resourceId", "canvas");
			ObjectNode stencilSetNode = objectMapper.createObjectNode();
			stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
			editorNode.put("stencilset", stencilSetNode);
			Model modelData = repositoryService.newModel();

			// modelData.setDeploymentId(deploymentId);
			modelData.setKey(deploymentId);

			// repositoryService.getM

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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
