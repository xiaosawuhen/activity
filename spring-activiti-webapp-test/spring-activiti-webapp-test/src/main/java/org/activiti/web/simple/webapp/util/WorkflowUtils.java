package org.activiti.web.simple.webapp.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author scott
 *
 */
public class WorkflowUtils {

	/**
	 * ת�����̽ڵ�����Ϊ����˵��
	 * @param type	Ӣ������
	 * @return	��������������
	 */
	public static String parseToZhType(String type) {
		Map<String, String> types = new HashMap<String, String>();
		types.put("userTask", "�û�����");
		types.put("serviceTask", "ϵͳ����");
		types.put("startEvent", "��ʼ�ڵ�");
		types.put("endEvent", "�����ڵ�");
		types.put("exclusiveGateway", "�����жϽڵ�(ϵͳ�Զ�������������)");
		types.put("inclusiveGateway", "���д�������");
		types.put("callActivity", "������");
		return types.get(type) == null ? type: types.get(type);
	}
	
}
