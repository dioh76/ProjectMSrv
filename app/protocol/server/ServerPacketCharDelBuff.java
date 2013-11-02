package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharDelBuff extends ServerPacket{

	public int buffId;
	public int targetchar;
	
	public ServerPacketCharDelBuff( int sender, int buffId, int targetchar )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_DEL_BUFF;
		this.buffId = buffId;
		this.targetchar = targetchar;
	}
}
