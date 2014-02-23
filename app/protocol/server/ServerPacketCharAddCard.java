package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharAddCard extends ServerPacket{

	public int	cId;
	public boolean show;
	
	public ServerPacketCharAddCard( int sender, int cardId, boolean show )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_ADD_CARD;
		this.cId = cardId;
		this.show = show;
	}
}
