package org.robby.web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.robby.hbase.CdrAttr;
import org.robby.hbase.SmCdr;
import org.robby.web.tool.RedisTool;

import java.util.List;
import java.util.Set;

public class CdrAction extends ActionSupport {

    private String simNum;
    List<CdrAttr> list;

    private String startTime;
    private String endTime;

    public CdrAction() {
        this.simNum = new String();
        this.startTime = "";
        this.endTime = "";
    }

    public String getSimNum() {
        return simNum;
    }

    public void setSimNum(String simNum) {
        this.simNum = simNum;
    }

    public List<CdrAttr> getList() {
        return list;
    }

    public void setList(List<CdrAttr> list) {
        this.list = list;
    }

    public String queryCdr() throws Exception {
        System.out.println(" input sim = " + simNum + "start = " + startTime + " end = " + endTime);
        if (simNum.length() > 0) {
            if(startTime.length()==0){
                startTime = "00000000000000";
            }else{
                startTime = startTime+"000000";
            }
            if(endTime.length()==0){
                endTime = "9999999999999999";
            }else{
                endTime = endTime+"000000";
            }
            SmCdr smCdr = new SmCdr();
//            list = smCdr.queryCdr(simNum,startTime,endTime);
            list = smCdr.queryCdr1(simNum);
        }
        return SUCCESS;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
