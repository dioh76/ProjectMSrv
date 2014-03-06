package protocol.server;

import protocol.ServerPacket;

public class ServerPacketSystemCharAdd extends ServerPacket{

	public int 		charId;
	public int		charType;
	public String 	name;

	
	public ServerPacketSystemCharAdd( int sender, int charId, int charType, String name )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SYSTEM_CHAR_ADD;
		this.charId = charId;
		this.charType = charType;
		this.name = name;
	}
}
