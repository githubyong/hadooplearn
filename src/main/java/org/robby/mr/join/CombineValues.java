package org.robby.mr.join;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;


public class CombineValues implements WritableComparable<CombineValues>{
    private Text joinKey;
    private Text flag;
    private Text secondPart;
    
    public void setJoinKey(Text joinKey) {
        this.joinKey = joinKey;
    }
    public void setFlag(Text flag) {
        this.flag = flag;
    }
    public void setSecondPart(Text secondPart) {
        this.secondPart = secondPart;
    }
    public Text getFlag() {
        return flag;
    }
    public Text getSecondPart() {
        return secondPart;
    }
    public Text getJoinKey() {
        return joinKey;
    }
    public CombineValues() {
        this.joinKey =  new Text();
        this.flag = new Text();
        this.secondPart = new Text();
    }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
    @Override
    public void write(DataOutput out) throws IOException {
        this.joinKey.write(out);
        this.flag.write(out);
        this.secondPart.write(out);
    }
    @Override
    public void readFields(DataInput in) throws IOException {
        this.joinKey.readFields(in);
        this.flag.readFields(in);
        this.secondPart.readFields(in);
    }
    @Override
    public int compareTo(CombineValues o) {
        return this.joinKey.compareTo(o.getJoinKey());
    }
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "[flag="+this.flag.toString()+",joinKey="+this.joinKey.toString()+",secondPart="+this.secondPart.toString()+"]";
    }

}