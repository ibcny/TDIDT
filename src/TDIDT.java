
/**
 * @author  Can Yuce 
 * @author  Oguz Uzman 
 */

import java.io.*;
import java.util.*;

public class TDIDT {

	// Traing/test sets
    private ArrayList<Boolean[]> trainingSet = null;
	private ArrayList<Boolean[]> testSet = null;

    // test and train file path/name
    public static String trainFile = "data/SPECT.train";
    public static String testFile = "data/SPECT.test";
    public static String infoGainFileName = "infogain.txt";
    public static int attributeCount;
    
    public static int LABEL_INDEX = 0;
    public static PrintWriter writer;
    private static int testCount = 10;  // number of repetition
    public static PrintWriter informationGainWriter;  // write
        
    /**
	 * @param args
	 * 		args[0] : Training file path/name 
	 */
	public static void main(String[] args) {
		
		TDIDT tdidt = new TDIDT();	
		// parse test and training files
		ArrayList<Boolean[]> SPECTtrainingSet = TDIDT.parseDataFile(TDIDT.trainFile);
		ArrayList<Boolean[]> SPECTtestSet     = TDIDT.parseDataFile(TDIDT.testFile);
		ArrayList<Boolean[]> combinedList     = new ArrayList<Boolean[]>();
		
		System.out.println("**Information Gain calculations will "
				+ "be written to: \"" + infoGainFileName+"\"\n");
		try {
			// create output.txt and infogain.txt
			TDIDT.writer = new PrintWriter("output.txt");
			TDIDT.informationGainWriter = new PrintWriter(infoGainFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TDIDT.writer.close();
			TDIDT.informationGainWriter.close();
		}
		
		System.out.println("Starting classifier..\n");
		Node root = null;
		
		//combine two data sets into one
		combinedList.addAll(SPECTtrainingSet);
		combinedList.addAll(SPECTtestSet);
		double overallAccuracy = 0;
		
		// try ten times
		for(int i=1 ; i<=testCount  ; i++){
			System.out.println("Experiment Repetition #" + i );
    		// information gains calculated in each test are written to another file.
			TDIDT.informationGainWriter.println("----------------------------------------------------");
			TDIDT.informationGainWriter.println("Calculation of the information gain for test #" + i);
			TDIDT.informationGainWriter.println("----------------------------------------------------");
			
			Node.nodeCnt = 0;
			//randomize the combine data set
			TDIDT.shuffleArrayList(combinedList); 
			int trainSize = (int) Math.floor(((double)combinedList.size())*2/3);
			int testSize  = (int) Math.floor(((double)combinedList.size())*1/3);
			
			// divide training/test data values
			tdidt.setTrainingSet( new ArrayList<Boolean[]>(combinedList.subList(0, trainSize)) );
			tdidt.setTestSet( new ArrayList<Boolean[]> (combinedList.subList(trainSize, trainSize+testSize)));	
			// build decision tree
			root = tdidt.buildTree(tdidt.getTrainingSet());
			overallAccuracy += tdidt.testClassifier(root);	
			
			TDIDT.writer.println("-------------------------------------------------------------");
			writer.println(      "Structure of the tree built for the training set: shuffle #" + i);
			TDIDT.writer.println("-------------------------------------------------------------");
			
			//pre-order traversal of the tree
			tdidt.traversePreOrder(root, false);
			TDIDT.writer.println("\n");
		}		
		
		overallAccuracy /= testCount;
		System.out.println("\n------------------------------------------------------");
		System.out.println("Overall accuracy for \"" + testCount+ "\" tests is " + overallAccuracy);
		System.out.println("------------------------------------------------------\n");
		
		TDIDT.writer.close();
		TDIDT.informationGainWriter.close();
		System.out.println("Exiting..");
	}
	
	/** Shuffle the given array list using "Fisher-Yates" algorithm
	 *  @list : list to be shuffled
	 */
	public static void shuffleArrayList(ArrayList<Boolean[]> list) {
		long seed = System.nanoTime();
		Random rnd = new Random(seed);
		int size = list.size();
		Object arr[] = list.toArray();
		int nextRnd;
		
		// Shuffle array
		for (int i=size; i>1; i--){
		    Object t = arr[i-1];
		    nextRnd = rnd.nextInt(i);
		    arr[i-1] = arr[nextRnd];
		    arr[nextRnd] = t;
		}
			
		for (int i=0; i < size; i++)
			list.set(i, (Boolean[]) arr[i]);
	}

	/** @return false if the entry is predicted wrong, 
	 *         otherwise @return true
	 * */
	private boolean classifyEntry(Boolean[] entry, Node root){
		boolean labelVal = entry[0]; // first index of the entry is label value
		
		while(root.getLeftChild() != null) {
			boolean attrVal = entry[root.getAttributeNo()+1];
			if(attrVal == true)
				root = root.getLeftChild();
			else
				root = root.getRightChild();
		}	
		return (root.getLabel() == labelVal);
	}
	
	/**
	 * @return accuracy of the classifier for the current test set
	 */
	private double testClassifier(Node root) {
		int success = 0;
		int testSetSize = this.testSet.size();
		double accuracy;
		
		Iterator<Boolean[]> listIterator = this.testSet.iterator();
		while (listIterator.hasNext()) {
			Boolean[] entry = listIterator.next();
			if( classifyEntry(entry, root) )
				success++;
		}
		accuracy = (double)success/testSetSize;
		System.out.println("Accuracy: " + (double)success/testSetSize);
		System.out.println("");
		
		return accuracy;
	}

	/** traverse the tree and dump the nodes and connections
	 * to output file
	 */
	private void traversePreOrder(Node root, boolean trueChild) {
		if(root == null) 
			 return;
		
		if(root.isLeaf() == false){
			 TDIDT.writer.println( root.getNodeId() + " " +
					 			   ((trueChild==true) ? "yes" : "no")
					 			   + " " + root.getAttributeNo() + 
					 			   " " + root.getLeftChild().getNodeId() +
					 			   " " + root.getRightChild().getNodeId() );
		}
		else{
			TDIDT.writer.println( root.getNodeId() + " " +
						 			   ((trueChild==true) ? "yes" : "no")
						 			   + " " + root.getLabel()+ 
						 			   " " + -1 +
						 			   " " + -1 );
			 }
		
		traversePreOrder( root.getLeftChild(), true ); 
		traversePreOrder( root.getRightChild(), false );
	}	

	/**
	 * @param parse the given file and fetch data entries to an arraylist
	 * @return
	 */
	public static ArrayList<Boolean[]> parseDataFile(String filename){
		ArrayList<Boolean[]> trainingSet = new ArrayList<Boolean[]>();

	    try{
	        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
	        String line = null;
	        String delims = ",";
	        TDIDT.attributeCount = Integer.parseInt((line = bufferedReader.readLine()));
	        
	        while ((line = bufferedReader.readLine()) != null) {
			    Boolean[] entry = new Boolean[TDIDT.attributeCount+1];
		        String[] tokens = line.split(delims);
			    for(int i = 0; i<=attributeCount; ++i)
			    	entry[i] = ( Integer.parseInt(tokens[i]) != 0);
			    trainingSet.add(entry);
	        }
	        bufferedReader.close();
	    }
    	catch(FileNotFoundException e) { 
    		System.out.println("File Not Exists");
    		System.err.println(e);
    		System.exit(-1);
    	}
    	catch(ArrayIndexOutOfBoundsException e) { 
    		System.err.println("Corrupted/Wrond classification data file");
    		System.err.println(e);
    		System.err.println(Arrays.toString(e.getStackTrace()));
    		System.exit(-1);
    	} 
    	catch(IOException e) { 
    		System.err.println("IO Exception"); 
    		System.err.println(Arrays.toString(e.getStackTrace()));
    		System.exit(-1);
    	} 	
    	catch(NullPointerException e) { 
    		System.out.println("File Not Found");
    		System.err.println(e);
    		System.exit(-1);	
    	}

	    return trainingSet;
	}
	
	/**
	 * @param list: the entries of which the entropies to be calculated 
	 * @return entropy value of the given list
	 */
	public double computeEntropy(ArrayList<Boolean[]> list){
		int posit = 0;
		int negat = 0;
		int listSize =  list.size();
		if ( list == null || list.isEmpty() )
			return 0;
		    
		Iterator<Boolean[]> listIterator = list.iterator();
		while (listIterator.hasNext()) {
			if( listIterator.next()[LABEL_INDEX] == Boolean.TRUE) 
				posit++;
		}
		negat = listSize - posit;
		
		double positRatio = (double)posit/listSize;
		double negatRatio = (double)negat/listSize;
		// entropy calculation
		double entropy = -positRatio * log2(positRatio) - negatRatio * log2(negatRatio);	    
		return entropy;
	}
	
	/** 
	 * @param list: entropy is calculated for the given entries
	 * @param attrIndex: attribute of which the information gain to be calculated on the list
	 * @return information gain of given attribute
	 */
	public double getInformationGain(ArrayList<Boolean[]> list, int attrIndex){
		int trueValues = 0;
		int falseValues = 0;
		int size = list.size();

		ArrayList<Boolean[]> truesList = new ArrayList<Boolean[]>();
		ArrayList<Boolean[]> falsesList = new ArrayList<Boolean[]>();
		    
		for(int i = 0 ; i < size; i++){
			if(list.get(i)[attrIndex] == true){
				trueValues++;
				truesList.add(list.get(i));
			}
			else{
				falseValues++;
		        falsesList.add(list.get(i));
			}
		}
		    
		double entropyTrueVals   = computeEntropy(truesList);
		double entropyFalseVals  = computeEntropy(falsesList);
		double entropyS			 = computeEntropy(list);
		return (entropyS - ((double)trueValues/size) * entropyTrueVals
				- ((double)falseValues/size) * entropyFalseVals);	
	}
	
	/**
	 * @param sList training data entries as a list
	 * @return the created node testing the attribute having the maximum information gain
	 */
	public Node createNodeWithMaxInfoGain(ArrayList<Boolean[]> sList){
		double infoGain = 0, currentInfoGain;
		int attributeNo = -1;
		Node node = new Node(0, 0.0);
		    		
		TDIDT.informationGainWriter.println("Candidate information gains for the node #" + node.getNodeId());
		TDIDT.informationGainWriter.println("----------------------------------------------------");
		for(int i=1; i <= attributeCount; ++i) {
	    	currentInfoGain = getInformationGain( sList, i );
	    	TDIDT.informationGainWriter.println("Attribute #" + (i-1) + " : " + currentInfoGain);
	    	
	    	if( Double.compare(currentInfoGain, infoGain) > 0 ){
	    		infoGain = currentInfoGain;
	    		attributeNo = i-1;
	    		node.setAttributeNo(attributeNo);
	    		node.setInformationGain(infoGain);
	    	}
	    }
	    if(attributeNo == -1)
	    	TDIDT.informationGainWriter.println("This node is a leaf so the information gain"
	    			+ " is \"0\" for all the attributes "+ "\n");
	    else
	    	TDIDT.informationGainWriter.println("Attribute with max. information gain is #" + attributeNo + "\n");

		return node;
	}

	/**
	 * @param S list involving training data entries 
	 * @return root
	 */
	public Node buildTree(ArrayList<Boolean []> S){
		Node newNode = createNodeWithMaxInfoGain( S );
		int listSize = S.size();
	    //if set if empty, return 
	    if( listSize == 0){
	    	System.out.println("Oops, we have a problem with the size of the list.");
	    	return newNode;
	    }
		
	    /* S is “perfectly classified” (contains only examples of one of the classes)
	     * THEN make Node a leaf node , set class(Node) to TRUE/FALSE and RETURN root
	     */
	    if( Double.compare( computeEntropy(S), 0.0 ) == 0){
	    	newNode.setLeaf(true);
	    	newNode.setLabel(S.get(0)[LABEL_INDEX]);
	    	newNode.setInformationGain(0);
	    	return newNode;
	    }
	    
	    /* IF no test splits the data 
	     * THEN make Node a leaf node in T, set class(Node) to the majority class of E, RETURN T
	     */
	    if ( newNode.getAttributeNo() == 0 && Double.compare(newNode.getInformationGain(), 0.0) == 0 ){
	    	int trueCount  = 0;
	        int falseCount = 0;
	        for( int i=0; i<listSize; i++ ){
	        	if( S.get(i)[0] == false)
	        		falseCount++;
	        	else
	        		trueCount++;
	        }
	        newNode.setLeaf(true);
	        newNode.setLabel( trueCount > falseCount );
	        return newNode;
	    }
			 
	    /*
	     * Select Test, the “best” test for Node based on attributes in Attr
	     */
	    ArrayList<Boolean[]> trueS  = new ArrayList<Boolean[]>();
	    ArrayList<Boolean[]> falseS = new ArrayList<Boolean[]>();
	    	    
	    // first slot is allocated for the label value
	    int arrayIndex = newNode.getAttributeNo() + 1; 
	    for( int i=0; i < listSize; i++ ){
	    	if( S.get(i)[arrayIndex] == true )
	    		trueS.add(S.get(i));
	    	else
	    		falseS.add(S.get(i));
	    }
	    
	    newNode.setLeftChild(  buildTree(trueS)  );
	    newNode.setRightChild( buildTree(falseS) );
	    
	    return newNode;
	}

	
	public ArrayList<Boolean[]> getTrainingSet() {
		return trainingSet;
	}
	
	public void setTrainingSet(ArrayList<Boolean[]> trainingSet) {
		this.trainingSet = trainingSet;
	}
	
	/**
	 *  @return log base 2 of number
	 */
	public double log2(double num){
		if( num == 0 )
			return 0.0;
		
	    return Math.log(num) / Math.log(2);
	}

	public ArrayList<Boolean[]> getTestSet() {
		return testSet;
	}

	public void setTestSet(ArrayList<Boolean[]> testSet) {
		this.testSet = testSet;
	}
}

