package com.yesee.gov.website.controller.rest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbProjectItem;
import com.yesee.gov.website.model.TbProjectItemSort;
import com.yesee.gov.website.model.TbWorkItem;
import com.yesee.gov.website.service.ProjectItemService;
import com.yesee.gov.website.service.ProjectService;
import com.yesee.gov.website.service.WorkItemService;
@RestController
@RequestMapping(value = "/rest/workItem")
public class RestWorkItemController {
	private static final Logger logger = LogManager.getLogger(RestWorkItemController.class);
	@Autowired
	private WorkItemService workItemService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private ProjectItemService projectItemService;
	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * 取得當日剩餘工時。
	 * 接收session中Account資料透過workItemService.getMainHour取得當日剩餘工時資料並回傳至前端。
	 */
	@PostMapping("/getMainHour")
	public Response getMainHour(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		String date = req.getParameter("date");
		float hour = workItemService.getMainHour(account, date);
		return Response.ok(new BigDecimal(String.valueOf(hour)).stripTrailingZeros().toPlainString(),
				MediaType.APPLICATION_JSON_TYPE).build();
	}
	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * 取得已簽核專案資訊。
	 * 透過workItemService.getRecentItem取得最近紀錄的三種專案。
	 * 透過workItemService.getProjectName取得可見專案資料後整合並回傳至前端。
	 */
//	@PostMapping("/getProjects")
//	public Response getProjects(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//		String account = (String) req.getSession().getAttribute("Account");
//		ArrayList<String> list = workItemService.getRecentItem(account);
//		List<TbProject> project= workItemService.getPersonalProject(account);
//		Map<String, Object> map = new LinkedHashMap<String, Object>();
//		for (int i = 0; i < project.size(); i++) {
//			if(!"deleted".equals(project.get(i).getDevStatus())) {
//				map.put(String.valueOf(project.get(i).getId()), project.get(i).getName());
//			}
//		}
//		Map<String, Object> map = workItemService.getProjectName("!deleted");
//		JSONArray data = new JSONArray();
//		for (int i = 0; i < list.size(); i++) {
//			if (map.containsKey(list.get(i))) {
//				JSONObject object = new JSONObject();
//				object.put("id", list.get(i));
//				object.put("name", map.get(list.get(i)));
//				object.put("visible", "Y");
//				data.put(object);
//				map.remove(list.get(i));
//			}
//		}
//		for (Object id : map.keySet()) {
//			JSONObject object = new JSONObject();
//			object.put("id", id);
//			object.put("name", map.get(id));
//			object.put("visible", "Y");
//			data.put(object);
//		}
//		List<TbProject> all = projectService.getAllProject();
//		for (int i = 0; i < all.size(); i++) {
//			JSONObject object = new JSONObject();
//			object.put("id", all.get(i).getId());
//			object.put("name", all.get(i).getName());
//			object.put("visible", "N");
//			data.put(object);
//		}
//		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
//	}
	@PostMapping("/getProjects")
	public Response getProjects(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		ArrayList<String> list = workItemService.getRecentItem(account);
		Map<String, Object> map = workItemService.getProjectName("!deleted");
		JSONArray data = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			if (map.containsKey(list.get(i))) {
				JSONObject object = new JSONObject();
				object.put("id", list.get(i));
				object.put("name", map.get(list.get(i)));
				object.put("deleted", "N");
				data.put(object);
				map.remove(list.get(i));
			}
		}
		for (Object id : map.keySet()) {
			JSONObject object = new JSONObject();
			object.put("id", id);
			object.put("name", map.get(id));
			object.put("deleted", "N");
			data.put(object);
		}
		map = workItemService.getProjectName("deleted");
		for (Object id : map.keySet()) {
			JSONObject object = new JSONObject();
			object.put("id", id);
			object.put("name", map.get(id));
			object.put("deleted", "Y");
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
	@PostMapping("/getProjectItems")
	public Response getProjectItems(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		List<TbProjectItem> list = projectItemService.getAllValidList();
		JSONArray data = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("projectId", list.get(i).getTbProject().getId());
			object.put("id", list.get(i).getItemId());
			object.put("name", list.get(i).getName());
			object.put("status", list.get(i).getStatus());
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	@PostMapping("/getItemSorts")
	public Response getItemSorts(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		List<TbProjectItemSort> list = workItemService.getItemSort();
		JSONArray data = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("id", list.get(i).getId());
			object.put("name", list.get(i).getName());
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * 取得工時資訊。
	 * 接受前端資料透過orkItemService.getRecords取得工時資料並回傳至前端。
	 */
	@PostMapping("/getRecords")
	public Response getRecords(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = req.getParameter("id");
		Integer year = Integer.parseInt(req.getParameter("year"));
		Integer month = Integer.parseInt(req.getParameter("month"));
		SimpleDateFormat jd = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DATE, -1);
		Date startDate = cal.getTime();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DATE, -1);
		Date endDate = cal.getTime();
		List<TbProject> all = projectService.getAllProject();
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		for(int i = 0; i < all.size(); i++) {
			map.put(all.get(i).getId(), all.get(i).getName());
		}
		List<TbWorkItem> list = workItemService.getRecords(account, startDate, endDate);
		Iterator<TbWorkItem> it = list.iterator();
		JSONArray data = new JSONArray();
		while (it.hasNext()) {
			TbWorkItem record = it.next();
			JSONObject object = new JSONObject();
			object.put("id", record.getId());
			object.put("date", jd.format(record.getDate()));
			object.put("chineseName", record.getTbEmployees().getChineseName());
			object.put("emp", record.getTbEmployees().getUsername());
			
			object.put("hour",String.valueOf(record.getWorkHr()));
			object.put("project", map.get(record.getProId()));
			if (record.getNote() != null) {
				object.put("note", record.getNote());
			} else {
				object.put("note", "");
			}
			object.put("projectId", record.getProId());
			if(record.getTbProjectItem() == null) {
				object.put("itemId", 0);
				object.put("item", "");
			}else {
				object.put("itemId", record.getTbProjectItem().getItemId());
				object.put("item", record.getTbProjectItem().getName());
			}
			if(record.getTbProjectItemSort() == null) {
				object.put("sortId", 0);
				object.put("sort", "");
			}else {
				object.put("sortId", record.getTbProjectItemSort().getId());
				object.put("sort", record.getTbProjectItemSort().getName());
			}
			object.put("createdAt", record.getCreatedAt());
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
	/**
	 * @param req
	 * @param resp
	 * @throws Exception
	 * 新增工時資料。
	 * 接收前端傳回的工時資訊並透過workItemService.save儲存。
	 */
	@PostMapping("/add")
	public void saveWork(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		String date = req.getParameter("date");
		String project = req.getParameter("project");
		String hour = req.getParameter("hour");
		String note = req.getParameter("note");
		Date createdAt = new Date();
		SimpleDateFormat jd = new SimpleDateFormat("yyyy-MM-dd");
		TbWorkItem record = new TbWorkItem();
		record.setTbEmployees(new TbEmployees());
		record.getTbEmployees().setUsername(account);
		record.setDate(jd.parse(date));
		record.setProId(Integer.parseInt(project));
		record.setWorkHr(Float.parseFloat(hour));
		if (note.length() < 1) {
			record.setNote(null);
		} else {
			record.setNote(note);
		}
		record.setCreatedAt(createdAt);
		record.setStatus("CREATED");
		logger.info("saveWork account:" + account);
		logger.info("date:" + date);
		logger.info("project:" + project);
		logger.info("hour:" + hour);
		logger.info("note:" + note);
		workItemService.save(record);
		logger.info("workItem Save Success");
	}

//	public Response saveWork(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//		String account = (String) req.getSession().getAttribute("Account");
//		String date = req.getParameter("date");
//		String project = req.getParameter("project");
//		String item = req.getParameter("item");
//		String sort = req.getParameter("sort");
//		String hour = req.getParameter("hour");
//		String note = req.getParameter("note");
//		Date createdAt = new Date();
//		SimpleDateFormat jd = new SimpleDateFormat("yyyy-MM-dd");
//		TbWorkItem record = new TbWorkItem();
//		record.setTbEmployees(new TbEmployees());
//		record.getTbEmployees().setUsername(account);
//		record.setDate(jd.parse(date));
//		record.setProId(Integer.parseInt(project));
//		if(!item.equals("0")) {
//			record.setTbProjectItem(new TbProjectItem());
//			record.getTbProjectItem().setItemId(Integer.valueOf(item));
//		}
//		record.setTbProjectItemSort(new TbProjectItemSort());
//		record.getTbProjectItemSort().setId(Integer.valueOf(sort));
//		record.setWorkHr(Float.parseFloat(hour));
//		if (note.length() < 1) {
//			record.setNote(null);
//		} else {
//			record.setNote(note);
//		}
//		record.setCreatedAt(createdAt);
//		record.setStatus("CREATED");
//		logger.info("saveWork account:" + account);
//		logger.info("date:" + date);
//		logger.info("project:" + project);
//		logger.info("hour:" + hour);
//		logger.info("note:" + note);
//		workItemService.save(record);
//		logger.info("workItem Save Success");
//		String result = "新增成功";
//		if(!item.equals("0")) {
//			List<Integer> id = new ArrayList<Integer>();
//			id.add(Integer.valueOf(item));
//			Map<Integer,Float> map = workItemService.getActualHour(id);
//			TbProjectItem projectItem = projectItemService.getProjectItemById(Integer.valueOf(item));
//			if(projectItem != null) {
//				if(projectItem.getHour() != null) {
//					if(map.get(Integer.parseInt(item)) > projectItem.getHour()) {
//						result = "提醒 : 該項目現有工時(" + map.get(Integer.parseInt(item)) + ")已超過預估工時(" + projectItem.getHour() + ")。<br>" + result;
//					}
//				}
//			}
//		}
//		return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
//	}
	/**
	 * @param req
	 * @param resp
	 * @throws Exception
	 * 修改工時資料。
	 * 接收前端傳回的工時資訊並透過workItemService.save儲存。
	 */
	@PostMapping("/edit")
	public Response edit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer id = Integer.parseInt(req.getParameter("id"));
		String account = req.getParameter("account");
		SimpleDateFormat jd = new SimpleDateFormat("yyyy-MM-dd");
		Date date = jd.parse(req.getParameter("date"));
		Integer project = Integer.parseInt(req.getParameter("project"));
		String item = req.getParameter("item");
		String sort = req.getParameter("sort");
		float hour = Float.parseFloat(req.getParameter("hour"));
		String note = req.getParameter("note");
		String created = req.getParameter("createdAt");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date createdAt = sdf.parse(created);
		Date updatedAt = new Date();
		TbWorkItem record = new TbWorkItem();
		record.setId(id);
		record.setProId(project);
		if(!item.equals("0")) {
			record.setTbProjectItem(new TbProjectItem());
			record.getTbProjectItem().setItemId(Integer.valueOf(item));
		}
		record.setTbProjectItemSort(new TbProjectItemSort());
		record.getTbProjectItemSort().setId(Integer.valueOf(sort));
		record.setTbEmployees(new TbEmployees());
		record.getTbEmployees().setUsername(account);
		record.setStatus("UPDATED");
		record.setWorkHr(hour);
		record.setDate(date);
		if (note.length() < 1) {
			record.setNote(null);
		} else {
			record.setNote(note);
		}
		record.setCreatedAt(createdAt);
		record.setUpdatedAt(updatedAt);
		logger.info("editWork id:" + id);
		logger.info("account:" + account);
		logger.info("date:" + date);
		logger.info("project:" + project);
		logger.info("hour:" + hour);
		logger.info("note:" + note);
		workItemService.save(record);
		logger.info("editWork Success");
		String result = "修改成功";
		if(!item.equals("0")) {
			List<Integer> ids = new ArrayList<Integer>();
			ids.add(Integer.valueOf(item));
			Map<Integer,Float> map = workItemService.getActualHour(ids);
			TbProjectItem projectItem = projectItemService.getProjectItemById(Integer.valueOf(item));
			if(projectItem != null) {
				if(projectItem.getHour() != null) {
					if(map.get(Integer.parseInt(item)) > projectItem.getHour()) {
						result = "提醒 : 該項目現有工時(" + map.get(Integer.parseInt(item)) + ")已超過預估工時(" + projectItem.getHour() + ")。<br>" + result;
					}
				}
			}
		}
		return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
	}
	/**
	 * @param req
	 * @param resp
	 * @throws Exception
	 * 刪除工時資料。
	 * 接收前端傳回的工時資訊並透過workItemService.del刪除。
	 */
	@PostMapping("/del")
	public void del(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer id = Integer.parseInt(req.getParameter("id"));
		logger.info("deleteWork id:" + id);
		workItemService.del(id);
		logger.info("deleteWork Success");
	}
}