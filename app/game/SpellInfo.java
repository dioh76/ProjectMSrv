package game;

public class SpellInfo {

	public final static int SPELL_MOVE = 0;
	public final static int SPELL_MOVESTART = 1;
	public final static int SPELL_SAFEGUARD = 2;
	public final static int SPELL_ATTACK = 3;
	public final static int SPELL_HEAL = 4;
	public final static int SPELL_MOVESELECT = 5;
	public final static int SPELL_MOVEPORTAL = 6;
	public final static int SPELL_SOUL = 7;
	public final static int SPELL_TRADEZONE = 8;
	public final static int SPELL_SELLZONE = 9;
	public final static int SPELL_WEAKENZONE = 10;
	public final static int SPELL_ATTACKALL = 11;
	public final static int SPELL_CHANGEZONE = 12;
	public final static int SPELL_DICE = 13;
	public final static int SPELL_MOVEREVERSE = 14;
	public final static int SPELL_TOLLZONE = 15;
	public final static int SPELL_TURNOVER = 16;
	public final static int SPELL_IMMUNE = 17;
	
	public final static int SPELL_BEAUTY = 101;
	public final static int SPELL_AMBUSH = 102;
	public final static int SPELL_REFUGEES = 103;
	public final static int SPELL_PROVOCATION = 104;
	public final static int SPELL_SACRIFICE = 105;
	public final static int SPELL_REST = 106;
	public final static int SPELL_DONATION = 107;
	
	public final static int SYSTEM_SPELL_CARDNUM = 1000;
	
	public int spellId;
	public String spellName;
	public int spellType;
}
