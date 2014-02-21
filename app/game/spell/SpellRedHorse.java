package game.spell;

import protocol.server.ServerPacketCharMoveBySpell;
import xml.ZoneTable;
import game.Buff;
import game.SrvCharacter;
import game.ZoneInfo;
import models.GameRoom;

public class SpellRedHorse extends Spell {

	public SpellRedHorse(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_REDHORSE;
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

		int zoneId = ZoneTable.getInstance().getPortalZone();
		
		//To determine where you are, and I go to the portal zone
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
