package org.activiti.web.simple.webapp.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.web.simple.webapp.service.AccountService;
import org.activiti.web.simple.webapp.service.ActivitiWorkFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {

	@Autowired
	private IdentityService identityService;
	
	@Resource(name="accountServiceImpl")
	private AccountService accountService;
	
	@Resource(name="activitiWorkFlowServiceImpl")
	private ActivitiWorkFlowService activitiWorkFlowService;
	
	ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return null;
	}
	
	/**
	 * ��ת����¼ҳ��
	 * @return
	 */
	@RequestMapping(value="/login",method={RequestMethod.POST,RequestMethod.GET})
	public String login(){
		return "login";
	}
	
	
	
	/**
	 * �˳�
	 * @return
	 */
	@RequestMapping(value="/loginout",method={RequestMethod.POST,RequestMethod.GET})
	public String loginout(HttpSession session){
		session.removeAttribute("loginuser");
		return "redirect:/login";
	}
	
	
	
	/**
	 * ��ת����ҳ��
	 * @return
	 */
	@RequestMapping(value="/main",method={RequestMethod.POST,RequestMethod.GET})
	public String main(){
		return "main";
	}
	
	
	
	/**
	 * ִ���û���¼
	 * @param username���ܱ��ύ�������û���
	 * @param password���ܱ��ύ����������
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/loginin",method={RequestMethod.POST,RequestMethod.GET})
	public String loginin(@RequestParam("username")String username,@RequestParam("password")String password,HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes){
		String forword="";
		if((username!=null&&username.length()>0)&&(password!=null&&password.length()>0)){
			boolean b = accountService.checkPassword(username, password);
			if(b){
				User user = activitiWorkFlowService.getUserInfo(username);
				user.setId(username);
				user.setPassword(password);
				
				//��ѯ�û����ڵ���
				
				List<Group> listGroup = identityService.createGroupQuery().groupMember(username).list();
				
				request.getSession().setAttribute("loginuser", user);
				request.getSession().setAttribute("listGroup", listGroup);
				redirectAttributes.addFlashAttribute("message", "��¼�ɹ�!");
				forword="/main";//main.jsp
			}else{
				redirectAttributes.addFlashAttribute("message", "�û������������!");
				forword="/login";//login.jsp
			}
		}else{
			forword="/login";//login.jsp
			redirectAttributes.addFlashAttribute("message", "�û��������벻��Ϊ��!");
		}
		return "redirect:"+forword;
	}
	
	
	
	/**
	 * ��ת���û�����ҳ��
	 * @return
	 */
	@RequestMapping(value="/userwork",method={RequestMethod.POST,RequestMethod.GET})
	public String userwork(){
		return "user/userwork";
	}
	
	
	
	/**
	 * ��ת���û�����ҳ��
	 * @return
	 */
	@RequestMapping(value="/groupwork",method={RequestMethod.POST,RequestMethod.GET})
	public String groupwork(){
		return "group/groupwork";
	}
	
	/**
	 * �鿴�û��б�
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/userlist",method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView userlist(HttpServletRequest request, HttpServletResponse response){
		List<org.activiti.engine.identity.User> listuser = accountService.createUserQuery().list();
		ModelAndView modelAndView=new ModelAndView();
		modelAndView.setViewName("user/listuser");
		modelAndView.addObject("listuser", listuser);
		return modelAndView;
	}
	
	
	/**
	 * �鿴��Ա�б�
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/grouplist",method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView grouplist(HttpServletRequest request, HttpServletResponse response){
		List<Group> listgroup = accountService.createGroupQuery().list();
		ModelAndView modelAndView=new ModelAndView();
		modelAndView.setViewName("group/listgroup");
		modelAndView.addObject("listgroup", listgroup);
		return modelAndView;
	}
	
	
	
	
	/**
	 * �鿴���ڵĳ�Ա
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/memberofgroup/{groupId}",method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView memberOfGroup(@PathVariable("groupId")String groupId){
		List<User> listMemberOfGroupUser = identityService.createUserQuery().memberOfGroup(groupId).list();
		ModelAndView modelAndView=new ModelAndView();
		modelAndView.setViewName("user/listuser");
		modelAndView.addObject("listuser", listMemberOfGroupUser);
		return modelAndView;
	}
	
	
	
	
}
