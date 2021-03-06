package employeeSystem.com.website.accounting.service.impl;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import employeeSystem.com.website.accounting.dao.HedgeDao;
import employeeSystem.com.website.accounting.dao.HedgeDelHistoryDao;
import employeeSystem.com.website.accounting.dao.VTbVoucherDetailDao;
import employeeSystem.com.website.accounting.dao.VoucherCommonDao;
import employeeSystem.com.website.accounting.dao.VoucherDetailDao;
import employeeSystem.com.website.accounting.dao.VoucherHeadDao;
import employeeSystem.com.website.accounting.dao.VoucherModifyAuthDao;
import employeeSystem.com.website.accounting.dao.VoucherSignDao;
import employeeSystem.com.website.accounting.exception.AccountingException;
import employeeSystem.com.website.accounting.model.TbAClassItem;
import employeeSystem.com.website.accounting.model.TbHedge;
import employeeSystem.com.website.accounting.model.TbHedgeDelHistory;
import employeeSystem.com.website.accounting.model.TbVoucherCommon;
import employeeSystem.com.website.accounting.model.TbVoucherDetail;
import employeeSystem.com.website.accounting.model.TbVoucherHead;
import employeeSystem.com.website.accounting.model.TbVoucherModifyAuth;
import employeeSystem.com.website.accounting.model.TbVoucherSign;
import employeeSystem.com.website.accounting.model.VTbVoucherDetail;
import employeeSystem.com.website.accounting.pojo.InsertVoucherInfo;
import employeeSystem.com.website.accounting.pojo.UpdateVoucherInfo;
import employeeSystem.com.website.accounting.service.VoucherService;
import employeeSystem.com.website.system.dao.CompanyDao;
import employeeSystem.com.website.system.dao.CustomerDao;
import employeeSystem.com.website.system.dao.EmployeesDao;
import employeeSystem.com.website.system.dao.ProjectDao;
import employeeSystem.com.website.system.model.TbCompany;
import employeeSystem.com.website.system.service.SendMailService;
import employeeSystem.com.website.system.util.Config;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("voucherService")
public class VoucherServiceImpl implements VoucherService {

	private static final Logger logger = LogManager.getLogger(VoucherServiceImpl.class);

	@Autowired
	public VoucherCommonDao voucherCommonDao;

	@Autowired
	public VoucherHeadDao voucherHeadDao;

	@Autowired
	public VoucherDetailDao voucherDetailDao;

	@Autowired
	public VoucherSignDao voucherSignDao;

	@Autowired
	public VoucherModifyAuthDao voucherModifyAuthDao;

	@Autowired
	public CustomerDao customerDao;

	@Autowired
	public EmployeesDao employeesDao;

	@Autowired
	public ProjectDao projectDao;

	@Autowired
	public CompanyDao companyDao;

	@Autowired
	public HedgeDao hedgeDao;

	@Autowired
	public SendMailService sendMailDao;

	@Autowired
	public HedgeDelHistoryDao hedgeDelHistoryDao;

	@Autowired
	public VTbVoucherDetailDao vTbVoucherDetailDao;

	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");

	@Override
	public String getVoucherCommonList(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception {
		return getVoucherCommonList();
	}

	private String getVoucherCommonList() throws AccountingException, Exception {

		List<TbVoucherCommon> list = voucherCommonDao.getVoucherCommonList();
		if (list == null || list.size() == 0) {
			throw new AccountingException("????????????");
		}
		JSONObject commonList = new JSONObject();
		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("voucherNo", list.get(i).getVoucherNo());
			object.put("voucherName", list.get(i).getVoucherName());
			result.add(object);
		}
		commonList.put("commonList", result);
		return commonList.toString();
	}

	@Override
	public String saveVoucher(HttpServletRequest req, HttpServletResponse resp, InsertVoucherInfo voucherInfo)
			throws AccountingException, Exception {
		String account = req.getSession().getAttribute("Account").toString();
		return saveVoucher(account, voucherInfo);
	}

	private String saveVoucher(String account, InsertVoucherInfo voucherInfo) throws AccountingException, Exception {

		// check voucherInfo
		if (voucherInfo.getTaxIdType() == "Y" || voucherInfo.getTaxIdType().equals("Y")) {
			if (voucherInfo.getCusTaxId() == null) {
				throw new AccountingException("???????????????");
			}
			// check if cus_tax_id from customer
			if (voucherInfo.getCustomer().equals(customerDao.findByEin(voucherInfo.getCusTaxId()).getName()) == false) {
				throw new AccountingException("????????????????????????");
			}
			if (voucherInfo.getCusTaxId() != null && voucherInfo.getCusTaxId().length() != 8) {
				throw new AccountingException("???????????????8???");
			}
		} else {
			if (voucherInfo.getCusTaxId() != null) {
				throw new AccountingException("??????????????????");
			}
		}
		// check applicant
		if (employeesDao.findByUserName(voucherInfo.getApplicant()) == null
				|| employeesDao.findByUserName(voucherInfo.getApplicant()).isEmpty()) {
			throw new AccountingException("????????????????????????");
		}
		// check common_voucher
		if (voucherInfo.getCommon().equals("Y") && CollectionUtils
				.isEmpty(voucherCommonDao.findVoucherCommonListByVoucherName(voucherInfo.getVoucherName())) == false) {
			throw new AccountingException("????????????????????????????????????");

		}
		// check hedgeNoD
		List<Map<String, String>> checkdetail = voucherInfo.getDetail();
		for (int i = 0; i < checkdetail.size(); i++) {
			String hedgeNoD = checkdetail.get(i).get("hedge_no_d");
			if (StringUtils.isEmpty(hedgeNoD) == false) {
				if (hedgeDao.findById(hedgeNoD) != null) {
					throw new AccountingException(hedgeNoD + " ?????????????????????");
				}
			}
		}

		// give new voucherNo
		String voucherNo = createNewVoucherNo();

		// insert new data to tb_hedge
		String hedgeNo = "";

		if (StringUtils.isEmpty(voucherInfo.getHedgeNoH())) {
			hedgeNo = createNewHedgeNo(voucherNo);

		} else {
			hedgeNo = voucherInfo.getHedgeNoH();
			if (hedgeDao.findById(hedgeNo) != null) {
				throw new AccountingException(hedgeNo + " ?????????????????????");
			}
		}

		TbHedge tbHedge = new TbHedge(hedgeNo, voucherInfo.getAmountTotal(), account, Calendar.getInstance().getTime());
		hedgeDao.save(tbHedge);

		// insert voucherInfo to tb_voucher_head
		TbCompany newCompany = new TbCompany();
		newCompany.setId(1);
		newCompany.setName(voucherInfo.getCompany().toString());

		String customer = StringUtils.isEmpty(voucherInfo.getCustomer()) ? null : voucherInfo.getCustomer();

		TbAClassItem newHeadItem = new TbAClassItem();
		newHeadItem.setiId(voucherInfo.getHeadItem());

		TbHedge newHedgeNo = new TbHedge();
		newHedgeNo.setHedgeNo(hedgeNo);

		String creditDate = voucherInfo.getCreditDate();
		creditDate = creditDate.replace("-", "");

		String predictDate = voucherInfo.getPredictDate();
		predictDate = predictDate.replace("-", "");

		TbVoucherHead tbVoucherHead = new TbVoucherHead(voucherNo, voucherInfo.getVoucherName(), newCompany, customer,
				voucherInfo.getCusTaxId(), newHeadItem, newHedgeNo, voucherInfo.getAmountTotal(),
				voucherInfo.getApplicant(), "0", creditDate, predictDate, voucherInfo.getDirections(), account,
				Calendar.getInstance().getTime());

		voucherHeadDao.save(tbVoucherHead);

		// insert voucher.detail to tb_voucher_head_detail
		List<Map<String, String>> detail = voucherInfo.getDetail();
		saveDetail(detail, voucherNo, account, "s");

		String common = voucherInfo.getCommon();
		String voucherName = voucherInfo.getVoucherName();
		checkIfCommonAndSave(common, voucherNo, voucherName, account);

		// add new token in tb_voucher_modify_auth
		TbVoucherHead newVoucherNo = new TbVoucherHead();
		newVoucherNo.setVoucherNo(voucherNo);
		TbVoucherModifyAuth tbVoucherModifyAuth = new TbVoucherModifyAuth(newVoucherNo, account,
				Calendar.getInstance().getTime());
		voucherModifyAuthDao.save(tbVoucherModifyAuth);

		String token = voucherModifyAuthDao.findVoucherModifyAuthByVNo(voucherNo, account).get(0).getToken();
		List<TbVoucherDetail> detailList = voucherDetailDao.findVoucherDetailByVoucherNo(voucherNo);
		JSONArray newDetail = new JSONArray();

		for (int j = 0; j < detailList.size(); j++) {
			JSONObject detailObject = new JSONObject();
			detailObject.put("detailNo", detailList.get(j).getDetailNo());
			detailObject.put("hedgeNoD", detailList.get(j).getHedgeNo().getHedgeNo());
			newDetail.add(detailObject);
		}

		JSONArray result = new JSONArray();
		JSONObject object = new JSONObject();
		object.put("voucherNo", voucherNo);
		object.put("detail", newDetail);
		object.put("hedgeNoH", hedgeNo);
		object.put("mToken", token);
		result.add(object);

		return result.toString();
	}

	@Override
	public String updateVoucher(HttpServletRequest req, HttpServletResponse resp, UpdateVoucherInfo voucherInfo)
			throws AccountingException, Exception {

		String account = req.getSession().getAttribute("Account").toString();

		return updateVoucher(account, voucherInfo, resp);
	}

	private String updateVoucher(String account, UpdateVoucherInfo voucherInfo, HttpServletResponse resp)
			throws AccountingException, Exception {

		// check token
		String voucherNo = voucherInfo.getVoucherNo();
		String token = voucherInfo.getmToken();

		if (voucherNo == null) {
			throw new AccountingException(" voucher_no ???????????????");
		}
		if (token == null) {
			throw new AccountingException(" m_token ???????????????");
		}
		List<TbVoucherModifyAuth> voucherModifyAuth = voucherModifyAuthDao.findVoucherModifyAuthByVNoAndToken(voucherNo,
				token);

		long lag = Calendar.getInstance().getTime().getTime();

		if (CollectionUtils.isEmpty(voucherModifyAuth)
				|| (lag - voucherModifyAuth.get(0).getCreateDate().getTime()) > 3600000) {

			resp.setStatus(401);
			return "error";

		} else if (voucherHeadDao.findVoucherHeadByVoucherNo(voucherNo).get(0).getStatus() == "0"
				|| voucherHeadDao.findVoucherHeadByVoucherNo(voucherNo).get(0).getStatus().equals("0")) {

			TbVoucherHead newVoucherNo = new TbVoucherHead();
			newVoucherNo.setVoucherNo(voucherNo);
			TbVoucherModifyAuth tbVoucherModifyAuth = new TbVoucherModifyAuth(newVoucherNo, account,
					Calendar.getInstance().getTime());
			voucherModifyAuthDao.save(tbVoucherModifyAuth);

		} else {

			resp.setStatus(401);
			return "error";
		}

		// check voucherInfo
		if (voucherInfo.getTaxIdType() == "Y" || voucherInfo.getTaxIdType().equals("Y")) {
			if (voucherInfo.getCusTaxId() == null) {
				throw new AccountingException("???????????????");
			}
			// check if cus_tax_id from customer
			if (voucherInfo.getCustomer().equals(customerDao.findByEin(voucherInfo.getCusTaxId()).getName()) == false) {
				throw new AccountingException("????????????????????????");
			}
			if (voucherInfo.getCusTaxId() != null && voucherInfo.getCusTaxId().length() != 8) {
				throw new AccountingException("???????????????8???");
			}
		} else {
			if (voucherInfo.getCusTaxId() != null) {
				throw new AccountingException("??????????????????");
			}
		}
		// check applicant
		if (employeesDao.findByUserName(voucherInfo.getApplicant()) == null
				|| employeesDao.findByUserName(voucherInfo.getApplicant()).isEmpty()) {
			throw new AccountingException("????????????????????????");
		}
		// check common_voucher
		if (voucherInfo.getCommon().equals("Y") && CollectionUtils
				.isEmpty(voucherCommonDao.findVoucherCommonListByVoucherName(voucherInfo.getVoucherName())) == false) {
			throw new AccountingException("????????????????????????????????????");

		}

		// check hedgeNoD
		List<Map<String, String>> checkdetail = voucherInfo.getDetail();
		for (int i = 0; i < checkdetail.size(); i++) {
			String hedgeNoDO = checkdetail.get(i).get("hedge_no_d_o");
			String hedgeNoDN = checkdetail.get(i).get("hedge_no_d_n");
			boolean oldNull = StringUtils.isEmpty(hedgeNoDO);
			boolean newNull = StringUtils.isEmpty(hedgeNoDN);
			if (newNull == false) {
				if (oldNull == true) {
					if (hedgeDao.findById(hedgeNoDN) != null) {
						throw new AccountingException(hedgeNoDN + " ?????????????????????");
					}
				}
				if (!hedgeNoDO.equals(hedgeNoDN)) {
					if (hedgeDao.findById(hedgeNoDN) != null) {
						throw new AccountingException(hedgeNoDN + " ?????????????????????");
					}
				}
			}
		}

		// update voucherInfo to tb_voucher_head
		TbVoucherHead tbVoucherHead = voucherHeadDao.findVoucherHeadByVoucherNo(voucherNo).get(0);

		// check hedge_no
		String hedgeNo = "";
		String hedgeNoHN = voucherInfo.getHedgeNoHN();
		String hedgeNoHO = voucherInfo.getHedgeNoHO();

		if (StringUtils.isEmpty(hedgeNoHN)) {

			hedgeNo = createNewHedgeNo(voucherNo);

			// delete old_hedge_no
			if (StringUtils.isEmpty(hedgeNoHO) == false) {
				delOldHedge(hedgeNoHO);
			}
			TbHedge tbHedge = new TbHedge(hedgeNo, voucherInfo.getAmountTotal(), account,
					Calendar.getInstance().getTime());
			hedgeDao.save(tbHedge);
		} else {
			if (StringUtils.isEmpty(hedgeNoHO) || !hedgeNoHO.equals(hedgeNoHN)) {

				if (hedgeDao.findById(hedgeNoHN) != null) {
					throw new AccountingException(hedgeNoHN + " ?????????????????????");
				}

				hedgeNo = hedgeNoHN;

				// delete old_hedge_no
				if (StringUtils.isEmpty(hedgeNoHO) == false) {
					delOldHedge(hedgeNoHO);
				}
				TbHedge tbHedge = new TbHedge(hedgeNo, voucherInfo.getAmountTotal(), account,
						Calendar.getInstance().getTime());
				hedgeDao.save(tbHedge);
			} else if (hedgeNoHO.equals(hedgeNoHN)) {
				hedgeNo = hedgeNoHN;
			} else {
				hedgeNo = hedgeNoHN;
			}
		}

		TbCompany newCompany = new TbCompany();
		newCompany.setId(1);
		newCompany.setName(voucherInfo.getCompany().toString());

		String customer = StringUtils.isEmpty(voucherInfo.getCustomer()) ? null : voucherInfo.getCustomer();

		TbAClassItem newHeadItem = new TbAClassItem();
		newHeadItem.setiId(voucherInfo.getHeadItem());

		TbHedge newHedgeNo = new TbHedge();

		newHedgeNo.setHedgeNo(hedgeNo);

		String creditDate = voucherInfo.getCreditDate();
		creditDate = creditDate.replace("-", "");

		String predictDate = voucherInfo.getPredictDate();
		predictDate = predictDate.replace("-", "");

		tbVoucherHead.setVoucherName(voucherInfo.getVoucherName());
		tbVoucherHead.setCompany(newCompany);
		tbVoucherHead.setCustomer(customer);
		tbVoucherHead.setCusTaxId(voucherInfo.getCusTaxId());
		tbVoucherHead.setHeadItem(newHeadItem);
		tbVoucherHead.setHedgeNo(newHedgeNo);
		tbVoucherHead.setApplicant(voucherInfo.getApplicant());
		tbVoucherHead.setPredictDate(predictDate);
		tbVoucherHead.setCusTaxId(voucherInfo.getCusTaxId());
		tbVoucherHead.setAmountTotal(voucherInfo.getAmountTotal());
		tbVoucherHead.setStatus("0");
		tbVoucherHead.setCreditDate(creditDate);
		tbVoucherHead.setModiDate(Calendar.getInstance().getTime());
		tbVoucherHead.setModiUser(account);

		voucherHeadDao.update(tbVoucherHead);

		// insert voucher.detail to tb_voucher_head_detail
		List<Map<String, String>> detail = voucherInfo.getDetail();
		saveDetail(detail, voucherNo, account, "u");

		// delete oldHedge
		if (StringUtils.isEmpty(hedgeNoHO) == false && !hedgeNoHO.equals(hedgeNoHN)) {
			TbHedge delHedge = hedgeDao.findById(hedgeNoHO);
			hedgeDao.delete(delHedge);
		}
		String common = voucherInfo.getCommon();
		String voucherName = voucherInfo.getVoucherName();
		if (voucherCommonDao.findVoucherCommonListByVoucherName(voucherName).isEmpty()) {
			checkIfCommonAndSave(common, voucherNo, voucherName, account);
		} else if (voucherInfo.getCommon() == "Y" || voucherInfo.getCommon().equals("Y")) {
			if (voucherCommonDao.findVoucherCommonListByVoucherName(voucherName).get(0).getVoucherNo() != voucherNo) {
				throw new AccountingException("????????????????????????????????????");
			}
		}

		String newToken = voucherModifyAuthDao.findVoucherModifyAuthByVNo(voucherNo, account).get(0).getToken();
		List<TbVoucherDetail> detailList = voucherDetailDao.findVoucherDetailByVoucherNo(voucherNo);
		JSONArray newDetail = new JSONArray();

		for (int j = 0; j < detailList.size(); j++) {
			JSONObject detailObject = new JSONObject();
			detailObject.put("detailNo", detailList.get(j).getDetailNo());
			detailObject.put("hedgeNoD", detailList.get(j).getHedgeNo().getHedgeNo());
			newDetail.add(detailObject);
		}

		JSONArray result = new JSONArray();
		JSONObject object = new JSONObject();
		object.put("voucherNo", voucherNo);
		object.put("detail", newDetail);
		object.put("hedgeNoH", hedgeNo);
		object.put("mToken", newToken);
		result.add(object);

		return result.toString();

	}

	@Override
	public String sendVoucher(HttpServletRequest req, HttpServletResponse resp, JSONObject voucherInfo)
			throws AccountingException, Exception {
		Object voucherNo = voucherInfo.get("voucher_no");
		Object type = voucherInfo.get("type");
		return sendVoucher(voucherNo.toString(), type.toString());
	}

	private String sendVoucher(String voucherNo, String type) throws AccountingException, Exception {

		if (CollectionUtils.isEmpty(voucherSignDao.getVoucherSignListByVoucherNo(voucherNo))) {

			throw new AccountingException("??????????????????");

		} else {

			TbVoucherHead tbVoucherHead = voucherHeadDao.findVoucherHeadByVoucherNo(voucherNo).get(0);
			tbVoucherHead.setStatus("1");
			voucherHeadDao.update(tbVoucherHead);

			// send email
			List<TbVoucherSign> list = voucherSignDao.getVoucherSignListByVoucherNo(voucherNo);

			if (CollectionUtils.isEmpty(list)) {
				throw new AccountingException("?????????????????????");
			}

			TbVoucherSign tbVoucherSign = null;
			for (int i = 0; i < list.size(); i++) {
				if (StringUtils.isEmpty(list.get(i).getSignUserCheck())) {
					tbVoucherSign = list.get(i);
					break;
				}
			}
			if (tbVoucherSign == null) {
				throw new AccountingException("????????????");
			} else {
				sendMail(voucherNo, tbVoucherSign);
			}

		}
		return "success";
	}

	@Override
	public String getVoucher(HttpServletRequest req, HttpServletResponse resp) throws AccountingException, Exception {
		String voucherNo = req.getParameter("voucher_no");
		return getVoucher(voucherNo);
	}

	private String getVoucher(String voucherNo) throws Exception {

		List<TbVoucherHead> list = voucherHeadDao.findVoucherHeadByVoucherNo(voucherNo);

		if (list == null || list.isEmpty()) {
			throw new AccountingException("????????????");
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("voucherNo", voucherNo);
			object.put("voucherName", list.get(i).getVoucherName());
			object.put("company", list.get(i).getCompany().getName());
			object.put("customer", StringUtils.isEmpty(list.get(i).getCustomer()) ? null : list.get(i).getCustomer());
			object.put("cusTaxId", list.get(i).getCusTaxId());
			object.put("headItem", list.get(i).getHeadItem().getiId());
			object.put("hedgeNoH", list.get(i).getHedgeNo().getHedgeNo());
			object.put("amountTotal", list.get(i).getAmountTotal());
			object.put("applicant", list.get(i).getApplicant());
			object.put("status", list.get(i).getStatus());
			object.put("creditDate", list.get(i).getCreditDate());
			object.put("predictDate", list.get(i).getPredictDate());

			JSONArray detailList = new JSONArray();
			List<TbVoucherDetail> detail = voucherDetailDao.findVoucherDetailByVoucherNo(voucherNo);

			if (list == null || list.size() == 0) {
				object.put("detail", null);
			} else {
				for (int j = 0; j < detail.size(); j++) {
					JSONObject detailObject = new JSONObject();
					detailObject.put("detailNo", detail.get(j).getDetailNo());
					detailObject.put("projectId", detail.get(j).getProjectId());
					if (detail.get(j).getProjectId() == null || detail.get(j).getProjectId().isEmpty()) {
						detailObject.put("projectName", null);
					} else {
						if (projectDao.get(Integer.parseInt(detail.get(j).getProjectId())) == null) {
							throw new AccountingException("??????project_id??????");
						} else {
							String projectName = projectDao.get(Integer.parseInt(detail.get(j).getProjectId()))
									.getName();
							detailObject.put("projectName", projectName);
						}
					}
					detailObject.put("detailItem", detail.get(j).getDetailItem().getiId());
					detailObject.put("amount", detail.get(j).getAmount());
					detailObject.put("directions", detail.get(j).getDirections());
					detailObject.put("hedgeNoD", detail.get(j).getHedgeNo().getHedgeNo());
					detailList.add(detailObject);
				}
				object.put("detail", detailList);
			}
			result.add(object);
		}

		return result.toString();
	}

	@Override
	public String getVoucherHead(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception {
		String voucherNo = req.getParameter("voucher_no");
		String voucherName = req.getParameter("voucher_name");
		String company = req.getParameter("company");
		String creditDateF = req.getParameter("credit_date_f");
		String creditDateT = req.getParameter("credit_date_t");
		String predictDateF = req.getParameter("predict_date_f");
		String predictDateT = req.getParameter("predict_date_t");
		String applicant = req.getParameter("applicant");
		String status = req.getParameter("status");

		return getVoucherHead(voucherNo, voucherName, company,
				(creditDateF == null ? null : creditDateF.replace("-", "")),
				(creditDateT == null ? null : creditDateT.replace("-", "")),
				(predictDateF == null ? null : predictDateF.replace("-", "")),
				(predictDateT == null ? null : predictDateT.replace("-", "")), applicant, status);
	}

	private String getVoucherHead(String voucherNo, String voucherName, String company, String creditDateF,
			String creditDateT, String predictDateF, String predictDateT, String applicant, String status)
			throws AccountingException, Exception {

		List<TbVoucherHead> list = voucherHeadDao.getVoucherHeadList(voucherNo, voucherName, company, creditDateF,
				creditDateT, predictDateF, predictDateT, applicant, status);
		if (list == null || list.size() == 0) {
			return new JSONArray().toString();
		}
		JSONObject voucherList = new JSONObject();
		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("voucherNo", list.get(i).getVoucherNo());
			object.put("voucherName", list.get(i).getVoucherName());
			object.put("company", list.get(i).getCompany().getName());
			object.put("customer", StringUtils.isEmpty(list.get(i).getCustomer()) ? null : list.get(i).getCustomer());
			object.put("cusTaxId", list.get(i).getCusTaxId());
			object.put("headItem", list.get(i).getHeadItem().getiId());
			object.put("amountTotal", list.get(i).getAmountTotal());
			object.put("applicant", list.get(i).getApplicant());
			object.put("status", list.get(i).getStatus());
			object.put("creditDate", list.get(i).getCreditDate());
			object.put("predictDate", list.get(i).getPredictDate());

			result.add(object);
		}
		voucherList.put("voucher", result);
		return voucherList.toString();
	}

	@Override
	public String getVoucherDetail(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception {
		String voucherNo = req.getParameter("voucher_no");
		String company = req.getParameter("company");
		String applicant = req.getParameter("applicant");
		String status = req.getParameter("status");
		String customer = req.getParameter("customer");
		String cusTaxId = req.getParameter("cus_tax_id");
		String item = req.getParameter("item");
		String projectId = req.getParameter("project_id");
		String pageNo = req.getParameter("page_no");
		String pageSize = req.getParameter("page_size");

		return getVoucherDetail(voucherNo, company, applicant, status, customer, cusTaxId, item, projectId, pageNo,
				pageSize);
	}

	public String getVoucherDetail(String voucherNo, String company, String applicant, String status, String customer,
			String cusTaxId, String item, String projectId, String pageNo, String pageSize)
			throws AccountingException, Exception {

		List<VTbVoucherDetail> list = vTbVoucherDetailDao.getVoucherDetailVList(voucherNo, company, applicant, status,
				customer, cusTaxId, item, projectId, pageNo, pageSize);

		if (list == null || list.size() == 0) {
			return new JSONArray().toString();
		}
		JSONObject detailList = new JSONObject();
		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("voucherNo", list.get(i).getVoucherNo());
			if (StringUtils.isEmpty(list.get(i).getProject())) {
				object.put("project", null);
			} else {
				object.put("project", list.get(i).getProject());
			}
			object.put("item", list.get(i).getDetailItem());
			object.put("hedgeNo", list.get(i).getHedgeNo());
			object.put("amount", list.get(i).getAmount());
			object.put("directions", list.get(i).getDirections());
			object.put("status", list.get(i).getStatus());
			object.put("creditDate", list.get(i).getCreditDate());
			object.put("predictDate", list.get(i).getPredictDate());

			result.add(object);
		}

		detailList.put("detail", result);
		return detailList.toString();
	}

	@Override
	public String getVoucherMToken(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception {
		String voucherNo = req.getParameter("voucher_no");
		String account = req.getSession().getAttribute("Account").toString();
		return getVoucherMToken(voucherNo, account);
	}

	private String getVoucherMToken(String voucherNo, String account) throws AccountingException, Exception {

		List<TbVoucherHead> list = voucherHeadDao.findVoucherHeadByVoucherNo(voucherNo);
		if (list == null || list.size() == 0) {
			throw new AccountingException("????????????");
		}

		String status = list.get(0).getStatus();

		if (!status.equals("0")) {
			throw new AccountingException("??????????????????????????????");
		} else {
			// add new token in tb_voucher_modify_auth

			TbVoucherHead newVoucherNo = new TbVoucherHead();
			newVoucherNo.setVoucherNo(voucherNo);
			TbVoucherModifyAuth tbVoucherModifyAuth = new TbVoucherModifyAuth(newVoucherNo, account,
					Calendar.getInstance().getTime());
			voucherModifyAuthDao.save(tbVoucherModifyAuth);
		}
		String newToken = voucherModifyAuthDao.findVoucherModifyAuthByVNo(voucherNo, account).get(0).getToken();

		return newToken;
	}

	@Override
	public String deleteVoucher(HttpServletRequest req, HttpServletResponse resp, JSONObject voucherInfo)
			throws AccountingException, Exception {

		Object voucherNo = voucherInfo.get("voucher_no");
		Object token = voucherInfo.get("m_token");
		String account = req.getSession().getAttribute("Account").toString();
		return deleteVoucher(voucherNo.toString(), token.toString(), account, resp);
	}

	private String deleteVoucher(String voucherNo, String token, String account, HttpServletResponse resp)
			throws AccountingException, Exception {

		// check token
		List<TbVoucherModifyAuth> voucherModifyAuth = voucherModifyAuthDao.findVoucherModifyAuthByVNoAndToken(voucherNo,
				token);

		long lag = Calendar.getInstance().getTime().getTime();

		if (voucherModifyAuth.isEmpty() || (lag - (voucherModifyAuth.get(0).getCreateDate().getTime())) > 3600000) {

			resp.setStatus(401);
			return "error";
		} else {
			if (!voucherHeadDao.findVoucherHeadByVoucherNo(voucherNo).get(0).getStatus().equals("0")) {

				throw new AccountingException("????????????");
			} else {
				TbVoucherHead tbVoucherHead = voucherHeadDao.findVoucherHeadByVoucherNo(voucherNo).get(0);

				tbVoucherHead.setStatus("3");
				tbVoucherHead.setModiUser(account);
				tbVoucherHead.setModiDate(Calendar.getInstance().getTime());
				voucherHeadDao.update(tbVoucherHead);

			}
		}

		return "success";
	}

	/**
	 * @param detail
	 * @param voucherNo
	 * @Param account
	 * @param m s=save,u=update
	 */
	private void saveDetail(List<Map<String, String>> detail, String voucherNo, String account, String m)
			throws Exception {

		// update voucher.detail to tb_voucher_head_detail
		for (int i = 0; i < detail.size(); i++) {

			String detailNo = detail.get(i).get("detail_no");
			String projectId = detail.get(i).get("project_id");
			String detailItem = detail.get(i).get("detail_item");
			String amonunt = detail.get(i).get("amount");
			String directions = null;
			if ((detail.get(i).get("directions") != null)) {
				directions = detail.get(i).get("directions");
			}
			String status = "N";

			String hedgeNo = "";
			if (m.equals("s")) {
				hedgeNo = detail.get(i).get("hedge_no_d");
				if (StringUtils.isEmpty(hedgeNo)) {
					hedgeNo = createNewHedgeNo(voucherNo);
				}
				if (hedgeDao.findById(hedgeNo) != null) {
					throw new AccountingException(hedgeNo + " ?????????????????????");
				}
				TbHedge tbHedge = new TbHedge(hedgeNo, Integer.parseInt(amonunt), account,
						Calendar.getInstance().getTime());
				hedgeDao.save(tbHedge);

			} else if (m.equals("u")) {
				String hedgeNoDO = detail.get(i).get("hedge_no_d_o");
				String hedgeNoDN = detail.get(i).get("hedge_no_d_n");

				if (StringUtils.isEmpty(hedgeNoDN)) {

					// delete old_hedge_no and detail = del
					if (StringUtils.isEmpty(hedgeNoDO) == false) {
						delOldHedge(hedgeNoDO);
						status = "D";
						hedgeNo = hedgeNoDO + "_del";
					} else {
						hedgeNo = createNewHedgeNo(voucherNo);
					}
					TbHedge tbHedge = new TbHedge(hedgeNo, Integer.parseInt(amonunt), account,
							Calendar.getInstance().getTime());
					hedgeDao.save(tbHedge);
				} else {
					if (StringUtils.isEmpty(hedgeNoDO) || !hedgeNoDO.equals(hedgeNoDN)) {

						if (hedgeDao.findById(hedgeNoDN) != null) {
							throw new AccountingException(hedgeNoDN + " ?????????????????????");
						}

						hedgeNo = hedgeNoDN;

						// delete old_hedge_no
						if (StringUtils.isEmpty(hedgeNoDO) == false) {
							delOldHedge(hedgeNoDO);
						}
						TbHedge tbHedge = new TbHedge(hedgeNo, Integer.parseInt(amonunt), account,
								Calendar.getInstance().getTime());
						hedgeDao.save(tbHedge);

					} else if (hedgeNoDO.equals(hedgeNoDN)) {
						hedgeNo = hedgeNoDN;
					} else {
						hedgeNo = hedgeNoDN;
					}
				}

			}

			TbVoucherHead newVoucherNo = new TbVoucherHead();
			newVoucherNo.setVoucherNo(voucherNo);

			TbAClassItem newDetailItem = new TbAClassItem();
			newDetailItem.setiId(detailItem);

			TbHedge newHedgeNo = new TbHedge();
			newHedgeNo.setHedgeNo(hedgeNo.toString());

			if (CollectionUtils.isEmpty(voucherDetailDao.findVoucherDetailByDetailNo(detailNo, voucherNo))) {

				TbVoucherDetail voucherDetailAdd = new TbVoucherDetail(detailNo, newVoucherNo, projectId, newDetailItem,
						Integer.parseInt(amonunt), directions, newHedgeNo, account, Calendar.getInstance().getTime(),
						status);
				voucherDetailDao.save(voucherDetailAdd);
			} else {
				TbVoucherDetail voucherDetailAdd = voucherDetailDao.findVoucherDetailByDetailNo(detailNo, voucherNo)
						.get(0);

				voucherDetailAdd.setProjectId(projectId);
				voucherDetailAdd.setDetailItem(newDetailItem);
				voucherDetailAdd.setAmount(Integer.parseInt(amonunt));
				voucherDetailAdd.setDirections(directions);
				voucherDetailAdd.setHedgeNo(newHedgeNo);
				voucherDetailAdd.setIsDel(status);
				voucherDetailAdd.setModiUser(account);
				voucherDetailAdd.setModiDate(Calendar.getInstance().getTime());

				voucherDetailDao.update(voucherDetailAdd);
			}
			// delete oldHedge
			if (m.equals("u")) {
				String hedgeNoDO = detail.get(i).get("hedge_no_d_o");
				String hedgeNoDN = detail.get(i).get("hedge_no_d_n");
				if (StringUtils.isEmpty(hedgeNoDO) == false && !hedgeNoDO.equals(hedgeNoDN)) {
					TbHedge delHedge = hedgeDao.findById(hedgeNoDO);
					hedgeDao.delete(delHedge);
				}
			}
		}

	}

	/**
	 * @param common
	 * @param voucherNo
	 * @Param voucherName
	 * @param account
	 */
	private void checkIfCommonAndSave(String common, String voucherNo, String voucherName, String account)
			throws Exception {
		try {
			if (common.equals("Y")) {

				if (voucherCommonDao.findVoucherCommonListByVoucherName(voucherName) == null
						|| voucherCommonDao.findVoucherCommonListByVoucherName(voucherName).isEmpty()) {

					TbVoucherCommon tbVoucherCommon = new TbVoucherCommon(voucherNo, voucherName, account,
							Calendar.getInstance().getTime());
					voucherCommonDao.save(tbVoucherCommon);
				} else {
					throw new AccountingException("????????????????????????????????????");
				}

			}
		} catch (AccountingException e) {
			throw new AccountingException("????????????????????????");
		}

	}

	private String createNewVoucherNo() throws Exception {

		String voucherNo = null;

		String date = sdFormat.format(Calendar.getInstance().getTime());

		if (voucherHeadDao.findVoucherHeadByVoucherNoDate(date) == null
				|| voucherHeadDao.findVoucherHeadByVoucherNoDate(date).isEmpty()) {
			voucherNo = date + "001";

		} else {
			String oldNo = voucherHeadDao.findVoucherHeadByVoucherNoDate(date).get(0).getVoucherNo();
			int num = Integer.parseInt(oldNo.substring(8, oldNo.length())) + 1;
			String numString = String.format("%03d", num);
			voucherNo = date + numString;
		}
		return voucherNo.toString();
	}

	@Override
	public void sendMail(String voucherNo, TbVoucherSign tbVoucherSign) throws Exception {
		try {
			Config config = Config.getInstance();
			if (config.getValue("mail_attendanceapply_send").equals("Y")) {

				String vCode = "";
				String signUser = "";
				String url = config.getValue("mail_smtpsetting_voucher_signURL") + voucherNo;

				// ????????????
				try {
					InetAddress myIPaddress = InetAddress.getLocalHost().getLoopbackAddress();

					if (myIPaddress.toString().contains("localhost")) {
						url = "http://localhost:8080/rest/accounting/sign/" + voucherNo;
					}
				} catch (Exception e) {
				}
				// ???????????????
				try {
					Configuration configset = new Configuration().configure();

					String signerSet = configset.getProperty("sign_user");
					signUser = signerSet;

				} catch (Exception ex) {
					ex.printStackTrace();
				}

				vCode = tbVoucherSign.getvCode();
				signUser = StringUtils.isEmpty(signUser) ? (tbVoucherSign.getSignUser().getUsername()) : signUser;

				StringBuilder builder = new StringBuilder();

				builder.append("?????? :");
				builder.append(voucherNo);
				builder.append("\n????????? :");
				builder.append(vCode);
				builder.append("\n???????????? :");
				builder.append(url);

				// ??????
				String recipient = signUser + "@yesee.com.tw";
				// ??????
				String subject = "????????????";
				// ??????
				String base = builder.toString();
				sendMailDao.sendEmail(recipient, subject, base);

			}
		} catch (Exception e) {
			throw new AccountingException("????????????");
		}
	}

	private String createNewHedgeNo(String voucherNo) throws Exception {
		String hedgeNo = "";
		// ??????3???
		int a = (int) (Math.random() * 1000);
		String numString = String.format("%03d", a);
		// ??????1????????????
		Random r = new Random();
		int upCase = r.nextInt(26) + 65;
		String up = String.valueOf((char) upCase);
		hedgeNo = voucherNo + up + numString;
		while (hedgeDao.findById(hedgeNo) != null) {
			a = (int) (Math.random() * 1000);
			r = new Random();
			upCase = r.nextInt(26) + 65;
			up = String.valueOf((char) upCase);
			numString = String.format("%03d", a);
			hedgeNo = voucherNo + up + numString;
		}
		return hedgeNo;
	}

	private void delOldHedge(String hedgeNo) throws Exception {
		TbHedge delHedge = hedgeDao.findById(hedgeNo);
		TbHedgeDelHistory tbHedgeDelHistory = new TbHedgeDelHistory(hedgeNo, delHedge.getBalance(),
				delHedge.getCreateUser(), delHedge.getCreateDate());
		hedgeDelHistoryDao.save(tbHedgeDelHistory);
	}

}
