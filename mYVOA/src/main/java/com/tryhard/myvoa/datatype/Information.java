package com.tryhard.myvoa.datatype;

import java.util.UUID;

public class Information {
	private UUID mId;
	private String Etitle;
	private String Ctitle;
	private String Website;
	public boolean isBuildTable = false;
	
	public Information(){
		 mId = UUID.randomUUID();
	}
	
	public String getWebsite() {
		return Website;
	}
	
	public void setWebsite(String website) {
		Website = website;
	}

	

	public String getEtitle() {
		return Etitle;
	}

	public void setEtitle(String etitle) {
		Etitle = etitle;
	}

	public String getCtitle() {
		return Ctitle;
	}

	public void setCtitle(String ctitle) {
		Ctitle = ctitle;
	}

	public UUID getId() {
		return mId;
	}
		
}