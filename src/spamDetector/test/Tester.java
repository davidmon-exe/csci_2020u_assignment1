package spamDetector.test;

import spamDetector.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Tester {
    /**
     * Method calculates the probability that the given file is spam. It does this through scanning the file and
     * determining if each token is valid and if it appears in the trained spam words map. It then uses a calculation,
     * to determine the probability of spam. It will then determine if the file is either a spam email
     * in the ham folder (falsePos) or if it is in the correct email class, and it will add to the appropriate counter
     * @param file
     * @return prSF                probability that a file is spam
     * @throws FileNotFoundException
     */
    public static double test(File file) throws FileNotFoundException {
        double n = 0.0;
        double thresh = 0.5;
        double prSF;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String token= scanner.next();
            if (isValidToken(token) && Controller.spamCatches.containsKey(token)) {
                n += (Math.log(1 - Controller.spamCatches.get(token)) - (Math.log(Controller.spamCatches.get(token))));
            }
        }
        prSF = 1 / (1 + Math.pow(Math.E, n));
        if (file.getParent().contains("spam") && prSF > thresh){ Controller.truePos += 1; }
        if (file.getParent().contains("ham") && prSF > thresh){ Controller.falsePos += 1; }
        if (file.getParent().contains("ham") && prSF < thresh){ Controller.trueNeg += 1; }
        Controller.numTestFiles += 1;
        return prSF;
    }

    /** Uses the matches() method to determine id the token is valid in terms of the match regex expression.
     * @param token   word or text entry contained in a file
     * @return        boolean, if matches() is true, return true, otherwise return false
     */
    private static boolean isValidToken(String token){
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return token.matches(allLetters);
    }
}
