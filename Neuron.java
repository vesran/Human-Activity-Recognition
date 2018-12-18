import java.util.function.DoubleFunction;
import java.util.InputMismatchException;
import java.util.Random;

public class Neuron {
    double [] weights;
    double biais;
    double signalError;

    public Neuron(int inputLength) {
        this.weights = Neuron.generateWeights(inputLength);
        this.biais = (new Random()).nextDouble() * 2 - 1;    // Get random values in range [-1; 1]
    }

    private static double [] generateWeights(int length) {
        Random rand = new Random();
        double [] weights = new double [length];

        for (int i = 0; i < weights.length; i++) {
            weights[i] = rand.nextDouble() * 2 - 1;         // Get random values in range [-1; 1]
        }

        return weights;
    }

    public double getSignalError() {
        return this.signalError;
    }

    public double [] getWeights() {
        return this.weights;
    }

    public void reset() {
        this.weights = Neuron.generateWeights(this.weights.length);
        this.biais = (new Random()).nextDouble() * 2 - 1;
    }

    public void setSignalError(double signalError) {
        this.signalError = signalError;
    }

    public void update(double learningRate, double [] input) {
        this.biais += (learningRate * this.signalError);            // Updating biais
        for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] += (learningRate * this.signalError * input[i]) ;
        }
    }

    public double execute(double [] input) {
        if (input == null || this.weights.length != input.length) {
            throw new InputMismatchException("Wrong length in execute Neuron." + this.weights.length + " != " + input.length);
        } else {
            double sum = 0.0;
            int i = 0;
            for (Double weight : this.weights) {
                sum += (weight * input[i++]);
            }
            return sum + this.biais;
        }
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder(this.biais + "||");
        for (double weight : this.weights) {
            strb.append(weight);
            strb.append(" ");
        }
        return strb.toString();
    }

}
