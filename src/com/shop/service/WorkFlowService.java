package com.shop.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import com.shop.pojo.Leavebill;

public interface WorkFlowService {

	/**
	 * ��������
	 * 
	 * @param in
	 * @param processName
	 */
	void saveNewDeploy(InputStream in, String processName);

	/**
	 * ��������
	 * @param leaveId 
	 * 
	 * @param name
	 */
	void startProcess(Long leaveId, String name);

	/**
	 * ��������
	 * 
	 * @param name
	 * @return
	 */
	List<Task> findTaskListByName(String name);

	/**
	 * ��������id��ѯ��ٵ�
	 * 
	 * @param taskId
	 * @return
	 */
	Leavebill findLeaveBillByTaskId(String taskId);

	/**
	 * ����taskId��ѯ����ע��������Ϣ
	 * 
	 * @param taskId
	 * @return
	 */
	List<Comment> findCommentListByTaskId(String taskId);
	
	/**
	 * �ύ��ע���������
	 * 
	 * @param id
	 * @param taskId
	 * @param comment
	 * @param username
	 */
	public void submitTask(long id,String taskId,String comment,String username,String days);
	
	Map<String, Object> findCoordingByTask(String taskId);

	InputStream findImageInputStream(String deploymentId, String imageName);

	ProcessDefinition findProcessDefinitionByTaskId(String taskId);

	void giveUpTask(long id, String taskId);

	/**
	 * ��������
	 * 
	 * @param id
	 * @param taskId
	 */
	void rejectTask(long id, String taskId);

}
