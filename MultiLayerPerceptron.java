import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.InputMismatchException;

public class MultiLayerPerceptron {
    private Layer layer;

    public MultiLayerPerceptron(int inputLength, int [] nbHiddenNeurons, int nbClasses) {
        Layer current;
        Layer save;

        this.layer = new Layer();
        this.layer.addNext( new Layer(nbHiddenNeurons[0], inputLength) );
        current = this.layer.getNext();
        for (int i = 1; i < nbHiddenNeurons.length; i++) {
            current.addNext( new Layer(nbHiddenNeurons[i], nbHiddenNeurons[i - 1]));
            current = current.getNext();
        }
        current.addNext( new OutputLayer(nbClasses, nbHiddenNeurons[nbHiddenNeurons.length - 1]) );
    }

    public void reset() {
        Layer current = this.layer;
        while (current != null) {
            current.reset();
            current = current.getNext();
        }
    }

    private void seeForComparing(List<Integer> predictions, List<Integer> realisations) {
        System.out.println("Predictions : ");
        predictions.stream().forEach( x->System.out.print(x + ", "));
        System.out.println();

        System.out.println("Realisations : ");
        realisations.stream().map( x->x+1 ).forEach( x->System.out.print(x + ", "));        // +1 so that class 0 becomes class 1
        System.out.println();
    }

    /**
    *   Calculates the pourcentage of error by comparing the two given lists.
    */
    public double totalError(List<Integer> predictions, List<Integer> realisations) {
        if (predictions.size() != realisations.size()) {
            System.out.println("Wrong sizes");
            return -1.0;
        } else {
            double badCard = 0.0;
            for (int i = 0; i < predictions.size(); i++) {
                if (predictions.get(i) != (realisations.get(i) + 1))  badCard += 1.0;   // +1 to remove 0 from the count
            }
            return (badCard / predictions.size() );
        }
    }

    /**
    *   Makes a prediction from the specified batches input. The neural network should be trained before.
    */
    public List<Integer> predict(Batch input) {
        List<double []> indivs = input.getIndivs();
        List<Integer> realisations = input.getRealisations();
        List<Integer> predictions = new ArrayList<>();
        int pred;

        for (double [] someone : indivs) {
            pred = 1 + MultiLayerPerceptron.getBelongClass(this.feedForward(someone).getGuess()); // +1 to get into the range [1; 6]
            predictions.add(pred);
        }

        return predictions;
    }

    /**
    *   Makes a prediction from the specified input. The neural network should be trained before.
    */
    public void predict(double [] input) {
        double [] finalGuess = this.feedForward(input).getGuess();
        System.out.println("Results : ");
        Arrays.stream(finalGuess).forEach(System.out::println);

        System.out.println("\nInput belongs to class : " + MultiLayerPerceptron.getBelongClass(finalGuess));
    }

    private Layer feedForward(double [] input) {
        Layer save = null;
        Layer current = (this.layer.getNeurons().length != 0) ? this.layer : this.layer.getNext();  // Skip the input layer
        this.layer.setGuess(input);             // guess' input layer becomes the actual input

        while (current != null) {
            input = current.execute(input);
            save = current;
            current = current.getNext();
        }
        return save;
    }

    private void backwardPropagation(int belongClass, Layer last) {
        Layer current = last;
        double [] error;
        double [] expected = MultiLayerPerceptron.getElementary(belongClass, current.getGuess().length);

        while (current != null && current.getPrevious() != null) {      // Case of input layer
            // Layer get its error
            error = current.computeError(expected);

            // Compute error signal for each neuron
            current.updateSignalErrors(error);

            // Update weights
            current.update();

            // To the previous layer
            current = current.getPrevious();
        }
    }

    /**
    *   Trains the neural network on all the batches.
    */
    public void train(Batch batch) {
        if (batch == null)  throw new InputMismatchException("Can't train a null batch.");
        Batch current = batch;
        while (current != null) {
            this.trainOneBatch(current);
            current = current.getNext();
            System.out.println("One batch trained.");
        }
    }

    /**
    *  Trains the neural network on one batch.
    */
    public void trainOneBatch(Batch batch) {
        if (batch == null)  throw new InputMismatchException("Can't train a null batch.");
        Random rand = new Random();
        int randIndex;
        Layer last;
        int epoch = 20 * batch.size();      // Change the value to modify the execution speed

        for (int i = 0; i < epoch; i++) {
            // Generate random index for a random indiv
            randIndex = rand.nextInt(batch.size());

            // feedforward
            last = this.feedForward(batch.getIndivs().get(randIndex));

            // backward propagation
            this.backwardPropagation(batch.getRealisations().get(randIndex), last);
        }
    }

    /**
    *   Trains a neural network by skipping a batch.
    */
    public void trainButSkip(Batch start, Batch toSkip) {
        if (start == null || toSkip == null)  throw new InputMismatchException("Can't train or skip a null batch.");
        if (start.getNext() == null) throw new InputMismatchException("There is only one batch, nothing to skip.");
        Batch current = start;

        while (current != null) {
            if (!current.equals(toSkip)) {
                this.trainOneBatch(current);
            }
            current = current.getNext();
        }
    }

    /**
    *   Tests the neural network and returns the errors in a list.
    */
    public List<Double> trainNTest(Batch first) {
        if (first == null)  throw new InputMismatchException("Can't train a null batch.");
        List<Double> errors = new ArrayList<>();
        List<Integer> predictions;
        Batch toSkip = first;
        int i = 1;

        System.out.println("Starting the training...");
        while (toSkip != null) {
            System.out.println("........Skipping batch " + i++);
            this.trainButSkip(first, toSkip);
            predictions = this.predict(toSkip);
            errors.add(this.totalError(predictions, toSkip.getRealisations()));

            toSkip = toSkip.getNext();

            this.reset();
        }

        return errors;
    }

    /**
    *   Creates a file filled with the guessed realisations.
    */
    public void write(String filename, double [] guess) {
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);

            for (Double value : guess) {
                bw.write(String.valueOf(value));
            }

            bw.close();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    *   Get an elementary vector (array) with a 1 in the target index
    */
    private static double [] getElementary(int target, int size) {
        double [] result = new double [size];
        for (int i = 0; i < size; i++) {
            result[i] = ((i == target) ? 1 : 0);
        }
        return result;
    }

    /**
    *   Returns the index of an array with the maximum value.
    */
    private static int getBelongClass(double [] guess) {
        int max = 0;
        for (int i = 0; i < guess.length; i++) {
            if (guess[i] > guess[max])  max = i;
        }
        return max;
    }

    /**
    *   Compute mean values from list.
    */
    public static double mean(List<Double> values) {
        if (values == null) return -1.0;
        double sum = 0.0;

        for (Double val : values) {
            if (val == -1.0)    return -1.0;
            sum += val;
        }

        return sum / values.size();
    }

    @Override
    public String toString() {
        Layer current = this.layer;
        StringBuilder strb = new StringBuilder();
        while (current != null) {
            strb.append(current);
            strb.append("\n");
            current = current.getNext();
        }
        return strb.toString();
    }

    public void show() {
        System.out.println(this);
    }
}
