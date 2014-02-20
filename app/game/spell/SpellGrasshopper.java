package game.spell;

import java.util.List;

import game.Buff;
import game.SrvCharacter;
import game.ZoneInfo;
import models.GameRoom;

public class SpellGrasshopper extends Spell {

	public SpellGrasshopper(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_GRASSHOPPER;
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
		
		if(targetChr == null)
    		return true;    	

		List<Integer> zones = room.getCharZones(targetChr.charId);
		
		for(int zoneId : zones)
			room.zoneAddBuff(castChr.charId, Buff.PLUS_TOLL, zoneId, value1, value2, spellId);
		
		return true;
	}

}
