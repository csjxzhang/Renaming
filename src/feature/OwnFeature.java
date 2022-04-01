package feature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import uk.ac.open.crc.intt.IdentifierNameTokeniser;
import uk.ac.open.crc.intt.IdentifierNameTokeniserFactory;
import utility.Stemmer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class OwnFeature {
	
	
	
	public static void ExtractOwnFeature() throws Exception
	{
		CountInPositiveAndNegative();
		
		Stemmer stem=new Stemmer();
		Vector<String> stopwords=new Vector<String>();
		BufferedReader reader=new BufferedReader(new FileReader((System.getProperty("user.dir")+"\\lib\\stop_words_english.txt")));
		String stoplist="";
		while((stoplist=reader.readLine())!=null)
		{
			stoplist=stem.porterstem(stoplist.toLowerCase());
			stopwords.add(stoplist);
		}
		reader.close();
		
		Vector<String> commonword=new Vector<String>();
		BufferedReader commonlistreader=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\commonEnglishWord.txt"));
		String line1="";
		while((line1=commonlistreader.readLine())!=null)
		{
			String stemmedword=line1.substring(0, line1.indexOf(" "));
			String ok=stem.porterstem(stemmedword.toLowerCase());
			commonword.add(ok);
		}
		commonlistreader.close();
		
		Hashtable<String,Integer> inPositive=new Hashtable<String,Integer>();
		Hashtable<String,Integer> inNegative=new Hashtable<String,Integer>();
		Hashtable<String,Integer> current_inPositive=new Hashtable<String,Integer>();
		Hashtable<String,Integer> current_inNegative=new Hashtable<String,Integer>();
		
		BufferedReader PorN=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data_new\\inPositive.txt"));
		String fre="";
		while((fre=PorN.readLine())!=null)
		{
			if(fre.contains(","))
			{
				String front=fre.substring(0, fre.lastIndexOf(","));
				int end=Integer.parseInt(fre.substring(fre.lastIndexOf(",")+1, fre.length()));
				inPositive.put(front, end);
			}
		}
		PorN.close();
		
		PorN=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data_new\\inNegative.txt"));
		fre="";
		while((fre=PorN.readLine())!=null)
		{
			if(fre.contains(","))
			{
				String front=fre.substring(0, fre.lastIndexOf(","));
				int end=Integer.parseInt(fre.substring(fre.lastIndexOf(",")+1, fre.length()));
				inNegative.put(front, end);
			}
		}
		PorN.close();
		
		PorN=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data_new\\current_inNegative.txt"));
		fre="";
		while((fre=PorN.readLine())!=null)
		{
			if(fre.contains(","))
			{
				String front=fre.substring(0, fre.lastIndexOf(","));
				int end=Integer.parseInt(fre.substring(fre.lastIndexOf(",")+1, fre.length()));
				current_inNegative.put(front, end);
			}
		}
		PorN.close();
		
		PorN=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data_new\\current_inPositive.txt"));
		fre="";
		while((fre=PorN.readLine())!=null)
		{
			if(fre.contains(","))
			{
				String front=fre.substring(0, fre.lastIndexOf(","));
				int end=Integer.parseInt(fre.substring(fre.lastIndexOf(",")+1, fre.length()));
				current_inPositive.put(front, end);
			}
		}
		PorN.close();
		
		
		IdentifierNameTokeniserFactory a=new IdentifierNameTokeniserFactory();
		IdentifierNameTokeniser b=a.create();
		MaxentTagger tagger = new MaxentTagger(System.getProperty("user.dir")+"\\models\\english-bidirectional-distsim.tagger");
		
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data_new\\FinalProjects.csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			
			String project=line.substring(0, line.indexOf(","));
			BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\feature_new\\"+project+"_G1.csv"));
			BufferedReader read=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\GoldenSet\\"+project+".csv"));
			String oneid="";
			while((oneid=read.readLine())!=null)
			{
				String[] split=oneid.split(",");
				if(split.length>=8)
				{
					String label=split[0];  //label
					
					String cate=split[1];
					int category=-1;
					if(isInteger(cate))
					{
						category=Integer.parseInt(cate);
					}
					
					String categoryDis=""; 
					if(category==1)
					{
						categoryDis="1,0,0,0,0";
					}
					else if(category==2)
					{
						categoryDis="0,1,0,0,0";
					}
					else if(category==3)
					{
						categoryDis="0,0,1,0,0";
					}
					else if(category==4)
					{
						categoryDis="0,0,0,1,0";
					}
					else if(category==5)
					{
						categoryDis="0,0,0,0,1";
					}
					
					String file=split[3];
					
					
					int location=-1;
					String last=split[split.length-1];
					if(isInteger(last))
					{
						location=Integer.parseInt(last);
					}
					
					
					String identifier=split[7];
					if(label.equals("1"))
					{
						identifier=findOldIdentifier(project,location,category,file,identifier);
					}
					
					
					identifier=identifier.trim();
					if(!identifier.isEmpty()&&location>=0&&category>0)
					{
						int charlength=identifier.length();
							
						String[] substring= {identifier};
						try 
						{
						        substring=b.tokenise(identifier);
						}
						catch(Exception e)
						{
							System.out.println(e.toString());
						}
						
						int termlength=substring.length;
						
						int verb=0;				
	                    int noun=0;             
	                    int adj=0;
	                    
						for(String s:substring)
						{
							String result=tagger.tagTokenizedString(s); 
							result=result.substring(result.lastIndexOf("_")+1, result.length());
							if(result.contains("JJ"))
							{
								adj=1;
							}
							if(result.contains("NN"))
							{
								noun=1;
							}
							if(result.contains("VB"))
							{
								verb=1;
							}
						}
							
						String styleDis=checkStyle(identifier);
						
						 
			            int contain$=0;
			            if(identifier.contains("$"))
			            	contain$=1;
			            
			            String visibility=checkVisibility(location, file);
			           
			             
			            int startUpper=0;
			            if(Character.isUpperCase(identifier.charAt(0)))
			            	startUpper=1;
				         
			            int uppercount=0;
			            int digitcount=0; 
			            for(int i=0;i<identifier.length();i++)
			            {
			            	if(Character.isUpperCase(identifier.charAt(i)))
			            	{
			            		uppercount++;
			            	}
			            	if(Character.isDigit(identifier.charAt(i)))
			            	{
			            		digitcount++;
			            	}
			            }
			            
			            int startOrEnd_ =0;
			            if(identifier.startsWith("_")||identifier.endsWith("_"))
			            	startOrEnd_=1;
			            
			            int consecutive_=0;
			            if(identifier.contains("__")||identifier.contains("___")||identifier.contains("____")||identifier.contains("_____"))
			            	consecutive_=1;
			            
			            int caseConsective=checkUpperOrLower(substring); 
			            
			            double commonPercentage=allCommonWord(substring, commonword, stem); 
			            
			            int count=0;
			            
			            double sumvalue=0.0;
			            double maxv=0.0;
			            double minv=1.0;
			            
			            double currentProject_sumvalue=0.0;
			            double currentProject_maxv=0.0;
			            double currentProject_minv=1.0;
			            
			            double currentCategory_sumvalue=0.0;
			            double currentCategory_maxv=0.0;
			            double currentCategory_minv=1.0;
			            
			            for(String s:substring)
						{
							s=s.toLowerCase();
							s=s.replaceAll("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×()]", ""); 
							s=s.replaceAll("\\d+",""); 
							s=stem.porterstem(s);
							s=s.trim();
							
							if(!stopwords.contains(s)&&!OwnFeature.isInteger(s)&&!s.isEmpty())
							{
								
								double frePos=0.0;
								int count1=0,count2=0;
								if(inPositive.keySet().contains(s))
								{
									count1=inPositive.get(s);
								}
								if(inNegative.keySet().contains(s))
								{
									count2=inNegative.get(s);
								}
								int sum=count1+count2;
								if(sum!=0)
								{
									frePos=count1*1.0/sum;
								}
								sumvalue+=frePos;
								if(frePos>maxv)
									maxv=frePos;
								if(frePos<minv)
									minv=frePos;
								
								
								double currentProjectfrePos=0.0;
								int currentProject_count1=0, currentProject_count2=0;
								for(int i=1;i<=5;i++)
								{
									if(current_inPositive.keySet().contains(project+","+i+","+s))
									{
										currentProject_count1+=current_inPositive.get(project+","+i+","+s);
									}
									if(current_inNegative.keySet().contains(project+","+i+","+s))
									{
										currentProject_count2+=current_inNegative.get(project+","+i+","+s);
									}
								}
								int currentProject_sum=currentProject_count1+currentProject_count2;
								if(currentProject_sum!=0)
								{
									currentProjectfrePos=currentProject_count1*1.0/currentProject_sum;
								}
								currentProject_sumvalue+=currentProjectfrePos;
								if(currentProjectfrePos>currentProject_maxv)
									currentProject_maxv=currentProjectfrePos;
								if(currentProjectfrePos<currentProject_minv)
									currentProject_minv=currentProjectfrePos;
								
								
								
								double currentCategoryfrePos=0.0;
								int currentCategory_count1=0, currentCategory_count2=0;
								
								if(current_inPositive.keySet().contains(project+","+category+","+s))
								{
									currentCategory_count1=current_inPositive.get(project+","+category+","+s);
								}
								if(current_inNegative.keySet().contains(project+","+category+","+s))
								{
									currentCategory_count2+=current_inNegative.get(project+","+category+","+s);
								}
								
								int currentCategory_sum=currentCategory_count1+currentCategory_count2;
								if(currentCategory_sum!=0)
								{
									currentCategoryfrePos=currentCategory_count1*1.0/currentCategory_sum;
								}
								currentCategory_sumvalue+=currentCategoryfrePos;
								if(currentCategoryfrePos>currentCategory_maxv)
									currentCategory_maxv=currentCategoryfrePos;
								if(currentCategoryfrePos<currentCategory_minv)
									currentCategory_minv=currentCategoryfrePos;
								
								count++;
								
							}
      					}
						
			            if(count!=0)
			            {
			            	sumvalue=sumvalue/count;
			            	currentProject_sumvalue=currentProject_sumvalue/count;
			            	currentCategory_sumvalue=currentCategory_sumvalue/count;
			            }
			            			            
						bw.write(categoryDis+","+charlength+","+termlength+","+verb+","+noun+","+adj+","+styleDis+","+contain$+","+visibility+","
								+startUpper+","+uppercount+","+digitcount+","+startOrEnd_+","+consecutive_+","+caseConsective+","+commonPercentage+","
								+sumvalue+","+minv+","+maxv+","+(1-sumvalue)+","+(1-minv)+","+(1-maxv)+","
								+currentProject_sumvalue+","+currentProject_minv+","+currentProject_maxv+","+(1-currentProject_sumvalue)+","+(1-currentProject_minv)+","+(1-currentProject_maxv)+","
								+currentCategory_sumvalue+","+currentCategory_minv+","+currentCategory_maxv+","+(1-currentCategory_sumvalue)+","+(1-currentCategory_minv)+","+(1-currentCategory_maxv)+","+label);
			            bw.newLine();
					}
				}
			}
			read.close();
			bw.close();
			
		}
		br.close();
	}
	
	
	
	public static void CountInPositiveAndNegative() throws Exception
	{
		IdentifierNameTokeniserFactory a=new IdentifierNameTokeniserFactory();
		IdentifierNameTokeniser b=a.create();
		Stemmer stem=new Stemmer();
		
		Vector<String> stopwords=new Vector<String>();
		BufferedReader reader=new BufferedReader(new FileReader((System.getProperty("user.dir")+"\\lib\\stop_words_english.txt")));
		String stoplist="";
		while((stoplist=reader.readLine())!=null)
		{
			stoplist=stem.porterstem(stoplist.toLowerCase());
			stopwords.add(stoplist);
		}
		reader.close();
		
		
		
		Hashtable<String,Integer> inPositive=new Hashtable<String,Integer>();
		Hashtable<String,Integer> inNegative=new Hashtable<String,Integer>();
		Hashtable<String,Integer> current_inPositive=new Hashtable<String,Integer>();
		Hashtable<String,Integer> current_inNegative=new Hashtable<String,Integer>();
		
		
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data_new\\FinalProjects.csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
		
			String project=line.substring(0, line.indexOf(","));
			System.out.println(project);
			BufferedReader read=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\GoldenSet\\"+project+".csv"));
			String oneid="";
			while((oneid=read.readLine())!=null)
			{
				String[] split=oneid.split(",");
				if(split.length>=8)
				{
					String label=split[0]; 
					
					String cate=split[1];
					int category=-1;    
					if(OwnFeature.isInteger(cate))
					{
						category=Integer.parseInt(cate);
					}
					
					
					String file=split[3];
					
					int location=-1;
					String last=split[split.length-1];
					if(OwnFeature.isInteger(last))
					{
						location=Integer.parseInt(last);
					}
					
					
					String identifier=split[7]; 
					if(label.equals("1"))
					{
						identifier=OwnFeature.findOldIdentifier(project,location,category,file,identifier);
					}
					
					
					identifier=identifier.trim();
					if(!identifier.isEmpty()&&location>=0&&category>0)
					{
						String[] substring= {identifier}; 
						try 
						{
						    substring=b.tokenise(identifier);
						}
						catch(Exception e)
						{
							System.out.println(e.toString());
						}
						
						 
						 
						for(String s:substring)
						{
							s=s.toLowerCase();
							s=s.replaceAll("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×()]", ""); 
							s=s.replaceAll("\\d+",""); 
							s=stem.porterstem(s); 
							s=s.trim();
							
							if(!stopwords.contains(s)&&!OwnFeature.isInteger(s)&&!s.isEmpty())
							{
								String current_s=project+","+category+","+s;
								
								if(label.equals("1"))
								{
									if(inPositive.keySet().contains(s))
									{
										int value=inPositive.get(s);
										value++;
										inPositive.remove(s);
										inPositive.put(s, value);
									}
									else
									{
										inPositive.put(s, 1);
									}
									
									if(current_inPositive.keySet().contains(current_s))
									{
										int value=current_inPositive.get(current_s);
										value++;
										current_inPositive.remove(current_s);
										current_inPositive.put(current_s, value);
									}
									else
									{
										current_inPositive.put(current_s, 1);
									}
								}
								else
								{
									if(inNegative.keySet().contains(s))
									{
										int value=inNegative.get(s);
										value++;
										inNegative.remove(s);
										inNegative.put(s, value);
									}
									else
									{
										inNegative.put(s, 1);
									}
									
									if(current_inNegative.keySet().contains(current_s))
									{
										int value=current_inNegative.get(current_s);
										value++;
										current_inNegative.remove(current_s);
										current_inNegative.put(current_s, value);
									}
									else
									{
										current_inNegative.put(current_s, 1);
									}
								}
							}
							
						}
						 
						
						 
						
						 
					}
				}
			}
			read.close();
			
		
		}
		br.close();
		
		BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\data_new\\inPositive.txt"));
		Set<String> keys=inPositive.keySet();
		for(String onekey:keys)
		{
			bw.write(onekey+","+inPositive.get(onekey));
			bw.newLine();
		}
		bw.close();
		
		
		bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\data_new\\inNegative.txt"));
		keys=inNegative.keySet();
		for(String onekey:keys)
		{
			bw.write(onekey+","+inNegative.get(onekey));
			bw.newLine();
		}
		bw.close();
		
		bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\data_new\\current_inPositive.txt"));
		keys=current_inPositive.keySet();
		for(String onekey:keys)
		{
			bw.write(onekey+","+current_inPositive.get(onekey));
			bw.newLine();
		}
		bw.close();
		
		bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\data_new\\current_inNegative.txt"));
		keys=current_inNegative.keySet();
		for(String onekey:keys)
		{
			bw.write(onekey+","+current_inNegative.get(onekey));
			bw.newLine();
		}
		bw.close();
	}
	
	public static double allCommonWord(String[] string, Vector<String> commonword, Stemmer stem)
	{
		int count=0;
		for(String sub:string)
		{
			if(commonword.contains(stem.porterstem(sub.toLowerCase())))
			{
				count++;
			}	
		}
		return (double)count/string.length;
	}
	
	
	
	public static int checkUpperOrLower(String[] string)
	{
		Set<Integer> result= new HashSet<Integer>();
		
		for(String substring: string)
		{
			int label=0; 
			if(substring.toUpperCase().equals(substring))
			{
				label=2;  
			}
			if(substring.toLowerCase().equals(substring))
			{
				label=1; 
			}
			result.add(label);
		}
		
		if(result.size()==1)
		{
			return 0;
		}
		if(result.size()==2)
		{
			if(result.contains(0)&&result.contains(1))
			{
			    return 0;	
			}
			else if(result.contains(0)&&result.contains(2))
			{
				return 1;
			}
			else 
			{
				return 1;
			}
		}
		else
		{
			return 1;
		}
		
	}
	
	
	public static String findOldIdentifier(String project,int location,int category, String file, String newIdentifier) throws Exception
	{
		int newlocation=location-1;
		String result="";
		BufferedReader br1=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\PureChange\\"+project+".csv"));
		String line1="";
		while((line1=br1.readLine())!=null)
		{
			if(line1.startsWith(project+","+category+","+location+","+file+","+newIdentifier+",")||line1.startsWith(project+","+category+","+newlocation+","+file+","+newIdentifier+","))
			{
				result=line1.substring(line1.lastIndexOf(",")+1,line1.length());
			}
		}
		br1.close();
		
		
		
		if(result.isEmpty())
		{
			BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\PureChange\\"+project+".csv"));
			String line="";
			while((line=br.readLine())!=null)
			{
				if(line.startsWith(project+","+category+",")&&line.contains(","+file+","+newIdentifier+","))
				{
					result=line.substring(line.lastIndexOf(",")+1,line.length());
				}
			}
			br.close();
		}
		
		
		
		String needreturn=newIdentifier;
		
		if(!result.isEmpty()&&result.startsWith(newIdentifier+"<-"))
		{
			String[] split=result.split("<-");
			
			for(int i=1;i<split.length;i++)
			{
				if(!split[i].equals(newIdentifier))
				{
					needreturn=split[i];
					break;
				}
			}
		}
		else
		{
			System.err.println(project+","+category+","+location+","+file+","+newIdentifier+",");
		}
		return needreturn;
	}

	public static boolean isInteger(String str) 
	{  
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches();  
	}
	
	public static String checkVisibility(int location, String file) throws Exception
	{
		String result="0,0,0,0";
		
		int pointer=0;
		BufferedReader br=new BufferedReader(new FileReader(file));
		String line="";
		while((line=br.readLine())!=null)
		{
			pointer++;
			if(pointer==location)
			{
				if(line.contains("public "))
				{
					result="1,0,0,0";
				}
				else if(line.contains("private "))
				{
					result="0,1,0,0";
				}
				else if(line.contains("protected "))
				{
					result="0,0,1,0";
				}
				else
				{
					result="0,0,0,1";
				}
			}
		}
		br.close();
		
		return result;
	}
	
	
	public static String checkStyle(String IdentifierName)
	{
		if(IdentifierName.contains("_"))
		{
			return "1,0,0";
		}
		else
		{
			LinkedList<String> resultlist = new LinkedList<String>(); 
			StringBuilder word = new StringBuilder();
			char[] buf = IdentifierName.toCharArray();
			boolean prevIsupper=false;
			for(int i=0;i<buf.length;i++)
			{
				char ch= buf[i];
				if(Character.isUpperCase(ch))
				{
					if(i==0)
					{
						word.append(ch);
					}
					else if(!prevIsupper)
					{
						resultlist.add(word.toString());
						word=new StringBuilder();
						word.append(ch);
					}else
					{
						word.append(ch);
					}
					prevIsupper=true;
				}
				else
				{
					word.append(ch);
					prevIsupper=false;
				}
			}
			if(word!=null&&word.length()>0)
			{
				resultlist.add(word.toString());
			}
					 
			if(resultlist.size()>1)
			{
				return "0,1,0";					
			}
			else
			{
				return "0,0,1";
			}
			 
		}
	}

	 
}
