import java.util.*;

/* Huffman code taken from https://rosettacode.org/wiki/Huffman_coding#Java */
abstract class HuffmanTree implements Comparable<HuffmanTree> {
    public final int frequency; // the frequency of this tree
    public HuffmanTree(int freq) { frequency = freq; }

    // compares on the frequency
    public int compareTo(HuffmanTree tree) {
        return frequency - tree.frequency;
    }
}

class HuffmanLeaf extends HuffmanTree {
    public final String value; // the character this leaf represents

    public HuffmanLeaf(int freq, String val) {
        super(freq);
        value = val;
    }
}

class HuffmanNode extends HuffmanTree {
    public final HuffmanTree left, right; // subtrees

    public HuffmanNode(HuffmanTree l, HuffmanTree r) {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }
}

public class HuffmanCode {
    // input is an array of frequencies, indexed by character code
    public static HuffmanTree buildTree(int[] charFreqs, int symbolOrder) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
        // initially, we have a forest of leaves
        // one for each non-empty character

        /* Change this to account for 1-symbol and 2-symbol */
        if (symbolOrder == 1) {
            for (int i = 0; i < charFreqs.length; i++)
                // MAKE SURE FREQ IS NOT NEGATIVE, THE EXTRA SPACES IN FREQ ARRAY MAKES FREQ -1
                if (charFreqs[i] > 0)
                    trees.offer(new HuffmanLeaf(charFreqs[i], String.valueOf((char)(i+65))));
        }
        else {
            HashMap<String, Integer> mapFreq = new HashMap<String, Integer>();
            for (int i = 0; i < charFreqs.length; i++) {
                for (int j = 0; j < charFreqs.length; j++) {
                    // MAKE SURE FREQ IS NOT NEGATIVE, THE EXTRA SPACES IN FREQ ARRAY MAKES FREQ -1
                    if (charFreqs[i] > 0 && charFreqs[j] > 0)
                        mapFreq.put(String.valueOf((char)(i+65) + String.valueOf((char)(j+65))), (charFreqs[i] * charFreqs[j]));
                }
            }
            // add symbols into tree
            for (String symbol: mapFreq.keySet()) {
                trees.offer(new HuffmanLeaf(mapFreq.get(symbol), symbol));
            }
        }

        assert trees.size() > 0;
        // loop until there is only one tree left
        while (trees.size() > 1) {
            // two trees with least frequency
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();

            // put into new node and re-insert into queue
            trees.offer(new HuffmanNode(a, b));
        }
        return trees.poll();
    }

    public static void printCodes(HuffmanTree tree, StringBuffer prefix) {
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;

            // print out character, frequency, and code for this leaf (which is just the prefix)
            System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);

        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;

            // traverse left
            prefix.append('0');
            printCodes(node.left, prefix);
            prefix.deleteCharAt(prefix.length()-1);

            // traverse right
            prefix.append('1');
            printCodes(node.right, prefix);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }
}
