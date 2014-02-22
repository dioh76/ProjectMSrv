package xml;

import game.CardInfo;
import game.CardOption;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import play.libs.XML;

public class CardTable {

	private Map<Integer, CardInfo> mCards = new HashMap<Integer, CardInfo>();
	private Map<Integer, ArrayList<Integer>> mCardGrades = new HashMap<Integer, ArrayList<Integer>>();
	private List<CardEventInfo> mCardEvents = new ArrayList<CardEventInfo>();
	private Map<Integer, CardOption> mCardOptions = new HashMap<Integer, CardOption>();

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
		}

		return cards;
	}

	public int getEventCard() {
		final Random random = new Random();

		if (mCardEvents.size() == 0)
			return -1;

		int nProb = random.nextInt(mCardEvents.get(mCardEvents.size() - 1).weight);

		for (CardEventInfo info : mCardEvents) {
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

	private void readCardEvents(Element elem) {
		NodeList child = elem.getElementsByTagName("card");
		if (child == null)
			return;

		Node current = null;
		int totalweight = 0;
		for (int i = 0; i < child.getLength(); i++) {
			current = child.item(i);
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element childElem = (Element) current;

				CardEventInfo info = new CardEventInfo();
				info.cardId = Integer.parseInt(childElem.getAttribute("id"));
				totalweight += Integer.parseInt(childElem.getAttribute("weight"));
				info.weight = totalweight;

				mCardEvents.add(info);
			}
		}
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
				info.attack = Boolean.parseBoolean(childElem.getAttribute("att"));
				info.defense = Boolean.parseBoolean(childElem.getAttribute("def"));
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
