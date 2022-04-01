package classifierRun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;
import DS.Instan;
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

public class TestPrediction {
	
	
	public static void TestClassifier(String classstr) throws Exception
	{
		
		String groupID="G1,G2,G3,G4,G5";
		Classifier base = new RandomForest();	
		String basestring="RandomForest";
		  		
		BufferedWriter write=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\RQ\\"+classstr+"_ClassifierSummary.csv",true));
		BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\RQ\\"+classstr+"_TestDifferentClassifier.csv",true));
		Vector<Double> allpre=new Vector<Double>();
		Vector<Double> allrec=new Vector<Double>();
		Vector<Double> allfm=new Vector<Double>();
		
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data_new\\"+classstr+".csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			String project=line.substring(0, line.indexOf(","));
			 
			Vector<Instan> featurelist=TenFoldImplement.obtainData(project,groupID);
			
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
			      TenFoldImplement.createArff(trainingfeature,filepathandname); 
				 
			      
			      String filepathandname2="D:\\project\\IdentifierRename\\Results\\arff\\"+project+"_test.arff";
			      TenFoldImplement.createArff(testfeature,filepathandname2); 
			      
			      
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
			
			System.out.println(basestring+","+project+","+TenFoldImplement.averagedouble(allp)+","+TenFoldImplement.averagedouble(allr)+","+TenFoldImplement.averagedouble(allf));
			bw.write(basestring+","+project+","+TenFoldImplement.averagedouble(allp)+","+TenFoldImplement.averagedouble(allr)+","+TenFoldImplement.averagedouble(allf));
		    bw.newLine();
			
		    allpre.add(TenFoldImplement.averagedouble(allp));
		    allrec.add(TenFoldImplement.averagedouble(allr));
		    allfm.add(TenFoldImplement.averagedouble(allf));

		}
		br.close();
		bw.close();
		
        write.write(basestring+","+TenFoldImplement.averagedouble(allpre)+","+TenFoldImplement.averagedouble(allrec)+","+TenFoldImplement.averagedouble(allfm));
        write.newLine();
		   
        write.close();
		  
	   }

}
