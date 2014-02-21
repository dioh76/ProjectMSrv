package game.spell;

import protocol.server.ServerPacketCharMoveBySpell;
import xml.ZoneTable;
import game.Buff;
import game.SrvCharacter;
import game.ZoneInfo;
import models.GameRoom;

public class SpellUnluckHorse extends Spell {

	public SpellUnluckHorse(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_UNLUCKHORSE;
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
		
		int zoneId = room.getMostExpensiveZone();
		
		if(zoneId == -1)
			return true;
		
		//To determine where you are, and I go to the most expensive land
		int pos = castChr.curzone;
		int move = zoneId - pos;
		if(move < 0)
		{
			move = ZoneTable.getInstance().getZoneCount() + move;
		}
		
		room.notifyAll(new ServerPacketCharMoveBySpell(castChr.charId,move,false,true).toJson());  
		
		return false;
	}

}
