package org.robby.strom;

import com.google.gson.Gson;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.drpc.LinearDRPCTopologyBuilder;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.robby.hbase.Post;
import org.robby.web.tool.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by yong on 2016/9/18.
 */
public class DrpcTest {
    private static final Logger logger = LoggerFactory.getLogger(DrpcTest.class);

    public static void main(String[] args) throws Exception {
        LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder("func");
        builder.addBolt(new TestBolt(), 1);
        Config conf = new Config();

        Post post = new Post("usr1", "test contnet ", "2016-09-18");

        if (args != null && args.length > 0) {//集群模式
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(args[0], conf, builder.createRemoteTopology());
        } else {//本地模式
            LocalCluster cluster = new LocalCluster();

            System.out.println("start queue test ...");
            LocalDRPC drpc = new LocalDRPC();
            Gson gson = new Gson();
            cluster.submitTopology("local queue test", conf, builder.createLocalTopology(drpc));
            System.err.println(drpc.execute("func", gson.toJson(post)));
            drpc.shutdown();
        }
    }


    private static class TestBolt extends BaseBasicBolt {

        @Override
        public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
            //0 id ; 1 val
            long id = tuple.getLong(0);
            String s = tuple.getString(1);
            logger.info(" receive bolt -------> {}", s);
            basicOutputCollector.emit(new Values(id, s + " =="));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            //drpc 最后一个bolt 必须是"id","result"
            outputFieldsDeclarer.declare(new Fields("id", "result"));
        }
    }
}
