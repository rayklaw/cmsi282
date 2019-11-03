/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  Names     : Adam Hirata, Ray Law
  Last Edit : 4/24/19
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package huffman;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.ByteArrayOutputStream;

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode 					trieRoot;
    private Map<Character, String> 		encodingMap 	= new HashMap<>();
    private HashMap<Character, Integer> distributions 	= new HashMap<>();
    private PriorityQueue<HuffNode> 	nodes			= new PriorityQueue<>();
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    Huffman (String corpus) {
    	//finding the distribution of chars in the corpus
    	distributions.put(corpus.charAt(0), 1);
    	for (int i = 1; i < corpus.length(); i++) {
    		char curChar = corpus.charAt(i);
    		if (distributions.containsKey(curChar)) {
    			distributions.replace(curChar, distributions.get(curChar) + 1);
    		} else {
    			distributions.put(curChar, 1);
    		}
    	}
    	
    	//initializing HuffNodes into the PriorityQueue
    	for (char key: distributions.keySet()) {
    		nodes.add(new HuffNode(key, distributions.get(key)));
    	}
    	
    	//constructing trie tree and assigning the encoding
    	createTree();
    	createEncoding(trieRoot, "");
    }
    
    /**
     * Constructs the trie encoding's HuffNode "tree"
     * @param  none
     * @return void
     */
    public void createTree() {
    	HuffNode curNode = new HuffNode('\0', 0);
    	while (nodes.size() != 1) {
    		HuffNode r 		= nodes.remove();
    		HuffNode l 		= nodes.remove();
    		curNode    		= new HuffNode('\0', r.count + l.count);
    		curNode.right 	= r;
    		curNode.left  	= l;
    		nodes.add(curNode);
    	}
    	trieRoot = curNode;
    }
    
    /**
     * Constructs the encoding assignments into the encoding map recursively
     * @param  node		the current node in the recursion
     * 		   encoding the encoding for the current node thus far
     * @return void
     */
    public void createEncoding(HuffNode node, String encoding) {
    	if (node.isLeaf()) {
    		encodingMap.put(node.character, encoding);
    		return;
    	}
    	
    	createEncoding(node.left,  encoding + "1");
    	createEncoding(node.right, encoding + "0");
    }
    
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as 3 components: (1) the
     *         first byte contains the number of characters in the message,
     *         (2) the bitstring containing the message itself, (3) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
    	//vars necessary for this method
    	ByteArrayOutputStream river = new ByteArrayOutputStream();
    	String encoded 				= String.format("%8s", Integer.toBinaryString(message.length())).replace(' ', '0');
		int counter 				= 0;
    	byte[] result;
    	
    	//encoding the original message
    	for (int i = 0; i < message.length(); i++) {
    		encoded += encodingMap.get(message.charAt(i));
    	}
    	
    	//constructing the byte array
    	while (counter < encoded.length() - 8) {
    		river.write(Integer.parseInt(encoded.substring(counter, counter + 8), 2));
    		counter += 8;
    	}
    	river.write(Integer.parseInt(encoded.substring(counter, encoded.length()), 2) << 8 - encoded.length() % 8);
    	result = river.toByteArray();
    	return result;
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as 3 components: (1) the
     *        first byte contains the number of characters in the message,
     *        (2) the bitstring containing the message itself, (3) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
    	int msgLength = compressedMsg[0];
    	String bitStr = "";
    	
    	//reconstructing the bitstring from the byte array; uses helper method getBitStr()
    	for (int i = 1; i < compressedMsg.length; i++) {
    		bitStr += getBitStr((int) compressedMsg[i]);
    	}
    	
    	//decoding the encoding
    	return decode (bitStr, msgLength);
    }
    
    /**
     * Once the correct bitstring has been constructed, this methods decodes it using the global encodingMap
     * @param 	input		The String to decode
     * 			msgLength	The known number of chars in result
     * @return 	result		A decoded representation of the input string
     */
    public String decode(String input, int msgLength) {
    	int counter 	= 0;
    	int index		= 0;
    	int endIndex	= 0;
    	String result	= "";
    	
    	Map<String, Character> reverseMap = new HashMap<>();
    	for (Character key: encodingMap.keySet()) {
    		reverseMap.put(encodingMap.get(key), key);
    	}
    	while (counter < msgLength) {
    		   		
    		String key = input.substring(index, endIndex + 1);
    		if (reverseMap.containsKey(key)) {
    			result += reverseMap.get(key);
    			index = endIndex + 1;
    			counter++;
    		}
    		endIndex++;
    	}
    	return result;
    }
    
    /**
     * Creates the correct bit representation of a byte/int type. Created b/c Integer.toBinaryString()
     * was not working for us for negative numbers
     * @param 	input	The number we are converting to string
     * @return 	String	String representation of the number
     */
    public String getBitStr(int input) {
    	String result = "";
    	if (input < 0) {
    		input += 128;
    		result = String.format("%7s", Integer.toBinaryString(input)).replace(' ', '0');
    		return "1" + result;
    	} else {
    		result = String.format("%8s", Integer.toBinaryString(input)).replace(' ', '0');
    	}
    	return result;
    }
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left and right child), contains
     * a character field that it represents (in the case of a leaf, otherwise
     * the null character \0), and a count field that holds the number of times
     * the node's character (or those in its subtrees) appear in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode left, right;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return left == null && right == null;
        }
        
        public int compareTo (HuffNode other) {
            return this.count - other.count;
        }
        
    }

}
