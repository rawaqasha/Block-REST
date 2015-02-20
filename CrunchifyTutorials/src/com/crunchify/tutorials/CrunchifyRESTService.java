package com.crunchify.tutorials;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
 
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.data.manipulation.ColumnSorter;
import org.pipeline.core.data.*; 
import org.pipeline.core.xmlstorage.XmlDataStore;

import com.connexience.api.model.*;
import com.connexience.api.*;
import com.connexience.api.model.EscDocument;
import com.connexience.api.model.EscFolder;
import com.connexience.api.model.EscUser;
import com.connexience.server.util.SerializationUtils;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
@Path("/")
public class CrunchifyRESTService {
	// Method implements an operation on Data wrapper (called from proxyblock block);
	@POST
	    @Path("/crunchifyService")
	    @Consumes(MediaType.TEXT_PLAIN)
	    public Response crunchifyREST(byte[] myBytes) throws ClassNotFoundException, IOException, Exception{
		
		
	        System.out.println("before des");
	        XmlDataStore inData=(XmlDataStore) deserialize(myBytes);
	        Data indata=new Data();
	        indata.recreateObject(inData);
	        Data output=new Data();
	            
	         int size = indata.getColumns();
	        for(int i=size-1;i>=0;i--)
	         {  
	        	output.addColumn(indata.column(i).getCopy());
				
	         }
	      System.out.println("before ser");
	      byte[] myByte=serialize(output.storeObject());
	     
	      // return HTTP response 200 in case of success
	        return Response.status(200).entity(myByte).build();
	    }

	// Method implements an operation on File (called from file_proxy block);
		@POST
	    @Path("/crunchifyService1")
	    @Consumes(MediaType.TEXT_PLAIN)
	    public Response crunchifyREST1(byte[] myBytes) throws ClassNotFoundException, IOException, Exception{
			String HOSTNAME="192.168.56.101";
			int PORT=8080;
			Boolean SECURE=false;
			String USERNAME="rawa_qasha@yahoo.com";
			String PASSWORD="rawa1975";
			byte[] myByte=null;
			String myByte_=null;
			try {
				// Create a new storage client with username and password
				StorageClient client = new StorageClient(HOSTNAME,PORT,SECURE, USERNAME,PASSWORD);
				// Check that the current user can be accessed
				EscUser currentUser = client.currentUser();
				EscFolder homeFolder = client.homeFolder();
				EscDocument[] docs = client.folderDocuments(homeFolder.getId());
				//
				System.out.println(currentUser.getName());
				System.out.println("the recieved file   "+new String(myBytes));
				
				//get file ID
				String ID=getFileID(new String(myBytes),docs);
				
				//download the file from the server
			   	EscDocument doc = client.getDocument(new String(ID));
			    java.io.File localFile = new java.io.File("d://image.jpg");
				client.download(doc, localFile);
					
				//      Zip the file
				
				File sourceFile = localFile;

				//output file
		         FileOutputStream fos = new FileOutputStream("d://zipFile.zip");
		         ZipOutputStream zos = new ZipOutputStream(fos);
		         //add a new Zip Entry to the ZipOutputStream
		         ZipEntry ze = new ZipEntry(sourceFile.getName());
		         zos.putNextEntry(ze);
		         //read the file and write to ZipOutputStream
		         FileInputStream fis = new FileInputStream(sourceFile);
		         byte[] buffer = new byte[1024];
		         int len;
		         while ((len = fis.read(buffer)) > 0) {
		             zos.write(buffer, 0, len);
		         }
		          
		         //Close the zip entry to write to zip file
		         zos.closeEntry();
		         //Close resources
		         zos.close();
		         fis.close();
		         fos.close();
		         
		         //upload a file
				java.io.File fileToUpload = new java.io.File("d://zipFile.zip");
				EscDocumentVersion version = client.upload(homeFolder, fileToUpload);
				EscDocument uploadedDocument = client.getDocument(version.getDocumentRecordId());
				
				System.out.println(version.getDocumentRecordId());
			    myByte=fileToUpload.getName().getBytes();
			    myByte_=uploadedDocument.getId();
			    System.out.println(uploadedDocument.getId()+uploadedDocument.getName());
			    System.out.println(uploadedDocument.getUploadPath());
			    
				} catch (Exception e){
				e.printStackTrace();
				}
	
			System.out.println(myByte_.getBytes());
			
	       // return HTTP response 200 in case of success
	        return Response.status(200).entity(myByte_.getBytes()).build();
	    }
		
	// Get File ID using File name
	public static String getFileID(String fileName,EscDocument[] docs )
	{System.out.println(docs.length);
		for( int i=0; i<docs.length;i++)
			{if(fileName.equalsIgnoreCase(docs[i].getName()))
			
				return docs[i].getId();
			}
		return null;
	}
	//Serialization and Deserialization methods
	 public static byte[] fromobjecttobyte(Person p) throws IOException
		{
			return SerializationUtils.serialize(p);
		}
	 
		public static Object frombytetoobject(byte[] bytes) throws ClassNotFoundException, IOException
		{
			return SerializationUtils.deserialize(bytes);
		}
		
	 public static byte[] serialize(Object obj) throws IOException {
	        ByteArrayOutputStream b = new ByteArrayOutputStream();
	        ObjectOutputStream o = new ObjectOutputStream(b);
	        o.writeObject(obj);
	        return b.toByteArray();
	    }
	 
	    public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
	        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
	        ObjectInputStream o = new ObjectInputStream(b);
	        return o.readObject();
	    }
}
