package org.robby.strom.billing;

import com.google.gson.Gson;
import org.robby.web.tool.RedisTool;

import java.util.Random;
import java.util.Set;

/**
 * Created by yong on 2016/9/18.
 */
public class VoiceCdr {
    public String org_msisdn;//资费的目标号码
    public String dst_misisdn;
    public String call_type;// 0 主叫    1 被叫

    public String org_ac;// area code 初始位置
    public String visit_ac;//打电话的位置(可能会漫游)
    public String dst_ac;

    public String roam_type;// 0 无漫游   1 省内漫游    2 国内漫游    4 其他省的在本省漫游(由归属地的省份计费)
    public String long_type; // 0 本地   1 长途

    public String dt;
    public int duration;
    public int fee;//以分为单位

    public String charge_rule;


    public void setRandomVal() {
        Random random = new Random();
        org_msisdn = randomSimNim("13");
        dst_misisdn = randomSimNim("13");

        call_type = Integer.toString(random.nextInt(2));
        visit_ac = String.format("0%s0", random.nextInt(10)); // 000 ~ 090

        duration = random.nextInt(3000);
        dt = String.format("201609%02d%02d%02d%02d", random.nextInt(31) + 1, random.nextInt(24), random.nextInt(60), random.nextInt(60));
    }

    public String randomSimNim(String prefix) {
        Random random = new Random();
        return String.format("%s%09d", prefix, random.nextInt(999999999));
    }

    public static void main(String[] args) {
        Gson gson = new Gson();
        VoiceCdr cdr = null;
        for (int i = 0; i < 100; i++) {
            cdr = new VoiceCdr();
            cdr.setRandomVal();
            RedisTool.lpush("cdr-list", gson.toJson(cdr));
            System.out.println(gson.toJson(cdr));
        }

    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
