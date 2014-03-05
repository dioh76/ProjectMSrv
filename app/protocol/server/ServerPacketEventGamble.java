package protocol.server;

import protocol.ServerPacket;

public class ServerPacketEventGamble extends ServerPacket{
	
	public int race;
	public int card;
	
	public ServerPacketEventGamble( int sender, int race, int card )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_EVENT_GAMBLE;
		this.race = race;
		this.card = card;
	}
}