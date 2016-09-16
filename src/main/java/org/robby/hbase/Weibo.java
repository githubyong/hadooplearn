package org.robby.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yong on 2016/9/11.
 */
public class Weibo {
    private static final Logger logger = LoggerFactory.getLogger(Weibo.class);

    public static final String tab_floow = "tab_floow";
    public static final String tab_floowed = "tab_floowed";//被关注
    //id ->content
    public static final String tab_weibo = "tab_weibo";
    public static final String tab_inbox = "tab_inbox";

    private static AtomicInteger postconter = new AtomicInteger();

    public static String col_cf = "cdr";

    public void createTable() throws IOException {
//        this.createTable(tab_floow, col_cf);
//        this.createTable(tab_floowed, col_cf);
        this.createTable(tab_weibo, col_cf);
        this.createTable(tab_inbox, col_cf);
    }

    /**
     * 发送weibo
     *
     * @param sender
     * @param content
     */
    public void post(String sender, String content) throws IOException {
        Date d = new Date();
        String id = getPostId(d);
        String dt = getDT(d);
        WeiBoData.Post.Builder postBuild = WeiBoData.Post.newBuilder();
        postBuild.setSender(sender);
        postBuild.setContent(content);
        postBuild.setDt(dt);

        //insert tab_weibo
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        HTable table = new HTable(conf, tab_weibo);
        Put p = new Put(id.getBytes());
        p.addColumn(col_cf.getBytes(), col_cf.getBytes(), postBuild.build().toByteArray());
        table.put(p);
        table.close();

        //insert tab_inbox  遍历tab_floowed中该用户的floowed
        table = new HTable(conf, tab_floowed);
        Get g = new Get(sender.getBytes());
        Result result = table.get(g);

        List<Put> putList = new ArrayList<>();
        p = new Put(sender.getBytes());
        p.addColumn(col_cf.getBytes(), getDT(new Date()).getBytes(), id.getBytes());
        putList.add(p);
        if (!result.isEmpty()) {
            for (Cell cell : result.listCells()) {
                p = new Put(cell.getValue());
                p.addColumn(col_cf.getBytes(), getDT(new Date()).getBytes(), id.getBytes());
                putList.add(p);
            }
        }
        table.close();
        table = new HTable(conf, tab_inbox);
        table.put(putList);
        table.close();
    }

    public List<Post> getPost(String name) throws IOException {
        List<Post> list = new ArrayList<>();
        //先从inbox获取id，再遍历取出 weibo
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        HTable table = new HTable(conf, tab_inbox);
        Get get = new Get(name.getBytes());
        Result inboxs = table.get(get);
        List<Get> getlist = new ArrayList<>();
        if (!inboxs.isEmpty()) {
            for (Cell cell : inboxs.listCells()) {
                Get g = new Get(cell.getValue());//inbox 的id
                getlist.add(g);
            }
        }
        table.close();
        table = new HTable(conf, tab_weibo);
        Result[] rs = table.get(getlist);
        for (Result r : rs) {
            //关于Result.getColumnLatestCell 和 Result.listCells  初步感觉是 rowkey 唯一时用前者，多个时用后者
            Cell cell = r.getColumnLatestCell(col_cf.getBytes(), col_cf.getBytes());
            WeiBoData.Post post = WeiBoData.Post.parseFrom(cell.getValue());
            list.add(0,new Post(post.getSender(), post.getContent(), post.getDt()));
        }
        return list;
    }

    private String getDT(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    private String getPostId(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String t = sdf.format(d) + "_" + postconter.getAndIncrement();
        return t;
    }

    public void createTable(String tab_name, String cf) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");

        HBaseAdmin admin = new HBaseAdmin(conf);
        if (admin.tableExists(tab_name)) {
            admin.disableTable(tab_name);
            admin.deleteTable(tab_name);
        }
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("default", tab_name));
        HColumnDescriptor cf1 = new HColumnDescriptor(cf);
        htd.addFamily(cf1);
        admin.createTable(htd);
    }

    /**
     *
     */

    public void floow(String usr1, String usr2) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        //关注表
        HTable table = new HTable(conf, tab_floow);
        Put p = new Put(usr1.getBytes());
        p.addColumn(col_cf.getBytes(), usr2.getBytes(), usr2.getBytes());
        table.put(p);
        table.close();
        //被关注表
        table = new HTable(conf, tab_floowed);
        p = new Put(usr2.getBytes());
        p.addColumn(col_cf.getBytes(), usr1.getBytes(), usr1.getBytes());
        table.put(p);
        table.close();
    }


    public void unFloow(String usr1, String usr2) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        //关注表
        HTable table = new HTable(conf, tab_floow);
        Delete del = new Delete(usr1.getBytes());
        del.deleteColumn(col_cf.getBytes(), usr2.getBytes());
        table.delete(del);
        table.close();
        //被关注表
        table = new HTable(conf, tab_floowed);
        del = new Delete(usr2.getBytes());
        del.deleteColumn(col_cf.getBytes(), usr1.getBytes());
        table.delete(del);
        table.close();
    }

    public List<String> getFloowUsers(String usr) throws IOException {
        List<String> list = new ArrayList<>();
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        HTable table = new HTable(conf, tab_floow);

        Get g = new Get(usr.getBytes());
        Result r = table.get(g);
        if (!r.isEmpty()) {
            for (Cell cell : r.listCells()) {
                String t = new String(cell.getValue());
                logger.info("t = {}", t);
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 获取被关注用户  粉丝儿
     *
     * @param usr
     * @return
     * @throws IOException
     */
    public List<String> getFloowedUsers(String usr) throws IOException {
        List<String> list = new ArrayList<>();
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        HTable table = new HTable(conf, tab_floowed);

        Get g = new Get(usr.getBytes());
        Result r = table.get(g);
        if (!r.isEmpty()) {
            for (Cell cell : r.listCells()) {
                String t = new String(cell.getValue());
                logger.info("t = {}", t);
                list.add(t);
            }
        }
        return list;
    }


    public static void main(String[] args) throws Exception {
        Weibo wb = new Weibo();
//        wb.createTable();

//        wb.floow("usr1", "usr2");
//        wb.floow("usr1", "usr3");
//        wb.unFloow("usr1", "usr2");
//        wb.floow("usr2", "usr1");
//        wb.floow("usr3", "usr1");

//        System.out.println(wb.getFloowUsers("usr1"));
//        wb.post("usr1", "test send weibo!");
        System.out.println(wb.getPost("usr1"));
    }


}
