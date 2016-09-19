package org.robby.strom.billing;

import org.robby.web.tool.RedisTool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yong on 2016/9/18.
 */
public class Charging {

    Map<String, String> ac_local;
    Map<String, String> ac_other;

    public Charging() {
        ac_local = RedisTool.hgetAll("ac_local");
        ac_other = RedisTool.hgetAll("ac_other");
    }

    /**
     * 000 - 090
     * 000 - 050 省内
     * 060 - 090 省外
     * <p>
     * 130 - 000
     * 131 - 010
     * ...
     * 139 - 090 存放到redis中
     * <p>
     * 0 无漫游
     */
    public void calRoamAndLong(VoiceCdr cdr) {
        //计算漫游
        String roam_type = "0";
        String pre = cdr.org_msisdn.substring(0, 3);
        String ac = ac_local.get(pre);
        if (ac == null) {
            ac = ac_other.get(pre);
            roam_type = "3";
        }
        cdr.org_ac = ac;
        if (roam_type.equals("0")) {
            if (!cdr.org_ac.equals(cdr.visit_ac)) {//
                roam_type = ac_local.containsValue(cdr.visit_ac) ? "1" : "2";
            }
        }
        cdr.roam_type = roam_type;

        //计算长途
        pre = cdr.dst_misisdn.substring(0, 3);
        ac = ac_local.get(pre);
        if (ac == null) {
            ac = ac_other.get(pre);
        }
        cdr.dst_ac = ac;
        if (!cdr.visit_ac.equals(cdr.dst_ac)) {
            cdr.long_type = "1";//表示长途
        } else {
            cdr.long_type = "0";
        }
    }


    public void calFee(VoiceCdr cdr) {
        String rule = cdr.roam_type + "-" + cdr.long_type;
        cdr.charge_rule = rule;
        int fee = cdr.duration / 10 * (Integer.valueOf(cdr.roam_type) + Integer.valueOf(cdr.long_type)) * 2;
        cdr.fee = fee;
    }

    public static void main(String[] args) {
        Charging c = new Charging();
        System.out.println(c.ac_local);
    }
}
