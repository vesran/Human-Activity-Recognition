import java.util.function.DoubleFunction;
import java.lang.Math;

public class OutputLayer extends Layer {

    public OutputLayer(int nbNeurons, int inputLength) {
        super(nbNeurons, inputLength);
    }

    @Override
    public double [] computeError(double [] realisation) {
        if (this.guess.length != realisation.length) {
            System.out.println("Wrong length in OutputLayer computeError.");
            return null;
            
        } else {
            double [] error = new double [this.guess.length];
            for (int i = 0; i < error.length; i++) {
                error[i] = realisation[i] - this.guess[i];
            }
            return error;
        }
    }

    @Override
    public Layer getNext() {
        return null;
    }

}
