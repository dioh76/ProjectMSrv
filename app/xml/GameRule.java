package xml;

import game.Race;
import game.ZoneBasicInfo;
import game.ZoneBuff;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.libs.XML;

public class GameRule {
	
	public float 	CHAR_INIT_SOUL = 30;
	public float 	BOUNS_START_SOUL = 30;
	public int		GAMEEND_MAX_TURN = 30;
	public int		START_ENHANCE_ROUND = 3;
	public static final int	INITIAL_CARDDECK_SIZE = 4;
	public static final int	SPELL_ID_BATTLE_ARENA_WIN = 1002;
	public static final int	SPELL_ID_BATTLE_ARENA_LOSE = 1001;
	
	private ArrayList<Float> mStartEnhance = new ArrayList<Float>();
	private Map<Integer, List<ZoneBuff>> mZoneBuffs = new HashMap<Integer, List<ZoneBuff>>();
	
	public void init(InputStream in)
	{
		try{
			Document doc = XML.fromInputStream(in, "UTF-8");
			
			readRules(doc.getDocumentElement());
			readZoneBuff(doc.getDocumentElement());
			
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public float getZoneBuff(int zoneRace, int creatureRace)
	{
		if(mZoneBuffs.containsKey(zoneRace) == false)
			return 1.0f;
		
		List<ZoneBuff> zoneBuffs = mZoneBuffs.get(zoneRace);
		for(ZoneBuff buff : zoneBuffs)
		{
			if(buff.creaturerace == creatureRace)
				return buff.buffvalue;
		}
		
		return 1.0f;
	}
	
	public float getStartEnhance(int round)
	{
		if(round > START_ENHANCE_ROUND)
			return mStartEnhance.get(START_ENHANCE_ROUND);
		
		return mStartEnhance.get(round);
	}
	
	private void readZoneBuff(Element elem)
	{
		NodeList child = elem.getElementsByTagName("zonebuff");
		if(child == null)
			return;
		
		Element elemZoneBuff = (Element)child.item(0);
		if(elemZoneBuff == null)
			return;
		
		NodeList childBuff = elemZoneBuff.getElementsByTagName("buff");
		
		int prevZoneRace = Race.NONE;
		List<ZoneBuff> zoneBuffs = new ArrayList<ZoneBuff>();
		
		Node current = null;
		for( int i = 0; i < childBuff.getLength(); i++ )
		{
			current = childBuff.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				int zoneRace = Integer.parseInt(childElem.getAttribute("zonerace"));
				if(prevZoneRace == Race.NONE)
				{
					prevZoneRace = zoneRace;
				}
				else if(prevZoneRace != Race.NONE && prevZoneRace != zoneRace)
				{
					mZoneBuffs.put(prevZoneRace, zoneBuffs);
					zoneBuffs = new ArrayList<ZoneBuff>();
					prevZoneRace = zoneRace;
				}
				
				ZoneBuff zoneBuff = new ZoneBuff();
				zoneBuff.creaturerace = Integer.parseInt(childElem.getAttribute("creaturerace"));
				zoneBuff.buffvalue = Float.parseFloat(childElem.getAttribute("value"));
				zoneBuffs.add(zoneBuff);
			}
		}
	}
	
	private void readRules(Element elem)
	{
		NodeList child = elem.getElementsByTagName("char");
		if(child == null)
			return;
		
		Element elemChild = (Element)child.item(0);
		if(elemChild == null)
			return;
		
		CHAR_INIT_SOUL = Float.parseFloat(elemChild.getAttribute("soul"));
		
		child = elem.getElementsByTagName("bonus");
		if(child == null)
			return;
		
		elemChild = (Element)child.item(0);
		if(elemChild == null)
			return;
		
		BOUNS_START_SOUL = Float.parseFloat(elemChild.getAttribute("start"));
		
		child = elem.getElementsByTagName("startenhance");
		if(child == null)
			return;
		
		elemChild = (Element)child.item(0);
		if(elemChild == null)
			return;
		
		START_ENHANCE_ROUND = Integer.parseInt(elemChild.getAttribute("round"));
		mStartEnhance.add(Float.parseFloat(elemChild.getAttribute("rate0")));		
		mStartEnhance.add(Float.parseFloat(elemChild.getAttribute("rate1")));
		mStartEnhance.add(Float.parseFloat(elemChild.getAttribute("rate2")));
		mStartEnhance.add(Float.parseFloat(elemChild.getAttribute("rate3")));
		
		child = elem.getElementsByTagName("gameend");
		if(child == null)
			return;
		
		elemChild = (Element)child.item(0);
		if(elemChild == null)
			return;
		
		GAMEEND_MAX_TURN = Integer.parseInt(elemChild.getAttribute("maxturn"));
		
	}

	private static class Holder {
		private static final GameRule Instance = new GameRule(); 
	}
	
	public static GameRule getInstance() 
	{
		return GameRule.Holder.Instance;
	}
}
