package org.activiti.web.simple.webapp.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public interface ActivitiWorkFlowService {

	/** 
	 * �������� 
	 *  
	 * @param taskId 
	 *            ��ǰ����ID 
	 * @param activityId 
	 *            ���ؽڵ�ID 
	 * @param variables 
	 *            ���̴洢���� 
	 * @throws Exception 
	 */
	public abstract void backProcess(String taskId, String activityId,
			Map<String, Object> variables) throws Exception;

	/** 
	 * ȡ������ 
	 *  
	 * @param taskId 
	 *            ��ǰ����ID 
	 * @param activityId 
	 *            ȡ�ؽڵ�ID 
	 * @throws Exception 
	 */
	public abstract void callBackProcess(String taskId, String activityId)
			throws Exception;

	/** 
	 * ��ֹ����(��Ȩ��ֱ������ͨ����) 
	 *  
	 * @param taskId 
	 */
	public abstract void endProcess(String taskId) throws Exception;

	/** 
	 * ���ݵ�ǰ����ID����ѯ���Բ��ص�����ڵ� 
	 *  
	 * @param taskId 
	 *            ��ǰ����ID 
	 */
	public abstract List<ActivityImpl> findBackAvtivity(String taskId)
			throws Exception;

	/** 
	 * ��ѯָ������ڵ�����¼�¼ 
	 *  
	 * @param processInstance 
	 *            ����ʵ�� 
	 * @param activityId 
	 * @return 
	 */
	public abstract HistoricActivityInstance findHistoricUserTask(
			ProcessInstance processInstance, String activityId);

	/** 
	 * ���ݵ�ǰ�ڵ㣬��ѯ��������Ƿ�Ϊ�����յ㣬���Ϊ�����յ㣬��ƴװ��Ӧ�Ĳ������ID 
	 *  
	 * @param activityImpl 
	 *            ��ǰ�ڵ� 
	 * @return 
	 */
	public abstract String findParallelGatewayId(ActivityImpl activityImpl);

	/** 
	 * ��������ID��ȡ���̶��� 
	 *  
	 * @param taskId 
	 *            ����ID 
	 * @return 
	 * @throws Exception 
	 */
	public abstract ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
			String taskId) throws Exception;

	/** 
	 * ��������ID��ȡ��Ӧ������ʵ�� 
	 *  
	 * @param taskId 
	 *            ����ID 
	 * @return 
	 * @throws Exception 
	 */
	public abstract ProcessInstance findProcessInstanceByTaskId(String taskId)
			throws Exception;

	/** 
	 * ��������ID�������ʵ�� 
	 *  
	 * @param taskId 
	 *            ����ID 
	 * @return 
	 * @throws Exception 
	 */
	public abstract TaskEntity findTaskById(String taskId) throws Exception;

	/** 
	 * ��������ʵ��ID������keyֵ��ѯ����ͬ�����񼯺� 
	 *  
	 * @param processInstanceId 
	 * @param key 
	 * @return 
	 */
	public abstract List<Task> findTaskListByKey(String processInstanceId,
			String key);

	/** 
	 * ����ѭ���������ṹ����ѯ��ǰ�ڵ�ɲ��ص�����ڵ� 
	 *  
	 * @param taskId 
	 *            ��ǰ����ID 
	 * @param currActivity 
	 *            ��ǰ��ڵ� 
	 * @param rtnList 
	 *            �洢���˽ڵ㼯�� 
	 * @param tempList 
	 *            ��ʱ�洢�ڵ㼯�ϣ��洢һ�ε��������е�ͬ��userTask�ڵ㣩 
	 * @return ���˽ڵ㼯�� 
	 */
	public abstract List<ActivityImpl> iteratorBackActivity(String taskId,
			ActivityImpl currActivity, List<ActivityImpl> rtnList,
			List<ActivityImpl> tempList) throws Exception;

	/** 
	 * ��ԭָ����ڵ����� 
	 *  
	 * @param activityImpl 
	 *            ��ڵ� 
	 * @param oriPvmTransitionList 
	 *            ԭ�нڵ����򼯺� 
	 */
	public abstract void restoreTransition(ActivityImpl activityImpl,
			List<PvmTransition> oriPvmTransitionList);

	/** 
	 * ��������list���ϣ����ڲ��ؽڵ㰴˳����ʾ 
	 *  
	 * @param list 
	 * @return 
	 */
	public abstract List<ActivityImpl> reverList(List<ActivityImpl> list);

	/** 
	 * ת������ 
	 *  
	 * @param taskId 
	 *            ��ǰ����ڵ�ID 
	 * @param userCode 
	 *            ��ת����Code 
	 */
	public abstract void transferAssignee(String taskId, String userCode);

	/** 
	 * ����ת����� 
	 *  
	 * @param taskId 
	 *            ��ǰ����ID 
	 * @param activityId 
	 *            Ŀ��ڵ�����ID 
	 * @param variables 
	 *            ���̱��� 
	 * @throws Exception 
	 */
	public abstract void turnTransition(String taskId, String activityId,
			Map<String, Object> variables) throws Exception;

	
	public abstract InputStream getImageStream(String taskId) throws Exception;
	
	
	/**
	 * ��¼
	 * @param userid
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public abstract boolean login(String userid,String password) throws Exception;
	
	/**
	 * ��ȡ�û���ϸ��Ϣ
	 * @param userid
	 * @return
	 */
	public abstract User getUserInfo(String userid);
	
	
	
	/**
	 * ��ȡ����ϸ��Ϣ
	 * @param groupid
	 * @return
	 */
	public abstract Group getGroupInfo(String groupid);
	
	
	/**
	 * �г��û���������
	 * @param userid
	 * @return
	 */
	public abstract List<Group> getUserOfGroup(String userid);
	
	
	/**
	 * ����groupId��ѯ���ڵ��û�
	 * @param groupId
	 * @return
	 */
	public abstract List<User> memberOfGroup(String groupId);
	
}