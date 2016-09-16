package org.robby.mr;

import org.apache.hadoop.conf.Configuration;  
import org.apache.hadoop.fs.Path;  
import org.apache.hadoop.io.Text;  
import org.apache.hadoop.mapreduce.Job;  
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;  
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;  
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;  
import org.apache.hadoop.mapreduce.lib.partition.InputSampler;  
import org.apache.hadoop.mapreduce.lib.partition.InputSampler.IntervalSampler;
import org.apache.hadoop.mapreduce.lib.partition.InputSampler.RandomSampler;  
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;


public class TotalSortMR { 
      
    @SuppressWarnings("deprecation")
	public static int runTotalSortJob(String[] args) throws Exception {  
        Path inputPath = new Path(args[0]);  
        Path outputPath = new Path(args[1]);  
        Path partitionFile = new Path(args[2]);  
        int reduceNumber = Integer.parseInt(args[3]);  
          

        //
        RandomSampler<Text, Text> sampler = new InputSampler.RandomSampler<Text, Text>(1, 3000, 10);
        //IntervalSampler<Text, Text> sampler = new InputSampler.IntervalSampler<Text, Text>(0.333, 10);

        
        Configuration conf = new Configuration();  
        TotalOrderPartitioner.setPartitionFile(conf, partitionFile);  
        
        Job job = Job.getInstance(conf);
        
        
        job.setJobName("Total-Sort");  
        job.setJarByClass(TotalSortMR.class);  
        job.setInputFormatClass(KeyValueTextInputFormat.class);  
        job.setMapOutputKeyClass(Text.class);  
        job.setMapOutputValueClass(Text.class);  
        job.setNumReduceTasks(reduceNumber);  

        
        //
        job.setPartitionerClass(TotalOrderPartitioner.class);  
          
        FileInputFormat.setInputPaths(job, inputPath);  
        FileOutputFormat.setOutputPath(job, outputPath);  
        outputPath.getFileSystem(conf).delete(outputPath, true);  
          
        //
        InputSampler.writePartitionFile(job, sampler); 
         
        return job.waitForCompletion(true)? 0 : 1;
        //return 0;
    }  
      
    public static void main(String[] args) throws Exception{  
        System.exit(runTotalSortJob(args));  
    }
}
