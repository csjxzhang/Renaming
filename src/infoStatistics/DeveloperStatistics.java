package infoStatistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DeveloperStatistics {


	public static void InfoStatistic() throws Exception
	{
		String indicator="class=\"link-gray-dark no-underline \"> Contributors <span title=";
		int id=1;
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\FinalProjects.csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			String project=line.substring(0, line.indexOf(","));
			System.out.print(id+","+project+","); 
			
			int contributor=1;
			
			File f=new File("D:\\project\\IdentifierStyle\\data\\GitWebInfo\\"+project+".html");
			if(!f.exists())
			{
				System.out.println(contributor+",error");
			}
			BufferedReader reader=new BufferedReader(new FileReader("D:\\project\\IdentifierStyle\\data\\GitWebInfo\\"+project+".html"));
			String content="";
			while((content=reader.readLine())!=null)
			{
				if(content.contains(indicator))
				{
					content=content.substring(0, content.indexOf("</span> </a></h2>"));
					content=content.substring(content.lastIndexOf(">")+1, content.length()).trim();
					contributor=Integer.parseInt(content);
					
				}
			}
			reader.close();
			
			
			System.out.print(contributor+",");
			
			int count=0;
			BufferedReader read=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\GoldenSet\\"+project+".csv"));
			String oneid="";
			while((oneid=read.readLine())!=null)
			{
				System.out.println(oneid);
				
				count++;
			}
			read.close();
			System.out.println(count);
			id++;
			
		}
		br.close();
	}
	
}
