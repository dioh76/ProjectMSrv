package game.spell;

import game.Buff;
import game.Character;
import game.ZoneInfo;
import models.GameRoom;

public class SpellRest extends Spell {

	public SpellRest(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_REST;
	}

	@Override
	public boolean onBuff() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUse(int spellId, GameRoom room, Character castChr,
			Character targetChr, ZoneInfo zoneInfo1, ZoneInfo zoneInfo2) {
		
		if(castChr == null)
			return true;
		
		room.charAddBuff(castChr.charId,Buff.TURN_SKIP,-1,castChr.charId,-1,value1,false,spellId);   
		
		return true;
	}

}
