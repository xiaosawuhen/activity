package org.activiti.web.simple.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.web.simple.webapp.model.Leave;
import org.activiti.web.simple.webapp.service.LeaveService;
import org.activiti.web.simple.webapp.service.LeaveWorkFlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("leaveWorkFlowServiceImpl")
@Transactional(propagation=Propagation.REQUIRED)
public class LeaveWorkFlowServiceImpl implements LeaveWorkFlowService {
	
	
	@Resource(name="leaveServiceImpl")
	private LeaveService leaveService;
	
	@SuppressWarnings("unused")
	@Resource(name="identityService")
	private IdentityService identityService;
	
	@Resource(name="runtimeService")
	private RuntimeService runtimeService;
	
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
	
	@Resource(name="repositoryService")
	private RepositoryService repositoryService;
	
	
	/**
	 * ����������
	 */
	public ProcessInstance startWorkflow(String key,String businessKey,Map<String, Object> variables) {
		//�������̶����key����������
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave", businessKey, variables);
		
		return processInstance;
	}

	
	/**
	 * �����û�Id��ѯ���������б�
	 * @param userid �û�id
	 * @param processDefinitionKey ���̶����key
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public List<Leave> findTask(String userid,String processDefinitionKey){

		//��ŵ�ǰ�û�����������
		List<Task> tasks=new ArrayList<Task>();
		
		
		List<Leave> leaves=new ArrayList<Leave>();
		
		
		//���ݵ�ǰ�û���id��ѯ���������б�(�Ѿ�ǩ��)
		List<Task> taskAssignees = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskAssignee(userid).orderByTaskPriority().desc().orderByTaskCreateTime().desc().list();
		//���ݵ�ǰ�û�id��ѯδǩ�յ������б�
		List<Task> taskCandidates = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskCandidateUser(userid).orderByTaskPriority().desc().orderByTaskCreateTime().desc().list();
		
		tasks.addAll(taskAssignees);//�����ǩ��׼��ִ�е�����(�Ѿ����䵽�������)
		tasks.addAll(taskCandidates);//��ӻ�δǩ�յ�����(����ĺ�ѡ��)
		
		
		//�������е������б�,����ʵ��
		for (Task task : tasks) {
			String processInstanceId = task.getProcessInstanceId();
			//��������ʵ��id��ѯ����ʵ��
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			//��ȡҵ��id
			String businessKey=processInstance.getBusinessKey();
			//��ѯ���ʵ��
			Leave leave = leaveService.findById(businessKey);
			//��������
			leave.setTask(task);
			leave.setProcessInstance(processInstance);
			leave.setProcessInstanceId(processInstance.getId());
			leave.setProcessDefinition(getProcessDefinition(processInstance.getProcessDefinitionId()));
			
			leaves.add(leave);
		}
		
		return leaves;
	}
	
	/**
	 * ��ѯ�����е�����ʵ��
	 * @param processDefinitionKey ���̶����key
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public List<Leave> findRunningProcessInstaces(String processDefinitionKey){
		List<Leave> leaves=new ArrayList<Leave>();
		
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
		
		//����ҵ��ʵ��
		for (ProcessInstance processInstance : processInstances) {
			
			String businessKey = processInstance.getBusinessKey();
			
			Leave leave = leaveService.findById(businessKey);
			
			leave.setProcessInstance(processInstance);
			leave.setProcessInstanceId(processInstance.getId());
			leave.setProcessDefinition(getProcessDefinition(processInstance.getProcessDefinitionId()));
			
			//���õ�ǰ������Ϣ
			//��������ʵ��id,�������񴴽�ʱ�併������,��ѯһ��������Ϣ
			List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().listPage(0, 1);
			leave.setTask(tasks.get(0));
			
			leaves.add(leave);
		}
		
		return leaves;
	}
	
	/**
	 * ��ѯ�ѽ���������ʵ��
	 * @param processDefinitionKey
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public List<Leave> findFinishedProcessInstaces(String processDefinitionKey){
		
		List<Leave> leaves=new ArrayList<Leave>();
		
		//�������̶����key��ѯ�Ѿ�����������ʵ��(HistoricProcessInstance)
		List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().processDefinitionKey(processDefinitionKey).list();
		
		//����ҵ��ʵ��
		for (HistoricProcessInstance historicProcessInstance : list) {
			
			String businessKey = historicProcessInstance.getBusinessKey();
			
			Leave leave = leaveService.findById(businessKey);
			
			leave.setHistoricProcessInstance(historicProcessInstance);
			leave.setProcessDefinition(getProcessDefinition(historicProcessInstance.getProcessDefinitionId()));
			
			leaves.add(leave);
		}
		
		return leaves;
	}

	/**
	 * �������̶���Id��ѯ���̶���
	 */
	public ProcessDefinition getProcessDefinition(String processDefinitionId) {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
		return processDefinition;
	}


	/**
	 * ��������Id��ѯ����
	 */
	public TaskEntity findTaskById(String taskId) throws Exception {
		TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();  
        if (task == null) {  
            throw new Exception("����ʵ��δ�ҵ�!");  
        }  
        return task; 
	}

	
}
