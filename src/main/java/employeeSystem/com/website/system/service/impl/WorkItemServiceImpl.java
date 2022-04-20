package com.yesee.gov.website.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yesee.gov.website.dao.ProjectDao;
import com.yesee.gov.website.dao.ProjectItemSortDao;
import com.yesee.gov.website.dao.ProjectMemberDao;
import com.yesee.gov.website.dao.WorkItemDao;
import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbProjectItemSort;
import com.yesee.gov.website.model.TbProjectMember;
import com.yesee.gov.website.model.TbWorkItem;
import com.yesee.gov.website.service.WorkItemService;
import com.yesee.gov.website.util.Config;
@Service("workItemService")
public class WorkItemServiceImpl implements WorkItemService {
	@Autowired
	private WorkItemDao workItemDao;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private ProjectMemberDao projectMemberDao;
	@Autowired
	private ProjectItemSortDao projectItemSortDao;
	@Override
	public float getMainHour(String account, String date) throws Exception {
		List<TbWorkItem> list = workItemDao.findHour(account, date);
		Iterator<TbWorkItem> it = list.iterator();
		float hour = 8;
		while (it.hasNext()) {
			TbWorkItem record = it.next();
			hour = hour - record.getWorkHr();
		}
		if (hour < 0) {
			hour = 0;
		}
		return hour;
	}
	@Override
	public List<TbWorkItem> getRecords(String account, Date startDate, Date endDate) throws Exception {
		return workItemDao.findRecords(account, startDate, endDate);
	}
	@Override
	public List<TbWorkItem> getRecordsByList(List<String> accounts, Date startDate, Date endDate) throws Exception {
		return workItemDao.findRecordsByList(accounts, startDate, endDate);
	}
	@Override
	public List<TbWorkItem> getRecordsByListAndProjectId(Integer id, List<String> accounts, Date startDate,
			Date endDate) throws Exception {
		return workItemDao.findRecordsByListAndProjectId(id, accounts, startDate, endDate);
	}
	@Override
	public ArrayList<String> getRecentItem(String account) throws Exception {
		List<Integer> list = workItemDao.findRecentItem(account);
		Iterator<Integer> it = list.iterator();
		Config config = Config.getInstance();
		int size = Integer.parseInt(config.getValue("recentworkitem_size"));
		ArrayList<String> result = new ArrayList<String>();
		int count = 0;
		while (it.hasNext() && count < size) {
			result.add(String.valueOf(it.next()));
			count++;
		}
		return result;
	}



	public Map<String, Object> getProjectName(String condition) throws Exception {
		Map<String, Object> map = new HashMap<>();
		List<TbProject> list = projectDao.getAllList();
		for (int i = 0; i < list.size(); i++) {
			if ("all".equals(condition)) {
				map.put(Integer.toString(list.get(i).getId()), list.get(i).getName());
			}else if("!deleted".equals(condition)) {
				if(!list.get(i).getDevStatus().equals("deleted")) {
					map.put(Integer.toString(list.get(i).getId()), list.get(i).getName());
				}
			}else if("deleted".equals(condition)){
				if(list.get(i).getDevStatus().equals("deleted")) {
					map.put(Integer.toString(list.get(i).getId()), list.get(i).getName());
				}
			}
		}

		return map;
	}
	@Override
	public void save(TbWorkItem record) throws Exception {
		workItemDao.save(record);
	}
	@Override
	public void del(Integer id) throws Exception {
		Date updatedAt = new Date();
		TbWorkItem record = workItemDao.findById(id);
		record.setId(id);
		record.setStatus("CANCELLED");
		record.setUpdatedAt(updatedAt);
		workItemDao.save(record);
	}
	

	@Override
	public List<TbProject> getPersonalProject(String user) throws Exception {
		List<TbProjectMember> projectIds = projectMemberDao.getByName(user);
		List<TbProject> project = projectIds.stream().map(m -> m.getTbProject()).collect(Collectors.toList());
		return project;
	}
	@Override
	public List<TbWorkItem> getRecordsByItem(Integer id) throws Exception {
		List<Integer> ids = new ArrayList<>();
		ids.add(id);
		return workItemDao.findRecordsByItems(ids);
	}
	@Override
	public Map<Integer, Float> getActualHour(List<Integer> ids) throws Exception {
		List<TbWorkItem> list = workItemDao.findRecordsByItems(ids);
		Map<Integer, Float> hours = new HashMap<>();
		int id;
		for(int i = 0; i < list.size(); i++) {
			id = list.get(i).getTbProjectItem().getItemId();
			if(hours.containsKey(id)) {
				Float hour = hours.get(id);
				hours.put(id, hour + list.get(i).getWorkHr());
			}else {
				hours.put(id, list.get(i).getWorkHr());
			}
		}
		return hours;
	}
	
	@Override
	public List<TbProjectItemSort> getItemSort() throws Exception {
		return projectItemSortDao.getList();
	}
}