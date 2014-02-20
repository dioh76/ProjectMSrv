package game.spell;

import protocol.server.ServerPacketCharChangeOwner;
import game.SrvCharacter;
import game.ZoneInfo;
import models.GameRoom;

public class SpellDonation extends Spell {

	public SpellDonation(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_DONATION;
	}

	@Override
	public boolean onBuff() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUse(int spellId, GameRoom room, SrvCharacter castChr,
			SrvCharacter targetChr, ZoneInfo zoneInfo1, ZoneInfo zoneInfo2) {
		
		if(castChr == null)
			return true;
		
		if(zoneInfo1 == null)
			return true;

		if(targetChr == null)
    		return true;    	
    	
    	zoneInfo1.setChar(targetChr.charId);
    	
    	float asset = zoneInfo1.tollSoul();
    	if(room.isOccpuyLinkedZone(zoneInfo1)) asset = asset * 2.0f;
    	targetChr.addZoneAsset(zoneInfo1.id, asset);
    	castChr.removeZoneAsset(zoneInfo1.id);
    	
    	room.notifyAll(new ServerPacketCharChangeOwner(castChr.charId,zoneInfo1.id,targetChr.charId,targetChr.getZoneCount(),targetChr.getZoneAssets(),castChr.charId,castChr.getZoneCount(),castChr.getZoneAssets()).toJson());
    	room.sendRanking();		
		
		return true;
	}

}