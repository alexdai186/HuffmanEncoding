import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.Scanner;


public class Encoder {
    private static String[] alphabet = new String[] {"A", "B", "C", "D", "E", "F", "G",
                                                    "H", "I", "J", "K", "L", "M", "N", "O",
                                                    "P", "Q", "R", "S", "T", "U", "V", "W", 
                                                    "X", "Y", "Z"};
    // Array to store frequencies of symbols, taken from file. Index = symbol, value = frequencies
    private static int[] freq = new int[alphabet.length];
    // Array to store probabilities of symbols. Index = symbol, value = probabilities
    private static double[] probs = new double[alphabet.length];
    private static int numberOfSymbols = 0;
    /* Checks the command line arguments for validity */
    public static boolean validArgs(String args[]) {
        return args.length == 2 && args[0].equals("frequenciesFile") && Integer.parseInt(args[1]) > 0;
    }

    public static void createFrequencyAndProbabilityArrays(File file) throws IOException{
        int sum = 0;
        int value;
        int index = 0;
        Scanner scan = new Scanner(file);
        // Calculate the sum of frequencies
        while (scan.hasNextLine()) {
            value = scan.nextInt();
            freq[index++] = value;
            sum += value;
            numberOfSymbols++;
        }
        System.out.println("SUM IS: " + sum);
        // The number of symbols might be less than 26, fill the remaining freq array spaces with -1
        while (index < alphabet.length) {
            freq[index++] = -1;
        }

        // Fill probability array. Divide frequency by sum to find each symbol's probability
        // In extra spaces, there will be negative probabilities. just ignore them
        for (int i = 0; i < alphabet.length; i++) {
            probs[i] = ((double)freq[i]) / sum;
        }
        // Print out arrays just to check
        for (int i = 0; i < numberOfSymbols; i++) {
            System.out.println("probability: " + alphabet[i] + " " + probs[i]);
        }
        scan.close();
    }

    public static void createTestFile(int numCharacters, int[] freq) throws IOException {
        PrintWriter pw = new PrintWriter("testText");
        double[] range = new double[numberOfSymbols];
        try {
            // randomly generate a double
            Random rand = new Random();
            
            // range array stores the upper bound of probabilities for each symbol
            // EX: if A has prob of 0.15, range stores its own prob 
            // If B has prob of 0.45, then range for B is 0.15 to 0.6 (range of A + p(B)). Range stores 0.6
            // If C has prob of 0.4, then range for C is 0.6 to 1 (range of B + p(C)). Range stores 1
            range[0] = probs[0];
            for (int i = 1; i < numberOfSymbols; i++) {
                range[i] = range[i-1] + probs[i];
            }
            for (int i = 0; i < numberOfSymbols; i++) {
                System.out.println("range: " + alphabet[i] + " " + range[i]);
            }
            for (int k = 0; k < numCharacters; k++) {
                double randomValue = rand.nextDouble();
                System.out.println("rand: " + randomValue);
                for (int i = 1; i < numberOfSymbols; i++) {
                    if (randomValue <= range[i]) {
                        // if symbol is within range, stop at the first range you find. print the symbol at that range
                        pw.print(alphabet[i]);
                        System.out.println(alphabet[i]);
                        break;
                    }
                }
            }
            pw.close();
        } catch (Exception e) {
            System.out.println("An error occurred while creating test file " + e.toString());
        }
    }
    public static void main(String args[]) {
        File file;

        /* Checks the arguments for validity */
        if (!validArgs(args)) {
            System.out.println("Invalid argument format");
            return;
        }

        /* Opens file and create freq and prob arrays */
        try {
            file = new File(args[0]);
            createFrequencyAndProbabilityArrays(file);
            createTestFile(Integer.parseInt(args[1]), freq);
        } catch(Exception e){
            System.out.println("Exception occurred when opening frequenciesFile: " + e.toString());
            return;
        }

        HuffmanTree tree = HuffmanCode.buildTree(freq, 1);
        HuffmanTree tree2 = HuffmanCode.buildTree(freq, 2);

        // print out results
        System.out.println("SYMBOL\tFREQUENCY\tHUFFMAN CODE");
        HuffmanCode.printCodes(tree, new StringBuffer());
        
        System.out.println("----------------------------");
        System.out.println("SYMBOL\tFREQUENCY\tHUFFMAN CODE");
        HuffmanCode.printCodes(tree2, new StringBuffer());

    }
}
