import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class Huffman { 
		
	public static void main(String[] args) throws Exception{
		
		String text = "";
		
		//read the file that we past in argument in the args array of command line
		//This text file contains our text we want to analyse
		
		FileReader reader = new FileReader(args[0]);
		int r;
	    while((r = reader.read())!= -1){ //loop until there is no more characters
	    	System.out.print((char) r);
	        text += (char) r;   
	    }
		
		//Output the text encoding and tree in another file because it is too big
		PrintStream out = new PrintStream(new FileOutputStream("outputHuffman.txt"));
		System.setOut(out);
		
		//Convert the string to char characters in an array
		char[] textInChar = text.toCharArray();
		ArrayList<Character> listOfChars = new ArrayList<Character>(); //create a list, not an array because we don't know its size yet
		
		//Get an array of the characters present in the quote (not repeating them)
		for(int i = 0; i < textInChar.length; i++){
			if (listOfChars.contains(textInChar[i]))
				continue;
			else{
				listOfChars.add(textInChar[i]);
			}
		}
		
		//print the list of the characters
		System.out.println("Here is the list of characters present in the text: ");
		System.out.println(listOfChars); 
		
		//Count the occurrence of the characters in the text
		int[] frequencies = new int[listOfChars.size()];
		
		//counting the numbers of characters
		for(int i = 0; i < frequencies.length; i++){
			frequencies[i] = 0; //initialize the values
		}
		
		for(int i = 0; i < frequencies.length; i++){
			int key = listOfChars.get(i);
			for(int j = 0; j < textInChar.length; j++){
				if(key == textInChar[j]){
					frequencies[i]++;
				}
			}
		}
		
		// Sort the arrays is ascending order with bubble sort 
		// We need to sort in order to do the Huffman coding tree from smallest frequency to the biggest
		// by sorting, it will respect the order of appearance of the characters
		for (int i = 0; i < frequencies.length - 1; i++) {
			for (int j = 0; j < frequencies.length - 1; j++) {
				if (frequencies[j] > frequencies[j + 1]) {
					int temp = frequencies[j];
					frequencies[j] = frequencies[j + 1];
					frequencies[j + 1] = temp;
					
					//When we swap the numbers, we also need to sort the characters to the same position as the frequency
					char tempChar = listOfChars.get(j);
					listOfChars.set(j, listOfChars.get(j + 1));
					listOfChars.set(j + 1, tempChar);
				}
			}
		}
		
		//Text encoding implementation
		
		//the char list is from ascending order, so the 2nd element is right and every other is left
		Node[] nodeList = new Node[frequencies.length];
		int size = listOfChars.size();
		
		//Call function
		setEncoding(nodeList, listOfChars, frequencies, size);
	
		System.out.println();
		System.out.println("Here are the characters with its frequency and encoding: ");
		
		//display characters with its frequency and encoding in ascending order
		//lesser frequency equals bigger path (encoding)
		for(int i = 0; i < frequencies.length; i++){
			System.out.println(nodeList[i].character + " -> " + frequencies[i] + " -> " + nodeList[i].encoding);
		}
		
		treeBuilder(nodeList, listOfChars);		
		System.out.println("Here is the text encoding: ");
		System.out.println();
		System.out.println(textEncodingGen(textInChar, listOfChars, nodeList)); //call function which returns a String
	}
	
	//Now, it is time to build the tree
	public static void treeBuilder(Node[] nodeList, ArrayList<Character> charList){
		
		System.out.println();
		System.out.println("Here are all the steps for Huffman Coding tree (the number in parenthese indicates the weight after each step): ");
		System.out.println("(The complete right is the root of the tree and the complete left is the furthest leaf) ");
		System.out.println();
		
		int freq = 0; //frequency variable
		String merge = ""; //String variable
		
		for (int i = 1; i < nodeList.length; i++){
			
			if (i == 1){ 
				freq += nodeList[i].getFrequency() + nodeList[i-1].getFrequency();
				merge += String.valueOf(nodeList[i-1].getCharacter()) + String.valueOf(nodeList[i].getCharacter()); //Convert the char to string
				displayTree(freq, merge, charList, i);
			}
			else{ //add next frequencies on it to change its weight when building the tree
				freq += nodeList[i].getFrequency();
				merge += String.valueOf(nodeList[i].getCharacter());
				displayTree(freq, merge, charList, i);
			}
		}
		System.out.println();
		System.out.println("*Note that the empty space after the arrow in the tree indicates the space from the text");
		System.out.println();
	}
	
	//Display function of the building of the Huffman tree 
	public static void displayTree(int freq, String merge, ArrayList<Character> charList, int j){
		
		System.out.print(merge + "(" + freq + ")");
		
		for (int i = j+1; i < charList.size(); i++){
			
			System.out.print(" <- ");
			System.out.print(charList.get(i));
		}
		System.out.println();
	}

	//to set encoding code for each characters
	public static void setEncoding(Node[] nodeList, ArrayList<Character> listOfChars, int[] frequencies, int size){
		
		for(int i = 0; i < listOfChars.size(); i++){
			
			size = size-1; //we have to decrease the size each time i increments because the more we increment,
			//the more frequency of character is increase and being near the top of tree. (path is smaller)
			//the first element has "size" number of 1 and 0 for the encoding.
			
			int size1 = size; //this size is the number of 1 and 0 for the first character (the longest path)
			String c = "";
			
			//set the encoding for each character
			while(size1 >= 0){
				if(i == 1){
					c += "1";
					size1--;
				}
				else{
					if(size1 != 0){
						c += "1";
						size1--;
					}
					else{
						c += "0";
						size1--;
					}
				}
			} 
			//create a new node for each characters with its own frequency and encoding
			Node node = new Node(listOfChars.get(i), frequencies[i], c);
			
			//Insert node into an arraylist
			nodeList[i] = node;
		}
	}
	
	public static String textEncodingGen(char[] text, ArrayList<Character> listOfChars, Node[] nodeList){
		
		String code = "";
		
		for (int i = 0; i < text.length; i++){
			
			char letter = text[i];
			
			for (int j = 0; j < listOfChars.size(); j++){
				
				if(nodeList[j].getCharacter() == letter){
					code += nodeList[j].encoding;
				}
			}
		}
		return code;
	}
}



