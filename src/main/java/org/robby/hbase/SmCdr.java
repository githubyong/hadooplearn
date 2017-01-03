package org.robby.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yong on 2016/9/11.
 */
public class SmCdr {

    public static final String table_name = "tab_sm";

    public static String col_cf = "cdr";
    public static String col_cf_desc = "dst";
    public static String col_cf_type = "type";

    public void createTable() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");

        HBaseAdmin admin = new HBaseAdmin(conf);
        boolean exist = admin.tableExists(table_name);
        if (admin.tableExists(table_name)) {
            admin.disableTable(table_name);
            admin.deleteTable(table_name);
        }
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("default", table_name));
        HColumnDescriptor cf1 = new HColumnDescriptor(col_cf);
        htd.addFamily(cf1);
        admin.createTable(htd);
    }

    public String randomSimNim(String prefix) {
        Random random = new Random();
        return String.format("%s%08d", prefix, random.nextInt(99999999));
    }

    public String randomTime() {
        Random random = new Random();
        return String.format("201609%02d%02d%02d%02d", random.nextInt(31) + 1, random.nextInt(24), random.nextInt(59), random.nextInt(59));
    }

    public String randomType() {
        return new Random().nextInt(2) + "";
    }

    /**
     * 使用 单 column family + 多 column qulifier 组合存储多列信息
     * @throws IOException
     */
    public void generateData() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        HTable table = new HTable(conf, table_name);
        int count = 0;
        for (int i = 0; i < 100; i++) {
            String org = randomSimNim("139");
            Random ran = new Random();
            for (int j = 0; j < ran.nextInt(200); j++) {//每个用户多少条话单
                //row key
                //sim + "_"+dt   eg:13900000001_20160911235723
                String rowKey = org + "_" + randomTime();
                Put p = new Put(rowKey.getBytes());
                p.addColumn(col_cf.getBytes(), col_cf_desc.getBytes(), randomSimNim("139").getBytes());
                p.addColumn(col_cf.getBytes(), col_cf_type.getBytes(), randomType().getBytes());
                table.put(p);
                count++;
            }
        }
        table.close();
        System.out.println(count);
    }

    public List<CdrAttr> queryCdr(String simnum) throws IOException {
        System.out.println("simnum = " + simnum);
        List<CdrAttr> list = new ArrayList<>();
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        HTable table = new HTable(conf, table_name);
        Scan scan = new Scan();
        scan.setStartRow((simnum + "_" + "0000000000000000").getBytes());
        scan.setStopRow((simnum + "_" + "9999999999999999").getBytes());
        ResultScanner rs = table.getScanner(scan);
        for (Result r : rs) {
            System.out.println(r);
            String dest = new String(r.getColumnLatestCell(col_cf.getBytes(), col_cf_desc.getBytes()).getValue());
            String type = new String(r.getColumnLatestCell(col_cf.getBytes(), col_cf_type.getBytes()).getValue());
            String dt = new String(r.getRow());
            dt = dt.substring(12);
            list.add(new CdrAttr(simnum, dest, type, dt));
        }
        rs.close();
        table.close();
        return list;
    }

    public static void main(String[] args) throws Exception {
        SmCdr smCdr = new SmCdr();
//        smCdr.createTable();
//        smCdr.generateData();
//        smCdr.generateData1();
        System.out.println(smCdr.queryCdr1("13999558044"));

//        System.out.println(smCdr.queryCdr("13997000386"));
//
    /*    Cdr.SmCdr.Builder smb = Cdr.SmCdr.newBuilder();
        smb.setDst("1");
        smb.setType("2");
        smb.setDt("3");
        Cdr.AllSmCdr.Builder asmb = Cdr.AllSmCdr.newBuilder();
        asmb.addCdr(smb.build());
        smb.setDst("5");
        smb.setType("6");
        smb.setDt("7");
        asmb.addCdr(smb.build());
        System.out.println(asmb.build());
        Cdr.AllSmCdr al = Cdr.AllSmCdr.parseFrom(asmb.build().toByteArray());*/


    }

    public List<CdrAttr> queryCdr(String simNum, String startTime, String endTime) throws IOException {
        System.out.println("simnum = " + simNum);
        List<CdrAttr> list = new ArrayList<>();
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        HTable table = new HTable(conf, table_name);
        Scan scan = new Scan();
        scan.setStartRow((simNum + "_" + startTime).getBytes());
        scan.setStopRow((simNum + "_" + endTime).getBytes());
        ResultScanner rs = table.getScanner(scan);
        for (Result r : rs) {
            System.out.println(r);
            String dest = new String(r.getColumnLatestCell(col_cf.getBytes(), col_cf_desc.getBytes()).getValue());
            String type = new String(r.getColumnLatestCell(col_cf.getBytes(), col_cf_type.getBytes()).getValue());
            String dt = new String(r.getRow());
            dt = dt.substring(12);
            list.add(new CdrAttr(simNum, dest, type, dt));
        }
        rs.close();
        table.close();
        return list;
    }


    /**
     * 使用 protobuf cdr的方式存储 数据，单column family 单 column qulifier
     * @throws IOException
     */
    public void generateData1() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        HTable table = new HTable(conf, table_name);
        int count = 0;
        for (int i = 0; i < 100; i++) {
            String org = randomSimNim("139");
            Random ran = new Random();
            Cdr.AllSmCdr.Builder asmb = Cdr.AllSmCdr.newBuilder();
            for (int j = 0; j < ran.nextInt(200); j++) {//每个用户多少条话单
                Cdr.SmCdr.Builder smb = Cdr.SmCdr.newBuilder();

                smb.setDst(randomSimNim("139"));
                smb.setType(randomType());
                smb.setDt(randomTime());

                asmb.addCdr(smb.build());
                count++;
            }
            String rowKey = org;
            Put p = new Put(rowKey.getBytes());
            //cdr:cdr
            p.addColumn(col_cf.getBytes(), col_cf.getBytes(), asmb.build().toByteArray());
            table.put(p);
        }
        table.close();
        System.out.println(count);
    }

    /**
     * 使用 protobuf 查询
     * @param simNum
     * @return
     * @throws IOException
     */
    public List<CdrAttr> queryCdr1(String simNum) throws IOException {
        System.out.println("simnum = " + simNum);
        List<CdrAttr> list = new ArrayList<>();
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        HTable table = new HTable(conf, table_name);

        Get g = new Get(simNum.getBytes());

        Result r = table.get(g);

        Cdr.AllSmCdr al = Cdr.AllSmCdr.parseFrom(r.getColumnLatestCell(col_cf.getBytes(), col_cf.getBytes()).getValue());
        for (Cdr.SmCdr cdr : al.getCdrList()) {
            System.out.println(cdr);
            CdrAttr attr = new CdrAttr(simNum, cdr.getDst(), cdr.getType(), cdr.getDt());
            list.add(attr);
        }

        table.close();
        return list;
    }

}
