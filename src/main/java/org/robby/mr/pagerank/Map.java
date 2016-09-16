package org.robby.mr.pagerank;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class Map
        extends Mapper<Text, Text, Text, Text> {

    private Text outKey = new Text();
    private Text outValue = new Text();

    @Override
    protected void map(Text key, Text value, Context context)
            throws IOException, InterruptedException {

        //把初始信息写入 即 “A  0.25     B       D”这样的信息，在reduce中需要更新初始节点的信息并写入到输出，作为下次mapreduce的输入
        //和下面的map输出结果不同，map输出结果是 把key的权重分给它的节点之后的，各个节点的权重
        context.write(key, value);
        System.out.println("  original ===> K[" + key + "],V[" + value + "]");

        org.robby.mr.pagerank.Node node = org.robby.mr.pagerank.Node.fromMR(value.toString());
        System.out.println("node ============ " + node);
        if (node.getAdjacentNodeNames() != null && node.getAdjacentNodeNames().length > 0) {
            //把当前的节点PR权重均分给它的节点
            double outboundPageRank = node.getPageRank() / (double) node.getAdjacentNodeNames().length;
            for (int i = 0; i < node.getAdjacentNodeNames().length; i++) {
                String neighbor = node.getAdjacentNodeNames()[i];
                outKey.set(neighbor);
                org.robby.mr.pagerank.Node adjacentNode = new Node().setPageRank(outboundPageRank);
                outValue.set(adjacentNode.toString());
                System.out.println("  output -> K[" + outKey + "],V[" + outValue + "]");
                context.write(outKey, outValue);
            }
        }
    }
}
