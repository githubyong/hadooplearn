package org.robby.strom;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
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
public class WordCount {
    private static final Logger logger = LoggerFactory.getLogger(WordCount.class);

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

    private static class RandomSentenceSpout implements IRichSpout {

        SpoutOutputCollector spoutOutputCollector;
        Random random;

        /**
         * 定义 数据 tuple 中的 key (发射数据的key)
         *
         * @param outputFieldsDeclarer
         */
        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("scence"));
        }

        @Override
        public Map<String, Object> getComponentConfiguration() {
            return null;
        }

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.spoutOutputCollector = spoutOutputCollector;
            random = new Random();
        }

        @Override
        public void close() {

        }

        @Override
        public void activate() {

        }

        @Override
        public void deactivate() {

        }

        /**
         * 产生新的数据
         */
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

        @Override
        public void ack(Object o) {

        }

        @Override
        public void fail(Object o) {

        }
    }

    public static class SplitSentence implements IRichBolt {
        OutputCollector outputCollector;

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.outputCollector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String s = tuple.getString(0);
            String[] words = s.split(" ");
            for (String word : words) {
                word = word.trim();
                if (!word.isEmpty()) {
                    this.outputCollector.emit(new Values(word));
                }
            }
            outputCollector.ack(tuple);
        }

        @Override
        public void cleanup() {

        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }

        @Override
        public Map<String, Object> getComponentConfiguration() {
            return null;
        }
    }

    private static class WordConter implements IRichBolt {

        OutputCollector outputCollector;
        Map<String, Integer> countMap = new HashMap<>();

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.outputCollector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String w = tuple.getString(0);
            Integer count = countMap.get(w);
            if (count == null) {
                count = 0;
            }
            countMap.put(w, ++count);
            outputCollector.emit(new Values("word", count));
            System.out.println(w + ":" + count);
            outputCollector.ack(tuple);
        }

        @Override
        public void cleanup() {

        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word", "count"));
        }

        @Override
        public Map<String, Object> getComponentConfiguration() {
            return null;
        }
    }
}
