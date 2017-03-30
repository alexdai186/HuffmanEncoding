/**
 * Created by Alex Dai on 3/28/2017.
 */
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.Scanner;


public class Encoder {

    /* Checks the arguments for validity */
    public static boolean parseArgs(String args[]) {
        if (args.length != 2) {
            return false;
        }
        if (Integer.parseInt(args[1]) < 0) {
            return false;
        }
        return true;
    }

    /* Finds frequency array for the given file*/
    public static int[] findFrequencies(File file) throws IOException{
        int sum = 0;
        int[] freq = new int[26];

        Scanner sc = new Scanner(file);

        /* Creates the relative frequency array */
        for (int i = 0; i < 26; i++){
            if (sc.hasNext())
                freq[i] = sc.nextInt();
            else
                freq[i] = 0;
            sum += freq[i];
        }

        /* Divides each element in the array by total sum to find probabilities */
        for (int i = 0; i < 26; i++) {
            freq[i] /= sum;
        }
        return freq;
    }

    public static void main(String args[]) {
        File file;
        int outputSize;
        int[] freq = new int[26];

        /* Checks the arguments for validity */
        if (!parseArgs(args)) {
            System.out.println("Invalid argument format");
            return;
        }

        /* Opens file and sets the output size */
        try {
            file = new File(args[0]);
            outputSize = Integer.parseInt(args[1]);
            freq = findFrequencies(file);
        } catch(Exception e){
            System.out.println("Exception occured when opening file: " + e);
            return;
        }

        HuffmanCode huffman = new HuffmanCode();
        HuffmanTree tree = huffman.buildTree(freq, 1);
        HuffmanTree tree2 = huffman.buildTree(freq, 2);

        // print out results
        System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
        huffman.printCodes(tree, new StringBuffer());

    }
}
