package spamDetector.train;

import spamDetector.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;

public class HamTrainer {

    /** Method iterates through the given dir and selects a file at a time.
     * Words(tokens) contained within file are scanned into a temporary TreeMap,
     * where each word is added to a counter. Pr(W|H) is then calculated,
     * and is saved for the given key in frequency map.
     *
     * @param dir
     * @throws FileNotFoundException
     */
    public static void trainHam(File dir) throws FileNotFoundException {
        File[] file = dir.listFiles();
        for(int n = 0; n < Objects.requireNonNull(file).length; n++) {
            TreeMap<String, Integer> temp = new TreeMap<>();
            Scanner fileScan = new Scanner(file[n]);
            while (fileScan.hasNext()){
                String token = fileScan.next();
                if(isValidToken(token) & !temp.containsKey(token)){
                    temp.put(token, 1);
                }
            }
            for (Map.Entry<String, Integer> entry : temp.entrySet()){
                if (Controller.hamCount.containsKey(entry.getKey())) {
                    int prev = Controller.hamCount.get(entry.getKey());
                    Controller.hamCount.put(entry.getKey(), prev + 1);
                } else {
                    Controller.hamCount.put(entry.getKey(), 1);
                }
            }

            temp.clear();
            for(Map.Entry<String, Integer> entry : Controller.hamCount.entrySet()){
                double prWH = (double) entry.getValue() / (double) file.length;
                Controller.trainHamFreq.put(entry.getKey(), prWH);
            }
        }
    }

    /** Uses the matches() method to determine id the token is valid in terms of the match regex expression.
     * @param token   word or text entry contained in a file
     * @return        boolean, if matches() is true, return true, otherwise return false
     */
    private static boolean isValidToken(String token){
        String match = "^[a-zA-Z]+$";
        return token.matches(match);
    }
}