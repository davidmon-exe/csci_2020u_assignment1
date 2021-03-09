package spamDetector.train;

import spamDetector.Controller;
import java.io.*;
import java.util.*;

public class SpamTrainer {

    /** Method iterates through the given dir and selects a file at a time.
     * Words(tokens) contained within file are scanned into a temporary TreeMap,
     * where each word is added to a counter. Pr(W|S) is then calculated,
     * and is saved for the given key in frequency map.
     *
     * @param dir
     * @throws FileNotFoundException
     */
    public static void trainSpam(File dir) throws FileNotFoundException {
        File[] file = dir.listFiles();
        for(int n = 0; n < Objects.requireNonNull(file).length; n++) {
            TreeMap<String, Integer> temp = new TreeMap<>();
            Scanner fileScan = new Scanner(file[n]);
            while (fileScan.hasNext()) {
                String token = fileScan.next();
                if (isValidToken(token) & !temp.containsKey(token)) {
                    temp.put(token, 1);
                }
            }
            for (Map.Entry<String, Integer> entry : temp.entrySet()){
                if (Controller.spamCount.containsKey(entry.getKey())) {
                    int prev = Controller.spamCount.get(entry.getKey());
                    Controller.spamCount.put(entry.getKey(), prev + 1);
                } else {
                    Controller.spamCount.put(entry.getKey(), 1);
                }
            }
            temp.clear();
            for(Map.Entry<String, Integer> entry : Controller.spamCount.entrySet()){
                double PrWS = (double) entry.getValue() / (double) file.length;
                Controller.trainSpamFreq.put(entry.getKey(), PrWS);
            }
        }
    }
    /** Uses the matches() method to determine id the token is valid in terms of the match regex expression.
     * @param token   word or text entry contained in a file
     * @return        boolean, if matches() is true, return true, otherwise return false
     */
    private static boolean isValidToken(String token){
        String allLetters = "^[a-zA-Z]+$";
        return token.matches(allLetters);
    }
}