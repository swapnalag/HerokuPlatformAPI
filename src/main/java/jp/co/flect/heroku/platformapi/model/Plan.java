package jp.co.flect.heroku.platformapi.model;

import java.util.Map;
import java.util.Date;

public class Plan extends BasicModel {
	
	public Plan() {
		super();
	}
	
	public Plan(Map<String, Object> map) {
		super(map);
	}
	
	public String getName() { return getAsString("name");}
	
	public String getPlanName() {
		String s = getName();
		if (s == null) {
			return null;
		}
		int idx = s.indexOf(":");
		if (idx == -1) {
			return null;
		}
		return s.substring(idx+1);
	}
	
	public String getAddonName() {
		String s = getName();
		if (s == null) {
			return null;
		}
		int idx = s.indexOf(":");
		if (idx == -1) {
			return null;
		}
		return s.substring(0, idx);
	}
	
	public String getDescription() { return getAsString("description");}
	
	public int getPriceCents() { return getAsInt("price.cents");}
	public String getPriceUnit() { return getAsString("price.unit");}
	
	public String getState() { return getAsString("state");}
}
