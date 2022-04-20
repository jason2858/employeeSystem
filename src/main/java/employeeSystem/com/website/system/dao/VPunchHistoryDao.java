package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.VTbPunchHistory;

public interface VPunchHistoryDao {

	public List<VTbPunchHistory> getPunchList(String start, String end) throws Exception;

}
