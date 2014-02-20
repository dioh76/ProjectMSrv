package game.spell;

public abstract class Spell implements SpellHandler {

	public final static int SPELL_BEAUTY = 101;
	public final static int SPELL_AMBUSH = 102;
	public final static int SPELL_REFUGEES = 103;
	public final static int SPELL_PROVOCATION = 104;
	public final static int SPELL_SACRIFICE = 105;
	public final static int SPELL_REST = 106;
	public final static int SPELL_DONATION = 107;
	
	public final static int SPELL_FIRE = 201;
	public final static int SPELL_FLOOD = 202;
	public final static int SPELL_THUNDER = 203;
	public final static int SPELL_PLAGUE = 204;
	public final static int SPELL_SNOWFALL = 205;
	public final static int SPELL_GRASSHOPPER = 206;

	public final static int SPELL_REDHORSE = 301;
	public final static int SPELL_UNLUCKHORSE = 302;
	public final static int SPELL_ENHANCEATT = 303;
	public final static int SPELL_ENHANCEDEF = 304;
	//public final static int SPELL_TOLLZONE = 305;
	public final static int SPELL_SOUL = 306;
	public final static int SPELL_CHANGEZONE = 307;
	public final static int SPELL_IMMUNE = 308;
	public final static int SPELL_SAFEGUARD = 309;
	
	public final static int SPELL_ATTACK = 401;
	public final static int SPELL_HEAL = 402;
	public final static int SPELL_MOVESELECT = 403;
	
	public final static int SYSTEM_SPELL_CARDNUM = 1000;
	
	public final static int USE_INSTANCE = 0;
	public final static int USE_NEXT = 1;
	public final static int USE_INFINITE = 2;
	
	public final static int USER_OWN = 0;
	public final static int USER_OTHER = 1;
	public final static int USER_ONE_IN_ALL = 2;
	public final static int USER_OWN_AND_OTHER = 3;
	public final static int USER_OWN_AND_OWN = 4;
	public final static int USER_EMPTY = 5;			//empty zone
	public final static int	USER_ALL_EXCLUSIVE_ME = 6;
	
	public final static int TARGET_CHAR = 0;
	public final static int TARGET_CHAR_AND_ZONE = 1;
	public final static int TARGET_ZONE = 2;
	
	public int spellId;
	public String spellName;
	public int spellType;
	public int value1;
	public int value2;
	
	public int useType;
	public int targetUser;
	public int targetType;
	
	public Spell(int id, String strName, int nVal1, int nVal2, int useType, int targetUser, int targetType)
	{
		this.spellId = id;
		this.spellName = strName;
		this.value1 = nVal1;
		this.value2 = nVal2;
		this.useType = useType;
		this.targetUser = targetUser;
		this.targetType = targetType;
	}
}
