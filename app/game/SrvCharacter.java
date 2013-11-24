package game;

import java.util.ArrayList;
import java.util.List;

import xml.SpellTable;

public class SrvCharacter {

	public long 	userId;
	public int 		charId;
	public int		charType;
	public String	userName;
	public boolean	userChar;
	public float	soul;
	public boolean	checkdirection;
	public boolean	addcard;
	
	public List<Buff> mBuffs;
	public List<Integer> mEquipSpells;
	
	private List<ZoneAsset> mZoneAssets;
	
	public SrvCharacter(long userId, int charId, int charType, String userName, boolean userChar, float soul, boolean checkdirection)
	{
		this.userId = userId;
		this.charId = charId;
		this.charType = charType;
		this.userName = userName;
		this.userChar = userChar;
		this.soul = soul;
		this.checkdirection = checkdirection;
		
		this.addcard = false; 
		
		mBuffs = new ArrayList<Buff>();
		mEquipSpells = new ArrayList<Integer>();
		mZoneAssets = new ArrayList<ZoneAsset>();
	}
	
	public boolean hasEquipSpell(int spellType)
	{
		for(int spellId : mEquipSpells)
		{
			SpellInfo info = SpellTable.getInstance().getSpell(spellId);
			if(info.spellType == spellType)
				return true;
		}
		
		return false;
	}
	
	public int removeEquipSpell(int spellType)
	{
		for(int i=0; i< mEquipSpells.size(); i++)
		{
			int spellId = mEquipSpells.get(i);
			SpellInfo info = SpellTable.getInstance().getSpell(spellId);
			if(info.spellType == spellType)
			{
				mEquipSpells.remove(i);
				return spellId;
			}
		}
		
		return -1;
	}
	
	public boolean removeEquipSpellId(int spellId)
	{
		return mEquipSpells.remove(new Integer(spellId));
	}
	
	public void addZoneAsset( int zoneId, float value )
	{
		boolean bHasZone = false;
		for(ZoneAsset asset : mZoneAssets)
		{
			if(asset.zoneId == zoneId)
			{
				bHasZone = true;
				asset.value = value;
			}
		}
		
		if(bHasZone == false)
			mZoneAssets.add(new ZoneAsset(zoneId, value));
	}
	
	public void removeZoneAsset(int zoneId)
	{
		for(int i = 0; i < mZoneAssets.size(); i++)
		{
			if(mZoneAssets.get(i).zoneId == zoneId)
			{
				mZoneAssets.remove(i);
				break;
			}
		}
	}
	
	public int getZoneCount()
	{
		return mZoneAssets.size();
	}
	
	public float getZoneAssets()
	{
		float sum = 0;
		for(ZoneAsset asset : mZoneAssets)
			sum += asset.value;
		
		return sum;
	}
}

class ZoneAsset {
	public int 	zoneId;
	public float value;
	
	public ZoneAsset(int zoneId, float value)
	{
		this.zoneId = zoneId;
		this.value = value;
	}
}