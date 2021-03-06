package org.robby.mr.join;

import org.apache.commons.collections.IteratorUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zengzhaozheng
 *         用途说明：
 *         reudce side join中的left outer join
 *         左连接，两个文件分别代表2个表,连接字段table1的id字段和table2的cityID字段
 *         table1(左表):tb_dim_city(id int,name string,orderid int,city_code,is_show)
 *         tb_dim_city.dat文件内容,分隔符为"|"：
 *         id     name  orderid  city_code  is_show
 *         0       其他        9999     9999         0
 *         1       长春        1        901          1
 *         2       吉林        2        902          1
 *         3       四平        3        903          1
 *         4       松原        4        904          1
 *         5       通化        5        905          1
 *         6       辽源        6        906          1
 *         7       白城        7        907          1
 *         8       白山        8        908          1
 *         9       延吉        9        909          1
 *         -------------------------风骚的分割线-------------------------------
 *         table2(右表)：tb_user_profiles(userID int,userName string,network string,double flow,cityID int)
 *         tb_user_profiles.dat文件内容,分隔符为"|"：
 *         userID   network     flow    cityID
 *         1           2G       123      1
 *         2           3G       333      2
 *         3           3G       555      1
 *         4           2G       777      3
 *         5           3G       666      4
 *         <p>
 *         -------------------------风骚的分割线-------------------------------
 *         结果：
 *         1   长春  1   901 1   1   2G  123
 *         1   长春  1   901 1   3   3G  555
 *         2   吉林  2   902 1   2   3G  333
 *         3   四平  3   903 1   4   2G  777
 *         4   松原  4   904 1   5   3G  666
 */
public class ReduceJoin {
    private static final Logger logger = LoggerFactory.getLogger(ReduceJoin.class);

    public static class LeftOutJoinMapper extends Mapper<Object, Text, Text, CombineValues> {
        private CombineValues combineValues = new CombineValues();
        private Text flag = new Text();
        private Text joinKey = new Text();
        private Text secondPart = new Text();

        @Override
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            //获得文件输入路径   
            String pathName = ((FileSplit) context.getInputSplit()).getPath().toString();
            //数据来自tb_dim_city.dat文件,标志即为"0"
            if (pathName.endsWith("tb_dim_city.dat")) {
                String[] valueItems = value.toString().split(",");
                //过滤格式错误的记录
                if (valueItems.length != 5) {
                    return;
                }
                flag.set("0");
                joinKey.set(valueItems[0]);
                secondPart.set(valueItems[1] + "\t" + valueItems[2] + "\t" + valueItems[3] + "\t" + valueItems[4]);
                combineValues.setFlag(flag);
                combineValues.setJoinKey(joinKey);
                combineValues.setSecondPart(secondPart);
                context.write(combineValues.getJoinKey(), combineValues);

            } else if (pathName.endsWith("tb_user_profiles.dat")) {//数据来自于tb_user_profiles.dat，标志即为"1"
                String[] valueItems = value.toString().split(",");
                //过滤格式错误的记录   
                if (valueItems.length != 4) {
                    return;
                }

                flag.set("1");
                joinKey.set(valueItems[3]);
                secondPart.set(valueItems[0] + "\t" + valueItems[1] + "\t" + valueItems[2]);
                combineValues.setFlag(flag);
                combineValues.setJoinKey(joinKey);
                combineValues.setSecondPart(secondPart);
                context.write(combineValues.getJoinKey(), combineValues);
            }
        }
    }

    public static class LeftOutJoinReducer extends Reducer<Text, CombineValues, Text, Text> {
        //存储一个分组中的左表信息   
        private ArrayList<Text> leftTable = new ArrayList<Text>();
        //存储一个分组中的右表信息   
        private ArrayList<Text> rightTable = new ArrayList<Text>();
        private Text secondPar = null;
        private Text output = null;

        private Map<Text, ArrayList<Text>> joinTable = new HashMap<>();

        /**
         * 一个分组调用一次reduce函数
         */
        @Override
        protected void reduce(Text key, Iterable<CombineValues> value, Context context)
                throws IOException, InterruptedException {

//            logger.info("########### key = {} val = {}",key, IteratorUtils.toList(value.iterator()));
            leftTable.clear();
            rightTable.clear();
            output = new Text();
            /**
             * 将分组中的元素按照文件分别进行存放   
             * 这种方法要注意的问题：   
             * 如果一个分组内的元素太多的话，可能会导致在reduce阶段出现OOM，   
             * 在处理分布式问题之前最好先了解数据的分布情况，根据不同的分布采取最   
             * 适当的处理方法，这样可以有效的防止导致OOM和数据过度倾斜问题。   
             */
            for (CombineValues cv : value) {
                secondPar = new Text(cv.getSecondPart().toString());
                //左表tb_dim_city   
                if ("0".equals(cv.getFlag().toString().trim())) {
                    leftTable.add(secondPar);
                }
                //右表tb_user_profiles   
                else if ("1".equals(cv.getFlag().toString().trim())) {
                    rightTable.add(secondPar);
                }
            }

            logger.info(String.format("key = %s lefttab = %s|\t righttab = %s", key, leftTable, rightTable));
//            logger.info("tb_dim_city: k"  + leftTable.toString() + " key = " + key);
//            logger.info("tb_user_profiles:" + rightTable.toString() + "  key = " + key);
            for (Text leftPart : leftTable) {
                for (Text rightPart : rightTable) {
                    output.set(leftPart + "\t" + rightPart);
                    context.write(key, output);
                }
            }


            logger.info("------------ reduce key ={}  vals ={}, out = {}", key, IteratorUtils.toList(value.iterator()), output);
        }

        ArrayList<Text> getTab(Text key) {
            if (!this.joinTable.containsKey(key)) {
                this.joinTable.put(key, new ArrayList<Text>());
            }
            return this.joinTable.get(key);

        }

    }


    public static void main(String[] args) throws IOException,
            ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(MyReduceJoin1.class);
        job.setMapperClass(LeftOutJoinMapper.class);
        job.setReducerClass(LeftOutJoinReducer.class);

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