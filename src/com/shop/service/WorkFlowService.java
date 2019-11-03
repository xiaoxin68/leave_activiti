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
	 * 部署流程
	 * 
	 * @param in
	 * @param processName
	 */
	void saveNewDeploy(InputStream in, String processName);

	/**
	 * 启动流程
	 * @param leaveId 
	 * 
	 * @param name
	 */
	void startProcess(Long leaveId, String name);

	/**
	 * 待办事务
	 * 
	 * @param name
	 * @return
	 */
	List<Task> findTaskListByName(String name);

	/**
	 * 根据任务id查询请假单
	 * 
	 * @param taskId
	 * @return
	 */
	Leavebill findLeaveBillByTaskId(String taskId);

	/**
	 * 根据taskId查询出批注审批表信息
	 * 
	 * @param taskId
	 * @return
	 */
	List<Comment> findCommentListByTaskId(String taskId);
	
	/**
	 * 提交批注和完成流程
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
	 * 驳回申请
	 * 
	 * @param id
	 * @param taskId
	 */
	void rejectTask(long id, String taskId);

}
