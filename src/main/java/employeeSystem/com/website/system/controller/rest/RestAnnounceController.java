package com.yesee.gov.website.controller.rest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesee.gov.website.model.TbAnnounce;
import com.yesee.gov.website.model.TbAnnounceDoc;
import com.yesee.gov.website.service.AnnounceService;
import com.yesee.gov.website.service.CompanyService;

@RestController
@RequestMapping(value = "/rest/announce")
public class RestAnnounceController {

	private static final Logger logger = LogManager.getLogger(RestAnnounceController.class);

	@Autowired
	private AnnounceService announceService;

	@Autowired
	private CompanyService companyService;

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得可見的公告及公告檔案資訊。 透過announceService.getRecords取得公告資料。
	 *                   透過announceService.getRecordsByAnnounceId取得公告檔案資料。
	 *                   整合公告及公告檔案資料並回傳至前端。
	 */
	@PostMapping("/getRecords")
	public Response getDepartment(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleDateFormat jd = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Integer authorise = (Integer) req.getSession().getAttribute("Authorise");
		String companyId = (String) req.getSession().getAttribute("companyId");
		String type = (String) req.getParameter("type");
		if (StringUtils.isEmpty(type)) {
			type = "";
		}
		Map<String, String> map = companyService.getCompanyName();
		List<TbAnnounce> list = announceService.getRecords(authorise, companyId, type);
		JSONArray data = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("id", list.get(i).getId());
			object.put("companyId", list.get(i).getCompanyId());
			object.put("type", list.get(i).getType());
			object.put("companyName", map.get(list.get(i).getCompanyId()));
			object.put("subject", list.get(i).getSubject());
			object.put("content", list.get(i).getContent());
			object.put("time", jd.format(list.get(i).getCreatedAt()));
			if (list.get(i).getUpdatedAt() != null) {
				object.put("updatedAt", sdf.format(list.get(i).getUpdatedAt()));
			} else {
				object.put("updatedAt", "");
			}
			List<TbAnnounceDoc> docList = announceService.getRecordsByAnnounceId(list.get(i).getId());
			object.put("quantity", docList.size());
			for (int j = 0; j < docList.size(); j++) {
				object.put("docId" + j, docList.get(j).getDocId());
				object.put("docName" + j, docList.get(j).getName());
				object.put("docPath" + j, docList.get(j).getDocPath());
			}
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得可見的公司資訊。 透過companyService.getCompanyName取得公司資料並回傳至前端。
	 */
	@PostMapping("/getCompany")
	public Response getCompany(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map<String, String> map = companyService.getCompanyName();
		Integer authorise = (Integer) req.getSession().getAttribute("Authorise");
		String companyId = (String) req.getSession().getAttribute("companyId");
		JSONArray data = new JSONArray();
		if (authorise == 1) {
			for (String key : map.keySet()) {
				JSONObject object = new JSONObject();
				object.put("id", key);
				object.put("name", map.get(key));
				data.put(object);
			}
		} else {
			JSONObject object = new JSONObject();
			object.put("id", companyId);
			object.put("name", map.get(companyId));
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @throws Exception 新增或更新公告及公告檔案資訊。
	 *                   接收前端傳回的公告及公告檔案資訊儲存至本地並透過announceService.update及announceService.updateDoc儲存。
	 *                   刪除的公告檔案透過announceService.deleteDoc進行刪除並同時刪除本地檔案。
	 */
	@PostMapping("/update")
	public Response updateAnnounce(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map<String, Object> map = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result = "操作失敗";
		try {
			List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
			int count = 0;
			for (FileItem item : multiparts) {
				if (!item.isFormField()) {
					break;
				}
				++count;
			}
			for (int i = 0; i < count; i++) {
				map.put(multiparts.get(i).getFieldName(), multiparts.get(i).getString("UTF-8"));
			}

			TbAnnounce object = new TbAnnounce();
			String mode = (String) map.get("mode");
			logger.info(mode + " announce : ");
			Integer id = null;
			String updatedAt = (String) map.get("updatedAt");
			String companyId = (String) map.get("companyId");
			String type = (String) map.get("type");
			String subject = (String) map.get("subject");
			String content = (String) map.get("content");
			String user = (String) req.getSession().getAttribute("Account");
			String delDoc = (String) map.get("delDoc");
			if ("edit".equals(mode)) {
				id = Integer.valueOf((String) map.get("id"));
				object = announceService.getAnnounceById(id);
				logger.info("announce_id : " + id);
				if (object == null) {
					result = "該公告已被刪除";
					return Response.status(Response.Status.NOT_FOUND).entity(result).build();
				} else if (object.getUpdatedAt() != null) {
					if (!sdf.format(object.getUpdatedAt()).equals(updatedAt)) {
						result = "該公告已被修改";
						return Response.status(Response.Status.CONFLICT).entity(result).build();
					}
				}
				result = "修改成功";
				object.setUpdatedAt(new Date());
				object.setUpdateUser(user);
			} else {
				object.setCreateUser(user);
				object.setCreatedAt(new Date());
				result = "新增成功";
			}
			object.setCompanyId(companyId);
			object.setSubject(subject);
			object.setType(type);
			object.setContent(content);
			logger.info("company_id : " + companyId);
			logger.info("type : " + type);
			logger.info("subject : " + subject);
			logger.info("content : " + content);
			logger.info("user : " + user);
			logger.info("delDoc : " + delDoc);
			announceService.update(object);
			id = object.getId();
			File fileSaveDir = new File(File.separator + "upload" + File.separator + "announce" + File.separator + id);
			fileSaveDir.mkdirs();
			if ("add".equals(mode)) {
				int quantity = Integer.parseInt((String) map.get("quantity"));
				logger.info("doc quantity : " + quantity);
				for (int i = count; i < quantity + count; i++) {
					TbAnnounceDoc docObject = new TbAnnounceDoc();
					docObject.setAnnounceId(String.valueOf(id));
					FileItem doc = multiparts.get(i);
					logger.info("doc getName : " + doc.getName());
					String docReplace = doc.getName().replace(" ", "_");
					doc.write(new File(fileSaveDir + File.separator + docReplace));
					docObject.setName(docReplace);
					docObject.setDocPath(fileSaveDir + File.separator + docReplace);
					announceService.updateDoc(docObject);
				}
			} else {
				if (delDoc != "") {
					int moveQuantity = delDoc.split(",").length - 1;
					logger.info("doc moveQuantity : " + moveQuantity);
					for (int i = 1; i < moveQuantity + 1; i++) {
						String[] splitDocId = delDoc.split(",");
						String delFilePath = announceService.getFilePath(Integer.valueOf(splitDocId[i]));
						File delFile = new File(delFilePath);
						logger.info("split" + splitDocId[i]);
						delFile.delete();
						announceService.deleteDoc(Integer.valueOf(splitDocId[i]));
					}
				}
				int newQuantity = Integer.parseInt((String) map.get("newQuantity"));
				logger.info("doc newQuantity : " + newQuantity);
				for (int i = count; i < newQuantity + count; i++) {
					TbAnnounceDoc docObject = new TbAnnounceDoc();
					docObject.setAnnounceId(String.valueOf(id));
					FileItem doc = multiparts.get(i);
					String docReplace = doc.getName().replace(" ", "_");
					doc.write(new File(fileSaveDir + File.separator + docReplace));
					docObject.setName(docReplace);
					docObject.setDocPath(fileSaveDir + File.separator + docReplace);
					logger.info("doc getName : " + doc.getName());
					announceService.updateDoc(docObject);
				}
			}
		} catch (Exception e) {
			logger.error("error:", e);
		}
		return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @throws Exception 刪除公告及其公告檔案資訊。 接收前端傳回的公告資料並透過announceService.delete刪除。
	 *                   同時刪除本地相關檔案。
	 */
	@PostMapping("/delete")
	public void deleteAnnounce(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer id = Integer.valueOf(req.getParameter("id"));
		List<TbAnnounceDoc> delDocList = announceService.getRecordsByAnnounceId(id);
		for (int i = 0; i < delDocList.size(); i++) {
			String delFilePath = delDocList.get(i).getDocPath();
			File delFile = new File(delFilePath);
			delFile.delete();
		}
		boolean bol = FileUtils
				.deleteQuietly(new File(File.separator + "upload" + File.separator + "announce" + File.separator + id));
		logger.info("delete announce file : " + bol);
		logger.info("delete announce id : " + id);
		announceService.delete(id);
	}
}