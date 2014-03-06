package protocol.client;

import protocol.ClientPacket;

public class ClientPacketSimulatorOn extends ClientPacket{
	
	public String name;
	
	public ClientPacketSimulatorOn()
	{
		proto = ClientPacket.MCP_SIMULATOR_ON;
	}
	
}