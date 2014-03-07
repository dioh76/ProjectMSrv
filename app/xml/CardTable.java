package xml;

import game.CardInfo;
import game.CardOption;
import game.Race;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;

import play.libs.XML;

public class CardTable {

	private Map<Integer, CardInfo> mCards = new HashMap<Integer, CardInfo>();
	private Map<Integer, ArrayList<Integer>> mCardGrades = new HashMap<Integer, ArrayList<Integer>>();
	private Map<Integer, ArrayList<CardEventInfo>> mCardEvents = new HashMap<Integer, ArrayList<CardEventInfo>>();
	//private List<CardEventInfo> mCardEvents = new ArrayList<CardEventInfo>();
	private Map<Integer, CardOption> mCardOptions = new HashMap<Integer, CardOption>();
	private Map<Integer, ArrayList<Integer>> mCardTribes = new HashMap<Integer, ArrayList<Integer>>();

	public void initCard(InputStream in) {
		try {
			Document doc = XML.fromInputStream(in, "UTF-8");

			readCards(doc.getDocumentElement());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initEvent(InputStream in) {
		try {
			Document doc = XML.fromInputStream(in, "UTF-8");

			readCardEvents(doc.getDocumentElement());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initCardOption(InputStream in) {
		try {
			Document doc = XML.fromInputStream(in, "UTF-8");

			readCardOption(doc.getDocumentElement());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initCardTribe(InputStream in) {
		try {
			Document doc = XML.fromInputStream(in, "UTF-8");

			readCardTribe(doc.getDocumentElement());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CardInfo getCard(int cardId) {
		if (mCards.containsKey(cardId) == false)
			return null;

		return mCards.get(cardId);
	}
	
	public CardOption getCardOption(int cardId) {
		if(mCardOptions.containsKey(cardId) == false)
			return null;
		
		return mCardOptions.get(cardId);
	}
	
	public int getCardTribe(int tribe) {
		if(mCardTribes.containsKey(tribe) == false)
			return -1;
		List<Integer> cards = mCardTribes.get(tribe);
		final Random random = new Random();
		
		return cards.get(random.nextInt(cards.size()));
	}

	public ArrayList<Integer> getSystemDeck(int type) {
		ArrayList<Integer> cards = new ArrayList<Integer>();

		switch (type) {
		case 0:
		{
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_S));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_S).subList(0, 7));
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_A));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_A).subList(0, 3));
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_B));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_B).subList(0, 3));			
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_C));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_C).subList(0, 3));
		}
			break;
		case 1:
		{
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_S));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_S).subList(0, 4));
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_A));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_A).subList(0, 6));
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_B));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_B).subList(0, 3));			
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_C));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_C).subList(0, 3));
		}
			break;
		case 2:
		{
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_A));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_A).subList(0, 10));
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_B));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_B).subList(0, 3));			
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_C));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_C).subList(0, 3));
		}
			break;
		case 3:
		{
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_A));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_A).subList(0, 4));
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_B));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_B).subList(0, 9));			
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_C));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_C).subList(0, 3));
		}
			break;
		case 4:
		{
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_A));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_A).subList(0, 1));
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_B));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_B).subList(0, 6));			
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_C));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_C).subList(0, 4));
			Collections.shuffle(mCardGrades.get(CardInfo.CARD_GRADE_D));
			cards.addAll(mCardGrades.get(CardInfo.CARD_GRADE_D).subList(0, 5));
			
		}
			break;
		case 5:
		{
			List<Integer> arr = new ArrayList<Integer>(mCardOptions.keySet());
			Collections.shuffle(arr);
			cards.addAll(arr.subList(0, 16));
		}		
			break;
		}

		return cards;
	}

	public int getEventCard(int race) {
		final Random random = new Random();
		
		ArrayList<CardEventInfo> cardEvents = mCardEvents.get(race);

		if (cardEvents.size() == 0)
			return -1;

		int nProb = random.nextInt(cardEvents.get(cardEvents.size() - 1).weight);

		for (CardEventInfo info : cardEvents) {
			if (nProb <= info.weight)
				return info.cardId;
		}

		return -1;
	}

	private void readCards(Element elem) {
		NodeList child = elem.getElementsByTagName("card");
		if (child == null)
			return;

		Node current = null;
		for (int i = 0; i < child.getLength(); i++) {
			current = child.item(i);
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element childElem = (Element) current;

				CardInfo cardInfo = new CardInfo();

				cardInfo.cardId = Integer.parseInt(childElem.getAttribute("id"));
				cardInfo.cardName = childElem.getAttribute("name");
				cardInfo.grade = Integer.parseInt(childElem.getAttribute("grade"));
				cardInfo.cost = Float.parseFloat(childElem.getAttribute("cost"));
				cardInfo.race = Integer.parseInt(childElem.getAttribute("type"));
				cardInfo.hp = Float.parseFloat(childElem.getAttribute("hp"));
				cardInfo.st = Float.parseFloat(childElem.getAttribute("st"));

				mCards.put(cardInfo.cardId, cardInfo);

				if(cardInfo.cardId < CardInfo.SYSTEM_CARD_NUM)
				{
					if (mCardGrades.containsKey(cardInfo.grade) == false) {
						ArrayList<Integer> cards = new ArrayList<Integer>();
						cards.add(cardInfo.cardId);
						mCardGrades.put(cardInfo.grade, cards);
					} else {
						mCardGrades.get(cardInfo.grade).add(cardInfo.cardId);
					}
				}
			}
		}
	}

	private void readCardEvents(Element elem) {
		NodeList child = elem.getElementsByTagName("choc_cards");
		if (child == null)
			return;
		
		Element elemChoc = (Element)child.item(0);
		if(elemChoc == null)
			return;
		
		NodeList childChoc = elemChoc.getElementsByTagName("card");

		Node current = null;
		int totalweight = 0;
		ArrayList<CardEventInfo> cardEventChoc = new ArrayList<CardEventInfo>();
		for (int i = 0; i < childChoc.getLength(); i++) {
			current = childChoc.item(i);
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element childElem = (Element) current;

				CardEventInfo info = new CardEventInfo();
				info.cardId = Integer.parseInt(childElem.getAttribute("id"));
				totalweight += Integer.parseInt(childElem.getAttribute("weight"));
				info.weight = totalweight;

				cardEventChoc.add(info);
			}
		}
		mCardEvents.put(Race.CHOC, cardEventChoc);
		
		child = elem.getElementsByTagName("wei_cards");
		if (child == null)
			return;
		
		Element elemWei = (Element)child.item(0);
		if(elemWei == null)
			return;
		
		NodeList childWei = elemWei.getElementsByTagName("card");

		current = null;
		totalweight = 0;
		ArrayList<CardEventInfo> cardEventWei = new ArrayList<CardEventInfo>();
		for (int i = 0; i < childWei.getLength(); i++) {
			current = childWei.item(i);
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element childElem = (Element) current;

				CardEventInfo info = new CardEventInfo();
				info.cardId = Integer.parseInt(childElem.getAttribute("id"));
				totalweight += Integer.parseInt(childElem.getAttribute("weight"));
				info.weight = totalweight;

				cardEventWei.add(info);
			}
		}
		mCardEvents.put(Race.WEI, cardEventWei);	
		
		child = elem.getElementsByTagName("oh_cards");
		if (child == null)
			return;
		
		Element elemOh = (Element)child.item(0);
		if(elemOh == null)
			return;
		
		NodeList childOh = elemOh.getElementsByTagName("card");

		current = null;
		totalweight = 0;
		ArrayList<CardEventInfo> cardEventOh = new ArrayList<CardEventInfo>();
		for (int i = 0; i < childOh.getLength(); i++) {
			current = childOh.item(i);
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element childElem = (Element) current;

				CardEventInfo info = new CardEventInfo();
				info.cardId = Integer.parseInt(childElem.getAttribute("id"));
				totalweight += Integer.parseInt(childElem.getAttribute("weight"));
				info.weight = totalweight;

				cardEventOh.add(info);
			}
		}
		mCardEvents.put(Race.OH, cardEventOh);		
		
		
	}
	
	private void readCardOption(Element elem) {
		NodeList child = elem.getElementsByTagName("card");
		if (child == null)
			return;

		Node current = null;
		for (int i = 0; i < child.getLength(); i++) {
			current = child.item(i);
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element childElem = (Element) current;

				CardOption info = new CardOption();
				info.cardId = Integer.parseInt(childElem.getAttribute("id"));
				info.attack = Integer.parseInt(childElem.getAttribute("att")) == 0 ? false : true;
				info.defense =Integer.parseInt(childElem.getAttribute("def")) == 0 ? false : true;
				String strCards = childElem.getAttribute("cards");
				StringTokenizer st = new StringTokenizer(strCards,",");
				while (st.hasMoreTokens()) {
					String strNext = st.nextToken();
					if(strNext.isEmpty() == false)
						info.targetCards.add(Integer.parseInt(strNext));
				}
				String strRegions = childElem.getAttribute("regions");
				st = new StringTokenizer(strRegions,",");
				while (st.hasMoreTokens()) {
					String strNext = st.nextToken();
					if(strNext.isEmpty() == false)
						info.targetRegions.add(Integer.parseInt(strNext));
				}
				
				mCardOptions.put(info.cardId, info);
			}
		}
	}
	
	private void readCardTribe(Element elem) {
		NodeList child = elem.getElementsByTagName("tribe");
		if (child == null)
			return;

		Node current = null;
		for (int i = 0; i < child.getLength(); i++) {
			current = child.item(i);
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element childElem = (Element) current;

				int tribe = Integer.parseInt(childElem.getAttribute("id"));
				ArrayList<Integer> cards = new ArrayList<Integer>();
				
				NodeList child2 = childElem.getElementsByTagName("card");
				if(childElem != null)
				{
					Node current2 = null;
					
					for(int j = 0; j < child2.getLength(); j++) {
						current2 = child2.item(j);
						if (current2.getNodeType() == Node.ELEMENT_NODE) {
							Element childElem2 = (Element) current2;
						
							int cardId = Integer.parseInt(childElem2.getAttribute("id"));
							cards.add(cardId);
						}
					}
				}
				
				mCardTribes.put(tribe, cards);
			}
		}		
	}

	private static class Holder {
		private static final CardTable Instance = new CardTable();
	}

	public static CardTable getInstance() {
		return CardTable.Holder.Instance;
	}
}

class CardEventInfo {
	public int cardId;
	public int weight;
}
