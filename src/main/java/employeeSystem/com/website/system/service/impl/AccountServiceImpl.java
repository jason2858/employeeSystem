package employeeSystem.com.website.system.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import employeeSystem.com.website.system.dao.AnnualLeaveDao;
import employeeSystem.com.website.system.dao.CustomerDao;
import employeeSystem.com.website.system.dao.DepartmentDao;
import employeeSystem.com.website.system.dao.EmployeesDao;
import employeeSystem.com.website.system.dao.FunctionDao;
import employeeSystem.com.website.system.dao.GroupFunctionDao;
import employeeSystem.com.website.system.dao.ProjectDao;
import employeeSystem.com.website.system.dao.ProjectItemDao;
import employeeSystem.com.website.system.dao.PunchRecordsDao;
import employeeSystem.com.website.system.dao.ScheduledLeaveDao;
import employeeSystem.com.website.system.dao.SchedulesDao;
import employeeSystem.com.website.system.model.TbAnnualLeave;
import employeeSystem.com.website.system.model.TbAnnualLeaveId;
import employeeSystem.com.website.system.model.TbDepartment;
import employeeSystem.com.website.system.model.TbEmployees;
import employeeSystem.com.website.system.model.TbFunction;
import employeeSystem.com.website.system.model.TbGroupFunction;
import employeeSystem.com.website.system.model.TbProject;
import employeeSystem.com.website.system.model.TbScheduledLeave;
import employeeSystem.com.website.system.model.TbScheduledLeaveId;
import employeeSystem.com.website.system.model.TbSchedules;
import employeeSystem.com.website.system.pojo.AnnualLeaveVo;
import employeeSystem.com.website.system.pojo.ScheduledLeaveVo;
import employeeSystem.com.website.system.service.AccountService;
import employeeSystem.com.website.system.service.DepartmentService;
import employeeSystem.com.website.system.service.EmployeesService;
import employeeSystem.com.website.system.service.PreferenceService;
import employeeSystem.com.website.system.util.HibernateUtil;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	private static final Logger logger = LogManager.getLogger(AccountServiceImpl.class);

	@Autowired
	private AnnualLeaveDao annualLeaveDao;

	@Autowired
	private EmployeesDao employeesDao;

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private ScheduledLeaveDao scheduledLeaveDao;

	@Autowired
	private GroupFunctionDao groupFunctionDao;

	@Autowired
	private FunctionDao functionDao;

	@Autowired
	private PunchRecordsDao punchRecordsDao;

	@Autowired
	private SchedulesDao schedulesDao;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private EmployeesService employeesService;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private ProjectItemDao projectItemDao;

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private PreferenceService preferenceService;

	@Override
	public TbEmployees getByUserName(String account) throws Exception {
		List<TbEmployees> empList = employeesDao.findByUserName(account);
		if (empList.size() != 0) {
			return empList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public boolean isHRM(String account) throws Exception {
		boolean isHRM = false;
		TbDepartment dep = departmentDao.findById(15);
		if (dep.getManager() != null) {
			if (account.equals(dep.getManager().getUsername())) {
				isHRM = true;
			}
		}
		return isHRM;
	}

	@Override
	public Set<String> getHR(String account) throws Exception {
		Set<String> set = new HashSet<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			List<TbEmployees> list = employeesDao.findHR(account);
			for (int i = 0; i < list.size(); i++) {
				set.add(list.get(i).getUsername());
			}
		} finally {
			// session.close();
		}
		return set;
	}

	@Override
	public Map<String, Object> getSidebar(int Authorise) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<TbGroupFunction> groupFuncList = groupFunctionDao.getListById(Authorise + "");
		List<String> ids = groupFuncList.stream().map(d -> d.getId().getFuncId() + "").collect(Collectors.toList());
		List<TbFunction> funcList = functionDao.getListById(ids);
		Set<String> set = new HashSet<String>();
		Set<String> temp = new HashSet<String>();

		for (int i = 0; i < funcList.size(); i++) {
			if (funcList.get(i).getParentId() != null) {
				if (set.contains(funcList.get(i).getParentId())) {
					temp.add(funcList.get(i).getParentId());
				} else {
					set.add(funcList.get(i).getParentId());
				}
			}
		}
		set.removeAll(temp);
		int count = 1;
		for (int i = 1; i <= funcList.size(); i++) {
			TbFunction func = funcList.get(i - 1);
			if (!set.contains(func.getId())) {
				map.put(count + ".id", func.getId());
				map.put(count + ".name", func.getName());
				map.put(count + ".url", func.getUrl());
				if (!set.contains(func.getParentId())) {
					map.put(count + ".parent_id", func.getParentId());
				} else {
					map.put(count + ".parent_id", null);
				}
				count++;
			}
		}

		map.put("sidebarcount", count);
		return map;
	}

	@Override
	public int getUnsign(String depId, int authorise, String account, String HRM) throws Exception {
		Map<Integer, String> departmentMap = getDepList(account);
		List<TbEmployees> empList = employeesDao.getList(null);
		List<TbEmployees> subList = new ArrayList<TbEmployees>();
		if ("true".equals(HRM)) {
			subList = empList.stream().filter(e -> !account.equals(e.getUsername()))
					.filter(e -> depId.equals(e.getDepartmentId())).collect(Collectors.toList());
		} else if (authorise == 3) {
			subList = empList.stream().filter(e -> !account.equals(e.getUsername())).filter(e -> {
				for (Integer key : departmentMap.keySet()) {
					if (key.equals(Integer.parseInt(e.getDepartmentId()))) {
						return true;
					}
				}
				return false;
			}).collect(Collectors.toList());
		} else {
			subList = empList.stream().filter(e -> {
				for (Integer key : departmentMap.keySet()) {
					if (key.equals(Integer.parseInt(e.getDepartmentId()))) {
						return true;
					}
				}
				return false;
			}).collect(Collectors.toList());
		}

		int UnsignCount = 0;

		if (authorise != 2 || "true".equals(HRM)) {
			if (subList.size() != 0) {
				UnsignCount += punchRecordsDao
						.getUnsignCount(subList.stream().map(TbEmployees::getUsername).collect(Collectors.toList()));
				UnsignCount += schedulesDao
						.getUnsignCount(subList.stream().map(TbEmployees::getUsername).collect(Collectors.toList()));
			}
		}
		return UnsignCount;
	}

	@Override
	public void saveAnnualLeave(AnnualLeaveVo vo) throws Exception {

		TbAnnualLeaveId id = new TbAnnualLeaveId();
		id.setEmpName(vo.getEmpName());
		id.setYear(vo.getYear());

		List<TbAnnualLeave> aList = annualLeaveDao.findById(id);
		if (!CollectionUtils.isEmpty(aList)) {
			throw new Exception("Duplicate Data");
		}

		TbAnnualLeave annMedel = new TbAnnualLeave();
		annMedel.setId(id);
		annMedel.setEntitledHours(vo.getEntitledHours());

		annualLeaveDao.save(annMedel);
	}

	@Override
	public void updAnnualLeave(AnnualLeaveVo vo) throws Exception {

		TbAnnualLeaveId id = new TbAnnualLeaveId();
		id.setEmpName(vo.getEmpName());
		id.setYear(vo.getYear());

		TbAnnualLeave annModel = new TbAnnualLeave();
		annModel.setId(id);
		annModel.setEntitledHours(vo.getEntitledHours());

		annualLeaveDao.save(annModel);
	}

	@Override
	public List<AnnualLeaveVo> getAnnualList(String nameSelect, String companyId, int authorise) throws Exception {
		List<AnnualLeaveVo> vos = new ArrayList<>();
		List<TbEmployees> emps = new ArrayList<>();
		if (1 == authorise) {
			emps = employeesService.getEmployees(null);
		} else {
			emps = employeesService.getEmployeesByCompany(companyId, null);
		}
		List<String> empsOfCompany = emps.stream().map(TbEmployees::getUsername).collect(Collectors.toList());
		List<TbAnnualLeave> models = annualLeaveDao.getListByEmployees(empsOfCompany);

		models.forEach(m -> {

			AnnualLeaveVo vo = new AnnualLeaveVo();
			if ("TW".equals(nameSelect)) {
				vo.setEmpName(m.getTbEmployees().getChineseName());
			} else {
				vo.setEmpName(m.getTbEmployees().getUsername());
			}
			vo.setAccountName(m.getTbEmployees().getUsername());
			vo.setEntitledHours(m.getEntitledHours());
			vo.setYear(m.getId().getYear());

			try {// 取出員工年份已排休時數
				List<TbScheduledLeave> slList = scheduledLeaveDao.findByYearAndEmployees(m.getId().getYear(),
						m.getId().getEmpName());
				List<Integer> intList = slList.stream().map(s -> Integer.parseInt(s.getSkdHours()))
						.collect(Collectors.toList());
				int totalHrs = intList.stream().mapToInt(i -> i.intValue()).sum();
				vo.setSkdHours(totalHrs + "");
				// 找出部門名
				List<TbEmployees> eList = employeesDao.findByUserName(m.getId().getEmpName());
				String depName = departmentDao.findById(Integer.parseInt(eList.get(0).getDepartmentId())).getName();
				vo.setDep(depName);
			} catch (Exception e) {
				logger.error(e);
			}
			vos.add(vo);
		});
		return vos;
	}

	@Override
	public List<AnnualLeaveVo> getAnnualLeaveOfMonth(String nameSelect, String name) throws Exception {
		List<AnnualLeaveVo> vos = new ArrayList<>();
		List<TbEmployees> empList = employeesDao.getList(null);
		List<TbDepartment> depList = departmentDao.getList();
		Set<Integer> depNum = new HashSet<Integer>();
		TbEmployees rootEmp = employeesDao.findByUserName(name).get(0);
		TbDepartment rootDep = departmentDao.findById(Integer.parseInt(rootEmp.getDepartmentId()));

		depList.forEach(d -> {
			if (d.getManager() != null) {
				if ("1".equals(rootEmp.getGroupId())) {
					depNum.add(d.getId());
				} else if ("2".equals(rootEmp.getGroupId()) && rootDep.getCompanyId().equals(d.getCompanyId())) {
					depNum.add(d.getId());
				} else if ("3".equals(rootEmp.getGroupId())) {
					if (name.equals(d.getManager().getUsername())) {
						depNum.add(d.getId());
					}
				}
			}
		});

		int oldSize = 0;
		do {
			oldSize = depNum.size();
			checkParents(depList, depNum);
		} while (oldSize != depNum.size());

		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		int year = localDate.getYear();

		empList.stream().filter(p -> {
			if (depNum.contains(Integer.parseInt(p.getDepartmentId()))) {
				return true;
			} else if (name.equals(p.getUsername())) {
				return true;
			} else {
				return false;
			}
		}).forEach(e -> {

			try {
				AnnualLeaveVo vo = new AnnualLeaveVo();
				// 取得至當月已休特休
				List<TbSchedules> models = schedulesDao.findByEmployeesAndType(e.getUsername(), 3);
				List<Float> floList = models.stream()
						.filter(m -> year == m.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
								.getYear())
						.filter(m -> month >= m.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
								.getMonthValue())
						.map(TbSchedules::getAnnualLeaveTimes).collect(Collectors.toList());
				int totalHrs = floList.stream().mapToInt(i -> i.intValue()).sum();

				// 取得至當月排休
				List<TbScheduledLeave> slList = scheduledLeaveDao.findByYearAndEmployees(year + "", e.getUsername());
				List<TbScheduledLeave> secList = slList.stream()
						.filter(sl -> month >= Integer.parseInt(sl.getId().getSkdDate().substring(5, 7)))
						.collect(Collectors.toList());
				List<Integer> hrsList = secList.stream().map(m -> Integer.parseInt(m.getSkdHours()))
						.collect(Collectors.toList());
				int skdHours = hrsList.stream().mapToInt(i -> i.intValue()).sum();

				if (!e.getDepartmentId().isEmpty()) {
					String depName = departmentDao.findById(Integer.parseInt(e.getDepartmentId())).getName();
					vo.setDep(depName);
				}

				if ("TW".equals(nameSelect)) {
					vo.setEmpName(e.getChineseName());
				} else {
					vo.setEmpName(e.getUsername());
				}

				vo.setPastLeave(totalHrs + "");
				vo.setSkdHours(skdHours + "");
				vos.add(vo);
			} catch (Exception e1) {
				logger.error(e);
			}
		});
		return vos;
	}

	private Set<Integer> checkParents(List<TbDepartment> depList, Set<Integer> depNum) {

		depList.forEach(d -> {
			if (depNum.contains(d.getParentId())) {
				depNum.add(d.getId());
			}
		});

		return depNum;
	}

	@Override
	public void deleteAnnualLeave(AnnualLeaveVo vo) throws Exception {

		TbAnnualLeaveId id = new TbAnnualLeaveId();
		id.setEmpName(vo.getEmpName());
		id.setYear(vo.getYear());

		TbAnnualLeave annMedel = new TbAnnualLeave();
		annMedel.setId(id);
		annualLeaveDao.delete(annMedel);
	}

	@Override
	public AnnualLeaveVo findAnnualLeaveById(AnnualLeaveVo vo) throws Exception {

		AnnualLeaveVo ann = new AnnualLeaveVo();

		TbAnnualLeaveId id = new TbAnnualLeaveId();
		id.setEmpName(vo.getEmpName());
		id.setYear(vo.getYear());
		List<TbAnnualLeave> aList = annualLeaveDao.findById(id);
		if (aList.size() > 0) {
			ann.setEntitledHours(aList.get(0).getEntitledHours());
		}
		return ann;
	}

	@Override
	public void saveScheduledLeave(ScheduledLeaveVo vo) throws Exception {

		List<TbScheduledLeave> oldList = scheduledLeaveDao.findByYearAndEmployees(vo.getYear(), vo.getEmployees());// 舊資料
		List<TbScheduledLeave> newList = new ArrayList<TbScheduledLeave>();// 新資料容器

		vo.getMap().forEach(map -> {
			TbScheduledLeaveId id = new TbScheduledLeaveId();
			id.setEmpName(vo.getEmployees());
			TbScheduledLeave model = new TbScheduledLeave();
			String k = map.get("mon").toString();
			int i = Integer.parseInt(k.replaceAll("\\D+", ""));
			String mon = String.format("%02d", i);
			id.setSkdDate(vo.getYear() + "-" + mon);
			model.setSkdHours(map.get("val").toString());
			model.setId(id);
			try {
				newList.add(model);
				scheduledLeaveDao.save(model);
			} catch (Exception e) {
				logger.error(e);
			}
		});
		deleteDiff(newList, oldList);
	}

	private void deleteDiff(List<TbScheduledLeave> newList, List<TbScheduledLeave> oldList) {
		Set<TbScheduledLeaveId> ids = newList.stream().map(TbScheduledLeave::getId).collect(Collectors.toSet());
		List<TbScheduledLeave> parentLeaves = oldList.stream().filter(l -> !ids.contains(l.getId()))
				.collect(Collectors.toList());
		parentLeaves.forEach(p -> {
			try {
				scheduledLeaveDao.delete(p);
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	@Override
	public ScheduledLeaveVo findSchedulesLeaveByEmployees(String emp, String year) throws Exception {
		List<TbScheduledLeave> sList = scheduledLeaveDao.findByEmployeesName(emp);
		ScheduledLeaveVo vo = new ScheduledLeaveVo();
		List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
		sList.forEach(sl -> {
			HashMap<String, String> map = new HashMap<String, String>();
			String skdDate = sl.getId().getSkdDate();
			String[] strArr = skdDate.split("-");
			if (strArr[0].contains(year)) {
				map.put("mon", strArr[1]);
				map.put("val", sl.getSkdHours());
				mList.add(map);
			}
		});
		vo.setEmployees(emp);
		vo.setYear(year);
		vo.setMap(mList);
		return vo;
	}

	@Override
	public Optional<List<TbEmployees>> getEmployeesByName(String empName) throws Exception {
		return Optional.ofNullable(employeesDao.findByUserName(empName));
	}

	@Override
	public Map<Integer, String> getDepList(String name) throws Exception {

		List<TbDepartment> depList = departmentDao.getList();
		Set<Integer> depNum = new HashSet<Integer>();
		TbEmployees rootEmp = employeesDao.findByUserName(name).get(0);
		TbDepartment rootDep = departmentDao.findById(Integer.parseInt(rootEmp.getDepartmentId()));

		depList.forEach(d -> {
			if ("1".equals(rootEmp.getGroupId())) {
				depNum.add(d.getId());
			} else if ("2".equals(rootEmp.getGroupId()) && rootDep.getCompanyId().equals(d.getCompanyId())) {
				depNum.add(d.getId());
			} else if ("3".equals(rootEmp.getGroupId())) {
				if (d.getId() == Integer.valueOf(rootEmp.getDepartmentId())) {
					depNum.add(d.getId());
				}
			}
		});

		int oldSize = 0;
		do {
			oldSize = depNum.size();
			checkParents(depList, depNum);
		} while (oldSize != depNum.size());

		Map<Integer, String> departmentMap = new HashMap<Integer, String>();
		depNum.forEach(n -> {
			try {
				departmentMap.put(n, departmentDao.findById(n).getName());
			} catch (Exception e) {
				logger.error(e);
			}
		});

		return departmentMap;
	}

	@Override
	public List<TbEmployees> getSubordinateList(String authorise, String depId, TbEmployees entity) throws Exception {
		String companyId = departmentDao.findById(Integer.parseInt(depId)).getCompanyId();
		List<TbEmployees> list = new ArrayList<>();
		if ("1".equals(authorise)) {
			list = employeesDao.getList(entity);
		} else {
			list = employeesService.getEmployeesByCompany(companyId, null);
		}
		List<TbEmployees> result = new ArrayList<TbEmployees>();
		if (authorise.equals("3")) {
			Set<String> dep = departmentService.getChildDepartments(depId);
			for (int i = 0; i < list.size(); i++) {
				if (dep.contains(list.get(i).getDepartmentId())) {
					result.add(list.get(i));
				}
			}
			return result;
		} else {
			return list;
		}
	}

	@Override
	public List<TbEmployees> getApplyMailRecipient(String depId, String account) throws Exception {
		List<TbEmployees> list = employeesDao.getList(null);
		List<TbEmployees> result = new ArrayList<TbEmployees>();
		Set<String> dep = departmentService.getParentDepartments(depId);
		List<Integer> ids = dep.stream().map(d -> Integer.parseInt(d)).collect(Collectors.toList());
		List<TbDepartment> ObjectList = departmentService.getDepListByIds(ids);
		List<String> parentOfDepList = ObjectList.stream().filter(m -> m.getManager() != null)
				.map(m -> m.getManager().getUsername()).collect(Collectors.toList());

		for (int i = 0; i < list.size(); i++) {
			if ((!list.get(i).getUsername().equals(account) && dep.contains(list.get(i).getDepartmentId())
					&& parentOfDepList.contains(list.get(i).getUsername())) || list.get(i).getGroupId().equals("1")) {
				result.add(list.get(i));
			}
		}

		return result;
	}

	@Override
	public List<TbEmployees> getRespondMailRecipient(String depId, String account) throws Exception {
		List<TbEmployees> list = employeesDao.getList(null);
		List<TbEmployees> result = new ArrayList<TbEmployees>();
		Set<String> dep = departmentService.getParentDepartments(depId);

		TbDepartment rootDep = departmentDao.findById(Integer.parseInt(depId));
		List<TbDepartment> depList = departmentDao.getList();
		for (int i = 0; i < depList.size(); i++) {
			if (depList.get(i).getName().contains("人資")
					&& rootDep.getCompanyId().equals(depList.get(i).getCompanyId())) {
				dep.add(depList.get(i).getId() + "");
			}
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getUsername().equals(account)
					|| (dep.contains(list.get(i).getDepartmentId()) && !list.get(i).getGroupId().equals("4"))) {
				result.add(list.get(i));
			}
		}
		return result;
	}

	@Override
	public List<TbEmployees> getByDepartmentSet(Set<String> dep) throws Exception {
		List<TbEmployees> list = employeesDao.getList(null);
		List<TbEmployees> result = new ArrayList<TbEmployees>();
		for (int i = 0; i < list.size(); i++) {
			if (dep.contains(list.get(i).getDepartmentId())) {
				result.add(list.get(i));
			}
		}
		return result;
	}

	@Override
	public Map<String, Object> getProjectAndCustomerUnsign(int authorise, String account) throws Exception {
		Integer unsignCount = 0;
		Integer pCount = 0;
		Integer pItemCount = 0;
		Integer cCount = 0;
		Map<String, Object> countVO = new HashMap<>();
		Map<String, Integer> detailCount = new HashMap<>();
		if (authorise == 1) {
			pCount = projectDao.getUnsignCount();
			pItemCount = projectItemDao.getUnsignCount();
			cCount = customerDao.getUnsignCount();
			unsignCount += pCount;
			unsignCount += pItemCount;
			unsignCount += cCount;
		} else {
			List<TbProject> project = projectDao.getList(account);
			if (project.size() != 0) {
				List<Integer> project_ids = new ArrayList<>();
				for (int i = 0; i < project.size(); i++) {
					project_ids.add(project.get(i).getId());
				}
				pItemCount = projectItemDao.getUnsignCount(project_ids);
				unsignCount += pItemCount;
			}
		}
		detailCount.put("projectCount", pCount);
		detailCount.put("projectItemCount", pItemCount);
		detailCount.put("customerCount", cCount);
		countVO.put("sum", unsignCount);
		countVO.put("detail", detailCount);
		return countVO;
	}

}
