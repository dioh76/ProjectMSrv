package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCardDeckUse extends ServerPacket{
	
	public int acttype;
	public int index;
	public int	card;
	
	public ServerPacketCardDeckUse( int sender, int acttype, int index, int card )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CARDDECK_USE;
		this.acttype = acttype;
		this.index = index;
		this.card = card;
	}
}