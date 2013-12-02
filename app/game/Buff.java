package game;

public class Buff {
	
	private static int BUFF_ID_SUPPLY = 1000;
	
	public static final int PLUS_HP = 0;
	public static final int DICE_LIMIT = 1;
	public static final int TURN_SKIP = 2;
	public static final int PLUS_TOLL = 3;
	public static final int SPELL_USE = 4;
	public static final int NONE = 100;
	
	public int id;
	public int owner;
	public int buffType;
	public int targetchar;
	public int targetzone;
	public int remainturn;
	public boolean creature;
	
	public static int getNewBuffID()
	{
		return ++BUFF_ID_SUPPLY;
	}
	
	public Buff()
	{
	}
	
	public void TurnOver()
	{
		if( buffType != Buff.SPELL_USE )
			remainturn--;
	}

	public boolean isValid()
	{
		return remainturn <= 0 ? false : true;
	}
}
