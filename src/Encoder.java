/**
 * Created by Alex Dai on 3/28/2017.
 */
import java.io.*;
import java.lang.*;
import java.util.*;


public class Encoder {

    public static boolean parseArgs(String args[]) {
        if (args.length != 2) {
            return false;
        }
        if (Integer.parseInt(args[1]) < 0) {
            return false;
        }
        return true;
    }

    public static void main(String args[]) {
        File file;
        int outputSize;

        if (!parseArgs(args)) {
            System.out.println("Invalid argument format");
            return;
        }

        try {
            file = new File(args[0]);
            outputSize = Integer.parseInt(args[1]);
        } catch(Exception e){
            System.out.println("Exception occured when opening file: " + e);
        }


    }
}
