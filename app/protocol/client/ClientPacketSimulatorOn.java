package protocol.client;

import protocol.ClientPacket;

public class ClientPacketSimulatorOn extends ClientPacket{
	
	public String name;
	
	public ClientPacketSimulatorOn( String name)
	{
		proto = ClientPacket.MCP_SIMULATOR_ON;
	}
	
}