
/**
 * @author  Can Yuce 
 * @author  Oguz Uzman
 */

public class Node {
	private int attributeNo;   // attribute no
	private Node leftChild = null;  
	private Node rightChild = null; 
	private boolean leaf = false;  
	private boolean label = false; // if the node is a leaf 
	private double informationGain; // info. gain of current attribute
	public int nodeId;  // used for node enumeration

	public static int nodeCnt = 0; 
	
	public Node(int attributeNo, double informationGain){
		this.setInformationGain(informationGain);
		this.attributeNo = attributeNo;
		this.nodeId = nodeCnt;
		nodeCnt++;
	}
	
	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getAttributeNo() {
		return attributeNo;
	}

	public void setAttributeNo(int attributeNo) {
		this.attributeNo = attributeNo;
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public boolean getLabel() {
		return label;
	}

	public void setLabel(boolean label) {
		this.label = label;
	}

	public double getInformationGain() {
		return informationGain;
	}

	public void setInformationGain(double informationGain) {
		this.informationGain = informationGain;
	}	
}
