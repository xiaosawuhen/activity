package org.activiti.web.simple.webapp.service.impl;

import org.activiti.web.simple.webapp.dao.LeaveRepository;
import org.activiti.web.simple.webapp.model.Leave;
import org.activiti.web.simple.webapp.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA�������ʵ��
 * @author scott
 *
 */
@Service("leaveServiceImpl")
@Transactional(propagation=Propagation.REQUIRED)
public class LeaveServiceImpl implements LeaveService {

	@Autowired
	private LeaveRepository leaveRepository;

	@Transactional(propagation=Propagation.REQUIRED)
	public void save(Leave leave) {
		leaveRepository.save(leave);
	}

	/**
	 * ����id��ѯʵ�����
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public Leave findById(String id) {
		return leaveRepository.findOne(Long.parseLong(id));
	}

}
