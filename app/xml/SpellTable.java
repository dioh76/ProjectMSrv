package xml;

import game.SpellInfo;

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
	private Map<Integer, SpellInfo> mSpells = new HashMap<Integer,SpellInfo>();

	
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
	
	public SpellInfo getSpell(int spellId)
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
				
				SpellInfo spellInfo = new SpellInfo();
				
				spellInfo.spellId = Integer.parseInt(childElem.getAttribute("id"));
				spellInfo.spellName = childElem.getAttribute("name");
				spellInfo.spellType = Integer.parseInt(childElem.getAttribute("type"));
				spellInfo.value = Integer.parseInt(childElem.getAttribute("value"));
				
				mSpells.put(spellInfo.spellId, spellInfo);
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
