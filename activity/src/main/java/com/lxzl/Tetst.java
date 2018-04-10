package com.lxzl;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;

public class Tetst {

	public static void main(String[] args) {
		new Tetst().test();
	}
	
	public void test() { 
        ProcessEngineConfiguration config = ProcessEngineConfiguration  
                .createProcessEngineConfigurationFromResource("activiti.cfg.xml", "processEngineConfiguration");  
        ProcessEngine pe = config.buildProcessEngine();  
        
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
            System.out.println(pd.getId());  
        }  
  
    
	}
}
