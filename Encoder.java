import java.io.*;
import java.lang.*;
import java.util.*;


public class Encoder {
    private static String[] alphabet = new String[] {"A", "B", "C", "D", "E", "F", "G",
                                                    "H", "I", "J", "K", "L", "M", "N", "O",
                                                    "P", "Q", "R", "S", "T", "U", "V", "W", 
                                                    "X", "Y", "Z"};
    private static String[] twoSymbolAlphabet = new String[alphabet.length * alphabet.length];
    // Array to store frequencies of 1-symbols, taken from file. Index = symbol, value = frequencies
    private static int[] freq = new int[alphabet.length];
    // Array to store probabilities of 1-symbols. Index = symbol, value = probabilities
    private static double[] probs = new double[alphabet.length];
    // Array to store probabilities of 2-symbols. Index = symbol, value = probabilities
    private static double[] twoSymbolProbs = new double[alphabet.length * alphabet.length];
    
    private static int numberOfSymbols = 0;
    private static HashMap<String, Double> symbolProbs = new HashMap<String, Double>();
    
    /* Checks the command line arguments for validity */
    public static boolean validArgs(String args[]) {
        return args.length == 2 && args[0].equals("frequenciesFile") && Integer.parseInt(args[1]) > 0;
    }

    public static void createFrequencyAndProbabilityArrays(File file) throws IOException {
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
        // The number of symbols might be less than 26, fill the remaining freq array spaces with -1
        while (index < alphabet.length) {
            freq[index++] = -1;
        }
        // Fill probability array. Divide frequency by sum to find each symbol's probability
        // In extra spaces, there will be negative probabilities. just ignore them
        for (int i = 0; i < alphabet.length; i++) {
            probs[i] = ((double)freq[i]) / sum;
        }
        scan.close();
    }
    
    public static void Fill2SymbolAlphabetAndArrays() {
        int index = 0;
        // traverse through the number of symbols in a double for loop to create the 2-symbol alphabet
        for (int i = 0; i < numberOfSymbols; i++) {
            for (int j = 0; j < numberOfSymbols; j++) {
                // Fill the 2-symbol alphabet programmatically
                twoSymbolAlphabet[index] = alphabet[i] + alphabet[j];
                // Calculate each 2-symbol's probabilities
                twoSymbolProbs[index] = probs[i] * probs[j];
                symbolProbs.put(twoSymbolAlphabet[index], twoSymbolProbs[index]);
                index++;
            }
        }
    }

    public static File createTestFile(int numCharacters, int symbolOrder) throws IOException {
        File file = new File("testText");
        PrintWriter pw = new PrintWriter(file);
        double[] range = new double[numberOfSymbols];
        double[] twoSymbolRange = new double[numberOfSymbols * numberOfSymbols];
        try {
            // randomly generate a double
            Random rand = new Random();
            double randomValue;
            
            // range array stores the upper bound of probabilities for each symbol
            // EX: if A has prob of 0.15, range stores its own prob 
            // If B has prob of 0.45, then range for B is 0.15 to 0.6 (range of A + p(B)). Range stores 0.6
            // If C has prob of 0.4, then range for C is 0.6 to 1 (range of B + p(C)). Range stores 1
            if (symbolOrder == 1) {
                range[0] = probs[0];
                for (int i = 1; i < numberOfSymbols; i++) {
                    range[i] = range[i-1] + probs[i];
                }
                for (int k = 0; k < numCharacters; k++) {
                    randomValue = rand.nextDouble();
                    for (int i = 1; i < numberOfSymbols; i++) {
                        if (randomValue <= range[i]) {
                            // if symbol is within range, stop at the first range you find. print the symbol at that range
                            pw.print(alphabet[i]);
                            break;
                        }
                    }
                }
            }
            else {
                twoSymbolRange[0] = twoSymbolProbs[0];
                for (int i = 1; i < numberOfSymbols * numberOfSymbols; i++) {
                    twoSymbolRange[i] = twoSymbolRange[i-1] + twoSymbolProbs[i];
                }
                for (int k = 0; k < numCharacters; k++) {
                    randomValue = rand.nextDouble();
                    for (int i = 1; i < numberOfSymbols * numberOfSymbols; i++) {
                        if (randomValue <= twoSymbolRange[i]) {
                            // if symbol is within range, stop at the first range you find. print the symbol at that range
                            pw.print(twoSymbolAlphabet[i]);
                            // System.out.println(twoSymbolAlphabet[i]);
                            break;
                        }
                    }
                }   
            }
            pw.close();
        } catch (Exception e) {
            System.out.println("An error occurred while creating test file " + e.toString());
        }
        return file;
    }

    // encode the testText symbols into their binary encryptions
    public static File encode(File testText, int symbolOrder, HuffmanCode huff) throws IOException {
        File file1 = new File("testText.enc1");
        File file2 = new File("testText.enc2");
        PrintWriter pw = new PrintWriter(symbolOrder == 1? file1: file2);
        Scanner scan = new Scanner(testText);
        try {
            // Print binary encryptions into file
            String line = scan.nextLine();
            if (symbolOrder == 1) {
                // encode each 1-symbol
                for (int i = 0; i < line.length(); i++) {
                    String symbol = String.valueOf(line.charAt(i));
                    String prefix = huff.encodes.get(symbol);
                    pw.print(prefix);
                }
            } else {
                // encode each 2-symbol
                for (int i = 0; i < line.length() - 1; i+=2) {
                    String symbol = String.valueOf(line.charAt(i)) + String.valueOf(line.charAt(i + 1));
                    String prefix = huff.twoSymbolencodes.get(symbol);
                    pw.print(prefix);
                }
            }
            scan.close();
            pw.close();
        } catch (Exception e) {
            System.out.println("An error occurred while encoding file " + e.toString());
        }
        return symbolOrder == 1? file1: file2;
    }
    
    // decode the testText symbols from their binary encryptions
    public static void decode(File encodedFile, int symbolOrder, HuffmanCode huff) throws IOException {
        File file1 = new File("testText.dec1");
        File file2 = new File("testText.dec2");
        PrintWriter pw = new PrintWriter(symbolOrder == 1? file1: file2);
        Scanner scan = new Scanner(encodedFile);
        try {
            // Print symbols into file
            String line = scan.nextLine();
            String prefix = "";
            String symbol;
            // decode each 1-symbol
            for (int i = 0; i < line.length(); i++) {
                prefix += String.valueOf(line.charAt(i));   // read each bit, add to prefix string
                if (symbolOrder == 1)
                    symbol = huff.decodes.get(prefix);
                else 
                    symbol = huff.twoSymboldecodes.get(prefix);
                if (symbol != null) {   // if the binary string is a prefix, print the corresponding symbol
                    pw.print(symbol);
                    // reset prefix to prepare for next symbol
                    prefix = "";
                }
            }
            scan.close();
            pw.close();
        } catch (Exception e) {
            System.out.println("An error occurred while encoding file " + e.toString());
        }
//        return symbolOrder == 1? file1: file2;
    }
    
    // Calculate the entropy of each order
    public static double computeEntropy(int symbolOrder) {
        double entropy = 0.0;
        if (symbolOrder == 1) {
            for (double d: probs) {
                if (d > 0) 
                    entropy += d * (Math.log(d)/Math.log(2));
            }
        } else {
            for (double d: twoSymbolProbs) {
                if (d > 0) 
                    entropy += d * (Math.log(d)/Math.log(2));
            }
        }
        return entropy*-1;
    }
    
    // average bits per symbol
    public static  double computeEfficiency(int symbolOrder, HuffmanCode huff) {
        double numBits = 0;
        if (symbolOrder == 1) {
            for (String key: huff.encodes.keySet()) {
                numBits += huff.encodes.get(key).length() * probs[key.charAt(0)-65];
            }
        } else {
            for (String key: huff.twoSymbolencodes.keySet()) {
                numBits += huff.twoSymbolencodes.get(key).length() * symbolProbs.get(key);
            }
        }
        return numBits;
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
            File testText = createTestFile(Integer.parseInt(args[1]), 1); // 1-symbol
            Fill2SymbolAlphabetAndArrays();
            File twoSymbolTestText = createTestFile(Integer.parseInt(args[1]), 2); // 2-symbol
            
            HuffmanCode huff = new HuffmanCode();
            HuffmanTree tree = huff.buildTree(freq, 1);
            HuffmanTree tree2 = huff.buildTree(freq, 2);

            // Call printcodes on 1-symbol, encode it, decode it, print out its entropy
            System.out.println("----------------------------");
            System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
            huff.printCodes(tree, new StringBuffer(), 1);
            File encodedFile = encode(testText, 1, huff);
            decode(encodedFile, 1, huff);
            double entropy1 = computeEntropy(1);
            System.out.println("Entropy for 1-symbol is " + entropy1);
            double eff1 = computeEfficiency(1, huff);
            System.out.println("Efficiency for 1-symbol is " + eff1);
            System.out.println("Percentage Difference for 1-symbol is " + ((eff1-entropy1)/entropy1) * 100 + "%");
            
            // Call printcodes on 2-symbol, encode it, decode it, print out its entropy
            System.out.println("----------------------------");
            System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE"); 
            huff.printCodes(tree2, new StringBuffer(), 2);
            File encodedFile2 = encode(twoSymbolTestText, 2, huff);
            decode(encodedFile2, 2, huff);
            double entropy2 = computeEntropy(2);
            System.out.println("Entropy for 2-symbol is " + entropy2);
            double eff2 = computeEfficiency(2, huff);
            System.out.println("Efficiency for 2-symbol is " + eff2);
            System.out.println("Percentage Difference for 2-symbol is " + ((eff2-entropy2)/entropy2) * 100 + "%");

        } catch(Exception e){
            System.out.println("Exception occurred when opening frequenciesFile: " + e.toString());
            return;
        }
    }
}
