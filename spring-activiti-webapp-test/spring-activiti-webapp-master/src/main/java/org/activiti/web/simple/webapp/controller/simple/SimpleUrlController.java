package org.activiti.web.simple.webapp.controller.simple;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * �ÿ�������spring�����б�ʶΪSimpleUrlController ������org.springframework.web.servlet.handler.SimpleUrlHandlerMapping�����б���
 * ���spring-mvc-servlet.xml�� <bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
 * ��<prop key="/SimpleUrl">SimpleUrlController</prop>������
 * ���������������·��Ϊ
 * /simpleUrl/jsp
 * /simpleUrl/velocity
 * /simpleUrl/freemarker
 * @author fengjing
 *
 */
@Controller(value="SimpleUrlController")
@RequestMapping(value="/simpleurl")
public class SimpleUrlController {
	
	
	@RequestMapping(value="/jsp")
	public ModelAndView jsp(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) throws Exception {
		ModelAndView modelAndView=new ModelAndView("welcome");
		modelAndView.addObject("message", "Hello,SpringMVC!");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/velocity")
	public ModelAndView velocity(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) throws Exception {
		ModelAndView modelAndView=new ModelAndView("velocity");
		modelAndView.addObject("message", "Hello,SpringMVC!");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/freemarker")
	public ModelAndView freemarker(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) throws Exception {
		ModelAndView modelAndView=new ModelAndView("freemarker");
		modelAndView.addObject("message", "Hello,SpringMVC!");
		return modelAndView;
	}
	
	
	/**
	 * �ӵ�ַ�����ܲ���  /simpleUrl/showUser/admin/admin nameΪadmin pwdΪadmin ����ת����showuser.jsp
	 * @param username
	 * @param password
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/showUser/{name}/{pwd}")
	public String showUserName(@PathVariable(value="name")String username,@PathVariable(value="pwd")String password,ModelMap map){
		try {
			map.addAttribute("username", new String(username.getBytes("iso-8859-1"),"utf-8"));
			map.addAttribute("pwd", password);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "showuser";
	}
	
}
