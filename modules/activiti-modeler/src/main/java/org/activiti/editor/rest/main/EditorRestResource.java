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

import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author Tijs Rademakers
 */
public class EditorRestResource extends ServerResource {
  
  @Get
  public InputRepresentation getEditorPage() {
    InputStream editorStream = this.getClass().getClassLoader().getResourceAsStream("editor.html");
    InputRepresentation editorResultRepresentation = new InputRepresentation(editorStream);
    editorResultRepresentation.setMediaType(MediaType.APPLICATION_XHTML);
    return editorResultRepresentation;
    
   
  }
}
