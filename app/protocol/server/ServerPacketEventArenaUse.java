package protocol.server;

import protocol.ServerPacket;

public class ServerPacketEventArenaUse extends ServerPacket{
	
	public int index;
	public int card;
	public int dice;
	
	public ServerPacketEventArenaUse( int sender, int index, int card, int dice )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_EVENT_ARENA_USE;
		this.index = index;
		this.card = card;
		this.dice = dice;
	}
}