package spamDetector;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import spamDetector.test.Tester;
import spamDetector.train.HamTrainer;
import spamDetector.train.SpamTrainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Controller extends AnchorPane {

    // various fxml objects
    @FXML public Button trainButton;
    @FXML public Button testButton;
    @FXML public TextField accuracyField;
    @FXML public TextField precisionField;
    @FXML public TextField trainLocation;
    @FXML public TextField testLocation;
    @FXML public ImageView imageView;
    @FXML private TableView<TestFile> tableView;
    @FXML private TableColumn<TestFile, String> fileNameCol;
    @FXML private TableColumn<TestFile, String> actualClassCol;
    @FXML private TableColumn<TestFile, Double> spamProbCol;

    // TreeMaps used in training ham files
    public static TreeMap<String, Double> trainHamFreq = new TreeMap<String, Double>();
    public static TreeMap<String, Integer> hamCount = new TreeMap<String, Integer>();

    // TreeMaps used  in training spam files
    public static TreeMap<String, Double> trainSpamFreq = new TreeMap<String, Double>();
    public static TreeMap<String, Integer> spamCount = new TreeMap<String, Integer>();

    // TreeMap containing spam words
    public static TreeMap<String, Double> spamCatches = new TreeMap<String, Double>();

    public static double truePos;
    public static double falsePos;
    public static double trueNeg;
    public static double numTestFiles;

    @FXML
    public void initialize() throws IOException {}

    /**
     * Handle selection of the Train button. A folder selector will open, where the train directory will be selected.
     * The directory is passed along to the processing method, and after that completes calculateProbSW() is called
     * @param event
     * @throws IOException
     */
    @FXML
    private void handleTrain(ActionEvent event) throws IOException {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        File dir = dc.showDialog(null);
        if (dir != null) {
            String path = dir.getPath();
            trainLocation.setText(path);
            processDirTrain(dir);
            calculateProbSW();
        }
    }

    /**
     * Determines what email class the given directory is, then pass directory along to appropriate trainer
     * @param file                  directory
     * @throws FileNotFoundException
     */
    private void processDirTrain(File file) throws FileNotFoundException {
        if (file.isDirectory()) {
            if (file.getName().equals("ham")) {
                HamTrainer.trainHam(file);
                System.out.println("Trained from Ham Folder");
            } else if (file.getName().equals("spam")) {
                SpamTrainer.trainSpam(file);
                System.out.println("Trained from Spam Folder");
            } else {
                File[] files = file.listFiles();
                for (int n = 0; n < Objects.requireNonNull(files).length; n++) {
                    processDirTrain(files[n]);
                }
            }
        }
    }

    /**
     * Calculates the probability that a file is spam, given that it contains a word found in spam
     */
    private void calculateProbSW() {
        for (Map.Entry<String, Double> entry : trainSpamFreq.entrySet()){
            if (trainHamFreq.containsKey(entry.getKey())) {
                double prSW = entry.getValue() / (entry.getValue() + trainHamFreq.get(entry.getKey()));
                spamCatches.put(entry.getKey(), prSW);
            }
        }
    }
    /** Handle selection of the Test button. A folder selector will open, where the test directory will be selected.
     * The directory is passed along to the processing method. After that has finished running, accuracy and precision
     * are calculated.
     * @param event
     * @throws FileNotFoundException
     */
    @FXML
    private void handleTest(ActionEvent event) throws FileNotFoundException {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        File dir = dc.showDialog(null);
        if (dir != null) {
            String path = dir.getPath();
            testLocation.setText(path);
            processDirTest(dir);

            DecimalFormat df = new DecimalFormat("0.00000");
            double accuracy = (truePos + trueNeg) / numTestFiles;
            accuracyField.setText(df.format(accuracy));

            double precision = truePos / (falsePos + trueNeg);
            precisionField.setText(df.format(precision));
        }
    }
    /**
     * Determine whether given dir is actually a dir, if not, recursive call back. Pass dir along to test() method in
     * Tester. Table entries are then added
     * @param dir                  directory
     * @throws FileNotFoundException
     */
    private void processDirTest(File dir) throws FileNotFoundException {
        if (dir.isDirectory()) {
            File[] file = dir.listFiles();
            for (int n = 0; n < Objects.requireNonNull(file).length; n++) {
                processDirTest(file[n]);
            }
        } else if (dir.exists()) {
            double spamProb = 0.0;
            spamProb = Tester.test(dir);
            DecimalFormat decimalFormat = new DecimalFormat("0.00000");
            if (dir.getParent().contains("ham")) {
                tableView.getItems().add(new TestFile(dir.getName(),"Ham", decimalFormat.format(spamProb)));
            } else {
                tableView.getItems().add(new TestFile(dir.getName(),"Spam",decimalFormat.format(spamProb)));
            }
        }
    }
}