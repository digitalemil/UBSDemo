package de.digitalemil.hdfs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.bind.JAXBException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.ToolRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import java.net.URLDecoder;
import java.io.PrintWriter;

/**
 */
public class HDFSWriter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Path outputPath = new Path("/appstudio/messages-"+System.currentTimeMillis());
    Configuration conf = new Configuration();
	FileSystem fs= null;
    OutputStream os= null;
	long timestamp= System.currentTimeMillis();
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HDFSWriter() {
		super();
	}

	@Override
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);
		System.out.println("configured filesystem = " + conf.toString());

		try {
		 fs = FileSystem.get(conf);
  
    	if (fs.exists(outputPath)) {
            System.err.println("output path exists");
		}
		else {
			os = fs.create(outputPath);
		}	
	}
	catch(Exception e) {
		e.printStackTrace();
	}
		/*
		String appdef= "";
		Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
			if(envName.equals("APPDEF"))
				appdef= env.get(envName);
		}
		String json= appdef.replaceAll("'", "\"");
		JSONObject jobj = null;
		jobj = new JSONObject(json);
		JSONArray fields= jobj.getJSONArray("fields");
		for (int i = 0; i < fields.length(); i++) {
			JSONObject field= fields.getJSONObject(i);	
			System.out.println(field);
			System.out.println(field.get("pivot"));
			
			if(field.get("pivot").toString().toLowerCase()== "true") {
				pivotfieldname= field.getString("name");
				System.out.println("Pivot field: "+pivotfieldname);
			}
		}
		*/
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}


	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		BufferedReader reader = request.getReader();
		PrintWriter writer = response.getWriter();

		StringBuffer json = new StringBuffer();
		JSONObject jobj = null;

		do {
			String line = reader.readLine();
			if (line == null)
				break;
			json.append(line + "\n");
		} while (true);
		String jsonstring= json.toString();
		jsonstring= URLDecoder.decode(jsonstring.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
			
		if(timestamp< System.currentTimeMillis()-10*1000) {
			timestamp= System.currentTimeMillis();
			if(os != null)
				os.close();
			outputPath = new Path("/appstudio/messages-"+timestamp);
			os = fs.create(outputPath);
		}

		try {
	    if(os!= null) {
			os.write(jsonstring.getBytes());
			os.flush();
		//	os.write("\n".getBytes());
			System.out.println("Data written to HDFS: "+jsonstring);
		}
		else {
			System.err.println("Output stream == null: "+os);
		}
		} 
		catch (Exception e) {	
			e.printStackTrace();
			System.out.println("Error: "+e);
		}
	}
}
