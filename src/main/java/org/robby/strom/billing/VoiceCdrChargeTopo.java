package org.robby.strom.billing;

import com.google.gson.Gson;
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
import org.robby.web.tool.MysqlUtils;
import org.robby.web.tool.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by yong on 2016/9/18.
 */
public class VoiceCdrChargeTopo {
    private static final Logger logger = LoggerFactory.getLogger(VoiceCdrChargeTopo.class);

    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("cdr-input", new CdrSpout(), 1);
        builder.setBolt("roam-bolt", new RoamBolt(), 1).shuffleGrouping("cdr-input");//计算长途和漫游
        builder.setBolt("charge-bolt", new ChargeBolt(), 1).shuffleGrouping("roam-bolt");//计费
        builder.setBolt("bolt-indb", new IndbBolt(), 1).shuffleGrouping("charge-bolt");
        Config conf = new Config();
        conf.put("redis.host", "hadoop1");

        if (args != null && args.length > 0) {//集群模式
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {//本地模式
            LocalCluster cluster = new LocalCluster();
            System.out.println("start cdr test ...");
            cluster.submitTopology("local cdr test", conf, builder.createTopology());
        }
    }

    private static class CdrSpout extends BaseRichSpout {

        SpoutOutputCollector spoutOutputCollector;
        private static final String QUEUE_NAME = "cdr-list";

        private Gson gson;

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("cdr"));
        }

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.spoutOutputCollector = spoutOutputCollector;
            System.out.println("############### CdrSpout prepare");
            gson = new Gson();
        }

        @Override
        public void nextTuple() {
            long len = 0;
            len = RedisTool.llen(QUEUE_NAME);
            if (len == 0) {
                Utils.sleep(500);
            } else {
                String s = RedisTool.rpop(QUEUE_NAME);
                VoiceCdr cdr = gson.fromJson(s, VoiceCdr.class);
                logger.info("CdrSpout emit {}", s);
                spoutOutputCollector.emit(new Values(cdr));

            }
        }
    }

    private static class IndbBolt extends BaseBasicBolt {

        String url = "jdbc:mysql://hadoop1:3306/cdr?allowMultiQueries=true&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
        String driver = "com.mysql.jdbc.Driver";
        String username = "root";
        String passwd = "123";

        Connection conn;

        @Override
        public void prepare(Map stormConf, TopologyContext context) {
            try {
                conn = MysqlUtils.getConnection(url, driver, username, passwd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
            VoiceCdr cdr = (VoiceCdr) tuple.getValue(0);
//            logger.info("indb-bolt receive bolt -------> {}", cdr);
            String insert = "INSERT INTO `tab_cdr` (`org_msisdn`, `dst_msisdn`, `call_type`, `org_ac`, `visit_ac`, `dst_ac`," +
                    " `roam_type`, `long_type`, `charge_rule`, `dt`, `duration`, `fee`) " +
                    "VALUES (%s, %s, %s, '%s', '%s', '%s', %s, %s, %s, %s, %s, %s);";
            Statement statement;
            try {
                statement = conn.createStatement();
                String sql = String.format(insert, cdr.org_msisdn, cdr.dst_misisdn, cdr.call_type, cdr.org_ac, cdr.visit_ac, cdr.dst_ac,
                        cdr.roam_type, cdr.long_type, cdr.charge_rule.toString(), cdr.dt, cdr.duration, cdr.fee);
                statement.execute(sql);
                logger.info("indb-bolt insert ---->{}", sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            basicOutputCollector.emit(new Values(cdr));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("cdr"));
        }
    }

    private static class RoamBolt extends BaseBasicBolt {
        Charging charging;

        @Override
        public void prepare(Map stormConf, TopologyContext context) {
            charging = new Charging();
        }

        @Override
        public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
            VoiceCdr cdr = (VoiceCdr) tuple.getValue(0);
            logger.info("roam-bolt receive -------> {}", cdr);
            charging.calRoamAndLong(cdr);
            basicOutputCollector.emit(new Values(cdr));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("cdr"));
        }
    }

    private static class ChargeBolt extends BaseBasicBolt {
        Charging charging;

        @Override
        public void prepare(Map stormConf, TopologyContext context) {
            charging = new Charging();
        }

        @Override
        public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
            VoiceCdr cdr = (VoiceCdr) tuple.getValue(0);
            charging.calFee(cdr);
            logger.info("charge-bolt receive -------> {}", cdr);
            basicOutputCollector.emit(new Values(cdr));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("cdr"));
        }
    }
}
