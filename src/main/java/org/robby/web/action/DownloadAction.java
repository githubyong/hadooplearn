package org.robby.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.struts2.ServletActionContext;
import org.robby.web.tool.GlobalDef;


public class DownloadAction {
	private String basePath = ServletActionContext.getServletContext().getRealPath("/uploads/");
	private String fileName;
	
	
	public String execute(){
		return "success";
	}
	
	public InputStream getInputStream() throws Exception{
		if(GlobalDef.useHdfs){
			Configuration conf = new Configuration();
			conf.set("fs.default.name", GlobalDef.HdfsHost);
			FileSystem hdfs = FileSystem.get(conf);
			
			Path src = new Path("/data/" + fileName);
			Path dst = new Path(basePath);
			
			System.out.println("hdfs cpToLocal src:" + src.getName());
			System.out.println("hdfs cpToLocal dst:" + basePath);
			hdfs.copyToLocalFile(false, src, dst, true);
		}
		
		FileInputStream fi = new FileInputStream(new File(basePath, fileName));
		return fi;
	}
	
	public String getFileName() throws UnsupportedEncodingException {
		return new String(fileName.getBytes(), "ISO-8859-1");
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}