package org.activiti.web.simple.webapp.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.web.simple.webapp.service.WorkflowTraceService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value="/workflow")
public class WorkFlowController {
	
	@Autowired
	private WorkflowTraceService traceService;
	
	@SuppressWarnings("unused")
	@Resource(name="identityService")
	private IdentityService identityService;
	
	@Resource(name="runtimeService")
	private RuntimeService runtimeService;
	
	@SuppressWarnings("unused")
	@Resource(name="historyService")
	private HistoryService historyService;
	
	@SuppressWarnings("unused")
	@Resource(name="taskService")
	private TaskService taskService;
	
	@SuppressWarnings("unused")
	@Resource(name="managementService")
	private ManagementService managementService;
	
	@SuppressWarnings("unused")
	@Resource(name="formService")
	private FormService formService;
	
	@Resource(name="repositoryService")
	private RepositoryService repositoryService;
	
	
	@RequestMapping("/toupload")
	public String toupload(){
		return "workflow/upload";
	}
	
	/**
	 * �������̶����ļ�(Spring MVC�ļ��ϴ�)
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/deploy",method=RequestMethod.POST)
	public String deploy(@RequestParam("username")String username,@RequestParam("file")MultipartFile file,HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes) throws Exception{
		System.out.println(username);
		if(!file.isEmpty()){
			//��ȡ�ļ��ֽ�����
			byte[] bytes= file.getBytes();
			//��ȡ�ļ�����·��
			String realPath = request.getSession().getServletContext().getRealPath("/upload");
			File out=new File(realPath,file.getOriginalFilename());
			//���ļ�д��ָ��Ŀ¼��
			FileUtils.writeByteArrayToFile(out, bytes);
			
			if(FilenameUtils.getExtension(file.getOriginalFilename()).equals("zip")||FilenameUtils.getExtension(file.getOriginalFilename()).equals("bar")){
				
				ZipInputStream zipInputStream=new ZipInputStream(file.getInputStream());
				//�������̶����ļ�
				repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
				
			}else{
				redirectAttributes.addFlashAttribute("message", "���ϴ�zip��bar��ʽ���ļ�!");
				return "redirect:/workflow/toupload";
			}
			redirectAttributes.addFlashAttribute("message", "�ļ��ϴ��ɹ�!������:"+out.getPath());
		}
		return "redirect:/workflow/processlist";
	}
	
	
	@RequestMapping(value="/processlist",method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView processlist(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView=new ModelAndView("workflow/processlist");
		
		/*
		 * ������������һ����ProcessDefinition�����̶��壩��һ����Deployment�����̲���
		 */
		List<Object[]> objects = new ArrayList<Object[]>();
		
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
		for (ProcessDefinition processDefinition : list) {
			String deploymentId = processDefinition.getDeploymentId();
			Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
			objects.add(new Object[]{processDefinition,deployment});
		}
		modelAndView.addObject("objects",objects);
		return modelAndView;
	}
	
	
	/**
	 * �������̲���Id����Դ���Ƽ���������Դ
	 * @param deploymentId
	 * @param resourceName
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/loadResourceByDeployment",method={RequestMethod.GET,RequestMethod.POST})
	public void loadResourceByDeployment(@RequestParam("deploymentId")String deploymentId,@RequestParam("resourceName")String resourceName,HttpServletRequest request, HttpServletResponse response){
		
		InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
		try {
			byte[] byteArray = IOUtils.toByteArray(resourceAsStream);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(byteArray, 0, byteArray.length);
			servletOutputStream.flush();
			servletOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �������̲���Id����ɾ���Ѳ��������
	 * @param deploymentId
	 * @param request
	 * @param response
	 * @return ��ת���Ѳ���������б�
	 */
	@RequestMapping(value="/deleteDeploymentById/{deploymentId}",method={RequestMethod.GET})
	public String deleteDeploymentById(@PathVariable("deploymentId")String deploymentId,HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes){
		try {
			repositoryService.deleteDeployment(deploymentId,true);
			redirectAttributes.addFlashAttribute("message", "�Ѳ��������"+deploymentId+"�ѳɹ�ɾ��!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "�Ѳ��������"+deploymentId+"ɾ��ʧ��!");
		}
		return "redirect:/workflow/processlist";
	}
	
	
	/**
	 * ��������ʵ��Id����Դ���ͼ���������Դ
	 * @param processInstanceId ����ʵ��id
	 * @param resourceType ������Դ����
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/loadResourceByProcessInstance",method={RequestMethod.GET,RequestMethod.POST})
	public void loadResourceByProcessInstance(@RequestParam("processInstanceId")String processInstanceId,@RequestParam("resourceType")String resourceType,HttpServletRequest request, HttpServletResponse response){
		//��������ʵ��id��ѯ����ʵ��
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		//�������̶���id��ѯ���̶���
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
		
		String resourceName="";
		if(resourceType.equals("xml")){
			//��ȡ���̶�����Դ����
			resourceName=processDefinition.getResourceName();
		}else if(resourceType.equals("image")){
			//��ȡ����ͼ��Դ����
			resourceName=processDefinition.getDiagramResourceName();
		}
		//��������Դ��
		InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
		//����������
		try {
			byte[] byteArray = IOUtils.toByteArray(resourceAsStream);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(byteArray, 0, byteArray.length);
			servletOutputStream.flush();
			servletOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * ����ת�����鿴����ͼҳ��
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value="/view/{executionId}/page/{processInstanceId}",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView viewImage(@PathVariable("executionId")String executionId,@PathVariable("processInstanceId")String processInstanceId){
		ModelAndView modelAndView=new ModelAndView("workflow/view");
		modelAndView.addObject("executionId", executionId);
		modelAndView.addObject("processInstanceId", processInstanceId);
		return modelAndView;
	}
	
	
	
	/**
	 * ��������ʵ��id��ѯ����ͼ(��������ͼ)
	 * @param processInstanceId ����ʵ��id
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/view/{processInstanceId}",method={RequestMethod.GET,RequestMethod.POST})
	public void viewProcessImageView(@PathVariable("processInstanceId")String processInstanceId,HttpServletRequest request, HttpServletResponse response){
		InputStream resourceAsStream = null;
		try {
			
			//��������ʵ��id��ѯ����ʵ��
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			
			//�������̶���id��ѯ���̶���
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
			
			String resourceName=processDefinition.getDiagramResourceName();
			
			//��������Դ��
			resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
			
			runtimeService.getActiveActivityIds(processInstance.getId());
			
			//����������
			byte[] byteArray = IOUtils.toByteArray(resourceAsStream);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(byteArray, 0, byteArray.length);
			servletOutputStream.flush();
			servletOutputStream.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	
	
	/**
	 * �������������Ϣ
	 * @param processInstanceId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/process/{executionId}/trace/{processInstanceId}",produces={MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody Map<String,Object> traceProcess(@PathVariable("executionId") String executionId,@PathVariable("processInstanceId") String processInstanceId) throws Exception {
		
		//����executionId��ѯ��ǰִ�еĽڵ�
		ExecutionEntity execution=(ExecutionEntity) runtimeService.createExecutionQuery().processInstanceId(processInstanceId).executionId(executionId).singleResult();
		
		//��ȡ��ǰ�ڵ��activityId
		String activityId=execution.getActivityId();
		
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
		
		List<ActivityImpl> activities = processDefinitionEntity.getActivities();
		
		Map<String,Object> activityImageInfo=new HashMap<String,Object>();
		
		for (ActivityImpl activityImpl : activities) {
			String id=activityImpl.getId();
			//�ж��Ƿ��ǵ�ǰ�ڵ�
			if(id.equals(activityId)){
				activityImageInfo.put("x", activityImpl.getX());
				activityImageInfo.put("y", activityImpl.getY());
				activityImageInfo.put("width", activityImpl.getWidth());
				activityImageInfo.put("height", activityImpl.getHeight());
				break;//����ѭ��
			}
		}
		return activityImageInfo;
	}
	
	
	
	/**
	 * �������������Ϣ
	 * @param processInstanceId ����ʵ��id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/process/{pid}/trace")
	@ResponseBody
	public List<Map<String, Object>> traceProcess(@PathVariable("pid") String processInstanceId) throws Exception {
		List<Map<String, Object>> activityInfos = traceService.traceProcess(processInstanceId);
		return activityInfos;
	}
	
	
	
}
