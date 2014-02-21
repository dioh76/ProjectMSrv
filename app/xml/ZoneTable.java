package xml;

import game.ZoneInfo;
import game.ZonePosInfo;
import game.ZoneValueInfo;
import game.ZoneBasicInfo;

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
	private Map<Integer, ZoneBasicInfo> mZoneInfos = new HashMap<Integer, ZoneBasicInfo>();
	private List<List<Integer>> mZoneLinkedInfos = new ArrayList<List<Integer>>();
	private int portalZone = -1;

	public void init(InputStream in)
	{
		try{
			Document doc = XML.fromInputStream(in, "UTF-8");
			
			readZones(doc.getDocumentElement());
			readZoneInfos(doc.getDocumentElement());
			readLinkedInfos(doc.getDocumentElement());
			
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public int getPortalZone()
	{
		return portalZone;
	}
	
	public int getZoneCount()
	{
		return mZones.size();
	}
	
	public ZonePosInfo getZonePosInfo(int index)
	{
		return mZones.get(index);
	}
	
	public ZoneBasicInfo getZoneBasicInfo(int infoId)
	{
		return mZoneInfos.get(infoId);
	}
	
	public List<Integer> getLinkedZones(int zoneId)
	{
		for(List<Integer> list : mZoneLinkedInfos)
		{
			for(int zId : list)
			{
				if(zId == zoneId)
					return list;
			}
		}
		
		return null;
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
				posInfo.info = Integer.parseInt(childElem.getAttribute("info"));
				mZones.add(posInfo);
				
				if(posInfo.type == ZoneInfo.ZONE_MAINTYPE_PORTAL)
					portalZone = posInfo.id;
			}
		}
	}
	
	private void readZoneInfos(Element elem)
	{
		NodeList child = elem.getElementsByTagName("info");
		if(child == null)
			return;
		
		Node current = null;
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				ZoneBasicInfo bInfo = new ZoneBasicInfo();
				for(int j = 0; j < 3; j++)
				{
					ZoneValueInfo valueInfo = new ZoneValueInfo();
					valueInfo.buy = Float.parseFloat(childElem.getAttribute("buy"+ (j+1)));
					valueInfo.sell = Float.parseFloat(childElem.getAttribute("sell"+ (j+1)));
					valueInfo.toll = Float.parseFloat(childElem.getAttribute("toll"+ (j+1)));
					
					bInfo.values.add(valueInfo);
				}
				
				bInfo.id = Integer.parseInt(childElem.getAttribute("id"));
				bInfo.race = Integer.parseInt(childElem.getAttribute("race"));
				bInfo.name = childElem.getAttribute("name");
				bInfo.enhancable = Integer.parseInt(childElem.getAttribute("enhance")) == 1 ? true : false;
				
				mZoneInfos.put(bInfo.id, bInfo);
			}
		}
	}
	
	private void readLinkedInfos(Element elem)
	{
		NodeList child = elem.getElementsByTagName("link");
		if(child == null)
			return;
		
		Node current = null;
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				String strVal = childElem.getAttribute("value");
				String[] split = strVal.split(",");
				ArrayList<Integer> items = new ArrayList<Integer>();
				for(String item : split)
				{
					items.add(Integer.parseInt(item));
				}
				
				mZoneLinkedInfos.add(items);
			}
		}
	}
		

	private static class Holder {
		private static final ZoneTable Instance = new ZoneTable(); 
	}
	
	public static ZoneTable getInstance() 
	{
		return ZoneTable.Holder.Instance;
	}
}


