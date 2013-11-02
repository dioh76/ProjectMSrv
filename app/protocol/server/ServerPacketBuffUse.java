package protocol.server;

import protocol.ServerPacket;

public class ServerPacketBuffUse extends ServerPacket{
	
	public int spellid;
	
	public ServerPacketBuffUse( int sender, int spellid )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_BUFF_USE;
		this.spellid = spellid;

	}
}