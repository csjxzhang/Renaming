package infoStatistics;

import java.io.BufferedReader;
import java.io.FileReader;

import feature.OwnFeature;

public class RenameInEachGranularity {
	
	public static void main(String args[]) throws Exception
	{
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\FinalProjects.csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			int count1=0,sum1=0;
			int count2=0,sum2=0;
			int count3=0,sum3=0;
			int count4=0,sum4=0;
			int count5=0,sum5=0;
			
			
			String project=line.substring(0, line.indexOf(","));
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
						if(category==1)
						{
							sum1++;
							if(label.equals("1"))
								count1++;
						}
						else if(category==2)
						{
							sum2++;
							if(label.equals("1"))
								count2++;
						}
						else if(category==3)
						{
							sum3++;
							if(label.equals("1"))
								count3++;
						}
						else if(category==4)
						{
							sum4++;
							if(label.equals("1"))
								count4++;
						}
						else if(category==5)
						{
							sum5++;
							if(label.equals("1"))
								count5++;
						}
					}
				}
			}
			read.close();
			System.out.println(project+","+count1+","+sum1+","+(count1*1.0/sum1)+","+count2+","+sum2+","+(count2*1.0/sum2)+","+count3+","+sum3+","+(count3*1.0/sum3)+","+count4+","+sum4+","+(count4*1.0/sum4)+","+count5+","+sum5+","+(count5*1.0/sum5));
			
		}
		br.close();
	}

}
