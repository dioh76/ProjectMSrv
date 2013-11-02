package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharAdd extends ServerPacket{

	public long 	userId;
	public int 		charId;
	public String 	name;
	public boolean	userChar;
	
	public ServerPacketCharAdd( int sender, long userId, int charId, String name, boolean userChar )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_ADD;
		this.userId = userId;
		this.charId = charId;
		this.name = name;
		this.userChar = userChar;
	}
}
