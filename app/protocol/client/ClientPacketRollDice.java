package protocol.client;

import protocol.ClientPacket;

public class ClientPacketRollDice extends ClientPacket{
	
	public int val;
	public boolean doubled;
	
	public ClientPacketRollDice()
	{
		proto = ClientPacket.MCP_ROLL_DICE;
	}
}