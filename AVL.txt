import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class ATree {

	Node root;
	static int compares = 0;
	static int newParent = 0;
	static int successFind = 0;
    static int failedFind = 0;
    static int valuesAdded = 0;
	
	public static void main(String[] args) throws IOException{
		
		ATree AVLtree = new ATree();
		
		PrintStream out = new PrintStream(new FileOutputStream("outputAVL.txt"));
		System.setOut(out);
		
		System.out.println("Here are the operations: ");
		System.out.println();
		
		//For reading purpose
		try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
		    String line;
		    char operationType = 0;
		    while ((line = br.readLine()) != null) { //read and execute line by line
		    	System.out.println(line);
		    	String nodeValue = "";
		    	char[] elements = line.toCharArray();
		    	for(int i = 0; i < elements.length; i++){
		    		if(i == 0){
		    			operationType = elements[i]; //store the type of operation
		    		} else{
		    			nodeValue += elements[i];
		    		}
		    	}
		    	//Convert String to int
		    	int nodeNumber = Integer.parseInt(nodeValue);
		    	Node node = new Node(nodeNumber);
		    	
		    	// if == a, it means it is an add operation
		    	if(operationType == 'a'){
		    		valuesAdded++;
		    		AVLtree.root = ATree.add(AVLtree.root, nodeNumber);
		    	} else if(operationType == 'r'){ //if r, then remove operation
		    		AVLtree.root = ATree.remove(AVLtree.root, nodeNumber); //to compare root and nodeNumber
		    	} else{ //it is find operation
		    		if(find(AVLtree.root, nodeNumber)){
		    			successFind++;
		    		} else{
		    			failedFind++;
		    		}
		    	}
		    }
		} catch(Exception e) {
	         e.printStackTrace();
	      }
	    
	    System.out.println();
	    System.out.print("Postorder traversal of the AVL tree: ");
	    AVLtree.postOrderTraversal(AVLtree.root);
	    System.out.println();
	    System.out.print(compares + " compares");
	    System.out.println();
	    System.out.print(newParent + " parents changed");
	    System.out.println();
	    System.out.print(successFind + " successful finds, " + failedFind + " unsuccessful finds");
	    System.out.println();
	    System.out.print(valuesAdded + " values added");
	}	
	
	//the balance is calculated by subtracting the left sub-tree by the right sub-tree
	public static int getBalance(Node node){
		if (node == null)
            return 0;
        return getHeight(node.left) - getHeight(node.right);
	}
	
	//function to get the longest path for the height of a node
	public static int longestPath(int leftHeight, int rightHeight){
		if(leftHeight > rightHeight){
			compares++;
			return leftHeight;
		}
		else
			return rightHeight;
	}
	
	public static int getHeight(Node node){
		if(node == null)
			return 0; //if node is null, there is no node so no height
		else
			return node.height; //if there is a node at left or right, it means height is 1.
	}
	
	public void postOrderTraversal(Node root){
		if(root !=  null) {
			postOrderTraversal(root.left);
			postOrderTraversal(root.right);
			//Visit the node by Printing the node data  
			System.out.print(root.value + ",");
		}
	}
	
	//Rotations implementation
	
	//left rotation is when the tree balance is right-heavy
	public static Node rotateLeft(Node n){
		
		Node rightNode = n.right;
		Node temp = rightNode.left; //left of m node is null
		newParent++; //increment every time a node parent changes
		
		//Do the rotation
		//m becomes the new parent
		rightNode.left = n;
		n.right = temp; //when the n node has been rotated, its rightChild node isn't m anymore. That is why we have
		//to make it equal to null.
		
		//change the heights because the nodes have changed places
		//Every node should be at least of height 1, that's why we do +1
		int nHeight = longestPath(getHeight(n.left), getHeight(n.right)) + 1;
		n.height = nHeight;
		//If the height of rightNode was 1, it becomes height of 2 because it is now a parent node
		int mHeight = longestPath(getHeight(rightNode.left), getHeight(rightNode.right)) + 1;
		rightNode.height = mHeight;
		
		return rightNode; //return the new parent node
	}
	
	//right rotation is when the tree balance is left-heavy
	public static Node rotateRight(Node n){
		
		Node leftNode = n.left;
		Node temp = leftNode.right; //left of m node is null
		newParent++;
		
		//Do the rotation
		//m becomes the new parent
		leftNode.right = n;
		n.left = temp; //when the n node has been rotated, its rightChild node isn't m anymore. That is why we have
		//to make it equal to null.
		
		//change the heights because the nodes have changed places
		int nHeight = longestPath(getHeight(n.left), getHeight(n.right)) + 1;
		n.height = nHeight;
		int mHeight = longestPath(getHeight(leftNode.left), getHeight(leftNode.right)) + 1;
		leftNode.height = mHeight;
		
		return leftNode; //return the new parent node
	}
	
	public static boolean find(Node root, int nodeValue){
		
		//if no root or node in the tree, not found.
		if(root == null){
			return false;
		} else{
			if(nodeValue < root.value){
				compares++;
				find(root.left, nodeValue);
			} 
			if(nodeValue > root.value){
				compares++;
				find(root.right, nodeValue);
			}
		}
		//if root != null, and nodeValue = root.value
		return true;
	}
	
	public static Node add(Node n, int nodeValue){
		
		int balance = getBalance(n); //to check if the tree is unbalanced
		
		//insert node in binary search tree
		if(n == null){
			Node node = new Node(nodeValue);
			return node;
		}
							
		if(nodeValue < n.value){ //go to left of sub-tree
			compares++;
			n.left = add(n.left, nodeValue);
		}
		else if(nodeValue > n.value){ //go to right of sub-tree
			compares++;
			n.right = add(n.right, nodeValue);
		}
		else
			return n; //can not add a node that already exists in the binary search tree
		
		//adjust height of the root because we added a node
		n.height = longestPath(getHeight(n.left), getHeight(n.right))+1;
		
		//4 cases: left left, right right, left right and right left
		//To keep the tree balanced
		
		if(balance < -1 && n.right.value < nodeValue){ //right right case
			//if nodeValue smaller than left child value, it is also unbalanced
			compares++;
			return rotateLeft(n);
		}
		
		if(balance > 1 && n.left.value > nodeValue){ //left left case 
			//if nodeValue smaller than left child value, it is also unbalanced
			compares++;
			return rotateRight(n);
		}
		
		//if the tree is unbalanced but the nodeValue is still bigger than its left child value
		if(balance > 1 && n.left.value < nodeValue){ //left right case
			compares++;
			n.left = rotateLeft(n.left); 
			return rotateRight(n); //for rotate, always take the root node of the nodes to rotate
		}
		
		//if the tree is unbalanced but the nodeValue is still smaller than its right child value
		if(balance < -1 && n.right.value > nodeValue){ //right left case
			compares++;
			n.right = rotateRight(n.right); 
			return rotateLeft(n); 
		}
		return n;
	}
	
	//minimum node value
	public static Node minValue(Node n){
		Node currentNode = n;
		/* loop down to find the leftmost leaf */
		while (currentNode.left != null)
			currentNode = currentNode.left;
		return currentNode;
	}
	
	public static Node remove(Node n, int nodeValue){
		
		//binary search tree delete
		
		//return n is there is only one node which is the root
		if (n == null) //if n == null
			return n;

		//if nodeValue (to delete) is smaller than node
		if (nodeValue < n.value)
			n.left = remove(n.left, nodeValue);

		//if nodeValue (to delete) is greater than node
		else if (nodeValue > n.value)
			n.right = remove(n.right, nodeValue);

		//if the value is the same, then delete it
		else{
			//node with only one child or no child
			if ((n.left == null) || (n.right == null)) {
				Node x = null;
			
				//If there is one child
				if (x == n.left){ //if left is null, then right contains something
					x = n.right;
				} else{
					x = n.left;
				}

				//If there is no child
				if (x == null){
					x = n;
					n = null;
				} else {  
					n = x; //delete n
				}
					
			} else {
				//get the smallest which will become the new parent
				newParent++;
				Node x = minValue(n.right);
				n.value = x.value; //child node x becomes the root
				n.right = remove(n.right, x.value);
			}
		
		}
     
		//change the height because there was a deletion of a node
		n.height = longestPath(getHeight(n.left), getHeight(n.right)) + 1;
		int balance = getBalance(n);

		//Balance the tree if not balanced
		// Left Left Case
		if (balance > 1 && getBalance(n.left) >= 0)
			return rotateRight(n);
		
		// Right Right Case
		if (balance < -1 && getBalance(n.right) <= 0)
			return rotateLeft(n);

		// Left Right Case
		if (balance > 1 && getBalance(n.left) < 0){
			n.left = rotateLeft(n.left);
			return rotateRight(n);
		}

		// Right Left Case
		if (balance < -1 && getBalance(n.right) > 0){
			n.right = rotateRight(n.right);
			return rotateLeft(n);
		}
		return n;
	}
}
