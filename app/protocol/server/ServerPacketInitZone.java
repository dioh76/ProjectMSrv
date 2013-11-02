package protocol.server;

import protocol.ServerPacket;
import java.util.List;

public class ServerPacketInitZone extends ServerPacket{
	
	public List<Integer> orders;
	
	public ServerPacketInitZone( int sender, List<Integer> orders )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_INIT_ZONE;
		this.orders = orders;
	}
}
