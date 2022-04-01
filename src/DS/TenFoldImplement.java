package classifierRun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;
import DS.Instan;
import feature.OwnFeature;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class TenFoldImplement {
	
	 public static void main(String args[]) throws Exception
	 {
		 runTenFoldCrossValidation();
	 }
	
	public static void runTenFoldCrossValidation() throws Exception
	{
		BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\Results\\result_tenfoldimplementation.csv",true));
		String groupID="G1,G2,G3,G4,G5";
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\FinalProjects.csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			String project=line.substring(0, line.indexOf(","));
			 
			Vector<Instan> featurelist=obtainData(project,groupID);
			 
			
		    Vector<Double> allp=new Vector<Double>();
		    Vector<Double> allr=new Vector<Double>();
		    Vector<Double> allf=new Vector<Double>();
		  
		 
			for(int i=0;i<10;i++)
			{
				Vector<Integer> trainingid=BalancedLearning.splitWholeDataset(featurelist.size(),i);
				Vector<Instan> trainingfeature=new Vector<Instan>();
				Vector<Instan> testfeature=new Vector<Instan>();
				for(int k=0;k<featurelist.size();k++)
				{
					if(trainingid.contains(k))
					{
						trainingfeature.add(featurelist.get(k));
					}
					else
					{
						testfeature.add(featurelist.get(k));
					}
				}
			 
				  String filepathandname="D:\\project\\IdentifierRename\\Results\\arff\\"+project+"_train.arff";
			      createArff(trainingfeature,filepathandname); 
				 
			      
			      String filepathandname2="D:\\project\\IdentifierRename\\Results\\arff\\"+project+"_test.arff";
			      createArff(testfeature,filepathandname2); 
			      
			      
			      File inputFile = new File(filepathandname);
				  ArffLoader atf = new ArffLoader();
				  atf.setFile(inputFile);
				  Instances traininstances = atf.getDataSet(); 
				  traininstances.setClassIndex(traininstances.numAttributes()-1);
				  				  
				  File outputFile=new File(filepathandname2);
				  ArffLoader load=new ArffLoader();
				  load.setFile(outputFile);
				  Instances testinstances=load.getDataSet();
				  testinstances.setClassIndex(testinstances.numAttributes()-1);
				 
				 
				  AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
				  ASEvaluation pca=new InfoGainAttributeEval();
				  ASSearch rank=new Ranker();
				  Classifier base = new RandomForest();			
				  classifier.setClassifier(base);
				  classifier.setEvaluator(pca);
				  classifier.setSearch(rank);
				  

				  classifier.buildClassifier(traininstances);
				  Vector<Integer> pre=new Vector<Integer>();
				  Vector<Integer> gold=new Vector<Integer>();
				  for(int k=0;k<testinstances.numInstances();k++)
				  {
					    Instance oneins=testinstances.instance(k);
					    int goldid=(int)oneins.classValue();
					    int predicted = 0;					  
					    predicted =(int) classifier.classifyInstance(oneins);											
						pre.add(predicted);
						gold.add(goldid);
	
			      }

				    
				    int inter=0;
					int presize=0;
					int goldsize=0;
					if(pre.size()==gold.size())
					{
						for(int k=0;k<pre.size();k++)
						{
							if(pre.get(k)==1&&gold.get(k)==1)
								inter++;
							if(pre.get(k)==1)
								presize++;
							if(gold.get(k)==1)
								goldsize++;
						}
					}
					double p=0.0;
					double r=0.0;
					double f=0.0;
					if(presize!=0)
					    p=(double)inter/presize;
					if(goldsize!=0)
					    r=(double)inter/goldsize;
					if((p+r)!=0)
					    f=2*p*r/(p+r);

				  allp.add(p);
				  allr.add(r);
				  allf.add(f);
				  
			}
			
			System.out.println(project+","+averagedouble(allp)+","+averagedouble(allr)+","+averagedouble(allf));
			bw.write(project+","+averagedouble(allp)+","+averagedouble(allr)+","+averagedouble(allf));
		    bw.newLine();
			

		}
		br.close();
		bw.close();
		
      
		   
		  
	   }
	
	public static Vector<String> obtainRealIdentifier(String project) throws Exception
	{
		Vector<String> allidentifier=new Vector<String>();
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
					String finalidentifier=category+"_"+identifier+","+label;
					allidentifier.add(finalidentifier);
				}
			}
		}
		read.close();
		return allidentifier;
	}
	
	
	
	
	 public static Vector<Instan> obtainData(String project, String groupID) throws Exception
	  {
		 Vector<Instan> allins=new Vector<Instan>();
		   
		   
		   if(!groupID.contains(","))
		   {
			   int id=0;
			   BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\feature_new\\"+project+"_"+groupID+".csv"));
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
				   BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\feature_new\\"+project+"_"+s+".csv"));
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
//							   System.err.println(featurelist.size());
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
	  
	 public static double averagedouble(Vector<Double> all)
	  {
		  double sum=0;
		  for(double one:all)
		  {
			  sum+=one;
		  }
		  if(all.size()>0)
			  return sum/all.size();
		  else
			  return 0;
		  
	  }
	 
	
	

}
