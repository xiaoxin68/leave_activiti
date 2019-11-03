package com.shop.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.shop.pojo.Employee;
import com.shop.pojo.Leavebill;
import com.shop.service.EmployeeService;
import com.shop.service.LeaveBillService;
import com.shop.service.WorkFlowService;
import com.shop.utils.Constants;

/**
 * 流程管理
 * 
 * @Description:
 * @Source: JDK 1.8
 * @Author: ZhangXiaoxin
 * @Date: 2019年10月20日
 * @Since: 1.0
 */
@Controller
public class WorkFlowController {

	@Autowired
	private WorkFlowService workFlowService;

	@Autowired
	private LeaveBillService leaveBillService;
	
	@Autowired
	private EmployeeService employeeService;

	/**
	 * 部署流程
	 * 
	 * @param fileName
	 * @param processName
	 * @return
	 */
	@RequestMapping(value = "/deployProcess")
	public String deployProcess(MultipartFile fileName, String processName) {

		try {
			this.workFlowService.saveNewDeploy(fileName.getInputStream(), processName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "add_process";
	}

	/**
	 * 启动流程并指定下一个任务的办理人
	 * 
	 * @param leavebill
	 * @param session
	 * @return
	 */
	@RequestMapping("/saveStartLeave")
	public String saveStartLeave(Leavebill leavebill, HttpSession session) {

		// 将请假业务信息插入到leaveBill表中
		leavebill.setState(1);// 1:表示当前流程正在运行；2表示结束
		Employee employee = (Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		leavebill.setUserId(employee.getId());
		leaveBillService.saveLeaveBill(leavebill);
		// 启动·流程（待办人）
		// 启动当前流程:必须要分配当前待办值
		// workFlowService.startProcess(employee.getName());
		workFlowService.startProcess(leavebill.getId(), employee.getName());
		return "redirect:/taskList";
	}

	/**
	 * 我的待办任务
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("/taskList")
	public ModelAndView taskList(HttpSession session) {
		String name = ((Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION)).getName();
		List<Task> taskList = workFlowService.findTaskListByName(name);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("taskList", taskList);
		modelAndView.setViewName("workflow_task");// 跳转到workflow_task
		return modelAndView;
	}

	@RequestMapping("/viewTaskForm")
	public ModelAndView viewTaskForm(String taskId,HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		// 根据taskId查询出LeaveBill
		Leavebill leavebill = workFlowService.findLeaveBillByTaskId(taskId);
		modelAndView.addObject("bill", leavebill);
		modelAndView.addObject("id", leavebill.getId());
		modelAndView.addObject("taskId", taskId);
		//获取当前任务用户的所有上级
		Employee employee = (Employee)session.getAttribute(Constants.GLOBLE_USER_SESSION);
		List<Employee> managerList = employeeService.findAllEmployeeByManageId(employee.getManagerId());
		modelAndView.addObject("managerList", managerList);
		//设置是否显示放弃和驳回框框
		modelAndView.addObject("employee", employee);
		// 根据taskId查询出批注审批表信息
		List<Comment> commentList = workFlowService.findCommentListByTaskId(taskId);
		modelAndView.addObject("commentList", commentList);
		modelAndView.setViewName("approve_leave");// 展示请假单信息
		return modelAndView;
	}

	/**
	 * 放弃任务，直接结束
	 * 
	 * @param id
	 * @param taskId
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/giveUpTask")
	public String giveUpTask(long id, String taskId, HttpSession session) {
		Employee employee = (Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		workFlowService.giveUpTask(id, taskId);
		return "redirect:/taskList";
	}
	
	/**
	 * 驳回申请
	 * 
	 * @param id
	 * @param taskId
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/rejectTask")
	public String rejectTask(long id, String taskId, HttpSession session) {
		Employee employee = (Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		workFlowService.rejectTask(id, taskId);
		return "redirect:/taskList";
	}
	
	/**
	 * 提交批注
	 * 
	 * @param id
	 * @param taskId
	 * @param comment
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/submitTask")
	public String submitTask(long id, String taskId, String comment,String assignee, HttpSession session,String days) {
		Employee employee = (Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		//设置上一级审批人
		session.setAttribute(Constants.MANAGER_ASSIGN, assignee);
		workFlowService.submitTask(id, taskId, comment, employee.getName(),days);
		return "redirect:/taskList";
	}

	/**
	 * 查看当前流程图（查看当前活动节点，并使用红色的框标注）
	 */
	@RequestMapping("/viewCurrentImage")
	public String viewCurrentImage(String taskId,ModelMap model){
		/**一：查看流程图*/
		//1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = workFlowService.findProcessDefinitionByTaskId(taskId);

		model.addAttribute("deploymentId", pd.getDeploymentId());
		model.addAttribute("imageName", pd.getDiagramResourceName());
		/**二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
		Map<String, Object> map = workFlowService.findCoordingByTask(taskId);

		model.addAttribute("acs", map);
		return "viewimage";
	}
	
	/**
	 * 查看流程图
	 * @throws Exception 
	 */
	@RequestMapping("/viewImage")
	public String viewImage(String deploymentId,String imageName,HttpServletResponse response) throws Exception{

		//2：获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
		InputStream in = workFlowService.findImageInputStream(deploymentId,imageName);
		//3：从response对象获取输出流
		OutputStream out = response.getOutputStream();
		//4：将输入流中的数据读取出来，写到输出流中
		for(int b=-1;(b=in.read())!=-1;){
			out.write(b);
		}
		out.close();
		in.close();
		return null;
	}

}
