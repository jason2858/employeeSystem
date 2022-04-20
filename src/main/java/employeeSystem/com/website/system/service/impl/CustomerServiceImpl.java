package com.yesee.gov.website.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.CustomerDao;
import com.yesee.gov.website.model.TbCustomer;
import com.yesee.gov.website.service.CustomerService;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {
	private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);
	
	@Autowired
	private CustomerDao customerDao;

	@Override
	public List<TbCustomer> getList(String account, String authorise) throws Exception {
		if("1".equals(authorise)){
			return customerDao.getListByNotEqualsStatus("delete");
		}else {
			return customerDao.getVisibleList(account);
		}
	}

	@Override
	public void save(TbCustomer customer, String account) throws Exception {
		customer.setCreator(account);
		customer.setStatus("unsign");
		customerDao.save(customer);
	}

	@Override
	public TbCustomer findById(Integer id) throws Exception {
		return customerDao.findById(id);
	}

	@Override
	public TbCustomer checkUpdate(TbCustomer customer) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TbCustomer object = this.findById(customer.getId());
		String SQLUpdated = "";
		String webUpdated = "";
		if(object.getUpdatedAt() != null) {
			SQLUpdated = sdf.format(object.getUpdatedAt());
		}
		if(customer.getUpdatedAt() != null) {
			webUpdated = sdf.format(customer.getUpdatedAt());
		}
		if(!SQLUpdated.equals(webUpdated)) {
			object = null;
			logger.info("customer has been updated");
		}
		return object;
	}
	
	@Override
	public void update(TbCustomer customer, TbCustomer object) throws Exception {
		object.setName(customer.getName());
		object.setType(customer.getType());
		object.setInfo(customer.getInfo());
		object.setEin(customer.getEin());
		object.setContactPerson(customer.getContactPerson());
		object.setContactPhone(customer.getContactPhone());
		object.setContactEmail(customer.getContactEmail());
		object.setUpdatedAt(new Date());
		customerDao.save(object);
	}

	@Override
	public void delete(TbCustomer object) throws Exception {
		if("unsign".equals(object.getStatus())){
			customerDao.delete(object);
		}else {
			object.setStatus("delete");
			object.setUpdatedAt(new Date());
			customerDao.save(object);
		}
	}

	@Override
	public void sign(TbCustomer object) throws Exception {
		object.setStatus("signed");
		object.setUpdatedAt(new Date());
		customerDao.save(object);
	}

}