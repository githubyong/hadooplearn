package org.robby.mr.join;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MyReduceJoin
{
    public static class MapClass extends 
        Mapper<LongWritable, Text, Text, Text>
    {
        private Text key = new Text();
        private Text value = new Text();
        private String[] keyValue = null;
        
        @Override
        protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
        {
            keyValue = value.toString().split(",", 2);
            this.key.set(keyValue[0]);
            this.value.set(keyValue[1]);
            context.write(this.key, this.value);
        }
        
    }
    
    public static class Reduce extends Reducer<Text, Text, Text, Text>
    {
        private Text value = new Text();
        
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException
        {
            StringBuilder valueStr = new StringBuilder();
            

            for(Text val : values)
            {
                valueStr.append(val);
                valueStr.append(",");
            }
            
            this.value.set(valueStr.deleteCharAt(valueStr.length()-1).toString());
            context.write(key, this.value);
        }
        
    }
    
    public static void main(String[] args) throws Exception
    {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        
        job.setJarByClass(MyReduceJoin.class);
        job.setMapperClass(MapClass.class);
        job.setReducerClass(Reduce.class);
        
        
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
