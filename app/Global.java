import models.RoomManager;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.*;
import play.libs.Akka;
import xml.CardTable;

public class Global extends GlobalSettings {
	  @Override
	  public void onStart(Application app) {
	    Logger.info("ProjectMSrv has started");
	    
	    CardTable.getInstance().initCard(app.resourceAsStream("xml/card_table.xml"));
	    CardTable.getInstance().initEvent(app.resourceAsStream("xml/card_eventzone.xml"));
	    
	    RoomManager.defaultRoomManager = Akka.system().actorOf(Props.create(RoomManager.class));
	  }  
	  
	  @Override
	  public void onStop(Application app) {
	    Logger.info("ProjectMSrv shutdown...");
	  }  
}
