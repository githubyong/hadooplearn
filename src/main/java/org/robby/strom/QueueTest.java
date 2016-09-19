package org.robby.strom;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
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
import org.robby.web.tool.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by yong on 2016/9/18.
 */
public class QueueTest {
    private static final Logger logger = LoggerFactory.getLogger(QueueTest.class);

    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("input", new QueSpout(), 1);
        builder.setBolt("queue_test", new TestBolt(), 1).shuffleGrouping("input");
        Config conf = new Config();
        conf.put("redis.host", "hadoop1");

        if (args != null && args.length > 0) {//集群模式
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {//本地模式
            LocalCluster cluster = new LocalCluster();
            System.out.println("start queue test ...");
            cluster.submitTopology("local queue test", conf, builder.createTopology());
        }
    }

    private static class QueSpout extends BaseRichSpout {

        SpoutOutputCollector spoutOutputCollector;


        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("msg"));
        }

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.spoutOutputCollector = spoutOutputCollector;
            String host = (String) map.get("redis.host");
            RedisTool.CreateJedisObj(host);
        }

        @Override
        public void nextTuple() {
            long len = 0;
            len = RedisTool.llen("storm.queue");
            if (len == 0) {
                Utils.sleep(500);
            } else {
                String s = RedisTool.rpop("storm.queue");
                logger.info("emit {}", s);
                spoutOutputCollector.emit(new Values(s));

            }
        }
    }

    private static class TestBolt extends BaseBasicBolt {

        @Override
        public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
            String s = tuple.getString(0);
            logger.info(" receive bolt -------> {}", s);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    }
}
