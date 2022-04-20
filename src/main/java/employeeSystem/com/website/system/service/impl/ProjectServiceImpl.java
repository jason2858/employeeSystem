package com.yesee.gov.website.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.CustomerDao;
import com.yesee.gov.website.dao.DepartmentDao;
import com.yesee.gov.website.dao.EmployeesDao;
import com.yesee.gov.website.dao.ProjectDao;
import com.yesee.gov.website.dao.ProjectItemDao;
import com.yesee.gov.website.dao.ProjectTypeDao;
import com.yesee.gov.website.dao.WorkItemDao;
import com.yesee.gov.website.model.TbCustomer;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbProjectItem;
import com.yesee.gov.website.model.TbProjectType;
import com.yesee.gov.website.model.TbWorkItem;
import com.yesee.gov.website.pojo.ProjectVO;
import com.yesee.gov.website.service.ProjectService;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
	private static final Logger logger = LogManager.getLogger(ProjectServiceImpl.class);

	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private ProjectTypeDao projectTypeDao;
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private DepartmentDao departmentDao;
	@Autowired
	private EmployeesDao employeesDao;
	@Autowired
	private ProjectItemDao projectItemDao;
	@Autowired
	private WorkItemDao workItemDao;

	public List<ProjectVO> getList(String nameSelect, String owner, String authorise) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<TbProject> pList = new ArrayList<>();
		if ("1".equals(authorise)) {
			pList = projectDao.getAllList();
		} else {
			pList = projectDao.getList(owner);
		}
		List<String> status = new ArrayList<>();
		status.add("SIGNED");
		status.add("DELETED");
		List<Integer> ids = new ArrayList<>();
		for(int i = 0; i < pList.size(); i++) {
			ids.add(pList.get(i).getId());
		}
		List<TbProjectItem> items = projectItemDao.getListByProejctId(ids, status);
		Map<Integer, Float> estimateHour = new HashMap<>();
		Set<Integer> estimateExtra = new HashSet<>();
		for(int i = 0; i < items.size(); i++) {
			int pId = items.get(i).getTbProject().getId();
			if(items.get(i).getHour() != null) {
				if(estimateHour.containsKey(pId)) {
					float hour = estimateHour.get(pId);
					estimateHour.put(pId, hour + items.get(i).getHour());
				}else {
					estimateHour.put(pId, items.get(i).getHour());
				}
			}else {
				estimateExtra.add(pId);
			}
		}
		List<TbWorkItem> work = workItemDao.findRecordsByProjects(ids);
		Map<Integer, Float> actualHour = new HashMap<>();
		for(int i = 0; i < work.size(); i++) {
			int pId = work.get(i).getProId();
			if(work.get(i).getTbProjectItem() != null) {
				if(actualHour.containsKey(pId)) {
					float hour = actualHour.get(pId);
					actualHour.put(pId, hour + work.get(i).getWorkHr());
				}else {
					actualHour.put(pId, work.get(i).getWorkHr());
				}
			}
		}
		List<ProjectVO> voList = new ArrayList<>();

		if (!CollectionUtils.isEmpty(pList)) {
			pList.forEach(p -> {
				try {
					String typeName = projectTypeDao.findById(p.getType()).getName();
					ProjectVO vo = new ProjectVO();
					vo.setId(p.getId() + "");
					if(p.getUpdatedAt() != null) {
					vo.setUpdatedAt(sdf.format(p.getUpdatedAt()));
					}else {
						vo.setUpdatedAt("");
					}
					vo.setProjectName(p.getName());
					vo.setTypeDropdown(typeName);
					if ("TW".equals(nameSelect)) {
						vo.setProjectPm(p.getPM().getChineseName());
					} else {
						vo.setProjectPm(p.getPM().getUsername());
					}
					vo.setDevStatus(p.getDevStatus());
					if("unsign".equals(p.getDevStatus()) || "deleted".equals(p.getDevStatus())) {
						vo.setEstimateHour("");
						vo.setActualHour("");
					}
					else {
						if(estimateHour.containsKey(p.getId())) {		
							float estimate = estimateHour.get(p.getId());
							if(estimateExtra.contains(p.getId())){
								vo.setEstimateHour(String.valueOf(estimate) + "+");
							}else {
								vo.setEstimateHour(String.valueOf(estimate));
							}
						}else {
							if(estimateExtra.contains(p.getId())) {
								vo.setEstimateHour("不設限");
							}else {
								vo.setEstimateHour("無資料");
							}
						}
						if(actualHour.containsKey(p.getId())) {
							vo.setActualHour(String.valueOf(actualHour.get(p.getId())));
						}else {
							vo.setActualHour("無資料");
						}
					}
					voList.add(vo);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		return voList;
	}

	@Override
	public List<TbProjectType> getTypeList() throws Exception {
		return projectTypeDao.getList();
	}

	@Override
	public List<TbCustomer> getCustomerList() throws Exception {
		return customerDao.getListByStatus("signed");
	}

	@Override
	public List<TbDepartment> getDepList() throws Exception {
		return departmentDao.getList();
	}

	@Override
	public void save(ProjectVO vo) throws Exception {

		try {
			TbProject p = new TbProject();
			p.setName(vo.getProjectName());
			p.setType(Integer.parseInt(vo.getTypeDropdown()));
			p.setPM(new TbEmployees());
			p.getPM().setUsername(vo.getProjectPm());
			p.setSiId(Integer.parseInt(vo.getSiDropdown()));
			p.setEndUserId(Integer.parseInt(vo.getEndUserDropdown()));
			p.setDevStatus("unsign");// (0:未開案->1:開發中->2:部署中->3:驗收中->4:已驗收)
			projectDao.save(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public TbProject findById(Integer id) throws Exception {
		return projectDao.get(id);
	}

	@Override
	public TbProject checkUpdate(ProjectVO vo) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TbProject p = this.findById(Integer.parseInt(vo.getId()));
		String SQLUpdated = "";
		String webUpdated = "";
		if(p.getUpdatedAt() != null) {
			SQLUpdated = sdf.format(p.getUpdatedAt());
		}
		if(vo.getUpdatedAt().length() != 0) {
			webUpdated = sdf.format(new Date(Long.parseLong(vo.getUpdatedAt())));
		}
		if(!SQLUpdated.equals(webUpdated)) {
			p = null;
			logger.info("Project has been updated");
		}
		return p;
	}
	
	@Override
	public void update(ProjectVO vo, TbProject object) throws Exception {
		try {
			object.setName(vo.getProjectName());
			object.setType(Integer.parseInt(vo.getTypeDropdown()));
			object.setPM(new TbEmployees());
			object.getPM().setUsername(vo.getProjectPm());
			object.setSiId(Integer.parseInt(vo.getSiDropdown()));
			object.setEndUserId(Integer.parseInt(vo.getEndUserDropdown()));
			object.setDevStatus(vo.getDevStatus());// (未開案->開發中->部署中->驗收中->已驗收 )
			object.setUpdatedAt(new Date());
			projectDao.save(object);
		} catch (Exception e) {
			logger.error(e);
		}

	}

	@Override
	public void delete(TbProject object) throws Exception {
		if (!"unsign".equals(object.getDevStatus())) {
			object.setDevStatus("deleted");
			object.setUpdatedAt(new Date());
			projectDao.save(object);
		} else {
			projectDao.delete(object);
		}
	}

	@Override
	public void sign(TbProject object) throws Exception {
		try {
			object.setDevStatus("prepare");
			object.setUpdatedAt(new Date());
			projectDao.save(object);
		} catch (Exception e) {
			logger.error(e);
		}

	}

	@Override
	public List<TbEmployees> getEmpList(TbEmployees entity) throws Exception {
		List<TbEmployees> empList = employeesDao.getList(entity);
		for (int i = 0; i < empList.size(); i++) {// 去除admin角色
			if (empList.get(i).getUsername().equals("admin")) {
				empList.remove(i);
			}
		}
		return empList;
	}

	@Override
	public List<TbProject> getAllProject() throws Exception {
		return projectDao.getAllList();
	}
}