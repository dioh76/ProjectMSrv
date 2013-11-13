package xml;

import game.ZoneInfo;
import game.ZonePosInfo;
import game.ZoneValueInfo;

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

public class ZoneTable {
	
	private List<ZonePosInfo> mZones = new ArrayList<ZonePosInfo>();
	private Map<Integer, List<ZoneBasicInfo>> mZoneInfos = new HashMap<Integer,List<ZoneBasicInfo>>();

	public void init(InputStream in)
	{
		try{
			Document doc = XML.fromInputStream(in, "UTF-8");
			
			readZones(doc.getDocumentElement());
			readZoneInfos(doc.getDocumentElement());
			
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public int getZoneCount()
	{
		return mZones.size();
	}
	
	public ZonePosInfo getZonePosInfo(int index)
	{
		return mZones.get(index);
	}
	
	public List<ZoneValueInfo> getZoneValues(int race, int index)
	{
		return mZoneInfos.get(race).get(index).values;
	}
	
	private void readZones(Element elem)
	{
		NodeList child = elem.getElementsByTagName("zone");
		if(child == null)
			return;
		
		Node current = null;
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				ZonePosInfo posInfo = new ZonePosInfo();
				
				posInfo.id = Integer.parseInt(childElem.getAttribute("id"));
				posInfo.type = Integer.parseInt(childElem.getAttribute("type"));
				posInfo.group = Integer.parseInt(childElem.getAttribute("group"));
				posInfo.advanced = Integer.parseInt(childElem.getAttribute("advanced"));
				mZones.add(posInfo);
			}
		}
	}
	
	private void readZoneInfos(Element elem)
	{
		NodeList child = elem.getElementsByTagName("info");
		if(child == null)
			return;
		
		Node current = null;
		
		int prevRaceType = ZoneInfo.ZONE_RACE_NONE;
		List<ZoneBasicInfo> basicInfos = new ArrayList<ZoneBasicInfo>();
		
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				int raceType = Integer.parseInt(childElem.getAttribute("race"));
				if(raceType != prevRaceType)
				{
					if(prevRaceType != ZoneInfo.ZONE_RACE_NONE)
					{
						mZoneInfos.put(prevRaceType, basicInfos);
						basicInfos = new ArrayList<ZoneBasicInfo>();
					}
					
					prevRaceType = raceType;
				}
				
				ZoneBasicInfo bInfo = new ZoneBasicInfo();
				for(int j = 0; j < 3; j++)
				{
					ZoneValueInfo valueInfo = new ZoneValueInfo();
					valueInfo.buy = Float.parseFloat(childElem.getAttribute("buy"+ (j+1)));
					valueInfo.sell = Float.parseFloat(childElem.getAttribute("sell"+ (j+1)));
					valueInfo.toll = Float.parseFloat(childElem.getAttribute("toll"+ (j+1)));
					
					bInfo.values.add(valueInfo);
				}
				
				bInfo.name = childElem.getAttribute("name");
				bInfo.advanced = Integer.parseInt(childElem.getAttribute("advanced"));
				
				basicInfos.add(bInfo);
			}
		}
		
		mZoneInfos.put(prevRaceType, basicInfos);
	}

	private static class Holder {
		private static final ZoneTable Instance = new ZoneTable(); 
	}
	
	public static ZoneTable getInstance() 
	{
		return ZoneTable.Holder.Instance;
	}
}

class ZoneBasicInfo
{
	public String name;
	public int advanced;
	public int race;
	public List<ZoneValueInfo> values = new ArrayList<ZoneValueInfo>();
}
