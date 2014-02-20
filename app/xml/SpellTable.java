package xml;

import game.spell.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.libs.XML;

public class SpellTable {
	private Map<Integer, Spell> mSpells = new HashMap<Integer,Spell>();

	
	public void init(InputStream in)
	{
		try{
			Document doc = XML.fromInputStream(in, "UTF-8");
			
			readSpells(doc.getDocumentElement());
			
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public Spell getSpell(int spellId)
	{
		if(mSpells.containsKey(spellId) == false)
			return null;
		
		return mSpells.get(spellId);
	}
	
	public Iterator<Integer> getInitSpellCards()
	{
		return mSpells.keySet().iterator();
	}
	
	private void readSpells(Element elem)
	{
		NodeList child = elem.getElementsByTagName("spell");
		if(child == null)
			return;
		
		Node current = null;
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				int spellId = Integer.parseInt(childElem.getAttribute("id"));
				String spellName = childElem.getAttribute("name");
				int spellType = Integer.parseInt(childElem.getAttribute("type"));
				int value1 = Integer.parseInt(childElem.getAttribute("value"));
				int value2 = Integer.parseInt(childElem.getAttribute("value2"));
				int targetUser = Integer.parseInt(childElem.getAttribute("userscope"));
				int targetType = Integer.parseInt(childElem.getAttribute("target2"));
				int useType = Integer.parseInt(childElem.getAttribute("use"));
				
				Spell spell;
				
				switch(spellType)
				{
				case Spell.SPELL_BEAUTY:
					spell = new SpellBeauty(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_AMBUSH:
					spell = new SpellAmbush(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_REFUGEES:
					spell = new SpellRefugees(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_PROVOCATION:
					spell = new SpellProvocation(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_SACRIFICE:
					spell = new SpellSacrifice(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_REST:
					spell = new SpellRest(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_DONATION:
					spell = new SpellDonation(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_FIRE:
					spell = new SpellFire(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_FLOOD:
					spell = new SpellFlood(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_THUNDER:
					spell = new SpellThunder(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_PLAGUE:
					spell = new SpellPlague(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_SNOWFALL:
					spell = new SpellSnowfall(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_GRASSHOPPER:
					spell = new SpellGrasshopper(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_REDHORSE:
					spell = new SpellRedHorse(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_UNLUCKHORSE:
					spell = new SpellUnluckHorse(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_SOUL:
					spell = new SpellSoul(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_CHANGEZONE:
					spell = new SpellChangeZone(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_IMMUNE:
					spell = new SpellImmune(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;				
				case Spell.SPELL_SAFEGUARD:
					spell = new SpellSafeGuard(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_MOVESELECT:
					spell = new SpellMoveSelect(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;				
				case Spell.SPELL_ATTACK:
					spell = new SpellAttack(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				case Spell.SPELL_HEAL:
					spell = new SpellHeal(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;				
				default:
					spell = new SpellNone(spellId,spellName,value1,value2,useType,targetUser,targetType);
					break;
				}
				
				mSpells.put(spell.spellId, spell);
			}
		}
	}

	private static class Holder {
		private static final SpellTable Instance = new SpellTable(); 
	}
	
	public static SpellTable getInstance() 
	{
		return SpellTable.Holder.Instance;
	}
}
