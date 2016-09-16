package org.robby.mr.join;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
 

public class MyReduceJoin2
{
    public static class Map extends 
        Mapper<LongWritable, Text, Text, CombineValues>
    {
    	private CombineValues combineValues = new CombineValues();
        private Text flag = new Text();
        private Text key = new Text();
        private Text value = new Text();
        private String[] keyValue = null;
        
        @Override
        protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
        {
        	String pathName = ((FileSplit) context.getInputSplit()).getPath().toString();
        	if(pathName.endsWith("input1.txt"))
        		flag.set("0");
        	else
        		flag.set("1");
        	
        	combineValues.setFlag(flag);
            keyValue = value.toString().split(",", 2);
            combineValues.setJoinKey(new Text(keyValue[0]));
            combineValues.setSecondPart(new Text(keyValue[1]));

            this.key.set(keyValue[0]);
            context.write(this.key, combineValues);
        }
        
    }
    
    public static class Reduce extends Reducer<Text, CombineValues, Text, Text>
    {
        private Text value = new Text();
        private Text left = new Text();
        private ArrayList<Text> right = new ArrayList<Text>();
        
        @Override
        protected void reduce(Text key, Iterable<CombineValues> values, Context context)
                throws IOException, InterruptedException
        {
        	right.clear();
            for(CombineValues val : values)
            {
            	System.out.println("val:" + val.toString());
            	Text secondPar = new Text(val.getSecondPart().toString());
            	if(val.getFlag().toString().equals("0")){
            		left.set(secondPar);
            	}
            	else{
            		right.add(secondPar);
            	}
            }
            
            for(Text t : right){
            	Text output = new Text(left.toString() + "," + t.toString());
                context.write(key, output);
            }
            
        }
        
    }
    
    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        
        job.setJarByClass(MyReduceJoin2.class);
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(CombineValues.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        
        Path outputPath = new Path(args[1]);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, outputPath);
        outputPath.getFileSystem(conf).delete(outputPath, true);  

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
