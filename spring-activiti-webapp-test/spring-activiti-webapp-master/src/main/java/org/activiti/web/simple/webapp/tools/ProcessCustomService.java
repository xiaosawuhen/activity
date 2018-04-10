package org.activiti.web.simple.webapp.tools;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
  
  
/** 
 * ���̲���������<br> 
 * �˺�������Ҫ��������ͨ�������ء�ת�졢��ֹ������Ⱥ��Ĳ���<br> 
 *  
 *  
 */  
public class  ProcessCustomService{  
	
	private static ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	private static RepositoryService repositoryService = processEngine.getRepositoryService();  
  
	private static RuntimeService runtimeService = processEngine.getRuntimeService();

	private static TaskService taskService = processEngine.getTaskService();

	private static FormService formService = processEngine.getFormService();

	private static HistoryService historyService = processEngine.getHistoryService();


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
    public static void backProcess(String taskId, String activityId,  
            Map<String, Object> variables) throws Exception {  
        if (StringUtils.isEmpty(activityId)) {  
            throw new Exception("����Ŀ��ڵ�IDΪ�գ�");  
        }  
  
        // �������в�������ڵ㣬ͬʱ����  
        List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(  
                taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());  
        for (Task task : taskList) {  
            commitProcess(task.getId(), variables, activityId);  
        }  
    }


	/** 
     * ȡ������ 
     *  
     * @param taskId 
     *            ��ǰ����ID 
     * @param activityId 
     *            ȡ�ؽڵ�ID 
     * @throws Exception 
     */  
    public static void callBackProcess(String taskId, String activityId)  
            throws Exception {  
        if (StringUtils.isEmpty(activityId)) {  
            throw new Exception("Ŀ��ڵ�IDΪ�գ�");  
        }  
  
        // �������в�������ڵ㣬ͬʱȡ��  
        List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(  
                taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());  
        for (Task task : taskList) {  
            commitProcess(task.getId(), null, activityId);  
        }  
    }


	/** 
     * ���ָ����ڵ����� 
     *  
     * @param activityImpl 
     *            ��ڵ� 
     * @return �ڵ����򼯺� 
     */  
    private static List<PvmTransition> clearTransition(ActivityImpl activityImpl) {  
        // �洢��ǰ�ڵ�����������ʱ����  
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();  
        // ��ȡ��ǰ�ڵ��������򣬴洢����ʱ������Ȼ�����  
        List<PvmTransition> pvmTransitionList = activityImpl  
                .getOutgoingTransitions();  
        for (PvmTransition pvmTransition : pvmTransitionList) {  
            oriPvmTransitionList.add(pvmTransition);  
        }  
        pvmTransitionList.clear();  
  
        return oriPvmTransitionList;  
    }


	/** 
     * @param taskId 
     *            ��ǰ����ID 
     * @param variables 
     *            ���̱��� 
     * @param activityId 
     *            ����ת��ִ������ڵ�ID<br> 
     *            �˲���Ϊ�գ�Ĭ��Ϊ�ύ���� 
     * @throws Exception 
     */  
    private static void commitProcess(String taskId, Map<String, Object> variables,  
            String activityId) throws Exception {  
        if (variables == null) {  
            variables = new HashMap<String, Object>();  
        }  
        // ��ת�ڵ�Ϊ�գ�Ĭ���ύ����  
        if (StringUtils.isEmpty(activityId)) {  
            taskService.complete(taskId, variables);  
        } else {// ����ת�����  
            turnTransition(taskId, activityId, variables);  
        }  
    }


	/** 
     * ��ֹ����(��Ȩ��ֱ������ͨ����) 
     *  
     * @param taskId 
     */  
    public static void endProcess(String taskId) throws Exception {  
        ActivityImpl endActivity = findActivitiImpl(taskId, "end");  
        commitProcess(taskId, null, endActivity.getId());  
    }


	/** 
     * �����������񼯺ϣ���ѯ���һ�ε���������ڵ� 
     *  
     * @param processInstance 
     *            ����ʵ�� 
     * @param tempList 
     *            �������񼯺� 
     * @return 
     */  
    private static ActivityImpl filterNewestActivity(ProcessInstance processInstance,  
            List<ActivityImpl> tempList) {  
        while (tempList.size() > 0) {  
            ActivityImpl activity_1 = tempList.get(0);  
            HistoricActivityInstance activityInstance_1 = findHistoricUserTask(  
                    processInstance, activity_1.getId());  
            if (activityInstance_1 == null) {  
                tempList.remove(activity_1);  
                continue;  
            }  
  
            if (tempList.size() > 1) {  
                ActivityImpl activity_2 = tempList.get(1);  
                HistoricActivityInstance activityInstance_2 = findHistoricUserTask(  
                        processInstance, activity_2.getId());  
                if (activityInstance_2 == null) {  
                    tempList.remove(activity_2);  
                    continue;  
                }  
  
                if (activityInstance_1.getEndTime().before(  
                        activityInstance_2.getEndTime())) {  
                    tempList.remove(activity_1);  
                } else {  
                    tempList.remove(activity_2);  
                }  
            } else {  
                break;  
            }  
        }  
        if (tempList.size() > 0) {  
            return tempList.get(0);  
        }  
        return null;  
    }


	/** 
     * ��������ID�ͽڵ�ID��ȡ��ڵ� <br> 
     *  
     * @param taskId 
     *            ����ID 
     * @param activityId 
     *            ��ڵ�ID <br> 
     *            ���Ϊnull��""����Ĭ�ϲ�ѯ��ǰ��ڵ� <br> 
     *            ���Ϊ"end"�����ѯ�����ڵ� <br> 
     *  
     * @return 
     * @throws Exception 
     */  
    private static ActivityImpl findActivitiImpl(String taskId, String activityId)  
            throws Exception {  
        // ȡ�����̶���  
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);  
  
        // ��ȡ��ǰ��ڵ�ID  
        if (StringUtils.isEmpty(activityId)) {  
            activityId = findTaskById(taskId).getTaskDefinitionKey();  
        }  
  
        // �������̶��壬��ȡ������ʵ���Ľ����ڵ�  
        if (activityId.toUpperCase().equals("END")) {  
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {  
                List<PvmTransition> pvmTransitionList = activityImpl  
                        .getOutgoingTransitions();  
                if (pvmTransitionList.isEmpty()) {  
                    return activityImpl;  
                }  
            }  
        }  
  
        // ���ݽڵ�ID����ȡ��Ӧ�Ļ�ڵ�  
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)  
                .findActivity(activityId);  
  
        return activityImpl;  
    }


	/** 
     * ���ݵ�ǰ����ID����ѯ���Բ��ص�����ڵ� 
     *  
     * @param taskId 
     *            ��ǰ����ID 
     */  
    public static List<ActivityImpl> findBackAvtivity(String taskId) throws Exception {  
        List<ActivityImpl> rtnList =  iteratorBackActivity(taskId, findActivitiImpl(taskId,  
                    null), new ArrayList<ActivityImpl>(),  
                    new ArrayList<ActivityImpl>());  
        return reverList(rtnList);  
    }

	/** 
     * ��ѯָ������ڵ�����¼�¼ 
     *  
     * @param processInstance 
     *            ����ʵ�� 
     * @param activityId 
     * @return 
     */  
    private static HistoricActivityInstance findHistoricUserTask(  
            ProcessInstance processInstance, String activityId) {  
        HistoricActivityInstance rtnVal = null;  
        // ��ѯ��ǰ����ʵ��������������ʷ�ڵ�  
        List<HistoricActivityInstance> historicActivityInstances = historyService  
                .createHistoricActivityInstanceQuery().activityType("userTask")  
                .processInstanceId(processInstance.getId()).activityId(  
                        activityId).finished()  
                .orderByHistoricActivityInstanceEndTime().desc().list();  
        if (historicActivityInstances.size() > 0) {  
            rtnVal = historicActivityInstances.get(0);  
        }  
  
        return rtnVal;  
    }  
  
	/** 
     * ���ݵ�ǰ�ڵ㣬��ѯ��������Ƿ�Ϊ�����յ㣬���Ϊ�����յ㣬��ƴװ��Ӧ�Ĳ������ID 
     *  
     * @param activityImpl 
     *            ��ǰ�ڵ� 
     * @return 
     */  
    private static String findParallelGatewayId(ActivityImpl activityImpl) {  
        List<PvmTransition> incomingTransitions = activityImpl  
                .getOutgoingTransitions();  
        for (PvmTransition pvmTransition : incomingTransitions) {  
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;  
            activityImpl = transitionImpl.getDestination();  
            String type = (String) activityImpl.getProperty("type");  
            if ("parallelGateway".equals(type)) {// ����·��  
                String gatewayId = activityImpl.getId();  
                String gatewayType = gatewayId.substring(gatewayId  
                        .lastIndexOf("_") + 1);  
                if ("END".equals(gatewayType.toUpperCase())) {  
                    return gatewayId.substring(0, gatewayId.lastIndexOf("_"))  
                            + "_start";  
                }  
            }  
        }  
        return null;  
    }  
  
	/** 
     * ��������ID��ȡ���̶��� 
     *  
     * @param taskId 
     *            ����ID 
     * @return 
     * @throws Exception 
     */  
    public static ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(  
            String taskId) throws Exception {  
        // ȡ�����̶���  
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)  
                .getDeployedProcessDefinition(findTaskById(taskId)  
                        .getProcessDefinitionId());  
  
        if (processDefinition == null) {  
            throw new Exception("���̶���δ�ҵ�!");  
        }  
  
        return processDefinition;  
    }  
  
	/** 
     * ��������ID��ȡ��Ӧ������ʵ�� 
     *  
     * @param taskId 
     *            ����ID 
     * @return 
     * @throws Exception 
     */  
    public static ProcessInstance findProcessInstanceByTaskId(String taskId)  
            throws Exception {  
        // �ҵ�����ʵ��  
        ProcessInstance processInstance = runtimeService  
                .createProcessInstanceQuery().processInstanceId(  
                        findTaskById(taskId).getProcessInstanceId())  
                .singleResult();  
        if (processInstance == null) {   
            throw new Exception("����ʵ��δ�ҵ�!");  
        }  
        return processInstance;  
    }  
  
    /** 
     * ��������ID�������ʵ�� 
     *  
     * @param taskId 
     *            ����ID 
     * @return 
     * @throws Exception 
     */  
    private static TaskEntity findTaskById(String taskId) throws Exception {  
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(  
                taskId).singleResult();  
        if (task == null) {  
            throw new Exception("����ʵ��δ�ҵ�!");  
        }  
        return task;  
    }  
  
  
    /** 
     * ��������ʵ��ID������keyֵ��ѯ����ͬ�����񼯺� 
     *  
     * @param processInstanceId 
     * @param key 
     * @return 
     */  
    private static List<Task> findTaskListByKey(String processInstanceId, String key) {  
        return taskService.createTaskQuery().processInstanceId(  
                processInstanceId).taskDefinitionKey(key).list();  
    }  
  
  
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
    private static List<ActivityImpl> iteratorBackActivity(String taskId,  
            ActivityImpl currActivity, List<ActivityImpl> rtnList,  
            List<ActivityImpl> tempList) throws Exception {  
        // ��ѯ���̶��壬�����������ṹ  
        ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);  
  
        // ��ǰ�ڵ��������Դ  
        List<PvmTransition> incomingTransitions = currActivity  
                .getIncomingTransitions();  
        // ������֧�ڵ㼯�ϣ�userTask�ڵ������ϣ����������˼��ϣ���ѯ������֧��Ӧ��userTask�ڵ�  
        List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();  
        // ���нڵ㼯�ϣ�userTask�ڵ������ϣ����������˼��ϣ���ѯ���нڵ��Ӧ��userTask�ڵ�  
        List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();  
        // ������ǰ�ڵ���������·��  
        for (PvmTransition pvmTransition : incomingTransitions) {  
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;  
            ActivityImpl activityImpl = transitionImpl.getSource();  
            String type = (String) activityImpl.getProperty("type");  
            /** 
             * ���нڵ�����Ҫ��<br> 
             * ����ɶԳ��֣���Ҫ��ֱ����ýڵ�IDΪ:XXX_start(��ʼ)��XXX_end(����) 
             */  
            if ("parallelGateway".equals(type)) {// ����·��  
                String gatewayId = activityImpl.getId();  
                String gatewayType = gatewayId.substring(gatewayId  
                        .lastIndexOf("_") + 1);  
                if ("START".equals(gatewayType.toUpperCase())) {// ������㣬ֹͣ�ݹ�  
                    return rtnList;  
                } else {// �����յ㣬��ʱ�洢�˽ڵ㣬����ѭ���������������ϣ���ѯ��Ӧ��userTask�ڵ�  
                    parallelGateways.add(activityImpl);  
                }  
            } else if ("startEvent".equals(type)) {// ��ʼ�ڵ㣬ֹͣ�ݹ�  
                return rtnList;  
            } else if ("userTask".equals(type)) {// �û�����  
                tempList.add(activityImpl);  
            } else if ("exclusiveGateway".equals(type)) {// ��֧·�ߣ���ʱ�洢�˽ڵ㣬����ѭ���������������ϣ���ѯ��Ӧ��userTask�ڵ�  
                currActivity = transitionImpl.getSource();  
                exclusiveGateways.add(currActivity);  
            }  
        }  
  
        /** 
         * ����������֧���ϣ���ѯ��Ӧ��userTask�ڵ� 
         */  
        for (ActivityImpl activityImpl : exclusiveGateways) {  
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);  
        }  
  
        /** 
         * �������м��ϣ���ѯ��Ӧ��userTask�ڵ� 
         */  
        for (ActivityImpl activityImpl : parallelGateways) {  
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);  
        }  
  
        /** 
         * ����ͬ��userTask���ϣ�������������Ľڵ� 
         */  
        currActivity = filterNewestActivity(processInstance, tempList);  
        if (currActivity != null) {  
            // ��ѯ��ǰ�ڵ�������Ƿ�Ϊ�����յ㣬����ȡ�������ID  
            String id = findParallelGatewayId(currActivity);  
            if (StringUtils.isEmpty(id)) {// �������IDΪ�գ��˽ڵ������ǲ����յ㣬���ϲ����������洢�˽ڵ�  
                rtnList.add(currActivity);  
            } else {// ���ݲ������ID��ѯ��ǰ�ڵ㣬Ȼ�������ѯ���Ӧ��userTask����ڵ�  
                currActivity = findActivitiImpl(taskId, id);  
            }  
  
            // ��ձ��ε�����ʱ����  
            tempList.clear();  
            // ִ���´ε���  
            iteratorBackActivity(taskId, currActivity, rtnList, tempList);  
        }  
        return rtnList;  
    }  
  
  
    /** 
     * ��ԭָ����ڵ����� 
     *  
     * @param activityImpl 
     *            ��ڵ� 
     * @param oriPvmTransitionList 
     *            ԭ�нڵ����򼯺� 
     */  
    private static void restoreTransition(ActivityImpl activityImpl,  
            List<PvmTransition> oriPvmTransitionList) {  
        // �����������  
        List<PvmTransition> pvmTransitionList = activityImpl  
                .getOutgoingTransitions();  
        pvmTransitionList.clear();  
        // ��ԭ��ǰ����  
        for (PvmTransition pvmTransition : oriPvmTransitionList) {  
            pvmTransitionList.add(pvmTransition);  
        }  
    }  
  
    /** 
     * ��������list���ϣ����ڲ��ؽڵ㰴˳����ʾ 
     *  
     * @param list 
     * @return 
     */  
    private static List<ActivityImpl> reverList(List<ActivityImpl> list) {  
        List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();  
        // ���ڵ��������ظ����ݣ��ų��ظ�  
        for (int i = list.size(); i > 0; i--) {  
            if (!rtnList.contains(list.get(i - 1)))  
                rtnList.add(list.get(i - 1));  
        }  
        return rtnList;  
    }  
  
    /** 
     * ת������ 
     *  
     * @param taskId 
     *            ��ǰ����ڵ�ID 
     * @param userCode 
     *            ��ת����Code 
     */  
    public static void transferAssignee(String taskId, String userCode) {  
        taskService.setAssignee(taskId, userCode);  
    }  
  
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
    private static void turnTransition(String taskId, String activityId,  
            Map<String, Object> variables) throws Exception {  
        // ��ǰ�ڵ�  
        ActivityImpl currActivity = findActivitiImpl(taskId, null);  
        // ��յ�ǰ����  
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);  
  
        // ����������  
        TransitionImpl newTransition = currActivity.createOutgoingTransition();  
        // Ŀ��ڵ�  
        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);  
        // �����������Ŀ��ڵ�  
        newTransition.setDestination(pointActivity);  
  
        // ִ��ת������  
        taskService.complete(taskId, variables);  
        // ɾ��Ŀ��ڵ�������  
        pointActivity.getIncomingTransitions().remove(newTransition);  
  
        // ��ԭ��ǰ����  
        restoreTransition(currActivity, oriPvmTransitionList);  
    }  
    
    public static InputStream getImageStream(String taskId) throws Exception{
    	ProcessDefinitionEntity pde = findProcessDefinitionEntityByTaskId(taskId);
    	InputStream imageStream = ProcessDiagramGenerator.generateDiagram(  
				pde, "png",  
		runtimeService.getActiveActivityIds(findProcessInstanceByTaskId(taskId).getId()));
    	return imageStream;
    }
    
    public static FormService getFormService() {
		return formService;
	}  
  
    public static HistoryService getHistoryService() {
		return historyService;
	}  
  
  
    public static ProcessEngine getProcessEngine() {
		return processEngine;
	}  
  
    public static RepositoryService getRepositoryService() {
		return repositoryService;
	}  
  
    public static RuntimeService getRuntimeService() {
		return runtimeService;
	}  
  
    public static TaskService getTaskService() {
		return taskService;
	}
}