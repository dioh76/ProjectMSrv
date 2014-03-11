package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketSpellThunder extends ServerPacket{
	
	public int sId;
	public int zId;
	public int tChr;
	public float asset;
	public List<Integer> ranks;
	
	public ServerPacketSpellThunder(int sender, int spellId, int zoneId, int targetChrId, float asset, List<Integer> ranks)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELL_THUNDER;
		this.sId = spellId;
		this.zId = zoneId;
		this.tChr = targetChrId;
		this.asset = asset;
		this.ranks = ranks;
	}
}