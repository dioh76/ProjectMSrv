package xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.libs.XML;
import scala.util.Random;

public class BattleDiceTable {
	private Map<Integer, BattleDiceProb> attackProbs = new HashMap<Integer,BattleDiceProb>();
	private Map<Integer, BattleDiceProb> defenseProbs = new HashMap<Integer,BattleDiceProb>();
	
	public void init(InputStream in)
	{
		try{
			Document doc = XML.fromInputStream(in, "UTF-8");
			
			readAttackProbs(doc.getDocumentElement());
			readDefenseProbs(doc.getDocumentElement());
			
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public int getAttackDice( int attack, int defense )
	{
		final Random random = new Random();
		
		int diceValue = random.nextInt(100);
		
		BattleDiceProb attackProb = attackProbs.get(attack);
		int[] diceprobs = attackProb.otherProbs.get(defense);
		
		for(int i = 0; i < diceprobs.length; i++)
		{
			if( diceValue <= diceprobs[i] )
				return i + 1;
		}
		
		return 3;
	}
	
	public int getDefenseDice( int defense, int attack )
	{
		final Random random = new Random();
		
		int diceValue = random.nextInt(100);
		
		BattleDiceProb defenseProb = defenseProbs.get(defense);
		int[] diceprobs = defenseProb.otherProbs.get(attack);
		
		for(int i = 0; i < diceprobs.length; i++)
		{
			if( diceValue <= diceprobs[i] )
				return i + 1;
		}
		
		return 3;
	}	
	
	private void readAttackProbs(Element elem)
	{
		NodeList attChild = elem.getElementsByTagName("att_def");
		if(attChild == null)
			return;
		
		NodeList child = ((Element)attChild.item(0)).getElementsByTagName("attack");
		if(child == null)
			return;
		
		Node current = null;
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				int myType = Integer.parseInt(childElem.getAttribute("type"));
				
				NodeList child2 = childElem.getElementsByTagName("defense");
				if(child2 == null)
					continue;
				
				Node current2 = null;
				BattleDiceProb otherProb = new BattleDiceProb();
				for(int j = 0; j < child2.getLength(); j++)
				{
					current2 = child2.item(j);
					if(current2.getNodeType() == Node.ELEMENT_NODE)
					{
						Element childElem2 = (Element)current2;
						
						int otherType = Integer.parseInt(childElem2.getAttribute("type"));
						int[] dices = new int[3];
						dices[0] = Integer.parseInt(childElem2.getAttribute("dice1"));
						dices[1] = dices[0] + Integer.parseInt(childElem2.getAttribute("dice2"));
						dices[2] = dices[1] + Integer.parseInt(childElem2.getAttribute("dice3"));
						
						otherProb.otherProbs.put(otherType, dices);
					}
				}
				
				attackProbs.put(myType, otherProb);
			}
		}
	}
	
	private void readDefenseProbs(Element elem)
	{
		NodeList defChild = elem.getElementsByTagName("def_att");
		if(defChild == null)
			return;
		
		NodeList child = ((Element)defChild.item(0)).getElementsByTagName("defense");
		if(child == null)
			return;		
		
		Node current = null;
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				int myType = Integer.parseInt(childElem.getAttribute("type"));
				
				NodeList child2 = childElem.getElementsByTagName("attack");
				if(child2 == null)
					continue;
				
				Node current2 = null;
				BattleDiceProb otherProb = new BattleDiceProb();
				for(int j = 0; j < child2.getLength(); j++)
				{
					current2 = child2.item(j);
					if(current2.getNodeType() == Node.ELEMENT_NODE)
					{
						Element childElem2 = (Element)current2;
						
						int otherType = Integer.parseInt(childElem2.getAttribute("type"));
						int[] dices = new int[3];
						dices[0] = Integer.parseInt(childElem2.getAttribute("dice1"));
						dices[1] = dices[0] + Integer.parseInt(childElem2.getAttribute("dice2"));
						dices[2] = dices[1] + Integer.parseInt(childElem2.getAttribute("dice3"));
						
						otherProb.otherProbs.put(otherType, dices);
					}
				}
				
				defenseProbs.put(myType, otherProb);
			}
		}
	}	

	private static class Holder {
		private static final BattleDiceTable Instance = new BattleDiceTable(); 
	}
	
	public static BattleDiceTable getInstance() 
	{
		return BattleDiceTable.Holder.Instance;
	}
	
	class BattleDiceProb {
		public Map<Integer, int[]> otherProbs = new HashMap<Integer, int[]>();
	}
}
