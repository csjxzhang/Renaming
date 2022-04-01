package infoStatistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;

public class FileLevel {
	
	public static void main(String args[]) throws Exception
	{
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\FinalProjects.csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			String project=line.substring(0, line.indexOf(","));
			
			HashSet<String> allfile=new HashSet<String>();
			HashSet<String> changedfile=new HashSet<String>();
			
			BufferedReader read=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\GoldenSet\\"+project+".csv"));
			String oneidr="";
			while((oneidr=read.readLine())!=null)
			{
			
				String[] split=oneidr.split(",");
				if(split.length>=8)
				{	
					String file=split[3];
					String label=split[0];  //label
					allfile.add(file);
					if(label.equals("1"))
						changedfile.add(file);
					 
				 				
				}
			}
			read.close();
			
			BufferedWriter bw=new BufferedWriter(new FileWriter("C:\\Users\\jxzha\\Desktop\\result\\"+project+".csv"));
			for(String onechange:changedfile)
			{
				int count1=0,count2=0;
				read=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\GoldenSet\\"+project+".csv"));
				oneidr="";
				while((oneidr=read.readLine())!=null)
				{
				
					String[] split=oneidr.split(",");
					if(split.length>=8)
					{	
						String file=split[3];
						String label=split[0];  //label
						if(onechange.equals(file))
						{
							if(label.equals("1"))
								count1++;
							else
								count2++;
						}
		 				
					}
				}
				read.close();
				bw.write(onechange+","+count1+","+count2+","+(count1*1.0/(count1+count2)));
				bw.newLine();
				
			}
			bw.close();
			
			System.out.println(project+","+allfile.size()+","+changedfile.size()+","+(1.0*changedfile.size()/allfile.size()));
			 
		}
		br.close();
	}


}
