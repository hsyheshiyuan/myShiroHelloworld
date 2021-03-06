package com.shiro.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.shiro.pojo.User;
import com.shiro.service.UserService;
@Controller
@RequestMapping("/login")
public class LoginController {

	static Logger logger = Logger.getLogger(LoginController.class);

	@Autowired
	private UserService userService;

    
    @RequestMapping("/loginPage")
    public String loginPage(HttpServletRequest request, String msg,ModelMap model){
    	try {
	    		if(msg!=null){
		    			msg=new String(msg.getBytes("iso-8859-1"),"utf-8");
		        		model.addAttribute("msg", msg);
		        		System.out.println("msg="+msg);
	    		}
		} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}
    	return "/anon/login.jsp";
    }
    
    
    /** 
     * 实际的登录代码 
     * 如果登录成功，跳转至首页；登录失败，则将失败信息反馈对用户 
     *  
     * @param request 
     * @param model 
     * @return 
     */  
    @RequestMapping(value = "/doLogin")  
    public String doLogin(HttpServletRequest request, HttpSession session,Model model,String username,String password,RedirectAttributes redirectAttr) {  
    	System.out.println("*****************************tomcat的resuest:"+request+",tomcat的session:"+session+"**************************");
	        String msg = "";  
	        Subject subject = SecurityUtils.getSubject();
	        if(subject.isAuthenticated()){
	        		return "redirect:/needAuthentication/index.jsp";  
	        }
	        boolean rememberMe = ServletRequestUtils.getBooleanParameter(request, "rememberMe", false);//默认是“不记住”！！
	        System.out.println("rememberMe="+rememberMe);
	        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
	        try {  
	        		subject.login(token);  
		            if (subject.isAuthenticated()) {  //认证通过
		            	    subject.getSession().setAttribute("username", username);
		                	return "redirect:/needAuthentication/index.jsp";  
		            } else {  
		                	return "redirect:/login/loginPage";  
		            }  
	        } catch (IncorrectCredentialsException e) {  
	        		msg = "登录密码错误. Password for account " + token.getPrincipal() + " was incorrect.";  
	        		System.out.println(msg);  
	        } catch (ExcessiveAttemptsException e) {  
		            msg = "登录失败次数过多";  
		            System.out.println(msg);  
	        } catch (LockedAccountException e) {  
		            msg = "帐号已被锁定. The account for username " + token.getPrincipal() + " was locked.";  
		            System.out.println(msg);  
	        } catch (DisabledAccountException e) {  
		            msg = "帐号已被禁用. The account for username " + token.getPrincipal() + " was disabled.";  
		            System.out.println(msg);  
	        } catch (ExpiredCredentialsException e) {  
		            msg = "帐号已过期. the account for username " + token.getPrincipal() + "  was expired.";  
		            System.out.println(msg);  
	        } catch (UnknownAccountException e) {  
		            msg = "帐号不存在. There is no user with username of " + token.getPrincipal();  
		            System.out.println(msg);  
	        } catch (UnauthorizedException e) {  
		            msg = "您没有得到相应的授权！" + e.getMessage();  
		            System.out.println(msg);  
	        }  
	        redirectAttr.addAttribute("msg", msg);
	        return "redirect:/login/loginPage";  
    }  
    
    
    @RequestMapping("/logout")
    public String logout(){
            Subject subject = SecurityUtils.getSubject();  
            Object principal = subject.getPrincipal();
            if (subject.isAuthenticated()) {  
	                subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存  
	                System.out.println("用户"+principal+"退出！");
            }  
            return "redirect:/login/loginPage";  
    }
    
    
    
  
    
    public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
	}
} 