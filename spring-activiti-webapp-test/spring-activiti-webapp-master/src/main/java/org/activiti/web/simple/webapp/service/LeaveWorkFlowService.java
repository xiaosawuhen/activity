package org.activiti.web.simple.webapp.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.web.simple.webapp.model.Leave;

public interface LeaveWorkFlowService {

	/**
	 * ����������
	 * @param key
	 * @param businessKey
	 * @param variables
	 * @return
	 */
	public ProcessInstance startWorkflow(String key,String businessKey,Map<String, Object> variables);
	
	/**
	 * �����û�Id��ѯ���������б�
	 * @param userid �û�id
	 * @param processDefinitionKey ���̶����key
	 * @return
	 */
	public List<Leave> findTask(String userid,String processDefinitionKey);
	

	/**
	 * ��ѯ�����е�����ʵ��
	 * @param processDefinitionKey ���̶����key
	 * @return
	 */
	public List<Leave> findRunningProcessInstaces(String processDefinitionKey);
	
	
	/**
	 * ��ѯ�ѽ���������ʵ��
	 * @param processDefinitionKey
	 * @return
	 */
	public List<Leave> findFinishedProcessInstaces(String processDefinitionKey);
	
	
	/**
	 * �������̶���Id��ѯ���̶���
	 * @param processDefinitionId
	 * @return
	 */
	public ProcessDefinition getProcessDefinition(String processDefinitionId);
	
	
	
	
	public TaskEntity findTaskById(String taskId) throws Exception;
	
	
}
