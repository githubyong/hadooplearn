package org.robby.web.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisTool {
    public static Jedis jedis = null;

    public static void CreateJedisObj() {
        if (jedis == null) {
            jedis = new Jedis("hadoop1");
        }
    }

    public static void CreateJedisObj(String host) {
        if (jedis == null) {
            jedis = new Jedis(host);
        }
    }

    public static synchronized boolean registerUser(String name, String pwd) {
        try {
            CreateJedisObj();
            //hadoop_users
            String pass = jedis.hget(GlobalDef.TabUser, name);
            if (pass != null)
                return false;

            jedis.hset(GlobalDef.TabUser, name, pwd);
        } catch (Exception e) {
            System.out.println("Redis exception");
            jedis = null;
            return false;
        }
        return true;
    }

    public static synchronized boolean loginUser(String name, String pwd) {
        try {
            CreateJedisObj();
            //hadoop_users

            String pass = jedis.hget(GlobalDef.TabUser, name);
            if (pass == null || !pass.equals(pwd))
                return false;

        } catch (Exception e) {
            System.out.println("Redis exception");
            jedis = null;
            return false;
        }
        return true;
    }

    public static synchronized boolean uploadFile(String tabname, String filename, String json) {
        try {
            CreateJedisObj();
            //hadoop_users

            String file = jedis.hget(tabname, filename);
            if (file != null)
                return false;

            jedis.hset(tabname, filename, json);

        } catch (Exception e) {
            System.out.println("Redis exception");
            jedis = null;
            return false;
        }
        return true;
    }

    public static Map<String, String> listFiles(String tabname) {
        Map<String, String> files;
        // TODO Auto-generated method stub
        try {
            CreateJedisObj();
            //hadoop_users

            files = jedis.hgetAll(tabname);

        } catch (Exception e) {
            System.out.println("Redis exception");
            jedis = null;
            return null;
        }
        return files;
    }

    public static boolean delUserFile(String tabname, String delFilename) {
        // TODO Auto-generated method stub
        try {
            CreateJedisObj();
            //hadoop_users

            jedis.hdel(tabname, delFilename);

        } catch (Exception e) {
            System.out.println("Redis exception");
            jedis = null;
            return false;
        }
        return true;
    }

    public static Set<String> zrevrange(String text, int i, int j) {
        // TODO Auto-generated method stub
        try {
            CreateJedisObj();
            //hadoop_users


            return jedis.zrevrange(text, 0, 5);

        } catch (Exception e) {
            e.printStackTrace();
            jedis = null;
            return null;
        }
    }

    public static boolean addKey(String key, String field, int value) {
        // TODO Auto-generated method stub
        try {
            CreateJedisObj();
            //hadoop_users

            jedis.zincrby(key, value, field);

        } catch (Exception e) {
            e.printStackTrace();
            jedis = null;
            return false;
        }
        return true;
    }

    public static List<String> getAllUsrs() {
        List<String> usrs = new ArrayList<>();
        try {
            CreateJedisObj();
            Map<String, String> map = jedis.hgetAll(GlobalDef.TabUser);
            for (String s : map.keySet()) {
                usrs.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usrs;
    }

    public static long llen(String key) {
        try {
            CreateJedisObj();
            long len =  jedis.llen(key);
            return  len;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String rpop(String key) {
        try {
            return jedis.rpop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void lpush(String key, String value) {
        try {
            CreateJedisObj();
            jedis.lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Map<String, String> hgetAll(String key) {
        try {;
            CreateJedisObj();
            Map<String, String> map =  jedis.hgetAll(key);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
     long len =   RedisTool.llen("cdr-test");
    }
}
