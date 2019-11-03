package com.shop.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.LeavebillMapper;
import com.shop.pojo.Leavebill;
import com.shop.utils.Constants;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private FormService formService;
	@Autowired
	private HistoryService historyService;

	@Autowired
	private LeavebillMapper leaveBillMapper;

	@Override
	public void saveNewDeploy(InputStream in, String processName) {

		try {
			ZipInputStream zipInputStream = new ZipInputStream(in);
			this.repositoryService.createDeployment().addZipInputStream(zipInputStream).name(processName).deploy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void startProcess(Long leaveId, String name) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", name);
		String key = Constants.Leave_KEY;
		// 定义规则
		String BUSSINSS_KEY = key + "." + leaveId;
		map.put("objId", BUSSINSS_KEY);
		// 指定任务的办理人
		map.put("name", name);

		// ProcessInstance processInstance =
		// runtimeService.startProcessInstanceByKey(Constants.Leave_KEY, map);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, BUSSINSS_KEY, map);
		System.out.println("流程启动成功:" + processInstance.getId() + "   " + processInstance.getBusinessKey() + "  "
				+ processInstance.getProcessDefinitionId() + "  " + processInstance.getProcessInstanceId());

	}

	@Override
	public List<Task> findTaskListByName(String name) {
		List<Task> list = taskService.createTaskQuery().taskAssignee(name).orderByTaskCreateTime().desc().list();
		return list;
	}

	@Override
	public Leavebill findLeaveBillByTaskId(String taskId) {
		// 从历史流程实例表中获取BUSINESS_KEY，切割出id
		// 根据任务id获取任务
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();

		// 根据任务中流程实例id去除流程实例对象
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();

		// 从流程实例对象中去除business_key
		String businessKey = processInstance.getBusinessKey();

		// 然后从bussinessKey中切割出 leavBill的主键id

		String id = "";
		if (businessKey != null && !"".equals(businessKey)) {
			id = businessKey.split("\\.")[1];
		}

		// 根据id 查询出当前请假单信息
		Leavebill leavebill = this.leaveBillMapper.selectByPrimaryKey(Long.parseLong(id));

		return leavebill;
	}

	@Override
	public List<Comment> findCommentListByTaskId(String taskId) {
		// 根据任务id获取任务
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		List<Comment> list = taskService.getProcessInstanceComments(task.getProcessInstanceId());
		return list;
	}

	@Override
	public void giveUpTask(long id, String taskId) {
		// String taskId = "12502";
		Map<String, Object> variables = new HashMap<>();
		variables.put("outcome", "放弃");
		// 根据任务ID去完成任务并指定流程变量
		taskService.complete(taskId, variables);
		// 更新请假单信息
		Leavebill leave = leaveBillMapper.selectByPrimaryKey(id);
		leave.setState(0);//表示结束状态，但是请假不成功
		leaveBillMapper.updateByPrimaryKey(leave);
		System.out.println("任务已放弃");
	}

	@Override
	public void rejectTask(long id, String taskId) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("outcome", "驳回");
		// 根据任务ID去完成任务并指定流程变量
		taskService.complete(taskId, variables);
		// 更新请假单信息
		Leavebill leave = leaveBillMapper.selectByPrimaryKey(id);
		leave.setState(3);//表示驳回状态，回到起始地方
		leaveBillMapper.updateByPrimaryKey(leave);
		System.out.println("任务已被驳回");
	}

	@Override
	public void submitTask(long id, String taskId, String comment, String username, String days) {
		// 根据任务id获取任务
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		// 设置当前任务的审核人
		Authentication.setAuthenticatedUserId(username);
		// 添加批注信息
		taskService.addComment(taskId, task.getProcessInstanceId(), comment);
		// 如果同意：流程往前推进一步
		Map<String, Object> variables = new HashMap<>();
		variables.put("outcome", "同意");
		variables.put("days", days);
		taskService.complete(taskId, variables);
		// 再判断请假时间，流程再往前推进一步
		// taskService.complete(taskId,variables);
		// 结束整个流程(更新leaveBill的state状态)
		// 根据任务中流程实例id去除流程实例对象
		// 获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 获取流程实例
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		if (pi == null) { // 流程结束
			Leavebill leave = leaveBillMapper.selectByPrimaryKey(id);
			// 设置业务的状态：审批结束 (2)，获得请假批准
			leave.setState(2);
			leaveBillMapper.updateByPrimaryKey(leave);
		}
	}

	@Override
	public Map<String, Object> findCoordingByTask(String taskId) {
		// 存放坐标
		Map<String, Object> map = new HashMap<String, Object>();
		// 使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程定义的ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()// 创建流程实例查询
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 获取当前活动的ID
		String activityId = pi.getActivityId();
		// 获取当前活动对象
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);// 活动ID
		// 获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		return map;
	}

	/** 使用部署对象ID和资源图片名称，获取图片的输入流 */
	@Override
	public InputStream findImageInputStream(String deploymentId, String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		// 使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		// 获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询对象，对应表act_re_procdef
				.processDefinitionId(processDefinitionId)// 使用流程定义ID查询
				.singleResult();
		return pd; // TODO Auto-generated method stub
	}

}
