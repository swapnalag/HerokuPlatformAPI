package jp.co.flect.heroku.platformapi;


import java.io.IOException;
import java.util.List;

import jp.co.flect.heroku.platformapi.model.App;
import jp.co.flect.heroku.platformapi.model.Dyno;

public class TestApp {
	
	public static final String USERNAME = "swapnalag@cybage.com";//System.getenv("HEROKU_USERNAME");
	public static final String PASSWORD = "Divine@22";//System.getenv("HEROKU_PASSWORD");

	public static void main(String[] args) throws IOException{
		
		System.out.println(USERNAME + ", " + PASSWORD);
		PlatformApi api = PlatformApi.fromPassword(USERNAME, PASSWORD);
		List<App> list = api.getAppList();
		
		App app = list.get(0);
		System.out.println("First app: " + app.getName());
		
		api.updateFormation("incomm-ingest-staging","system",1,"Performance-L");
		
		List<Dyno> listDyno = api.getDynoList("incomm-ingest-staging");
		System.out.println("listDyno Count: " + listDyno.size());
		
		Dyno dyno = listDyno.get(0);
		System.out.println("Dyno Name: " + dyno.getName());
		System.out.println("Dyno Type: " + dyno.getType());
		System.out.println("Dyno Command: " + dyno.getCommand());
		System.out.println("Dyno State: " + dyno.getState());
		System.out.println("Dyno Size: " + dyno.getSize());
		//dyno.setSize("Performance-M");
		//System.out.println("Dyno Size: " + dyno.getSize());
		//Dyno dyno1 = new Dyno();
		dyno.set("size", "standard-1X");
		//dyno.setDyno(dyno1);
		//System.out.println("Dyno Size: " + dyno.getSize());
		//api.restart("incomm-ingest-staging");
		//api.runDynoSize("incomm-ingest-staging",dyno );
		//System.out.println("Dyno Size: " + dyno.getSize());
		
		//api.createLogSession("incomm-ingest-staging");
		
		//api.changePassword("Divine@22","Divine@20" );
		
		
		
	}
	
	
	
}
