package game.spell;

import protocol.server.ServerPacketCharRemoveZone;
import protocol.server.ServerPacketCharZoneAsset;
import protocol.server.ServerPacketZoneDelBuff;
import game.Buff;
import game.Character;
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
	public boolean onUse(int spellId, GameRoom room, Character castChr,
			Character targetChr, ZoneInfo zoneInfo1, ZoneInfo zoneInfo2) {
    	
		if(castChr == null)
			return true;
		
		if(zoneInfo1 == null)
			return true;
		
		if(zoneInfo1.type == ZoneInfo.ZONE_MAINTYPE_TRIBE)
			return true;		
		
		Character ownChr = room.getCharacter(zoneInfo1.getChar());
		if(ownChr == null)
			return true;		
		
		ownChr.money += zoneInfo1.sellMoney();
		room.sendMoneyChanged(ownChr,false);
    	
    	
		ownChr.removeZoneAsset(zoneInfo1.id);
		zoneInfo1.setChar(0);
		zoneInfo1.setCardInfo(null);
    	
    	//check remove buff or not
		Buff prevBuff = zoneInfo1.getBuff();
		if(prevBuff != null)
		{
			zoneInfo1.setBuff(null);
			room.notifyAll(new ServerPacketZoneDelBuff(ownChr.charId,prevBuff.id,prevBuff.targetzone).toJson());
		}
    	
    	room.notifyAll( new ServerPacketCharZoneAsset(ownChr.charId,ownChr.getZoneCount(),ownChr.getZoneAssets()).toJson());
    	
    	room.sendRanking();
    	
    	room.notifyAll( new ServerPacketCharRemoveZone(ownChr.charId,zoneInfo1.id,true,false).toJson());
    	
    	return true;
	}

}
