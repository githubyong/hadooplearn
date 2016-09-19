package org.robby.strom;

import org.apache.storm.Config;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;
import org.apache.storm.utils.Utils;
import org.robby.hbase.Post;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yong on 2016/9/18.
 */
public class DrpcClientTest {
    public static void main(String[] args) throws Exception {
        Config conf = new Config();
     /*   conf.setDebug(false);
        conf.put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
        conf.put(Config.STORM_NIMBUS_RETRY_TIMES, 3);
        conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL, 10);
        conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL_CEILING, 20);
        conf.put(Config.DRPC_MAX_BUFFER_SIZE, 1048576);*/

        Map defaultConfig = Utils.readDefaultConfig();
        conf.putAll(defaultConfig);
        System.out.println(conf);
        DRPCClient client = new DRPCClient(conf, "hadoop1", 3772);
        System.err.println(client.execute("func", "hello storm"));
        client.close();
    }
}
