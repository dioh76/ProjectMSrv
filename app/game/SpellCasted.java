package game;

public class SpellCasted {
	public int caster;
	public int spellId;
	public int targetchar;
	public int targetzone;
	public int targetzone2;
	
	public SpellCasted( int caster, int spellId, int targetchar, int targetzone, int targetzone2)
	{
		this.caster = caster;
		this.spellId = spellId;
		this.targetchar = targetchar;
		this.targetzone = targetzone;
		this.targetzone2 = targetzone2;
	}
}
