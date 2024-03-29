package game.spell;

import models.GameRoom;
import protocol.server.ServerPacketZoneAmbush;
import game.spell.Spell;
import game.Buff;
import game.Character;
import game.ZoneInfo;

public class SpellAmbush extends Spell {

	public SpellAmbush(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_AMBUSH;
	}

	@Override
	public boolean onBuff() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUse(int spellId, GameRoom room, Character castChr, Character targetChr, ZoneInfo zoneInfo1, ZoneInfo zoneInfo2) {
		
		if(castChr == null)
			return true;
		
		if(zoneInfo1 == null)
			return true;
		
		zoneInfo1.setAmbush(true, castChr.charId);
    	
    	room.notifyAll(new ServerPacketZoneAmbush(castChr.charId,zoneInfo1.id,true).toJson());
    	room.zoneAddBuff(castChr.charId, Buff.ZONE_AMBUSH, zoneInfo1.id, value1, 1, spellId);
		
		return false;
	}
}
