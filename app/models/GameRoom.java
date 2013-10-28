package models;

import play.mvc.*;
import play.libs.*;
import play.libs.F.*;

import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.*;
import static akka.pattern.Patterns.ask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.*;

import static java.util.concurrent.TimeUnit.*;


public class GameRoom {
	
	private long mRoomId;
	private int mMaxUser;
	private List<User> mUsers = new ArrayList<User>();
	private boolean mPlaying = false;
	
	public GameRoom(long roomId, int maxuser)
	{
		mRoomId = roomId;
		mMaxUser = maxuser;
	}
	
	public long getRoomId()
	{
		return mRoomId;
	}
	
    public boolean isPlaying() {
		return mPlaying;
	}

	public void setPlaying(boolean mPlaying) {
		this.mPlaying = mPlaying;
	}
	
	public synchronized boolean isFull()
	{
		return mUsers.size() == mMaxUser ? true : false;
	}
	
	public synchronized void addUser( User user )
	{
		mUsers.add(user);
	}
    
    private synchronized User removeUser( long userId )
    {
    	for( int i = 0; i < mUsers.size(); i++ )
    	{
    		if( mUsers.get(i).getUserId() == userId )
    		{
    			return mUsers.remove(i); 
    		}
    	}
    	
    	return null;
    }
    
    // Send a Json event to all members
    public synchronized void notifyAll(String kind, String username, String text) 
    {
        for( int i = 0; i < mUsers.size(); i++)
        {
        	WebSocket.Out<JsonNode> channel = mUsers.get(i).getChannel();
            ObjectNode event = Json.newObject();
            event.put("kind", kind);
            event.put("user", username);
            event.put("message", text);
            
            
            channel.write(event);
        }
    }
    
    // -- Messages
    


	public class Join {
        
        final String username;
        final User user;
        
        public Join(String username, User user) {
            this.username = username;
            this.user = user;
        }
        
    }
    
    public class Talk {
        
        final String username;
        final String text;
        
        public Talk(String username, String text) {
            this.username = username;
            this.text = text;
        }
        
    }
    
    public class Quit {
        
        final long userId;
        
        public Quit(long userId) {
            this.userId = userId;
        }
        
    }
    
}
