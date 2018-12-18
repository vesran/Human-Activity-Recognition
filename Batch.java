import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.DoubleStream;

public class Batch {
    private List<double []> indivs;
    private List<Integer> realisations;
    private Batch next;

    public Batch() {
        this.indivs = new ArrayList<>();
        this.realisations = new ArrayList<>();
    }

    public Batch(String fileX, String fileY, int length, String separator) {
        this();
        this.fill(fileX, fileY, length, separator);
    }

    public int size() {
        return this.indivs.size();
    }

    public Batch getNext() {
        return this.next;
    }

    public List<double []> getIndivs() {
        return this.indivs;
    }

    public List<Integer> getRealisations() {
        return this.realisations;
    }

    /**
    *   Getter for the i-th data line.
    */
    public double [] getIndivI(int i) {
        return this.indivs.get(i);
    }

    /**
    *   Getter for the i-th realisation.
    */
    public int getRealisationI(int i) {
        return this.realisations.get(i);
    }

    /**
    *   Add a realisation in this batch.
    */
    private void addRealisation(String line, String separator) {
        int value = Integer.parseInt( line.trim() );
        this.realisations.add(value - 1);               // Get the range [0, 5]
    }

    /**
    *   Add a line of data in this batch.
    */
    private void addIndiv(String line, String separator) {
        double [] array;
        array = Arrays.stream(line.trim().split(separator))
                            .filter(x->!x.equals(""))
                            .mapToDouble(Double::parseDouble)
                            .map( x->x + 0.6 )        // Standardization : esp(X) = -0.6
                            .toArray();
        this.indivs.add(array);
    }

    /**
    *   Returns a boolean value according to the BufferedReaders (if the batch could have been filled or not)
    */
    private boolean fill(BufferedReader brX, BufferedReader brY, int length, String separator) throws IOException {
        String lineX = brX.readLine();
        String lineY = brY.readLine();
        double [] array;
        int cpt = 0;

        while (lineX != null && lineY != null && cpt++ < length) {
            this.addIndiv(lineX, separator);
            this.addRealisation(lineY, separator);

            lineX = brX.readLine();
            lineY = brY.readLine();
        }

        if (cpt == 0) {
            return false;       // This batch couldn't have been filled.
        } else {
            return true;        // This batch have been filled.
        }
    }

    /**
    *   Fill a set of Batch linked together. Data from 2 files, X for input data, Y for output data.
    */
    public void fill(String fileX, String fileY, int length, String separator) {
        if (length <= 0)    throw new InputMismatchException("The length of each batch must be a positive integer.");
        BufferedReader brX = null;
        BufferedReader brY = null;
        Batch current = this;
        Batch another = new Batch();
        int i = 0;

        try {
            brX = new BufferedReader(new FileReader(fileX));
            brY = new BufferedReader(new FileReader(fileY));

            current.fill(brX, brY, length, separator);

            System.out.println("Loading data...");
            while (another.fill(brX, brY, length, separator)) {
                if (another.indivs.size() > 0.8 * length) {     // 80% is arbitrary
                    current.next = another;
                    current = current.next;
                    System.out.println("\tOne batch done (" + ++i + ")");

                } else {
                    System.out.println("\tOne batch of size " + another.size() + " has been omitted due to its relative low length.");
                }
                another = new Batch();                          // Reinit batch
            }
            System.out.println("Number of batches : " + i);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            System.out.println("All Batches are ready.");
        }
    }

    @Deprecated
    public void fill(String filename, String separator) {
        FileReader fr  = null;
        BufferedReader br = null;
        String line;
        double [] array;

        int limit = 0;

        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null && limit++ < 100) {
                array = Arrays.stream(line.trim().split(separator))
                                    .filter( x->x.equals("") )
                                    .mapToDouble(Double::parseDouble)
                                    .toArray();
                this.indivs.add(Arrays.copyOfRange(array, 0, array.length - 1));
                this.realisations.add((int)array[array.length - 1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
