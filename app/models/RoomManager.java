package models;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.HashMap;

import play.libs.Akka;
import play.libs.Json;
import protocol.JoinSuccessMsg;

import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class RoomManager extends UntypedActor {
	
    // Default RoomManager.
    static ActorRef defaultRoomManager = Akka.system().actorOf(Props.create(RoomManager.class));
    
	private long mNewRoomId = 10000;
	
	private HashMap<Long,GameRoom> mRooms = new HashMap<Long,GameRoom>();
	private ArrayList<GameRoom> mJoinRooms = new ArrayList<GameRoom>();
		
	private RoomManager()
	{

	}
	
	public synchronized GameRoom get( long roomId )
	{
		return mRooms.get( roomId );
	}
	
    public static void join(final User user, final int maxuser) throws Exception
    {
        // Send the Join message to the room
        String result = (String)Await.result(ask(defaultRoomManager,new Join(user, maxuser), 1000), Duration.create(1, SECONDS));
        
        if("OK".equals(result) == false) {
            
            // Cannot connect, create a Json error.
            ObjectNode error = Json.newObject();
            error.put("error", result);
            
            // Send the error to the socket.
            user.getChannel().write(error);           
        } 
    }

	@Override
	public void onReceive(Object message) throws Exception {
        
		if(message instanceof Join) {
            
            // Received a Join message
            Join join = (Join)message;
            
            User user = join.user;
            
            if(user.getGameRoom() == null)
        	{
        		if( mJoinRooms.size() > 0 )
        		{
        			GameRoom room = mJoinRooms.get(0);
        			room.addUser(user);
        			
        			if( room.isFull() )
        				mJoinRooms.remove(0);
        			
        			user.setGameRoom(room);
        			user.SendPacket(new JoinSuccessMsg(room.getRoomId()).toJson());
        		}
        		else
        		{
        			 GameRoom room = new GameRoom(mNewRoomId++, join.maxuser);
        			 room.addUser(user);
        			 
        			 mRooms.put(room.getRoomId(), room);
        			 mJoinRooms.add(room);
        			 
        			 user.setGameRoom(room);
        			 user.SendPacket(new JoinSuccessMsg(room.getRoomId()).toJson());
        		}
        	}
            
        } else if(message instanceof Leave)  {
            
            // Received a Quit message
            Leave leave = (Leave)message;
            

        
        } else {
            unhandled(message);
        }
		
	}
	
    public static class Join {
        
        final User user;
        final int maxuser;
        
        public Join(User user, int maxuser) {
            this.user = user;
            this.maxuser = maxuser;
        }
        
    }
    
    public static class Leave {
        
        final long roomId;
        final long userId;
        
        public Leave(long roomId, long userId) {
            this.roomId = roomId;
            this.userId = userId;
        }
        
    }	
}
