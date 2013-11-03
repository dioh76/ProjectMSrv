package xml;

import game.CharInfo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.libs.XML;
import scala.util.Random;

public class CharTable {
	private Map<Integer, CharInfo> mChars = new HashMap<Integer,CharInfo>();
	
	public void init(InputStream in)
	{
		try{
			Document doc = XML.fromInputStream(in, "UTF-8");
			
			readChars(doc.getDocumentElement());
			
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public CharInfo getChar(int charType)
	{
		if(mChars.containsKey(charType) == false)
			return null;
		
		return mChars.get(charType);
	}
	
	public CharInfo randomChar()
	{
		final Random random = new Random();
		CharInfo[] values = mChars.values().toArray(new CharInfo[mChars.size()]);
		
		return values[random.nextInt(mChars.size())];
	}
	
	private void readChars(Element elem)
	{
		NodeList child = elem.getElementsByTagName("char");
		if(child == null)
			return;
		
		Node current = null;
		for( int i = 0; i < child.getLength(); i++ )
		{
			current = child.item(i);
			if( current.getNodeType() == Node.ELEMENT_NODE )
			{
				Element childElem = (Element)current;
				
				CharInfo charInfo = new CharInfo();
				
				charInfo.charType = Integer.parseInt(childElem.getAttribute("id"));
				charInfo.hp = Integer.parseInt(childElem.getAttribute("hp"));
				charInfo.st = Integer.parseInt(childElem.getAttribute("st"));
				
				mChars.put(charInfo.charType, charInfo);
			}
		}
	}

	private static class Holder {
		private static final CharTable Instance = new CharTable(); 
	}
	
	public static CharTable getInstance() 
	{
		return CharTable.Holder.Instance;
	}
}
