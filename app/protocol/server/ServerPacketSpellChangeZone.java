package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketSpellChangeZone extends ServerPacket{
	
	public int sId;
	public int zId;
	public int tChr;
	public int cId;
	public float asset;
	public List<Integer> ranks;
	
	public ServerPacketSpellChangeZone(int sender, int spellId, int zoneId, int targetChrId, int cardId, float asset, List<Integer> ranks)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELL_CHANGEZONE;
		this.sId = spellId;
		this.zId = zoneId;
		this.tChr = targetChrId;
		this.cId = cardId;
		this.asset = asset;
		this.ranks = ranks;
	}
}