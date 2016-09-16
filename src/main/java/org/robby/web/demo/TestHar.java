package org.robby.web.demo;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.HarFileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

public class TestHar {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		conf.set("fs.default.name", "hdfs://hadoop1:9000");
		HarFileSystem hdfs = new HarFileSystem();
 
		hdfs.initialize(new URI("har:///test.har"), conf);
		Path dst = new Path("/test.har");

		RemoteIterator<LocatedFileStatus> it = hdfs.listFiles(dst, false);
		while (it.hasNext()) {
			LocatedFileStatus st = it.next();
			System.out.println(st.toString());
		}
	}

}
