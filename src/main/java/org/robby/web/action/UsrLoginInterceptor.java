package org.robby.web.action;


import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;


public class UsrLoginInterceptor extends AbstractInterceptor {

	public String intercept(ActionInvocation arg0) throws Exception {
        if (UserAction.class == arg0.getAction().getClass())
            return arg0.invoke();
        
        Map map = arg0.getInvocationContext().getSession();
        if (null == map.get("USERNAME")){
        	System.out.println("nologin");

            return "noLogin";
        }

        return arg0.invoke();
    }

}