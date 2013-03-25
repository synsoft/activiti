package de.pickert.bpmn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/* Binary file format used to transport bpmn models 
 * from one database to one other */
public class PPWFile {
	
	
	private List<byte[]> byteList;
	
	private byte[] jsonBytes;
	private byte[] svgBytes;
	private byte[] bpmnBytes;
	
	@SuppressWarnings("unchecked")
	public PPWFile(ObjectInputStream in) throws ClassNotFoundException, IOException {
	    this.byteList = (List<byte[]>) in.readObject();
		this.jsonBytes = byteList.get(0);
		this.svgBytes = byteList.get(1);
		this.bpmnBytes = byteList.get(2);
	}
	
	public PPWFile(List<byte[]> byteList){
	    this.byteList = byteList;
		this.jsonBytes = byteList.get(0);
		this.svgBytes = byteList.get(1);
		this.bpmnBytes = byteList.get(2);
	}
	
	public PPWFile(byte[] jsonBytes, byte[] svgBytes, byte[] bpmnBytes) {
		this.jsonBytes = jsonBytes;
		this.svgBytes = svgBytes;
		this.bpmnBytes = bpmnBytes;
		
		this.byteList = new ArrayList<byte[]>();
		byteList.add(jsonBytes);
		byteList.add(svgBytes);
		byteList.add(bpmnBytes);
		
	}
	
	public void writeTo(ObjectOutputStream out) throws IOException
	{
		out.writeObject(byteList);
	}
	public void writeTo(ByteArrayOutputStream bout) throws IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(byteList);
	}
	
	public ByteArrayOutputStream getByteArrayOutputStream() throws IOException
	{
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);
        out.writeObject(byteList);
        return bOut;
	}

	public List<byte[]> getByteList() {
		return byteList;
	}

	public byte[] getJsonBytes() {
		return jsonBytes;
	}

	public byte[] getSvgBytes() {
		return svgBytes;
	}

	public byte[] getBpmnBytes() {
		return bpmnBytes;
	}
	
	
	
	

}
