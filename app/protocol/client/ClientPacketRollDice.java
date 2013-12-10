package protocol.client;

import protocol.ClientPacket;

public class ClientPacketRollDice extends ClientPacket{
	
	public int rVal;
	public int bVal;
	public boolean doubled;
	
	public ClientPacketRollDice()
	{
		proto = ClientPacket.MCP_ROLL_DICE;
	}
}