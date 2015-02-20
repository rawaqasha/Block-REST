package com.crunchify.tutorials;
import	java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.connexience.server.util.SerializationUtils;

//import org.json.JSONObject;
public class CrunchifyRESTServiceClient {
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
	public static byte[] fromobjecttobyte(Person p) throws IOException
	{
		return SerializationUtils.serialize(p);
	}
	public static Object frombytetoobject(byte[] bytes) throws ClassNotFoundException, IOException
	{
		return SerializationUtils.deserialize(bytes);
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Person person=new Person("aaa","bbb");
		System.out.println(person.getFirstName()+person.getLastName());
       byte[] myBytes = SerializationUtils.serialize(person);
       System.out.println(myBytes.toString());
     //   String s1=myBytes.toString();
    //    person = (Person) frombytetoobject(s1.getBytes());
    //    System.out.println(person.getFirstName()+person.getLastName());
		// TODO Auto-generated method stub
		//String string = "";
      /*  try {
 
            // Step1: Let's 1st read file from fileSystem
            InputStream crunchifyInputStream = new FileInputStream(
                    "/Users/<username>/Documents/crunchify-git/JSONFile.txt");
            InputStreamReader crunchifyReader = new InputStreamReader(crunchifyInputStream);
            BufferedReader br = new BufferedReader(crunchifyReader);
            String line;
            while ((line = br.readLine()) != null) {
                string += line + "\n";
            }
 */try{
            //JSONObject jsonObject = new JSONObject("{\"phonetype\":\"N95\",\"cat\":\"WP\"}");
            //System.out.println(jsonObject);
            //String st="fhjfhdkjhfj";
            // Step2: Now pass JSON File Data to REST Service
            try {
                URL url = new URL("http://localhost:8080/CrunchifyTutorials/api/crunchifyService");
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "text/plain");
                //connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
             //   ByteArrayOutputStream out =new ByteArrayOutputStream();
           //     OutputStreamWriter out_ = new OutputStreamWriter(connection.getOutputStream());
               // out_.write();
                //out.write(jsonObject.toString());
                //out_.close();
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(myBytes);
                outputStream.flush();
                byte[] myByte=new byte[1000];
                System.out.println("before");
                
                InputStream inputStream=connection.getInputStream();
                inputStream.read(myByte);
                System.out.println("after");
               /* BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String s=null;
                while ((s=in.readLine()) != null) {System.out.println("\nREST Service Invoked Successfully..\n"+s);
                }
                */
               // in.close();
                System.out.println(myByte.toString());
                person=(Person) SerializationUtils.deserialize(myByte);
                System.out.println(person.getFirstName()+person.getLastName());
            } catch (Exception e) {
                System.out.println("\nError while calling REST Service");
                System.out.println(e);
            }
 
        //    br.close();
      } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	   

}
