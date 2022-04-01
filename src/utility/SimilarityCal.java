package utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import DS.Word2vec;
import uk.ac.open.crc.intt.IdentifierNameTokeniser;

public class SimilarityCal {
	
	
	
	/**
	 * 计算余弦相似度
	 * @param s1
	 * @param s2
	 * @return
	 * @throws Exception
	 */
	public static float calVSMSimi(String s1,String s2,Hashtable<String,Float> idf) throws Exception
    {
   	    Hashtable<String,Integer> ht1=StringProcess.convertStringToVector(s1);
		Hashtable<String,Integer> ht2=StringProcess.convertStringToVector(s2);
		float similarity=0;
		float upper=0;
		float down1=0;
		float down2=0;
		Set<String> alltoken=new HashSet<String>();
		alltoken.addAll(ht1.keySet());
		alltoken.addAll(ht2.keySet());
		Iterator<String> it=alltoken.iterator();
		while(it.hasNext())
		{
			String token=it.next();
			if(ht1.keySet().contains(token)&&ht2.keySet().contains(token)&&idf.keySet().contains(token))
			{
				upper+=(Math.log(ht1.get(token))+1.0)*idf.get(token)*(Math.log(ht2.get(token))+1.0)*idf.get(token);
			}
			
		}
		Iterator<String> itht1=ht1.keySet().iterator();
		while(itht1.hasNext())
		{
			String tokeninht1=itht1.next();
			if(idf.keySet().contains(tokeninht1))
			{
				down1+=(Math.log(ht1.get(tokeninht1))+1)*idf.get(tokeninht1)*(Math.log(ht1.get(tokeninht1))+1)*idf.get(tokeninht1);
			}
		}
		Iterator<String> itht2=ht2.keySet().iterator();
		while(itht2.hasNext())
		{
			String tokeninht2=itht2.next();
			if(idf.keySet().contains(tokeninht2))
			{
				down2+=(Math.log(ht2.get(tokeninht2))+1)*idf.get(tokeninht2)*(Math.log(ht2.get(tokeninht2))+1)*idf.get(tokeninht2);
			}
		}
		if(down1!=0&&down2!=0)
		    similarity=(float) (upper/(Math.sqrt(down1)*Math.sqrt(down2)));
		return similarity;
    }
    
	
    /**
     * 计算编辑距离相似度
     * @param str1
     * @param str2
     * @return
     * @throws Exception
     */
	public static float calEditSimi(String str1,String str2) throws Exception
    {
    	str1=str1.toLowerCase();
		str2=str2.toLowerCase(); 

        int len1 = str1.length();  
        int len2 = str2.length();  

        int[][] dif = new int[len1 + 1][len2 + 1];  

        for (int a = 0; a <= len1; a++) {  
            dif[a][0] = a;  
        }  
        for (int a = 0; a <= len2; a++) {  
            dif[0][a] = a;  
        }  
      
        int temp;  
        for (int i = 1; i <= len1; i++) {  
            for (int j = 1; j <= len2; j++) {  
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {  
                    temp = 0;  
                } else {  
                    temp = 1;  
                }     
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,  
                        dif[i - 1][j] + 1);  
            }  
        }  
    
         float simi = 1 - (float) dif[len1][len2]/ Math.max(str1.length(), str2.length());  
         return simi;
    }
    
   
	
	/**
	 * 计算NGram相似度
	 * @param source
	 * @param target
	 * @param n
	 * @return
	 */
    public static  float calNGramSimi(String source,String target,int n)
    {

    	
    	final int sl = source.length();
        final int tl = target.length();
        
        if (sl == 0 || tl == 0) {
          if (sl == tl) {
            return 1;
          }
          else {
            return 0;
          }
        }

        int cost = 0;
        if (sl < n || tl < n) {
          for (int i=0,ni=Math.min(sl,tl);i<ni;i++) {
            if (source.charAt(i) == target.charAt(i)) {
              cost++;
            }
          }
          return (float) cost/Math.max(sl, tl);
        }

        char[] sa = new char[sl+n-1];
        float p[]; //'previous' cost array, horizontally
        float d[]; // cost array, horizontally
        float _d[]; //placeholder to assist in swapping p and d
        
        //construct sa with prefix
        for (int i=0;i<sa.length;i++) {
          if (i < n-1) {
            sa[i]=0; //add prefix
          }
          else {
            sa[i] = source.charAt(i-n+1);
          }
        }
        p = new float[sl+1]; 
        d = new float[sl+1]; 
      
        // indexes into strings s and t
        int i; // iterates through source
        int j; // iterates through target

        char[] t_j = new char[n]; // jth n-gram of t

        for (i = 0; i<=sl; i++) {
            p[i] = i;
        }

        for (j = 1; j<=tl; j++) {
            //construct t_j n-gram 
            if (j < n) {
              for (int ti=0;ti<n-j;ti++) {
                t_j[ti]=0; //add prefix
              }
              for (int ti=n-j;ti<n;ti++) {
                t_j[ti]=target.charAt(ti-(n-j));
              }
            }
            else {
              t_j = target.substring(j-n, j).toCharArray();
            }
            d[0] = j;
            for (i=1; i<=sl; i++) {
                cost = 0;
                int tn=n;
                //compare sa to t_j
                for (int ni=0;ni<n;ni++) {
                  if (sa[i-1+ni] != t_j[ni]) {
                    cost++;
                  }
                  else if (sa[i-1+ni] == 0) { //discount matches on prefix
                    tn--;
                  }
                }
                float ec = (float) cost/tn;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+ec);
            }
            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return 1.0f - (p[sl] / Math.max(tl, sl));
    	
    	
    }
   
    
    public static float calJaccardSimi(String[] sub1, String[] sub2) throws Exception
    {
    	Set<String> se=new HashSet<String>();
    	for(String s:sub1)
    		se.add(s.toLowerCase());
    	for(String s:sub2)
    		se.add(s.toLowerCase());
    	 
    	
    	int count=0;
    	Iterator<String> it=se.iterator();
    	while(it.hasNext())
    	{
    		String key=it.next();
    		
    		int label1=0;
    		for(String s:sub1)
    		{
    			if(s.equalsIgnoreCase(key))
    			{
    				label1=1;
    			}
    		}
    		
    		int label2=0;
    		for(String s:sub2)
    		{
    			if(s.equalsIgnoreCase(key))
    			{
    				label2=1;
    			}
    		}
    		if(label1==1&&label2==1)
    		{
    			count++;
    		}
    	}
    	
    	if(se.size()!=0)
    	    return ((float)count)/se.size();
    	else
    		 return 0.0f;
    }
    
    
    /**
     * 计算Jaccard相似度 
     * @param s1
     * @param s2
     * @return
     * @throws Exception
     */
    public static float calJaccardSimi(String s1,String s2) throws Exception
    {
    	Hashtable<String,Integer> hsAPI=StringProcess.convertStringToVectorNoSplit(s1);
 		Hashtable<String,Integer> hsSegment=StringProcess.convertStringToVectorNoSplit(s2);
 		 
    	
    	Set<String> se=new HashSet<String>();
    	se.addAll(hsAPI.keySet());
    	se.addAll(hsSegment.keySet());
    	
    	
    	int count=0;
    	Iterator<String> it=se.iterator();
    	while(it.hasNext())
    	{
    		String key=it.next();
    		if(hsAPI.keySet().contains(key)&&hsSegment.keySet().contains(key))
    		{
    			count++;
    		}
    	}
    	if(se.size()!=0)
    	    return ((float)count)/se.size();
    	else
    		 return 0.0f;
    	
    }
    
    public static float calJaccardSimiForIdentifier(IdentifierNameTokeniser b,String s1,String s2) throws Exception
    {

		String[] str1substring= {s1};
		try
		{
			str1substring=b.tokenise(s1);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
 		 
		String[] str2substring= {s2};
		try
		{
			str2substring=b.tokenise(s2);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
    	
		Set<String> hsAPI=new HashSet<String>();
		Set<String> hsSegment=new HashSet<String>();
		for(String s:str1substring)
		{
			hsAPI.add(s);
		}
		for(String s:str2substring)
		{
			hsSegment.add(s);
		}
    	Set<String> se=new HashSet<String>();
    	se.addAll(hsAPI);
    	se.addAll(hsSegment);
    	
    	
    	int count=0;
    	Iterator<String> it=se.iterator();
    	while(it.hasNext())
    	{
    		String key=it.next();
    		if(hsAPI.contains(key)&&hsSegment.contains(key))
    		{
    			count++;
    		}
    	}
    	if(se.size()!=0)
    	    return ((float)count)/se.size();
    	else
    		 return 0.0f;
    	
    }
    
    
    public static float Jaccard(String s1, String s2) throws Exception
    {
    	Set<String> s1set=new HashSet<String>();
    	Set<String> s2set=new HashSet<String>();
    	
    	String[] split1=s1.split(" ");
    	for(int i=0;i<split1.length;i++)
    	{
    		if(!split1[i].trim().isEmpty())
    		{
    			s1set.add(split1[i].trim());
    		}
    	}
    	
    	String[] split2=s2.split(" ");
    	for(int i=0;i<split2.length;i++)
    	{
    		if(!split2[i].trim().isEmpty())
    		{
    			s2set.add(split2[i].trim());
    		}
    	}
    	
    	Set<String> se=new HashSet<String>();
    	se.addAll(s1set);
    	se.addAll(s2set);
    	
    	
    	int count=0;
    	Iterator<String> it=se.iterator();
    	while(it.hasNext())
    	{
    		String key=it.next();
    		if(s1set.contains(key)&&s2set.contains(key))
    		{
    			count++;
    		}
    	}
    	if(se.size()!=0)
    	    return ((float)count)/se.size();
    	else
    		 return 0.0f;
    	
    }
    

    
    public static float calWordNetSimi(String s1,String s2)
	{
	   if(s1.contains("\""))
		   s1=s1.replace("\"", " ");
	   if(s2.contains("\""))
		   s2=s2.replace("\"", " ");
	   String news1="";
	   StringTokenizer st = new StringTokenizer(s1," .	*\n\r[]()/\\{};:~''^_\"=,<>+-!|&@#%$?");
	   while (st.hasMoreTokens()) 
	   {
		   news1=news1+" "+st.nextToken();
	   }
	   
	   
	   String news2="";
	   st = new StringTokenizer(s2," .	*\n\r[]()/\\{};:~''^_\"=,<>+-!|&@#%$?");
	   while (st.hasMoreTokens()) 
	   {
		   news2=news2+" "+st.nextToken();
	   }
	   
		String command="C:\\Users\\jingxuan\\Thanh\\bin\\Debug\\WordsMatching.exe \""+news1+"\" \""+news2+"\"";
	    float result=-1;
	    String cmd = "cmd /c "+command ;
	    
	   try {
	    Process process = Runtime.getRuntime().exec(cmd);	    
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    String line = null;	    
	    while ((line = reader.readLine()) != null)
	    {	     
	    	result=Float.parseFloat(line);
	    }
	    process.waitFor();
	    process.getOutputStream().close();	
	    process.getInputStream().close();
	    reader.close();
	   } catch (Exception e)
	   {
	    e.printStackTrace();
	   }
	   
	   return result;
	}
   
    
    
    
    public static int min(int... is)
	{  
       int min = Integer.MAX_VALUE;  
       for (int i : is) 
       {  
           if (min > i) 
           {  
               min = i;  
           }  
       }  
       return min; 
	} 
    
    
    public static Hashtable<String,Double> makeIDF(Vector<String> allSentence) throws Exception
    {
    	Hashtable<String,Integer> idf=new Hashtable<String,Integer>();
    	
    	for(String onesen:allSentence)
    	{
    		Hashtable<String,Integer> termvec=StringProcess.convertStringToVector(onesen);
    		Set<String> keyset=termvec.keySet();
    		for(String oneterm:keyset)
    		{
    			if(idf.keySet().contains(oneterm))
    			{
    				int size=idf.get(oneterm);
    				size++;
    				idf.remove(oneterm);
    				idf.put(oneterm, size);
    			}
    			else
    			{
    				idf.put(oneterm, 1);
    			}
    		}
    	}
    	
    	Hashtable<String,Double> newidf=new Hashtable<String,Double>();
    	Set<String> keys=idf.keySet();
    	for(String onekey:keys)
    	{
    		int value=idf.get(onekey);
    		double result=Math.log10(allSentence.size()*1.0/(value+1));
    		newidf.put(onekey, result);
    	}
    	
    	return newidf;
    }
    
   
    
    
    public static String Clean(String s)
    {
    	String content=s;
    	content=content.replaceAll("[\\pP‘’“”]", " ");
		content=content.replace("<", " ");
		content=content.replace(">", " ");
		content=content.replace("=", " ");
		content=content.replace("+", " ");
		content=content.replaceAll(" +", " ");
		StringBuilder sb=new StringBuilder();
		if(content.contains(" "))
		{
			String[] spli1=content.split(" ");
		    for(int j=0;j<spli1.length;j++)
		    {
			   sb.append(spli1[j].trim()+" ");
		    }
		}
		else
		{
			 sb.append(content.trim()+" ");
		}
		
		String needreturn=sb.toString().trim();
		return needreturn;
    }
    
    public static float word2vecSimi(IdentifierNameTokeniser b, String term1,String term2,Vector<Word2vec> wordvec)
    {
    	float simi=0f;
    	
		
    	float[] arrAPI=new float[200];
    	float[] arrsegment=new float[200];
    	for(int i=0;i<200;i++)
    	{
    		arrAPI[i]=0f;
    		arrsegment[i]=0f;
    	}
    	
    	
    	int count1=0;
    	  
		String[] str1substring= {term1};
		try
		{
			str1substring=b.tokenise(term1);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
	    for(int j=0;j<str1substring.length;j++)
	    {
		   String ok=str1substring[j].toLowerCase();
//		   System.err.println(ok);
		   for(int i=0;i<wordvec.size();i++)
		   {
			   if(ok.equals(wordvec.get(i).getWord()))
			   {
				   Vector<Float> vec=wordvec.get(i).getVec();
				   if(vec.size()==200)
				   {
					   for(int k=0;k<200;k++)
					   {
						   arrAPI[k]=arrAPI[k]+vec.get(k);
						   
					   }
					   //System.out.print(ok+"  ");
					   count1++;
					   break;
				   }
			   }
		   }
	    }
	    	    
	    int count2=0;
	    
		
	    String[] str2substring= {term2};
		try
		{
			str2substring=b.tokenise(term2);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
	    for(int j=0;j<str2substring.length;j++)
	    {

		   String ok=str2substring[j].toLowerCase();
		   
		   for(int i=0;i<wordvec.size();i++)
		   {
			   if(ok.equals(wordvec.get(i).getWord()))
			   {
				   Vector<Float> vec=wordvec.get(i).getVec();
				   if(vec.size()==200)
				   {
					   for(int k=0;k<200;k++)
					   {
						   arrsegment[k]=arrsegment[k]+vec.get(k);
						  
					   }
					   count2++;
					   break;
				   } 
			   }
		   }
	    }

	    
	    float up=0f;
		float down1=0f;
		float down2=0f;
	
		if(count1!=0&&count2!=0)
		{
			for(int i=0;i<200;i++)
			{
				up+=(arrAPI[i]/count1)*(arrsegment[i]/count2);
				down1+=(arrAPI[i]/count1)*(arrAPI[i]/count1);
				down2+=(arrsegment[i]/count2)*(arrsegment[i]/count2);
			}
			
			if(down1!=0&&down2!=0)
			{
				simi=(float) (up/(Math.sqrt(down1)*Math.sqrt(down2)));
			}
		}
    	
    	return simi;
    }

    
    
}
