package controllers;


import models.UserManager;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.*;

public class Application extends Controller {

    public static WebSocket<String> index() {
    	  return new WebSocket<String>() {
    	      
    	    public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
    	      out.write("Hello!");
    	      out.close();
    	    }
    	    
    	  };
    	}    
    
    public static WebSocket<JsonNode> connect(final String username) {
        return new WebSocket<JsonNode>() {
            
            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
                
                // Join the chat room.
                try { 
                    UserManager.getInstance().onConnect(username, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }    

}
