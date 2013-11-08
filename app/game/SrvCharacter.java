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
	public boolean	discardcard;
	
	public List<Buff> mBuffs;
	public List<Integer> mEquipSpells;
	
	public SrvCharacter(long userId, int charId, int charType, String userName, boolean userChar, float soul, boolean checkdirection)
	{
		this.userId = userId;
		this.charId = charId;
		this.charType = charType;
		this.userName = userName;
		this.userChar = userChar;
		this.soul = soul;
		this.checkdirection = checkdirection;
		
		this.discardcard = false; 
		
		mBuffs = new ArrayList<Buff>();
		mEquipSpells = new ArrayList<Integer>();
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
}
