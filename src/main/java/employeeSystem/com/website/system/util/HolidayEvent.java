package com.yesee.gov.website.util;

public enum HolidayEvent {
	PMD("228"), NY("元旦"), CNY("春節"), CD("兒童節"), CMF("清明節"), DBF("端午節"), ND("國慶日"), LC("農曆"), MAF("中秋節"), LD("勞動節");

	private String holiday;

	HolidayEvent(String holiday) {
		this.holiday = holiday;
	}

	public String holiday() {
		return holiday;
	}
}