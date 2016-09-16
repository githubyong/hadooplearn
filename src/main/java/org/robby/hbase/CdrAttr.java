package org.robby.hbase;

/**
 * Created by yong on 2016/9/11.
 */
public class CdrAttr {

    private String org;
    private String dest;
    private String type;
    private String dt;


    public CdrAttr(String org, String dest, String type, String dt) {
        this.org = org;
        this.dest = dest;
        this.type = type;
        this.dt = dt;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    @Override
    public String toString() {
        return org + "," + dest + "," + "," + dt;
    }
}
