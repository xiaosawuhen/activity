package org.activiti.web.simple.webapp.dao;

import org.activiti.web.simple.webapp.model.Leave;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * ʹ��Spring Data JPA�����������
 * @author scott
 *
 */
public interface LeaveRepository extends PagingAndSortingRepository<Leave, Long>{
	
	
	
}
