package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharRemoveCard extends ServerPacket{

	public int	cId;
	
	public ServerPacketCharRemoveCard( int sender, int cardId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_REMOVE_CARD;
		this.cId = cardId;
	}
}
