package game.spell;

import protocol.server.ServerPacketCharAddZone;
import protocol.server.ServerPacketCharRemoveZone;
import protocol.server.ServerPacketCharZoneAsset;
import xml.CardTable;
import game.CardInfo;
import game.Character;
import game.ZoneInfo;
import models.GameRoom;

public class SpellChangeZone extends Spell {

	public SpellChangeZone(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_CHANGEZONE;
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
		
		if(targetChr == null)
			return true;

		CardInfo cardInfo = CardTable.getInstance().getCard(value1);
		if(cardInfo == null)
			return true;
		
		targetChr.removeZoneAsset(zoneInfo1.id);
		room.notifyAll( new ServerPacketCharRemoveZone(targetChr.charId,zoneInfo1.id,false,true).toJson());
    	
    	zoneInfo1.setCardInfo(cardInfo);
    	targetChr.addZoneAsset(zoneInfo1.id, zoneInfo1.tollMoney());
    	room.notifyAll( new ServerPacketCharAddZone(targetChr.charId,zoneInfo1.id,cardInfo.cardId,targetChr.charId,false,-1).toJson());    
		
    	room.notifyAll( new ServerPacketCharZoneAsset(targetChr.charId,targetChr.getZoneCount(),targetChr.getZoneAssets()).toJson());
    	
    	room.sendRanking();
		
		return true;
	}

}
