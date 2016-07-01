package jp.co.flect.heroku.platformapi.model;

import java.util.Map;
import java.util.Date;

public class Dyno extends BasicModel {
	
	public Dyno() {
		super();
	}
	
	public Dyno(Map<String, Object> map) {
		super(map);
	}
	
	public String command = null;
	
	public String getName() { return getAsString("name");}
	
	public String getCommand() { return getAsString("command");}
	
	public String getType() { return getAsString("type");}
	
	public String getSize() { return getAsString("size");}
	public void setSize(String commandNew) {
		command=commandNew;
		}
	public String getReleaseId() { return getAsString("release.id");}
	public int getReleaseVersion() { return getAsInt("release.version");}
	
	public String getState() { return getAsString("state");}
	
	public boolean isAttach() { return getAsBoolean("attach");}
	public String getAttachUrl() { return getAsString("attach_url");}
	
	//public Dyno getDyno() { return getAsString("dyno");}
	public void setDyno(Dyno s) { set("dyno", s);}
	
}
