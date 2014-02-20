package game.spell;

import protocol.server.ServerPacketCharRemoveZone;
import protocol.server.ServerPacketCharZoneAsset;
import game.SrvCharacter;
import game.ZoneInfo;
import models.GameRoom;

public class SpellThunder extends Spell {

	public SpellThunder(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_THUNDER;
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
		
		
		castChr.soul += zoneInfo1.sellSoul();
		room.sendSoulChanged(castChr,false);
    	
    	
		castChr.removeZoneAsset(zoneInfo1.id);
		zoneInfo1.setChar(0);
		zoneInfo1.setCardInfo(null);
    	
    	//check remove buff or not
    	
    	room.notifyAll( new ServerPacketCharZoneAsset(castChr.charId,castChr.getZoneCount(),castChr.getZoneAssets()).toJson());
    	
    	room.sendRanking();
    	
    	room.notifyAll( new ServerPacketCharRemoveZone(castChr.charId,zoneInfo1.id,true,false).toJson());
    	
    	return true;
	}

}
