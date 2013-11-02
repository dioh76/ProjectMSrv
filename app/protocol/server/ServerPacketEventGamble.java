package protocol.server;

import protocol.ServerPacket;

public class ServerPacketEventGamble extends ServerPacket{
	
	public int index;
	public int card;
	
	public ServerPacketEventGamble( int sender, int index, int card )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_EVENT_GAMBLE;
		this.index = index;
		this.card = card;
	}
}