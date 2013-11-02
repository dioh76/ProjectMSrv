package models;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.HashMap;

import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import protocol.*;

import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.node.ObjectNode;

import protocol.server.*;

public class RoomManager extends UntypedActor {
	
    // Default RoomManager.
	public static ActorRef defaultRoomManager;
    
	private long mNewRoomId = 10000;
	
	private HashMap<Long,GameRoom> mRooms = new HashMap<Long,GameRoom>();
	private ArrayList<GameRoom> mJoinRooms = new ArrayList<GameRoom>();//ready to start play
		
	private RoomManager()
	{
		Akka.system().scheduler().schedule(
	            Duration.create(5000, MILLISECONDS),
	            Duration.create(5000, MILLISECONDS),
	            defaultRoomManager,
	            new UpdateTimer(System.currentTimeMillis()),
	            Akka.system().dispatcher(),
	            /** sender **/ null
	        );
	}
	
	public synchronized GameRoom get( long roomId )
	{
		return mRooms.get( roomId );
	}
	
    public static void join(final User user, final int maxuser) throws Exception
    {
    	defaultRoomManager.tell(new Join(user, maxuser), null);
    }
    
    public static void leave(final User user, final long roomId)
    {
    	defaultRoomManager.tell(new Leave(roomId,user), null);
    }
    
    public static void ready(final long roomId)
    {
    	defaultRoomManager.tell( new Ready(roomId), null);
    }

	@Override
	public void onReceive(Object message) throws Exception {
        
		if(message instanceof Join) {
            
            // Received a Join message
            Join join = (Join)message;
            
            User user = join.user;
            
            if(user.getGameRoom() == null)
        	{
            	synchronized(mJoinRooms)
            	{
            		if( mJoinRooms.size() > 0 )
            		{
            			//TODO : check room status if room is playing or not
            			GameRoom room = mJoinRooms.get(0);
            			room.addUser(user);
            			
            			if( room.isFull() )
            			{
            				mJoinRooms.remove(0);
            				room.setPlaying(true);
            			}
            			
            			user.setGameRoom(room);
            			
            		}
            		else
            		{
            			 GameRoom room = new GameRoom(mNewRoomId++, join.maxuser);
            			 room.addUser(user);
            			 
            			 mRooms.put(room.getRoomId(), room);
            			 mJoinRooms.add(room);
            			 
            			 user.setGameRoom(room);
            			
            		}
            	}
        	}
            
        } else if(message instanceof Leave)  {
            
            // Received a Quit message
            Leave leave = (Leave)message;
            synchronized(mRooms)
            {
            	GameRoom room = this.get(leave.roomId);
            	room.removeUser(leave.user.getUserId());
            	leave.user.setGameRoom(null);
            	
            	mRooms.remove(room.getRoomId());
            	
            	//If user leave, notify others
            	leave.user.SendPacket(new LeaveMsg(room.getRoomId()).toJson());
            }
        
        } else if(message instanceof Ready) {
        	Ready ready = (Ready)message;
        	
        	synchronized(mJoinRooms)
        	{
        		for(int i = 0; i < mJoinRooms.size(); i++)
        		{
        			if(mJoinRooms.get(i).getRoomId() == ready.roomId)
        			{
        				mJoinRooms.remove(i);
        				break;
        			}
        		}
        	}
        }
        else if(message instanceof UpdateTimer ){
        	UpdateTimer timer = (UpdateTimer)message;
        	Update(timer.currentMilliSec);
        }
        else {
            unhandled(message);
        }
		
	}
	
	private void Update(long currentmillisec )
	{
		synchronized(mRooms)
		{
			Logger.info("room count="+mRooms.size());
			ArrayList<Long> removes = new ArrayList<Long>();
			for(GameRoom room : mRooms.values())
			{
				room.Update(currentmillisec);
				if( room.isEmpty() && room.isPlaying() )
					removes.add(room.getRoomId());
			}
			
			for(int i = 0; i < removes.size(); i++)
			{
				mRooms.remove(removes.get(i));
			}
		}
	}
	
	public static class UpdateTimer 
	{
		long currentMilliSec;
		
		public UpdateTimer(long millsec)
		{
			this.currentMilliSec = millsec;
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
        
    	final User user;
        final long roomId;
        
        public Leave(long roomId, User user) {
            this.roomId = roomId;
            this.user = user;
        }
    }
    
    public static class Ready {
    	
    	final long roomId;
    	
    	public Ready(long roomId) {
    		this.roomId = roomId;
    	}
    }
}
