package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketGameInitDecks extends ServerPacket{
	
	public int deckType;
	public List<Integer> cards;
	public List<Integer> playcards;
	
	public ServerPacketGameInitDecks( int sender, int deckType, List<Integer> cards, List<Integer> playcards )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_GAME_INITDECKS;
		this.deckType = deckType;
		this.cards = cards;
		this.playcards = playcards;
	}
}
