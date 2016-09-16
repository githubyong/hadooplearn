package org.robby.mr.join;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MapJoinWithCache {
	public static class Map extends
			Mapper<LongWritable, Text, Text, Text> {
		private CombineValues combineValues = new CombineValues();
		private Text flag = new Text();
		private Text key = new Text();
		private Text value = new Text();
		private String[] keyValue = null;
		private HashMap<String, String> keyMap = null;

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			keyValue = value.toString().split(",", 2);

			String name = keyMap.get(keyValue[0]);
			
			this.key.set(keyValue[0]);
			
			String output = name + "," + keyValue[1];
			this.value.set(output);
			context.write(this.key, this.value);
		}

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			URI[] localPaths = context.getCacheFiles();
			
			keyMap = new HashMap<String, String>();
			for(URI url : localPaths){
			     FileSystem fs = FileSystem.get(URI.create("hdfs://hadoop1:9000"), context.getConfiguration());
			     FSDataInputStream in = null;
			     in = fs.open(new Path(url.getPath()));
			     BufferedReader br=new BufferedReader(new InputStreamReader(in));
			     String s1 = null;
			     while ((s1 = br.readLine()) != null)
			     {
			    	 keyValue = s1.split(",", 2);
			    	 
			    	 keyMap.put(keyValue[0], keyValue[1]);
			         System.out.println(s1);
			     }
			     br.close();
			}
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {


		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			
			for(Text val : values)
				context.write(key, val);
			
		}

	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);

		job.setJarByClass(MapJoinWithCache.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		Path outputPath = new Path(args[1]);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, outputPath);
		outputPath.getFileSystem(conf).delete(outputPath, true);

		job.addCacheFile(new Path(args[2]).toUri());

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

