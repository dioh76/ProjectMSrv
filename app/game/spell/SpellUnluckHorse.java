package game.spell;

import protocol.server.ServerPacketCharMoveBySpell;
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
		
		if(targetChr == null)
			return true;
		
		int zoneId = room.getMostExpensiveZone();
		
		if(zoneId == -1)
			return true;
		
		//check my position and move to specific zone
		//room.notifyAll(new ServerPacketCharMoveBySpell(pkt.sender,pkt.move,pkt.reverse,pkt.bonus).toJson());
		
		//room.charAddBuff(castChr.charId,Buff.SPELL_USE,-1,targetChr.charId,-1,1,false,spellId);   
		
		return false;
	}

}
