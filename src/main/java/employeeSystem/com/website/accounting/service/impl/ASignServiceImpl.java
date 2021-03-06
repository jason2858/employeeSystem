package employeeSystem.com.website.accounting.service.impl;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import employeeSystem.com.website.accounting.dao.AccountingClosedDao;
import employeeSystem.com.website.accounting.dao.SignRoleDao;
import employeeSystem.com.website.accounting.dao.VoucherHeadDao;
import employeeSystem.com.website.accounting.dao.VoucherSignDao;
import employeeSystem.com.website.accounting.exception.AccountingException;
import employeeSystem.com.website.accounting.model.TbAccountingClosedPK;
import employeeSystem.com.website.accounting.model.TbSignRole;
import employeeSystem.com.website.accounting.model.TbVoucherHead;
import employeeSystem.com.website.accounting.model.TbVoucherSign;
import employeeSystem.com.website.accounting.service.ASignService;
import employeeSystem.com.website.accounting.service.ClosedService;
import employeeSystem.com.website.accounting.service.VoucherService;
import employeeSystem.com.website.system.model.TbEmployees;
import employeeSystem.com.website.system.service.SendMailService;
import employeeSystem.com.website.system.util.Config;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("ASignService")
public class ASignServiceImpl implements ASignService {

	private static final Logger logger = LogManager.getLogger(ASignServiceImpl.class);

	@Autowired
	public VoucherSignDao voucherSignDao;

	@Autowired
	public VoucherHeadDao voucherheadDao;

	@Autowired
	public AccountingClosedDao accountingClosedDao;

	@Autowired
	public ClosedService closedService;

	@Autowired
	public SignRoleDao signRoleDao;

	@Autowired
	public VoucherService voucherService;

	@Autowired
	public SendMailService sendMailDao;

	@Override
	public String getSignList(HttpServletRequest req, HttpServletResponse resp) throws AccountingException, Exception {
		String voucherNo = req.getParameter("voucher_no");

		return getSignList(voucherNo);
	}

	private String getSignList(String voucherNo) throws AccountingException, Exception {

		List<TbVoucherSign> list = voucherSignDao.getVoucherSignListByVoucherNo(voucherNo);

		if (list == null || list.size() == 0) {
			throw new AccountingException("????????????");
		}
		JSONObject setList = new JSONObject();
		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("voucherNo", list.get(i).getVoucherNo().getVoucherNo());
			object.put("roleId", list.get(i).getRoleId().getRoleId());
			object.put("role", list.get(i).getRoleId().getRole().getName());
			object.put("signUser", list.get(i).getSignUser().getUsername());
			String type = list.get(i).getSignType();
			String signUserCheck = list.get(i).getSignUserCheck();

			if (StringUtils.isEmpty(type) || StringUtils.isEmpty(signUserCheck)) {
				object.put("status", "UNDONE");
			} else if (type.equals("Y")) {
				object.put("status", "FINISHED");
			} else if (type.equals("N")) {
				object.put("status", "REJECT");
			}

			result.add(object);
		}
		setList.put("set", result);
		return setList.toString();
	}

	@Override
	public void saveVoucherSign(HttpServletRequest req, JSONObject body) throws AccountingException, Exception {
		String voucherNoString = (String) body.get("voucher_no");
		JSONArray set = (JSONArray) body.get("set");
		String createUser = req.getSession().getAttribute("Account").toString();

		saveVoucherSign(voucherNoString, set, createUser);
	}

	private void saveVoucherSign(String voucherNoString, JSONArray set, String createUser) throws Exception {

		TbVoucherHead voucherhead = voucherheadDao.findVoucherHeadByVoucherNo(voucherNoString).get(0);
		if (!voucherhead.getStatus().equals("0")) {
			throw new AccountingException("Voucher????????????????????????");
		}

		voucherSignDao.delete(voucherNoString);

		for (int i = 0; i < set.size(); i++) {

			String roleIdString = (String) ((JSONObject) set.get(i)).get("role_id");
			String signUserString = (String) ((JSONObject) set.get(i)).get("sign_user");
			TbEmployees signUser = new TbEmployees();
			signUser.setUsername(signUserString);

			Integer seqNo = null;
			TbVoucherHead voucherNo = new TbVoucherHead();
			voucherNo.setVoucherNo(voucherNoString);
			UUID uuid = UUID.randomUUID();
			String uuidString = uuid.toString();
			String[] vCodelist = uuidString.split("-");
			String vCode = vCodelist[0] + vCodelist[1] + vCodelist[2];
			String signUserCheck = null;
			Date signDate = null;
			String signType = null;
			TbSignRole roleId = new TbSignRole();
			roleId.setRoleId(roleIdString);
			String reason = null;
			Timestamp createDate = new Timestamp(System.currentTimeMillis());

			TbVoucherSign tbVoucherSign = new TbVoucherSign(seqNo, voucherNo, signUser, vCode, signUserCheck, signDate,
					signType, roleId, reason, createUser, createDate);

			voucherSignDao.update(tbVoucherSign);
		}
	}

	@Override
	public String updateVoucherSign(HttpServletRequest req, JSONObject body) throws AccountingException, Exception {
		String voucherNoString = (String) body.get("voucher_no");
		String signUser = (String) body.get("sign_user");
		String vCode = (String) body.get("v_code");
		String signType = (String) body.get("sign_type");
		String reason = (String) body.get("reason");
		String createUser = req.getSession().getAttribute("Account").toString();
		if (StringUtils.isEmpty(reason)) {
			reason = "";
		}
		if (!signUser.equals(createUser)) {
			throw new AccountingException("????????????????????????????????????");
		}

		return updateVoucherSign(voucherNoString, signUser, vCode, signType, reason, createUser);
	}

	private String updateVoucherSign(String voucherNoString, String signUser, String vCode, String signType,
			String reason, String createUser) throws Exception {
		TbVoucherHead voucherhead = voucherheadDao.findVoucherHeadByVoucherNo(voucherNoString).get(0);
		String AccountDay = voucherhead.getCreditDate();

		String year = AccountDay.substring(0, 4);
		String month = AccountDay.substring(4, 6);

		TbAccountingClosedPK pk = new TbAccountingClosedPK(year, month);
		if (Objects.isNull(accountingClosedDao.findById(pk))) {
			closedService.getClosed(year, createUser);
		} else if (accountingClosedDao.findById(pk).getStatus().equals("C")
				|| accountingClosedDao.findById(pk).getStatus().equals("L")) {
			throw new AccountingException("???????????????????????????????????????");
		}

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.voucherNo.voucherNo", voucherNoString);
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("voucherNo.voucherNo");
		orderBy.add("roleId");
		Boolean desc = true;
		List<TbVoucherSign> list = voucherSignDao.getList(param, orderBy, desc);

		if (list == null || list.size() == 0) {
			throw new AccountingException("????????????");
		}

		for (int i = 0; i < list.size(); i++) {
			TbVoucherSign tbVoucherSign = list.get(i);
			if (StringUtils.isEmpty(tbVoucherSign.getSignUserCheck())) {
				if (tbVoucherSign.getSignUser().getUsername().equals(signUser)) {
					if (StringUtils.isEmpty(tbVoucherSign.getSignType())) {
						// ??????v_code
						if (!tbVoucherSign.getvCode().equals(vCode)) {
							throw new AccountingException("???????????????");
						}
						// ??????????????????
						Timestamp signDate = new Timestamp(System.currentTimeMillis());
						tbVoucherSign.setSignUserCheck(signUser);
						tbVoucherSign.setSignType(signType);
						tbVoucherSign.setSignDate(signDate);
						tbVoucherSign.setReason(reason);
						voucherSignDao.update(tbVoucherSign);

						// ??????
						if (signType.equals("N")) {
							TbVoucherHead tbVoucherHead = voucherheadDao.findVoucherHeadByVoucherNo(voucherNoString)
									.get(0);
							tbVoucherHead.setStatus("4");
							voucherheadDao.update(tbVoucherHead);

							// ????????????????????????????????????
							Config config = Config.getInstance();
							if (config.getValue("mail_attendanceapply_send").equals("Y")) {

								String url = config.getValue("mail_smtpsetting_voucher_signURL") + voucherNoString;

								// ????????????
								try {
									InetAddress myIPaddress = InetAddress.getLocalHost().getLoopbackAddress();

									if (myIPaddress.toString().contains("localhost")) {
										url = "http://localhost:8080/rest/accounting/sign/" + voucherNoString;
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
								signUser = StringUtils.isEmpty(signUser) ? (tbVoucherSign.getSignUser().getUsername())
										: signUser;

								StringBuilder builder = new StringBuilder();

								builder.append("?????????");
								builder.append(voucherNoString);
								builder.append("\n???????????????");
								builder.append(signUser);
								builder.append("\n???????????????");
								builder.append(reason);

								// ??????
								String recipient = signUser + "@yesee.com.tw";
								// ??????
								String subject = "??????????????????";
								// ??????
								String base = builder.toString();
								sendMailDao.sendEmail(recipient, subject, base);
							}
							return "????????????";
						}

						// ??????????????????
						if (i != list.size() - 1) {
							TbVoucherSign nextUser = list.get(i + 1);
							voucherService.sendMail(voucherNoString, nextUser);
							return "??????????????????????????????????????????";
						} else {
							TbVoucherHead tbVoucherHead = voucherheadDao.findVoucherHeadByVoucherNo(voucherNoString)
									.get(0);
							tbVoucherHead.setStatus("2");
							voucherheadDao.update(tbVoucherHead);
							return "?????????????????????";
						}
					} else {
						throw new AccountingException("??????????????????");
					}
				}
			} else {
				if (tbVoucherSign.getSignUserCheck().equals(tbVoucherSign.getSignUser().getUsername())) {
					if (tbVoucherSign.getSignUser().getUsername().equals(signUser)) {
						if (tbVoucherSign.getSignType().equals("N")) {
							return "?????????";
						} else if (tbVoucherSign.getSignType().equals("Y")) {
							continue;
						}
					} else {
						if (tbVoucherSign.getSignType().equals("N")) {
							return "?????????";
						} else if (tbVoucherSign.getSignType().equals("Y")) {
							continue;
						}
					}
				}

				if (!tbVoucherSign.getSignUserCheck().equals(tbVoucherSign.getSignUser().getUsername())) {
					if (StringUtils.isEmpty(tbVoucherSign.getSignUser().getUsername())) {
						if (tbVoucherSign.getSignType().equals("N")) {
							throw new AccountingException("??????????????????");
						} else if (tbVoucherSign.getSignType().equals("Y")) {
							throw new AccountingException("??????????????????");
						}
					}
				}
			}
		}
		return "???????????????";
	}

	@Override
	public String getSignRoleList() throws AccountingException, Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		List<TbSignRole> list = new ArrayList<TbSignRole>();
		list = signRoleDao.getList(param);
		System.out.println("string = :" + list.size());

		if (list == null || list.size() == 0) {
			throw new AccountingException("????????????");
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("roleId", list.get(i).getRoleId());
			object.put("role", list.get(i).getRole().getName());
			result.add(object);
		}

		return result.toString();
	}
}
