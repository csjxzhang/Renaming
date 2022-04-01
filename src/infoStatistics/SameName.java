package infoStatistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Set;

import feature.OwnFeature;


public class SameName {
	
	public static void main(String args[]) throws Exception
	{
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\FinalProjects.csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			String project=line.substring(0, line.indexOf(","));
			
			Hashtable<String,Integer> hs=new Hashtable<String,Integer>();
			int noofid=0;
			BufferedReader read=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\GoldenSet\\"+project+".csv"));
			String oneidr="";
			while((oneidr=read.readLine())!=null)
			{
			
				String[] split=oneidr.split(",");
				if(split.length>=8)
				{	
					noofid++;
					String label=split[0];
					String cate=split[1];
					int category=-1;
					if(OwnFeature.isInteger(cate))
					{
						category=Integer.parseInt(cate);
					}					
					String identifier=split[7];
					String newid=category+"_"+identifier;
					if(hs.keySet().contains(newid))
					{
						int value=hs.get(newid);
						int count1=value/10000;
						int count2=value%10000;
						if(label.equals("1"))
						{
							count1++;
							
						}
						else
						{
							count2++;
						}
						int newvalue=count1*10000+count2;
						hs.remove(newid);
						hs.put(newid, newvalue);
					}
					else
					{
						int value=0;
						if(label.equals("1"))
							value=1*10000;
						else
							value=1;
						hs.put(newid, value);
					}
				 				
				}
			}
			read.close();
			
			int spe=0;
			BufferedWriter bw=new BufferedWriter(new FileWriter("C:\\Users\\jxzha\\Desktop\\result\\"+project+".csv"));
			Set<String> keys=hs.keySet();
			for(String onekey:keys)
			{
				int value=hs.get(onekey);
				int count1=value/10000;
				int count2=value%10000;
				bw.write(onekey+","+count1+","+count2+","+(count1*1.0/(count1+count2))+","+(count2*1.0/(count1+count2)));
				bw.newLine();
				if(count1==0||count2==0)
					spe++;
			}
			bw.close();
			System.out.println(project+","+noofid+","+hs.size()+","+(hs.size()*1.0/noofid)+","+(1-(hs.size()*1.0/noofid))+","+spe*1.0/hs.size());
		}
		br.close();
	}

}