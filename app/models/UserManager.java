package models;

import java.util.HashMap;

import play.libs.Json;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserManager {
	private long mNewUserId = 1000;
	
	private HashMap<Long,User> mUsers = new HashMap<Long,User>();
		
	private UserManager()
	{

	}
	
	public synchronized User get( long userId )
	{
		return mUsers.get( userId );
	}
	
	public synchronized User add( String username, WebSocket.Out<JsonNode> channel)
	{
		User user = new User(mNewUserId++,username,channel);
		mUsers.put(user.getUserId(), user);
		
		return user;
	}
	
    public void onConnect(final String username, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) throws Exception{
        
    	User user = this.add(username, out);
    	
    	in.onMessage(user);
    	in.onClose(user);
        
    }
    
    // Send a Json event to all members
    public synchronized void broadcastAll(String kind, String username, String text) {
        for(User user: mUsers.values()) {
        	WebSocket.Out<JsonNode> channel = user.getChannel();
            ObjectNode event = Json.newObject();
            event.put("kind", kind);
            event.put("user", username);
            event.put("message", text);
            
            channel.write(event);
        }
    }    

	private static class Holder {
		private static final UserManager Instance = new UserManager(); 
	}
	
	public static UserManager getInstance() 
	{
		return UserManager.Holder.Instance;
	}	
}
