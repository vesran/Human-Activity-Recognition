import java.util.function.DoubleFunction;
import java.util.stream.DoubleStream;
import java.util.Arrays;
import java.util.Random;
import java.util.InputMismatchException;

public class Layer {
    protected Neuron [] neurons;
    protected double [] guess;
    protected double learningRate = 0.9;
    protected DoubleFunction<Double> activationFunc = (x) -> (1 / (1 + Math.exp(-x)));  // Sigmoid function
    protected DoubleFunction<Double> derivActivation = (x) -> (x * (1 - x));
    protected Layer next;
    protected Layer previous;

    public Layer() {
        this.neurons = new Neuron[0];
    }

    public Layer(int nbNeurons, int inputLength) {
        this.neurons = new Neuron [nbNeurons];
        for (int i = 0; i < nbNeurons; i++) {
            this.neurons[i] = new Neuron(inputLength);
        }
    }

    public Layer getNext() {
        return this.next;
    }

    public Layer getPrevious() {
        return this.previous;
    }

    public double [] getGuess() {
        return this.guess;
    }

    public Neuron [] getNeurons() {
        return this.neurons;
    }

    public void setGuess(double [] input) {
        this.guess = input;
    }

    public void setPrevious(Layer another) {
        this.previous = another;
    }

    public void reset() {
        for (Neuron n : this.neurons) {
            n.reset();
        }
    }

    public void addNext(Layer another) {
        this.next = another;
        another.setPrevious(this);
    }

    public void update() {
        for (Neuron n : this.neurons) {
            n.update(this.learningRate, this.getPrevious().getGuess());
        }
    }

    public void updateSignalErrors(double [] error) {
        if (this.neurons.length != error.length) {
            throw new InputMismatchException("Wrong length in updateSignalErrors Layer: " + this.neurons.length + " != " +  error.length);
        } else {
            int i = 0;
            for (Neuron n : this.neurons) {
                n.setSignalError( error[i] * derivActivation.apply(this.getGuess()[i++]) );
            }
        }
    }

    public double [] computeError(double [] realisation) {              // realisation is useless
        double [] error = new double [this.guess.length];
        for (int j = 0; j < error.length; j++) {
            error[j] = 0;
            for (Neuron nNext : this.getNext().getNeurons()) {
                error[j] += (nNext.getWeights()[j] * nNext.getSignalError());
            }
        }
        return error;

    }

    public double [] execute(double [] input) {
        int i = 0;
        this.guess = new double [this.neurons.length];
        for (Neuron n : this.neurons) {
            this.guess[i++] = this.activationFunc.apply(n.execute(input));
        }
        return this.guess;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        for (Neuron n : this.neurons) {
            strb.append(n);
            strb.append("\n");
        }
        return strb.toString();
    }
}
