package org.robby.mr.pagerank;


import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.robby.mr.pagerank.Map;
import org.robby.mr.pagerank.Node;
import org.robby.mr.pagerank.Reduce;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public final class Main {
    /**
     * 1,   通过/input/grah.txt生成/output/input.txt作为第一次MR的输入
     * 2，  (/output/input.txt, /output/1)
     * 3,   (/output/1, /output/2
     * 4,   (/output/2, /output/3)
     * 5,   (/output/3, /output/4)
     */


    public static void main(String... args) throws Exception {

        String inputFile = args[0];
        String outputDir = args[1];

        iterate(inputFile, outputDir);
    }

    public static void iterate(String input, String output)
            throws Exception {

        Configuration conf = new Configuration();
        Path outputPath = new Path(output);
        outputPath.getFileSystem(conf).delete(outputPath, true);
        outputPath.getFileSystem(conf).mkdirs(outputPath);

        Path inputPath = new Path(outputPath, "input.txt");

        int numNodes = createInputFile(new Path(input), inputPath);

        int iter = 1;
        double desiredConvergence = 0.01;//当德尔塔 △ 平均变化率 小于该值时结束任务

        while (true) {

            Path jobOutputPath = new Path(outputPath, String.valueOf(iter));

            System.out.println("======================================");
            System.out.println("=  Iteration:    " + iter);
            System.out.println("=  Input path:   " + inputPath);
            System.out.println("=  Output path:  " + jobOutputPath);
            System.out.println("======================================");

            if (calcPageRank(inputPath, jobOutputPath, numNodes) < desiredConvergence) {
                System.out.println("Convergence is below " + desiredConvergence + ", we're done");
                break;
            }
            inputPath = jobOutputPath;
            iter++;
        }
    }

    /**
     * 把输入的文件按page个数写成格式化的输入文件，返回page个数
     * （数据文件）
     * A  B  D
     * B  C
     * C  A  B
     * D  B  C
     * (生成初始的map输入)
     * A  0.25  B  D
     * B  0.25  C
     * C  0.25  A  B
     * D  0.25  B  C
     *
     * @param file
     * @param targetFile
     * @return
     * @throws IOException
     */
    public static int createInputFile(Path file, Path targetFile)
            throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = file.getFileSystem(conf);

        int numNodes = getNumNodes(file);
        double initialPageRank = 1.0 / (double) numNodes;

        OutputStream os = fs.create(targetFile);
        LineIterator iter = IOUtils
                .lineIterator(fs.open(file), "UTF8");

        while (iter.hasNext()) {
            String line = iter.nextLine();

            String[] parts = StringUtils.split(line);

            org.robby.mr.pagerank.Node node = new Node().setPageRank(initialPageRank)
                    .setAdjacentNodeNames(Arrays.copyOfRange(parts, 1, parts.length));
            IOUtils.write(parts[0] + '\t' + node.toString() + '\n', os);
        }
        os.close();
        return numNodes;
    }

    /**
     * 返回节点数(数据文件的行数)
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static int getNumNodes(Path file) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = file.getFileSystem(conf);

        return IOUtils.readLines(fs.open(file), "UTF8").size();
    }

    /**
     *进行mapreduce计算，返回该次计算结果的平均变化率，output将作为下次mapreduce的输入
     *
     * @param inputPath
     * @param outputPath
     * @param numNodes
     * @return
     * @throws Exception
     */
    public static double calcPageRank(Path inputPath, Path outputPath, int numNodes)
            throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(Reduce.CONF_NUM_NODES_GRAPH, numNodes);

        Job job = Job.getInstance(conf);
        job.setJarByClass(Main.class);
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setInputFormatClass(KeyValueTextInputFormat.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        if (!job.waitForCompletion(true)) {
            throw new Exception("Job failed");
        }

        long summedConvergence = job.getCounters().findCounter(
                Reduce.Counter.CONV_DELTAS).getValue();
        double convergence = ((double) summedConvergence / Reduce.CONVERGENCE_SCALING_FACTOR) / (double) numNodes;

        System.out.println("======================================");
        System.out.println("=  Num nodes:           " + numNodes);
        System.out.println("=  Summed convergence:  " + summedConvergence);
        System.out.println("=  Convergence:         " + convergence);
        System.out.println("======================================");

        return convergence;
    }


}

