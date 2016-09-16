package org.robby.web.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.robby.hbase.CdrAttr;
import org.robby.hbase.Post;
import org.robby.hbase.SmCdr;
import org.robby.hbase.Weibo;
import org.robby.web.tool.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeiboAction extends ActionSupport {
    private static final Logger logger = LoggerFactory.getLogger(WeiboAction.class);


    private List<String> floow;
    private List<String> unfloow;
    private List<String> fans;//粉丝

    private String name;
    private String postContent;//发送内容

    private List<Post> postList;

    public WeiboAction() {
        postContent = new String();
    }

    public String exec() throws IOException {
        return SUCCESS;
    }

    public String inbox() throws IOException {
        Map map = ActionContext.getContext().getSession();
        String usr = (String) map.get("USERNAME");
        Weibo wb = new Weibo();
        postList = wb.getPost(usr);
        return SUCCESS;
    }


    public String floow() throws IOException {
        Map map = ActionContext.getContext().getSession();
        String usr = (String) map.get("USERNAME");
        logger.info(" {} floow -> {}", usr, name);
        Weibo wb = new Weibo();
        wb.floow(usr, name);
        return SUCCESS;
    }

    public String unfloow() throws IOException {
        Map map = ActionContext.getContext().getSession();
        String usr = (String) map.get("USERNAME");
        logger.info(" {} unfloow -> {}", usr, name);
        Weibo wb = new Weibo();
        wb.unFloow(usr, name);
        return SUCCESS;
    }

    public String post() throws IOException {
        if (StringUtils.isBlank(postContent)) {
            return SUCCESS;
        }
        Map map = ActionContext.getContext().getSession();
        String usr = (String) map.get("USERNAME");
        logger.info(" {} unfloow -> {}", usr, name);
        Weibo wb = new Weibo();
        wb.post(usr, postContent);
        return SUCCESS;
    }

    public List<String> getFloow() throws IOException {
        Map map = ActionContext.getContext().getSession();
        String usr = (String) map.get("USERNAME");
        logger.info(" usr = {}", usr);
        Weibo wb = new Weibo();
        floow = wb.getFloowUsers(usr);
        return floow;
    }

    public void setFloow(List<String> floow) {
        this.floow = floow;
    }

    public List<String> getUnfloow() throws IOException {
        Map map = ActionContext.getContext().getSession();
        String usr = (String) map.get("USERNAME");
        Weibo wb = new Weibo();
        List<String> floow = wb.getFloowUsers(usr);
        List<String> usrsAll = RedisTool.getAllUsrs();
        unfloow = new ArrayList<>(CollectionUtils.subtract(new ArrayList(usrsAll), new ArrayList(floow)));
        return unfloow;
    }

    public List<String> getFans() throws IOException {
        Map map = ActionContext.getContext().getSession();
        String usr = (String) map.get("USERNAME");
        logger.info(" usr = {}", usr);
        Weibo wb = new Weibo();
        fans = wb.getFloowedUsers(usr);
        return fans;
    }

    public void setFans(List<String> fans) {
        this.fans = fans;
    }

    public void setUnfloow(List<String> unfloow) {
        this.unfloow = unfloow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }
}
