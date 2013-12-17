package xml;

import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import play.libs.XML;

public class GameRule {
	
	public float 	CHAR_INIT_SOUL = 30;
	public float 	BOUNS_START_SOUL = 30;
	public int		GAMEEND_MAX_TURN = 30;
	public int		START_ENHANCE_ROUND = 3;
	
	private ArrayList<Float> mStartEnhance = new ArrayList<Float>();	
	
	public void init(InputStream in)
	{
		try{
			Document doc = XML.fromInputStream(in, "UTF-8");
			
			readRules(doc.getDocumentElement());
			
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public float getStartEnhance(int round)
	{
		if(round > START_ENHANCE_ROUND)
			return mStartEnhance.get(START_ENHANCE_ROUND);
		
		return mStartEnhance.get(round);
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
