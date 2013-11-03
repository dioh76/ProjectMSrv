package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCardDeckUse extends ServerPacket{
	
	public final static int ACT_OCCUPY = 0;
	public final static int ACT_BATTLE = 1;
	public final static int ACT_DISCARD = 2;
	
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