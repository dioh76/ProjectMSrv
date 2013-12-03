package protocol.client;

import protocol.ClientPacket;

public class ClientPacketZoneDelBuff extends ClientPacket{
	
	public int zId;
	
	public ClientPacketZoneDelBuff()
	{
		proto = ClientPacket.MCP_ZONE_DEL_BUFF;
	}
}