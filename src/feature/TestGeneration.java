package classifierRun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import uk.ac.open.crc.intt.IdentifierNameTokeniser;
import uk.ac.open.crc.intt.IdentifierNameTokeniserFactory;
import utility.SimilarityCal;
import utility.Stemmer;

public class TestGeneration {
	
    static int topk=10;
	
	
	public static void TenFoldGeneration(String granu, String classstr) throws Exception
	{
		IdentifierNameTokeniserFactory a=new IdentifierNameTokeniserFactory();
		IdentifierNameTokeniser b=a.create();
		
		BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\RQ\\RQ6_new\\"+classstr+"_new_result.csv",true));
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data_new\\"+classstr+".csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			String project=line.substring(0, line.indexOf(","));
									
			Vector<idinstance> allins=new Vector<idinstance>();
			int id=0;
			BufferedReader read=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\PureChange\\"+project+".csv"));
			String state="";
			while((state=read.readLine())!=null)
			{
				state=state.substring(state.indexOf(",")+1, state.length());
                
				String category=state.substring(0, state.indexOf(","));
				
				String detail=state.substring(state.lastIndexOf(",")+1, state.length());
	    		    
				detail=detail.replace("<-", " ").trim();
				String[] split=detail.split(" ");
				    
				String previous=split[1].replaceAll("\\d+",""); 
				String current=split[0].replaceAll("\\d+",""); 
				    
				if(granu.equals("6"))
				{
	                if(!previous.equals(current))
	                {
		                idinstance oneid=new idinstance(id,previous,current);
		                allins.add(oneid);
					    id++;
	                }
				}
				else
				{
	                if(granu.equals(category)&&!previous.equals(current))
	                {
		                idinstance oneid=new idinstance(id,previous,current);
		                allins.add(oneid);
					    id++;
	                }
				}
			}
			read.close();
			 
			int allcount=0;
			int[] hitcount=new int[topk];
			int[] part_hit=new int[topk];
			int[] stemmed_hit=new int[topk];
			
			for(int aa=0;aa<10;aa++)
			{
				Vector<Integer> trainingid=BalancedLearning.splitWholeDataset(allins.size(),aa);
				
				Vector<idinstance> training=new Vector<idinstance>();
				Vector<idinstance> test=new Vector<idinstance>();

				for(int k=0;k<allins.size();k++)
				{
					if(trainingid.contains(k))
					{
						training.add(allins.get(k));
					}
					else
					{
						test.add(allins.get(k));
					}
				}
			
			
				Vector<pattern> patterns=FindPattern(b, training);

				
				for(idinstance oneins:test)
				{
					String previous=oneins.getPrevious();
					String current=oneins.getCurrent();
					Vector<String> recommendationlist=RecBasedOnMerge(b,training,previous,patterns);				    				    
				    current=current.toLowerCase();

				    for(int i=0;i<recommendationlist.size();i++)
				    {
				    	if(recommendationlist.get(i).equals(current))
				    	{
				    		for(int k=i;k<topk;k++)
				    			hitcount[k]++;
				    		break;
				    	}
				    }	
				    
				    String[] currentsubstring= {current};
					try 
					{
						currentsubstring=b.tokenise(current);
					}
					catch(Exception e)
					{
						System.out.println(e.toString());
					}
					
					Stemmer s=new Stemmer();
				    			     
					HashSet<String> stemmed_current=new HashSet<String>();
					for(String onecur:currentsubstring)
						stemmed_current.add(s.porterstem(onecur));
					
				    
				    for(int i=0;i<recommendationlist.size();i++)
				    {
				    	String onerecom=recommendationlist.get(i);
				    	
				    	String[] recsubstring= {onerecom};
						try 
						{
							recsubstring=b.tokenise(onerecom);
						}
						catch(Exception e)
						{
							System.out.println(e.toString());
						}
						
						HashSet<String> merge=new HashSet<String>();
						for(String onecur:currentsubstring)
							merge.add(onecur);
						for(String onerec:recsubstring)
							merge.add(onerec);
						
						if(merge.size()==currentsubstring.length&&merge.size()==recsubstring.length)
				    	{
				    		for(int k=i;k<topk;k++)
				    			part_hit[k]++;
				    		break;
				    	}
				    }
				    
				    for(int i=0;i<recommendationlist.size();i++)
				    {
				    	String onerecom=recommendationlist.get(i);
				    	
				    	String[] recsubstring= {onerecom};
						try 
						{
							recsubstring=b.tokenise(onerecom);
						}
						catch(Exception e)
						{
							System.out.println(e.toString());
						}
						
						HashSet<String> stemmed_rec=new HashSet<String>();
						for(String onerec:recsubstring)
							stemmed_rec.add(s.porterstem(onerec));
						
						
						HashSet<String> merge=new HashSet<String>();
						merge.addAll(stemmed_current);
						merge.addAll(stemmed_rec);
						
						if(merge.size()==stemmed_current.size()&&merge.size()==stemmed_rec.size())
				    	{
				    		for(int k=i;k<topk;k++)
				    			stemmed_hit[k]++;
				    		break;
				    	}
				    }
				    
				    allcount++;
				     
			     }
		    }
			
			System.out.print(granu+","+project+","+allins.size()+",");
			bw.write(granu+","+project+","+allins.size()+",");
			for(int i=0;i<topk;i++)
			{
				if(allcount!=0)
				{
					System.out.print(hitcount[i]*1.0/allcount+",");
					bw.write(hitcount[i]*1.0/allcount+",");
				}
				else
				{
					System.out.print("0,");
					bw.write("0,");
				}
				
			}
			for(int i=0;i<topk;i++)
			{
				if(allcount!=0)
				{
					System.out.print(part_hit[i]*1.0/allcount+",");
					bw.write(part_hit[i]*1.0/allcount+",");
				}
				else
				{
					System.out.print("0,");
					bw.write("0,");
				}
			}
			for(int i=0;i<topk;i++)
			{
				if(allcount!=0)
				{
					System.out.print(stemmed_hit[i]*1.0/allcount+",");
					bw.write(stemmed_hit[i]*1.0/allcount+",");
				}
				else
				{
					System.out.print("0,");
					bw.write("0,");
				}
			}
			System.out.println();
			bw.newLine();
		}
		
		br.close();
		bw.close();
		  
	   }
	
	public static Vector<pattern> FindPattern(IdentifierNameTokeniser b, Vector<idinstance> training)
	{
		Stemmer stem=new Stemmer();
		
		Vector<pattern> result=new Vector<pattern>();
		for(idinstance oneid:training)
		{
			HashSet<String> merge=new HashSet<String>();
			Vector<String> previous_set=new Vector<String>();
			Vector<String> current_set=new Vector<String>();
			
			String previous=oneid.getPrevious();
			String[] str1substring= {previous};
			try
			{
				str1substring=b.tokenise(previous);
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
			for(String s:str1substring)
			{
				s=s.toLowerCase();
				merge.add(s);
				previous_set.add(s);
			}
			
			String current=oneid.getCurrent();
			String[] str2substring= {current};
			try
			{
				str2substring=b.tokenise(current);
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
			for(String s:str2substring)
			{
				s=s.toLowerCase();
				merge.add(s);
				current_set.add(s);
			}
			
			for(String s:merge)
			{
				if(previous_set.contains(s)&&current_set.contains(s))
				{
					previous_set.remove(s);
					current_set.remove(s);
				}
			}
			
			if(!previous_set.isEmpty()&&!current_set.isEmpty())
			{
				for(String onepre:previous_set)
				{
					String stemedonepre=stem.porterstem(onepre);
					int label=0;
					for(pattern onep:result)   
					{
						String stemmedstr=onep.getThisStemmedId();
						if(stemedonepre.equals(stemmedstr))
						{
							Hashtable<String,Integer> changes=onep.getChange();
							
							for(String onecurr:current_set)
							{
								
								if(changes.keySet().contains(onecurr))
								{
									int value=changes.get(onecurr);
									value++;
									changes.remove(onecurr);
									changes.put(onecurr,value);
									
								}
								else
								{
									changes.put(onecurr, 1);
								}
								
							}
							
							onep.setChange(changes);
							
							label=1;
							break;
						}
					}
					if(label==0) 
					{
						Hashtable<String,Integer> hs=new Hashtable<String,Integer>();
						for(String onecurr:current_set)
							hs.put(onecurr, 1);
						pattern onep=new pattern(onepre,stemedonepre,hs,0,0);
						result.add(onep);
						
					}
				}
			}
			else if(!previous_set.isEmpty()&&current_set.isEmpty())
			{
				
				for(String onepre:previous_set)
				{
					int label=0;
					String stemedonepre=stem.porterstem(onepre);
					for(pattern onep:result)
					{
						if(stemedonepre.equals(onep.getThisStemmedId()))
						{
							int deletedno=onep.getDelete();
							deletedno++;
							onep.setDelete(deletedno);
							
							label=1;
							break;
						}
					}
					if(label==0)
					{
						Hashtable<String,Integer> hs=new Hashtable<String,Integer>();
						pattern onep=new pattern(onepre,stemedonepre,hs,1,0);
						result.add(onep);
					}
				}
			}
			else if(previous_set.isEmpty()&&!current_set.isEmpty())
			{

				for(String onecur:current_set)
				{
					int label=0;
					String stemedonecur=stem.porterstem(onecur);
					for(pattern onep:result)
					{
						if(stemedonecur.equals(onep.getThisStemmedId()))
						{
							int addno=onep.getAdd();
							addno++;
							onep.setAdd(addno);
							
							label=1;
							break;
						}
					}
					if(label==0)
					{
						Hashtable<String,Integer> hs=new Hashtable<String,Integer>();
						pattern onep=new pattern(onecur,stemedonecur,hs,0,1);
						result.add(onep);
					}
							
				}
			}
			
			
			
		}
		return result;
	}
	
	public static Vector<String> RecBasedOnMerge(IdentifierNameTokeniser b, Vector<idinstance> training, String previous, Vector<pattern> patterns) throws Exception
    {
    	Hashtable<String,Float> result1=RecBasedOnHistory2(b,training,previous);
    	Hashtable<String,Float> result2=RecBasedOnFrequency2(b,previous,patterns, training);
    	Set<String> mergeset=new HashSet<String>();
    	mergeset.addAll(result1.keySet());
    	mergeset.addAll(result2.keySet());
    	
    	Hashtable<String,Float> ht=new Hashtable<String,Float>();
    	for(String onemerge:mergeset)
    	{
    		float value1=0, value2=0;
    		if(result1.keySet().contains(onemerge))
    			value1=result1.get(onemerge);
    		if(result2.keySet().contains(onemerge))
    			value2=result2.get(onemerge);

    		
    		float value=(value1+value2)/2;
    	    ht.put(onemerge, value);
    	}
    	
    	Vector<String> result=new Vector<String>();
		while(result.size()<topk&&!ht.isEmpty())
		{
			String maxstring="";
			float max=-1;
			Set<String> keys=ht.keySet();
			for(String onekey:keys)
			{
				float value=ht.get(onekey);
				if(value>max)
				{
					max=value;
					maxstring=onekey;
				}
			}
			ht.remove(maxstring);
			maxstring=maxstring.toLowerCase();
			if(!result.contains(maxstring))
				result.add(maxstring);
			
		}
		
		return result;
    }
    
    
    public static Hashtable<String,Float> RecBasedOnHistory2(IdentifierNameTokeniser b, Vector<idinstance> training, String previous) throws Exception
	{
		
		Set<String> allcurrent=new HashSet<String>();
		Hashtable<String,Float> hs=new Hashtable<String,Float>();
		for(idinstance oneins:training)
		{
			String onecurrent=oneins.getCurrent();
			allcurrent.add(onecurrent);
		}
		
		for(String onecurr:allcurrent)
		{
			float sum=0;
			int count=0;
		    for(idinstance oneins:training)
		    {
		    	if(oneins.getCurrent().equals(onecurr))
		    	{
		    		sum+=SimilarityCal.calJaccardSimiForIdentifier(b,oneins.getPrevious(), previous);
		    		count++;
		    	}
		    }
		    
		    hs.put(onecurr, sum/count);
		}
		return hs;
		
		 
	}


    public static Hashtable<String,Float> RecBasedOnFrequency2(IdentifierNameTokeniser b, String previous, Vector<pattern> patterns, Vector<idinstance> training) throws Exception
    {
    	Stemmer stem=new Stemmer();		
		String[] str1substring= {previous};
		try
		{
			str1substring=b.tokenise(previous);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
		Hashtable<String,Float> ht=new Hashtable<String,Float>();
		for(String sub:str1substring)
		{
			Hashtable<String,Integer> result=new Hashtable<String,Integer>();
			int count=0;
			String sublow=sub.toLowerCase();
			String stemedsub=stem.porterstem(sublow);
			
			for(pattern onep:patterns)
			{
				if(onep.getThisStemmedId().equals(stemedsub))
				{
					Hashtable<String, Integer> hs=onep.getChange();
					int deleteno=onep.getDelete();
					count+=deleteno;
					Set<String> keys=hs.keySet();
					while(!keys.isEmpty())
					{
						int maxvalue=-1;
						String maxstring="";
						for(String onekey:keys)
						{
							int value=hs.get(onekey);
							if(maxvalue<value)
							{
								maxvalue=value;
								maxstring=onekey;
							}
						}
						keys.remove(maxstring);
						count+=maxvalue;
						String newstr=previous.replace(sub, maxstring);
						if(!result.keySet().contains(newstr)&&!newstr.equals(previous))
							result.put(newstr,maxvalue);
						 
					}
					if(deleteno>0)
					{
							String newstr=previous.replace(sub, "");
							if(!result.keySet().contains(newstr)&&!newstr.equals(previous))
								result.put(newstr,deleteno);
					}
					break;
				}
			}
			
			
			Set<String> hskeys=result.keySet();
			for(String onehskey:hskeys)
			{
				ht.put(onehskey, result.get(onehskey)*1.0f/count);
			}
			
			
			Hashtable<String,Integer> mapping=new Hashtable<String, Integer>();
			for(idinstance onetrain:training)
			{
				String onecurrent=onetrain.getCurrent();
				String[] currsubstring= {onecurrent};
				try
				{
					currsubstring=b.tokenise(previous);
				}
				catch(Exception e)
				{
					System.out.println(e.toString());
				}
				
				HashSet<String> currentsub=new HashSet<String>();
				for(String currsub:currsubstring)
				{
					String currsublow=currsub.toLowerCase();
					String currstemedsub=stem.porterstem(currsublow);
					currentsub.add(currstemedsub);
				}
				
				if(currentsub.contains(stemedsub))
				{
					for(String onecurrentsub:currentsub)
					{
						if(!onecurrentsub.equals(stemedsub))
						{
							if(mapping.keySet().contains(onecurrentsub))
							{
								int value=mapping.get(onecurrentsub);
								value++;
								mapping.remove(onecurrentsub);
								mapping.put(onecurrentsub, value);
							}
							else
							{
								mapping.put(onecurrentsub, 1);
							}
						}
					}
					
				}
			}
			

			Hashtable<String,Integer> selected=new Hashtable<String,Integer>();
			int sum=0;
			while(selected.size()<10&&mapping.size()>0)
			{
				int max=-1;
				String maxstring="";
				Set<String> mappingset=mapping.keySet();
				for(String onemapping:mappingset)
				{
					int value=mapping.get(onemapping);
					if(value>max)
					{
						max=value;
						maxstring=onemapping;
					}
				}
				mapping.remove(maxstring);
				selected.put(maxstring,max);
				sum+=max;

			}
			
			Set<String> selectedkeys=selected.keySet();
			for(String oneselectedkey:selectedkeys)
			{
				String newstring=oneselectedkey+""+previous;
				ht.put(newstring, selected.get(oneselectedkey)*1.0f/sum);
			}

		}

		
		return ht;

    }
}

class idinstance
{
	int id;
	String previous;
	String current;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPrevious() {
		return previous;
	}
	public void setPrevious(String previous) {
		this.previous = previous;
	}
	public String getCurrent() {
		return current;
	}
	public void setCurrent(String current) {
		this.current = current;
	}
	public idinstance(int id, String previous, String current) {
		super();
		this.id = id;
		this.previous = previous;
		this.current = current;
	}
	
	
	
}

class pattern
{
	String thisId;
	String thisStemmedId;
	Hashtable<String, Integer> Change;
	int Delete;
	int Add;
	public String getThisId() {
		return thisId;
	}
	public void setThisId(String thisId) {
		this.thisId = thisId;
	}
	public Hashtable<String, Integer> getChange() {
		return Change;
	}
	public void setChange(Hashtable<String, Integer> change) {
		Change = change;
	}
	public int getDelete() {
		return Delete;
	}
	public void setDelete(int delete) {
		Delete = delete;
	}
	public int getAdd() {
		return Add;
	}
	public void setAdd(int add) {
		Add = add;
	}
	public String getThisStemmedId() {
		return thisStemmedId;
	}
	public void setThisStemmedId(String thisStemmedId) {
		this.thisStemmedId = thisStemmedId;
	}
	public pattern(String thisId, String thisStemmedId, Hashtable<String, Integer> change, int delete, int add) {
		super();
		this.thisId = thisId;
		this.thisStemmedId = thisStemmedId;
		Change = change;
		Delete = delete;
		Add = add;
	}
	public void PrintString()
	{
		System.out.print(this.thisId+" "+this.getThisStemmedId()+" "+this.getDelete()+" "+this.getAdd()+" ");
		Set<String> hs=this.getChange().keySet();
		for(String onehs:hs)
		{
			System.out.print(onehs+":"+this.getChange().get(onehs)+" ");
		}
		System.out.println();
		
		
		
	}
	
	
	
}
