package classifierRun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import DS.Instan;
import weka.filters.unsupervised.attribute.Normalize;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IB1;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;


public class ClassifierRun {
	
	  public static void main(String args[]) throws Exception
	  {
		  TenFoldCrossValidation();
	  }
	 
	
	public static void TenFoldCrossValidation() throws Exception
	{
		BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\Results\\result.csv"));
		String groupID="G1,G2,G3,G4,G5";
		BufferedReader br=new BufferedReader(new FileReader("D:\\project\\IdentifierRename\\data\\FinalProjects.csv"));
		String line="";
		while((line=br.readLine())!=null)
		{
			String project=line.substring(0, line.indexOf(","));
			createArff(project, groupID);

			File inputFile = new File("D:\\project\\IdentifierRename\\Results\\arff\\"+project+".arff");
			ArffLoader atf = new ArffLoader();
			atf.setFile(inputFile);
			Instances instances = atf.getDataSet(); 
			instances.setClassIndex(instances.numAttributes()-1);

			Normalize norm=new Normalize();
			norm.setScale(1.0);  
		    norm.setTranslation(0.0);  
			norm.setIgnoreClass(true);  
			norm.setInputFormat(instances);
			instances = Filter.useFilter(instances, norm); 
	       
			
			Classifier m_classifier = new RandomForest();  			
			Evaluation eval = new Evaluation(instances); 
		    eval.crossValidateModel(m_classifier, instances, 10, new Random(1));    //10 fold cross validation
		    System.out.println(project+","+eval.precision(1)+","+eval.recall(1)+","+eval.fMeasure(1)+","+eval.precision(0)+","+eval.recall(0)+","+eval.fMeasure(0));
		    bw.write(project+","+eval.precision(1)+","+eval.recall(1)+","+eval.fMeasure(1)+","+eval.precision(0)+","+eval.recall(0)+","+eval.fMeasure(0));
		    bw.newLine();
			
		}
		br.close();
		bw.close();
		
      
		   
		  
	   }
	
	
	   public static String TrainandTest(Vector<Instan> testextendfeaturelist,Vector<Instan> mergedtrainfeaturelist) throws Exception
	   {

	        File inputFile = new File("F:\\directive\\classify\\cross_train.arff");
	        ArffLoader atf = new ArffLoader();
	        atf.setFile(inputFile);
	        Instances instancesTrain = atf.getDataSet();  
	        instancesTrain.setClassIndex(instancesTrain.numAttributes()-1); 
	        
	        inputFile = new File("F:\\directive\\classify\\cross_test.arff");
	        atf.setFile(inputFile);
	        Instances instancesTest = atf.getDataSet();  
	        instancesTest.setClassIndex(instancesTest.numAttributes()-1);  
	     
	        
	        if(instancesTest.numInstances()==testextendfeaturelist.size()&&instancesTrain.numInstances()==mergedtrainfeaturelist.size())
	        {
	        	RandomForest m_classifier = new RandomForest();
	        	m_classifier.setNumTrees(50);
	        	
	        	m_classifier.buildClassifier(instancesTrain);  
	        
	        	 Evaluation eval = new Evaluation(instancesTrain);
	        	 eval.evaluateModel(m_classifier, instancesTest);
	        	
	        	 System.out.println(eval.toSummaryString("\nResults\n======\n", false));
	        	
	        	Vector<Integer> gold=new Vector<Integer>();
	        	for(int i=0;i<testextendfeaturelist.size();i++)
	        	{
	        		if(testextendfeaturelist.get(i).getLabel()==1)
	        		{
	        			gold.add(i);
	        		}
	        	}
	        	
	        	
	        	
	        	Vector<Integer> pre=new Vector<Integer>();
		        for (int i = 0; i < instancesTest.numInstances(); i++)
		        {
		        	
		            int predicted = (int) m_classifier.classifyInstance(instancesTest.instance(i));
		            if(predicted==1)
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
		        if(Double.isNaN(fmeasure))
		        	fmeasure=0.0;
		        
		        String result=precision+","+recall+","+fmeasure;
		        return result;
	        }        
	        else
	        {
	        	return "";
	        }
		   
	   }
	   
	   
	   public static Classifier TrainandTest(Vector<Instan> mergedtrainfeaturelist,int classifierid,int treenum) throws Exception
	   {
		   
	        File inputFile = new File("F:\\directive\\classify\\cross_train.arff");
	        ArffLoader atf = new ArffLoader();
	        atf.setFile(inputFile);
	        Instances instancesTrain = atf.getDataSet();  
	        instancesTrain.setClassIndex(instancesTrain.numAttributes()-1); 
	        
	        Classifier m_classifier = null;
	        if(classifierid==0)
	        {
	        	m_classifier=new RandomForest();
	        	((RandomForest) m_classifier).setNumTrees(treenum);
	        	
	        	
	        }
	        if(classifierid==1)
	        {
	        	m_classifier=new NaiveBayes();
	        }
	        if(classifierid==2)
	        {
	        	m_classifier=new J48();
	        }
	        if(classifierid==3)
	        {
	        	m_classifier=new SMO();
	        }
	        if(classifierid==4)
	        {
	        	m_classifier=new AdaBoostM1();
	        }
	        if(classifierid==5)
	        {
	        	m_classifier=new Bagging();
	        } 
	        if(classifierid==6)
	        {
	        	m_classifier=new Logistic();
	        }
	        if(classifierid==7)
	        {
	        	m_classifier=new IB1();
	        }
	        
	        m_classifier.buildClassifier(instancesTrain); 
	        
	        return m_classifier;
	        		   
	   }
	   
	   public static void createArff(String project, String groupID) throws Exception 
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
		   
			   
		   BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\project\\IdentifierRename\\Results\\arff\\"+project+".arff"));
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

}
