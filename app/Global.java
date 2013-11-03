import java.util.TimerTask;

import models.RoomManager;
import models.RoomManager.Join;
import models.RoomManager.UpdateTimer;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.*;
import play.libs.Akka;
import system.TaskManager;
import system.TimeUtil;
import xml.CardTable;

public class Global extends GlobalSettings {
	  @Override
	  public void onStart(Application app) {
	    Logger.info("ProjectMSrv has started");
	    
	    CardTable.getInstance().initCard(app.resourceAsStream("xml/card_table.xml"));
	    CardTable.getInstance().initEvent(app.resourceAsStream("xml/card_eventzone.xml"));
	    
	    RoomManager.defaultRoomManager = Akka.system().actorOf(Props.create(RoomManager.class));
	    
		TaskManager.getInstance().addTask( new TimerTask(){
			@Override
			public void run() {
				//SessionManager.getInstance().update();
				RoomManager.defaultRoomManager.tell(new UpdateTimer(System.currentTimeMillis()), null);
			}}, TimeUtil.second( 5 ), TimeUtil.second( 5 )  );
	  }  
	  
	  @Override
	  public void onStop(Application app) {
	    Logger.info("ProjectMSrv shutdown...");
	  }  
}
