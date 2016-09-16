package org.robby.web.demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.io.SequenceFile.Writer.Option;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.robby.web.tool.GlobalDef;

public class TestSeqFile {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		conf.set("fs.default.name", GlobalDef.HdfsHost);

		Text key = new Text();
		Text value = new Text();

		String fileName = "/data/testseqfile";
		Path path = new Path(fileName);
		Option optPath = SequenceFile.Writer.file(path);
		Option optKey = SequenceFile.Writer.keyClass(key.getClass());
		Option optVal = SequenceFile.Writer.valueClass(value.getClass());
		

		SequenceFile.Writer writer = SequenceFile.createWriter(conf, optPath,
				optKey, optVal);

		writer.append(new Text("key1"), new Text("value1"));
		writer.append(new Text("key2"), new Text("value2"));
		writer.close();

		SequenceFile.Reader reader = new SequenceFile.Reader(conf,
				Reader.file(path));

		key = new Text();
	    Text val = new Text();
	    
		while (reader.next(key, val)) {
			System.out.println(key + "\t" + val);
		}

		reader.close();
	}

}
