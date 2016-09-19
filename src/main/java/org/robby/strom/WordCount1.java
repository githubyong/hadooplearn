package org.robby.strom;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.*;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by yong on 2016/9/18.
 */
public class WordCount1 {
    private static final Logger logger = LoggerFactory.getLogger(WordCount1.class);

    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("input", new RandomSentenceSpout(), 1);
        builder.setBolt("bolt_sencence", new SplitSentence(), 1).shuffleGrouping("input");
        //fieldsGrouping 对应 上面这个 bolt_sencence 和 它的output 的 declareOutputFields
        builder.setBolt("bolt_wordcount", new WordConter(), 1).fieldsGrouping("bolt_sencence", new Fields("word"));
        Config conf = new Config();
        if (args != null && args.length > 0) {//集群模式
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {//本地模式
            LocalCluster cluster = new LocalCluster();
            System.out.println("start local word count ...");
            cluster.submitTopology("local word count", conf, builder.createTopology());
        }
    }

    private static class RandomSentenceSpout extends BaseRichSpout {

        SpoutOutputCollector spoutOutputCollector;
        Random random;


        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("scence"));
        }

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.spoutOutputCollector = spoutOutputCollector;
            random = new Random();
        }

        @Override
        public void nextTuple() {
            String[] sentences = new String[]{"this is a strom test",
                    "hello storm",
                    "hello xiaodai"};
            String s = sentences[random.nextInt(sentences.length)];
            logger.info(s);
            spoutOutputCollector.emit(new Values(s));
            Utils.sleep(1000);
        }
    }

    public static class SplitSentence extends BaseBasicBolt {

        @Override
        public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
            String s = tuple.getString(0);
            String[] words = s.split(" ");
            for (String word : words) {
                word = word.trim();
                if (!word.isEmpty()) {
                    basicOutputCollector.emit(new Values(word));
                }
            }
            //ack 自动完成，如果是fail 需要抛出特定的异常
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    }

    private static class WordConter extends BaseBasicBolt {

        Map<String, Integer> countMap = new HashMap<>();

        @Override
        public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
            String w = tuple.getString(0);
            Integer count = countMap.get(w);
            if (count == null) {
                count = 0;
            }
            countMap.put(w, ++count);
            basicOutputCollector.emit(new Values("word", count));
            System.out.println(w + ":" + count);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word", "count"));
        }
    }
}
