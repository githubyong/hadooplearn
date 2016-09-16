package org.robby.hbase;

/**
 * Created by yong on 2016/9/16.
 */
public class Post {

    private String sender;
    private String contnet;
    private String dt;


    public Post(String sender, String contnet, String dt) {
        this.sender = sender;
        this.contnet = contnet;
        this.dt = dt;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getContnet() {
        return contnet;
    }

    public void setContnet(String contnet) {
        this.contnet = contnet;
    }


    @Override
    public String toString() {
        return "Post{" +
                "sender='" + sender + '\'' +
                ", dt='" + dt + '\'' +
                ", contnet='" + contnet + '\'' +
                '}';
    }
}
