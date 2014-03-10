package protocol.client;

import protocol.ClientPacket;
import java.util.List;



public class ClientPacketGameInitDecks extends ClientPacket{
	
	public long uId;
	public boolean hasDeck;
	public List<Integer> cards;
	
	public ClientPacketGameInitDecks()
	{
		proto = ClientPacket.MCP_GAME_INITDECKS;
	}

}
