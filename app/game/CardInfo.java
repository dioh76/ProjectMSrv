package game;

public class CardInfo {
	
	public static final int CARD_GRADE_D = 0;
	public static final int CARD_GRADE_C = 1;
	public static final int CARD_GRADE_B = 2;
	public static final int CARD_GRADE_A = 3;
	public static final int CARD_GRADE_S = 4;
	
	public static final int SYSTEM_CARD_NUM = 5000000;

	public int cardId;
	public String cardName;
	public int grade;
	public int race;
	public float hp;
	public float st;
	public float cost;
	
	public int getBaseId()
	{
		return cardId / 100;
	}
}
