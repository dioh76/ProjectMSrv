package xml;

import game.CardInfo;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;

import play.api.Play;
import play.api.Application;
import play.*;
import play.libs.XML;

public class CardTable {
	
	private Map<Integer, CardInfo> mCards = new HashMap<Integer,CardInfo>();
	private List<CardEventInfo> mCardEvents = new ArrayList<CardEventInfo>();
	
	public void initCard(InputStream in)
	{
		try{
			Document doc = XML.fromInputStream(in, "UTF-8");
			
			readCards(doc.getDocumentElement());
			
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void initEvent(InputStream in)
	{
		try{
			Document doc = XML.fromInputStream(in, "UTF-8");
			
			readCardEvents(doc.getDocumentElement());
			
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public CardInfo getCard(int cardId)
	{
		if(mCards.containsKey(cardId) == false)
			return null;
		
		return mCards.get(cardId);
	}
	
	public int getEventCard()
	{
		final Random random = new Random();
		
		if(mCardEvents.size() == 0)
			return -1;
		
		int nProb = random.nextInt(mCardEvents.get(mCardEvents.size() -1 ).weight);
		
		for(CardEventInfo info : mCardEvents)
		{
			if(nProb <= info.weight)
				return info.cardId;
		}
		
		return -1;
	}
	
	private void readCards(Element elem)
	{
		NodeList child = elem.getElementsByTagName("card");
		if(child == null)
			return;
		
		Node current = null;
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				CardInfo cardInfo = new CardInfo();
				
				cardInfo.cardId = Integer.parseInt(childElem.getAttribute("id"));
				cardInfo.cardName = childElem.getAttribute("name");
				cardInfo.grade = Integer.parseInt(childElem.getAttribute("grade"));
				cardInfo.race = Integer.parseInt(childElem.getAttribute("type"));
				cardInfo.hp = Integer.parseInt(childElem.getAttribute("hp"));
				cardInfo.ap = Integer.parseInt(childElem.getAttribute("st"));
				
				mCards.put(cardInfo.cardId, cardInfo);
			}
		}
	}
	
	private void readCardEvents(Element elem)
	{
		NodeList child = elem.getElementsByTagName("card");
		if(child == null)
			return;
		
		Node current = null;
		int totalweight = 0;
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				CardEventInfo info = new CardEventInfo();
				info.cardId = Integer.parseInt(childElem.getAttribute("id"));
				totalweight += Integer.parseInt(childElem.getAttribute("weight"));
				info.weight = totalweight;
				
				mCardEvents.add(info);
			}
		}
	}

	private static class Holder {
		private static final CardTable Instance = new CardTable(); 
	}
	
	public static CardTable getInstance() 
	{
		return CardTable.Holder.Instance;
	}
}

class CardEventInfo
{
	public int cardId;
	public int weight;
}
