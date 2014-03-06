package protocol.server;

import protocol.ServerPacket;

public class ServerPacketSimulatorOn extends ServerPacket{

	public String name;

	
	public ServerPacketSimulatorOn( int sender, String name )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SIMULATOR_ON;
		this.name = name;
	}
}
