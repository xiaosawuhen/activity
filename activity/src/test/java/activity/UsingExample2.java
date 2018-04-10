package activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;

public class UsingExample2 {

	ProcessEngine pe = ProcessEngines.getDefaultProcessEngine();

	// 根据流程定义启动流程实例----操作的数据表：act_ru_execution act_ru_task
	@Test
	public void testStartProcess() {
		String processDefinitionId = "myProcess:3:504";
		// 根据流程实例ID去启动流程
		ProcessInstance pInstance = pe.getRuntimeService().startProcessInstanceById(processDefinitionId);

		System.out.println(pInstance.getId());
	}

	/**
	 * 查询部署列表(同一个流程定义因为修改会进行多次的部署，版本号不一样version会增加，每次部署act_re_deployment表增加一条记录)
	 */
	@Test
	public void queryDeployment() {
		// 部署查询对象，查询表act_re_deployment
		DeploymentQuery query = pe.getRepositoryService().createDeploymentQuery();
		List<Deployment> list = query.list();
		for (Deployment deployment : list) {
			String id = deployment.getId();

			System.out.println(id + ":" + deployment.getName());
		}
	}

	/** 完成任务 */
	@Test
	public void completeTask() {

        //创建任务查询对象，查询表act_ru_task  
        TaskQuery query = pe.getTaskService().createTaskQuery();  
        String assignee ="003";  
        //添加过滤条件  
        query.taskAssignee(assignee);  
        //排序  
        query.orderByTaskCreateTime().desc();  
        List<Task> taskList = query.list();  
        for(Task task : taskList){  
    		// 任务ID
    		String taskId = task.getId();
    		// 完成任务的同时，设置流程变量，让流程变量判断连线该如何执行
    		Map<String, Object> variables = new HashMap<String, Object>();
    		// 其中message对应sequenceFlow.bpmn中的${message=='不重要'}，不重要对应流程变量的值
    		variables.put("message", "important");
    		pe.getTaskService().complete(taskId, variables);
    		System.out.println("完成任务：" + taskId); 
        }  
	}

    //查询任务列表  
    @Test  
    public void testQueryTaskList(){  
        //创建任务查询对象，查询表act_ru_task  
        TaskQuery query = pe.getTaskService().createTaskQuery();  
        String assignee ="001";  
        //添加过滤条件  
        query.taskAssignee(assignee);  
        //排序  
        query.orderByTaskCreateTime().desc();  
        List<Task> taskList = query.list();  
        for(Task task : taskList){  
        	System.out.println("==============================================");
        	System.out.println("getAssignee: " + task.getAssignee());
        	System.out.println("getAssignee: " + task.getCategory());
        	System.out.println("getDelegationState: " + task.getDelegationState());
        	System.out.println("getDescription: " + task.getDescription());
        	System.out.println("getDueDate: " + task.getDueDate());
        	System.out.println("getExecutionId: " + task.getExecutionId());
        	System.out.println("getId: " + task.getId());
        	System.out.println("getName: " + task.getName());
        	System.out.println("getOwner: " + task.getOwner());
        	System.out.println("getParentTaskId: " + task.getParentTaskId());
        	System.out.println("getPriority: " + task.getPriority());
        	System.out.println("getProcessDefinitionId: " + task.getProcessDefinitionId());
        	System.out.println("getProcessInstanceId: " + task.getProcessInstanceId());
        	task.getProcessVariables().forEach((k,v)->{ 
        	    System.out.println("ProcessVariablesItem : " + k + " Value : " + v);
        	});
        	
        	System.out.println("getTaskDefinitionKey: " + task.getTaskDefinitionKey());
        	task.getTaskLocalVariables().forEach((k,v)->{
        	    System.out.println("TaskLocalVariablesItem : " + k + " Value : " + v);
        	});
        	System.out.println("getTenantId: " + task.getTenantId());
        	
//            System.out.println("taskId:"+task.getId()+",taskName:"+task.getName());  
        }  
    }  
	
	/**
	 * 启动所有流程
	 */
	@Test
	public void start() {  
		
        // 创建部署构建器对象，用于加载流程定义文件(UserInfoAudit.bpmn,UserInfoAudit.myProcess.png)，部署流程定义  
        DeploymentBuilder deploymentBuilder = pe.getRepositoryService().createDeployment();  
        deploymentBuilder.addClasspathResource("process/MyProcess.bpmn");  
        Deployment deployment = deploymentBuilder.deploy();  
        System.out.println(deployment.getId() + "-->deploied");  

		// 流程定义查询对象，用于查询流程定义表----act_re_procdef  
        ProcessDefinitionQuery query = pe.getRepositoryService().createProcessDefinitionQuery();  
        // 添加过滤条件,取最新版本  
        query.latestVersion();  
        // query.processDefinitionId(processDefinitionId)  
        // query.processDefinitionKey(processDefinitionKey);  
        // 添加排序条件  
        query.orderByProcessDefinitionVersion().desc();  
  
        // 添加分页条件  
        // query.listPage(firstResult, maxResults);  
  
        // 查询所有流程  
        List<ProcessDefinition> processDefinitionList = query.list();  
        for (ProcessDefinition pd : processDefinitionList) {  
            ProcessInstance pInstance = pe.getRuntimeService().startProcessInstanceById(pd.getId());
            System.out.println(pd.getId() + "-->started");
        }  
	}
}
