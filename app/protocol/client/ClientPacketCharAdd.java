package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharAdd extends ClientPacket{
	
	public int 		charId;
	public String 	name;
	public boolean 	userChar;
	public long 	userId;
	
	public ClientPacketCharAdd()
	{
		proto = ClientPacket.MCP_CHAR_ADD;
	}
}
