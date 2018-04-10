package org.activiti.web.simple.webapp.controller;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.web.simple.webapp.model.Leave;
import org.activiti.web.simple.webapp.service.LeaveService;
import org.activiti.web.simple.webapp.service.LeaveWorkFlowService;
import org.activiti.web.simple.webapp.util.Variable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/leave")
public class LeaveController {
	
	@Resource(name="leaveServiceImpl")
	private LeaveService leaveService;
	
	@Resource(name="leaveWorkFlowServiceImpl")
	private LeaveWorkFlowService leaveWorkFlowService;
	
	@Resource(name="identityService")
	private IdentityService identityService;
	
	@Resource(name="runtimeService")
	private RuntimeService runtimeService;
	
	@SuppressWarnings("unused")
	@Resource(name="historyService")
	private HistoryService historyService;
	
	@Resource(name="taskService")
	private TaskService taskService;
	
	@SuppressWarnings("unused")
	@Resource(name="managementService")
	private ManagementService managementService;
	
	@SuppressWarnings("unused")
	@Resource(name="formService")
	private FormService formService;
	
	@SuppressWarnings("unused")
	@Resource(name="repositoryService")
	private RepositoryService repositoryService;
	
	
	@RequestMapping(value="/form",method={RequestMethod.GET})
	public String toleaveForm(){
		return "leave/leaveform";
	}
	
	/**
	 * �ռ�����Ϣ������������
	 * @param leave �Զ��󶨱��ύ����������
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/start",method={RequestMethod.POST})
	public String startWorkFlow(Leave leave,HttpSession session,HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes){
		
		Map<String, Object> variables=new HashMap<String, Object>();
		
		//�趨�ʼ������˺��ʼ��ռ���
		variables.put("from", "184675420@qq.com");
		
		User user=(User) session.getAttribute("loginuser");
		if(user!=null){
			
			//�����û�id
			leave.setUserId(user.getId());	
			
			leave.setApplyTime(Calendar.getInstance().getTime());
			
			variables.put("to", user.getEmail());
			
			//�ȳ־û����ʵ��
			leaveService.save(leave);
			
			//��ҵ���(�����ʵ����id������ʵ����)
			String businessKey=leave.getId().toString();
			
			// ���������������̵���ԱID��������Զ����û�ID���浽activiti:initiator��
			identityService.setAuthenticatedUserId(user.getId());
			
			try {
				ProcessInstance processInstance = leaveWorkFlowService.startWorkflow("leave",businessKey,variables);
				
				leave.setProcessInstance(processInstance);
				leave.setProcessInstanceId(processInstance.getId());
				
				redirectAttributes.addFlashAttribute("message", "����������������ID��" + processInstance.getId());
			} catch (ActivitiException e) {
				if (e.getMessage().indexOf("no processes deployed with key") != -1) {
					redirectAttributes.addFlashAttribute("message", "û�в�������");
					return "redirect:/workflow/toupload";
				} else {
					redirectAttributes.addFlashAttribute("message", "ϵͳ�ڲ�����");
				}
			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("message", "ϵͳ�ڲ�����");
			}
		}else{
			return "redirect:/login";//��ת����¼����
		}
		return "redirect:/leave/form"; //��ת��ԭ����ҳ��
	}
	
	
	/**
	 * �����û�Id��ѯ���������б�
	 * @param userid
	 * @return
	 */
	@RequestMapping(value="/task/list",method={RequestMethod.GET})
	public ModelAndView findTask(HttpServletRequest request, HttpServletResponse response,HttpSession session){
		ModelAndView modelAndView=new ModelAndView("leave/tasklist");
		User user=(User) session.getAttribute("loginuser");
		List<Leave> tasklist = leaveWorkFlowService.findTask(user.getId(),"leave");
		modelAndView.addObject("tasklist", tasklist);
		return modelAndView;
	}
	
	
	/**
	 * �������̶����key��ѯ�����е�����ʵ��
	 * @param processDefinitionKey //���̶���key
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/process/running/{processDefinitionKey}/list",method={RequestMethod.GET})
	public ModelAndView findRunningProcessInstaces(@PathVariable("processDefinitionKey")String processDefinitionKey,HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView=new ModelAndView("leave/run-proc");
		List<Leave> runningProcessInstaces = leaveWorkFlowService.findRunningProcessInstaces(processDefinitionKey);
		modelAndView.addObject("runprocess", runningProcessInstaces);
		return modelAndView;
	}
	
	/**
	 * ��ѯ�ѽ���������ʵ��
	 * @param processDefinitionKey
	 * @return
	 */
	@RequestMapping(value="/process/finished/{processDefinitionKey}/list",method={RequestMethod.GET})
	public ModelAndView findFinishedProcessInstaces(@PathVariable("processDefinitionKey")String processDefinitionKey,HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView=new ModelAndView("leave/end-proc");
		List<Leave> finishedProcessInstaces = leaveWorkFlowService.findFinishedProcessInstaces(processDefinitionKey);
		modelAndView.addObject("endprocess", finishedProcessInstaces);
		return modelAndView;
	}
	
	
	/**
	 * ��������Idǩ������
	 * @param userid
	 * @return
	 */
	@RequestMapping(value="/task/{taskId}/claim",method={RequestMethod.GET})
	public String claimTask(@PathVariable("taskId")String taskId,HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes,HttpSession session){
		User user=(User) session.getAttribute("loginuser");
		taskService.claim(taskId, user.getId());
		redirectAttributes.addFlashAttribute("message", "����ǩ�ճɹ�!");
		return "redirect:/leave/task/list";//��ת�����������б�
	}
	
	
	
	
	/**
	 * ��������Id�������
	 * @param userid
	 * @return
	 */
	@RequestMapping(value="/task/{taskId}/complete",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String completeTask(@PathVariable("taskId")String taskId,Variable variable){
		//String message="";
		try {
			Map<String, Object> variables = variable.getVariableMap();
			taskService.complete(taskId,variables);
			//message="success";
			return "success";
		} catch (Exception e) {
			//message="error";
			return "error";
		}
		//redirectAttributes.addFlashAttribute("message", message);
		//"redirect:/leave/task/list" ��ת�����������б�
	}
	
	
	/**
	 * ��ȡ���ʵ��
	 * @param id id
	 * @return
	 */
	@RequestMapping(value="/detail/{id}/leave",method={RequestMethod.GET})
	public @ResponseBody Leave getLeaveById(@PathVariable("id")String id){
		Leave leave = leaveService.findById(id);
		return leave;
	}
	
	/**
	 * �������id������id��ȡʵ��
	 * @param id
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value="/detail/leave/{taskId}",method={RequestMethod.GET})
	public ModelAndView getLeaveWithVars(@PathVariable("taskId") String taskId){
		
		ModelAndView modelAndView=new ModelAndView("leave/viewform");
		
		
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		
		
		ExecutionEntity executionEntity=(ExecutionEntity) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).processInstanceId(task.getProcessInstanceId()).singleResult();
		
		//��ȡ��ǰ����ִ�еĽڵ�
		String activityId = executionEntity.getActivityId();
		
		String processInstanceId = task.getProcessInstanceId();
		
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		
		String businessKey = processInstance.getBusinessKey();
		
		Leave leave = leaveService.findById(businessKey);
		
		Map<String, Object> variables = taskService.getVariables(taskId);
		
		leave.setVariables(variables);
		
		modelAndView.addObject("taskId", taskId);
		modelAndView.addObject("leave", leave);
		modelAndView.addObject("activityId", activityId);
		
		return modelAndView;
	}
	
	
}
