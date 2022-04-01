package classifierRun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import DS.Instan;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class BalancedLearning {
	

	public static void RunBalancedLearning() throws Exception
	{
		String groupID="G1,G2,G3,G4,G5";
		BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\Results\\result_balanced.csv"));
		int id=0;
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\FinalProjects.csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			String project=line.substring(0, line.indexOf(","));
			System.out.println(id+"   "+project);
			Vector<Instan> allins=InitialDataset(project, groupID);
			
			dumpfile(project, allins);
			
			for(double x=0.00;x<=1.01;x+=0.10)
			{
				Vector<Float> allpre=new Vector<Float>();
				Vector<Float> allrecall=new Vector<Float>();
				Vector<Float> allfm=new Vector<Float>();
				
				for(int experid=0;experid<=9;experid++)
				{
					
					File f=new File("D:\\project\\IdentifierRename\\Results\\arff\\"+project+"\\"+experid+"\\");
					File[] dumpf=f.listFiles();
					int foldsize=dumpf.length-1;
					 
					
					Vector<Classifier> classifiers=new Vector<Classifier>();
					
				 
					for(int i=0;i<foldsize;i++)
					{
						
						File inputFile = new File("D:\\project\\IdentifierRename\\Results\\arff\\"+project+"\\"+experid+"\\training_"+i+".arff");
				        ArffLoader atf = new ArffLoader();
				        atf.setFile(inputFile);
				        Instances instancesTrain = atf.getDataSet();  
				        instancesTrain.setClassIndex(instancesTrain.numAttributes()-1); 				 		        
				        Classifier m_classifier=new RandomForest(); 			        	
			        	m_classifier.buildClassifier(instancesTrain);  
						classifiers.add(m_classifier);
						
					}
					
		 
					File inputFile = new File("D:\\project\\IdentifierRename\\Results\\arff\\"+project+"\\"+experid+"\\test.arff");
					ArffLoader atf = new ArffLoader();   
					atf.setFile(inputFile);
				    Instances instancesTest = atf.getDataSet();  
				    instancesTest.setClassIndex(instancesTest.numAttributes()-1);  
				    
				   		   	
			    	Vector<Integer> gold=new Vector<Integer>();
				    Vector<Integer> pre=new Vector<Integer>();
			    	for(int i=0;i<instancesTest.numInstances();i++)
			    	{
			    		if(instancesTest.instance(i).classValue()==1)
			    		{
			    			gold.add(i); 
			    		}
			    		
		
			    		int sum=0;
			        	for(Classifier m_classifier:classifiers)
			        	{
			        		int predicted = (int) m_classifier.classifyInstance(instancesTest.instance(i));
			        		sum+=predicted;
			        	}
			        	if(sum>=foldsize*x) 
			        	{
			        		pre.add(i);
			        	}
			    	}
		    
			        HashSet<Integer> all=new HashSet<Integer>();
			        all.addAll(gold);
			        all.addAll(pre);
			        
			        int inter=0;
			        Iterator<Integer> it=all.iterator();
			        while(it.hasNext())
			        {
			        	int key=it.next();
			        	if(gold.contains(key)&&pre.contains(key))
			        	{
			        		inter++;
			        	}
			        }
			        
			        double precision=((double)inter)/pre.size();
			        double recall=((double)inter)/gold.size();
			        double fmeasure=2*precision*recall/(precision+recall);
			        if(Double.isNaN(precision))
			        	precision=0.0;
			        if(Double.isNaN(recall))
			        	recall=0.0;
			        if(Double.isNaN(fmeasure))
			        	fmeasure=0.0;
			        
			        allpre.add((float) precision);
			        allrecall.add((float) recall);
			        allfm.add((float) fmeasure);   
				}
			
			
			   float finalpre=averageofVector(allpre);
			   float finalrec=averageofVector(allrecall);
			   float finalfm=averageofVector(allfm);
			   bw.write(project+","+x+","+finalpre+","+finalrec+","+finalfm);
			   bw.newLine();
			}
			id++;
		}
		br.close();
		bw.close();
	}
	
	
	public static void dumpfile(String project, Vector<Instan> allins) throws Exception
	{		 
		for(int experid=0;experid<=9;experid++)
		{
			File f=new File("D:\\project\\IdentifierRename\\Results\\arff\\"+project+"\\"+experid+"\\");
			f.mkdirs();
			
			Vector<Integer> trainingid=splitWholeDataset(allins.size(),experid);
			Vector<Instan> training=new Vector<Instan>();
			Vector<Instan> test=new Vector<Instan>();
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
			
			Vector<Vector<Instan>> fold=imbalanceSample(training);
			for(int i=0;i<fold.size();i++)
			{
				Vector<Instan> onefold=fold.get(i);
				createArff(onefold,"D:\\project\\IdentifierRename\\Results\\arff\\"+project+"\\"+experid+"\\training_"+i+".arff");			
			}
			
			createArff(test,"D:\\project\\IdentifierRename\\Results\\arff\\"+project+"\\"+experid+"\\test.arff");
			
	   }
		 
	}
	
	 public static void createArff(Vector<Instan> allins,String filepathandname) throws Exception 
	  {
		   
		   BufferedWriter bw=new BufferedWriter(new FileWriter(filepathandname));
		   bw.write("@relation C__Users_sky_Desktop_label_a\n\n");

		   Vector<Float> prop=allins.get(0).getFeature();
		   for(int i=0;i<prop.size();i++)
		   {
			   bw.write("@attribute feature"+i+" numeric\n");
		   }
		   bw.write("@attribute label {0,1}\n\n");
		   bw.write("@data\n");
		   
		  for(int i=0;i<allins.size();i++)
		  {
			  Instan oneins=allins.get(i);
			  int label=oneins.getLabel();
			  Vector<Float> pro=oneins.getFeature();
			  for(int j=0;j<pro.size();j++)
				  bw.write(pro.get(j)+",");
			  bw.write(label+"");
			  bw.newLine();
		  }
		  bw.close();
	   }
	
	
	
	public static Vector<Vector<Instan>> imbalanceSample(Vector<Instan> allsen) throws Exception
	{
		Vector<Vector<Instan>> result=new Vector<Vector<Instan>>();
		Vector<Integer> pos=new Vector<Integer>();
		Vector<Integer> neg=new Vector<Integer>();
		
		for(int i=0;i<allsen.size();i++)
		{
			Instan onesen=allsen.get(i);
			if(onesen.getLabel()==1)
				pos.add(i);
			else
				neg.add(i);
		}
		
		int fold=neg.size()/pos.size();
		
		if(fold==0)
		{
			Vector<Instan> onefold=new Vector<Instan>();
			onefold.addAll(allsen);
			result.add(onefold);
		}
		else
		{
			int foldsize=neg.size()/fold;
			for(int i=0;i<fold;i++)
			{
				Vector<Instan> onefold=new Vector<Instan>();
				for(int j=i*foldsize;j<(i+1)*foldsize;j++)
				{
					int senid=neg.get(j);
					onefold.add(allsen.get(senid));				
				}
				for(int senid:pos)
				{
					onefold.add(allsen.get(senid));
				}
				result.add(onefold);
				
			}
		}
		return result;
		
	}
	
	
	public static Vector<Instan> InitialDataset(String project, String groupID) throws Exception
	{
		 Vector<Instan> allins=new Vector<Instan>();
		 if(!groupID.contains(","))
		 {
			   int id=0;
			   BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\feature\\"+project+"_"+groupID+".csv"));
			   String line="";
			   while((line=br.readLine())!=null)
			   {
				   String[] split=line.split(",");
				   Vector<Float> feature=new Vector<Float>();
				   int label=Integer.parseInt(split[split.length-1]);
				   for(int i=0;i<split.length-1;i++)
				   {
					   feature.add(Float.parseFloat(split[i]));
				   }
				   Instan oneins=new Instan(id, label, feature);
				   allins.add(oneins);
				   id++;
			   }
			   br.close();
		  }
		  else
		  {
			   String[] diffgroups=groupID.split(",");
			   int loop=0;
			   for(String s:diffgroups)
			   {
				   
				   int id=0;
				   BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\feature\\"+project+"_"+s+".csv"));
				   String line="";
				   while((line=br.readLine())!=null)
				   {
					   String[] split=line.split(",");
					   Vector<Float> feature=new Vector<Float>();
					   int label=Integer.parseInt(split[split.length-1]);
					   for(int i=0;i<split.length-1;i++)
					   {
						   feature.add(Float.parseFloat(split[i]));
					   }
					   if(loop>0)
					   {
						   Instan currentins=allins.get(id);
						   if(currentins.getLabel()==label)
						   {
							   Vector<Float> featurelist=currentins.getFeature();
							   featurelist.addAll(feature);
							   allins.remove(id);
							   allins.add(id, new Instan(id,label,featurelist));
						   }
						   else
						   {
							   System.err.println("label inconsistent");
						   }
						   
					   }
					   else
					   {
						   Instan oneins=new Instan(id, label, feature);
						   allins.add(oneins);
					   }
					   id++;
				   }
				   br.close();
				   loop++;
			  }
		 }
		 return allins;
	}
	
	
	public static Vector<Integer> splitWholeDataset(int size, int yushu) throws Exception
	{
		Vector<Integer> trainingid=new Vector<Integer>();
		for(int i=0;i<size;i++)
		{
			if(i%10!=yushu)
				trainingid.add(i);
		}
		return trainingid;
	}
	
	
	public static float averageofVector(Vector<Float> v)
	  {
		  if(v.size()==0)
			  return 0.0f;
		  else
		  {
			  float sum=0.0f;
			  for(int i=0;i<v.size();i++)
			  {
				  sum+=v.get(i);
			  }
			  return sum/v.size();
		  }
	  }

}
