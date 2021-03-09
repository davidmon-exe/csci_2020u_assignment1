package spamDetector;

public class TestFile {
    private String fileName;
    private String actualClass;
    private double spamProbability;

    public TestFile(String fileName, String actualClass, String spamProb) {
        this.fileName = fileName;
        this.actualClass = actualClass;
        this.spamProbability = Double.parseDouble(spamProb);
    }

    public void setFileName(String value) { this.fileName = value; }
    public String getFileName(){ return this.fileName; }

    public void setActualClass(String value) { this.actualClass = value; }
    public String getActualClass(){return this.actualClass;}

    public void setSpamProbability(double val) { this.spamProbability = val; }
    public double getSpamProbability(){ return this.spamProbability; }

}
