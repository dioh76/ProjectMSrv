package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharAddSoul extends ServerPacket{

	public float	remain;
	public boolean	bankrupt;
	public boolean	notify;
	
	public ServerPacketCharAddSoul( int sender, float remain, boolean bankrupt, boolean notify )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_ADD_SOUL;
		this.remain = remain;
		this.bankrupt = bankrupt;
		this.notify = notify;
	}
}
