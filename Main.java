import java.util.function.DoubleFunction;
import java.util.List;

public class Main {

    public static void main(String [] args) {
        String fileX = "X_train.txt";
        String fileY = "y_train.txt";
        int [] nbHiddenNeurons = {7};
        MultiLayerPerceptron mlp = new MultiLayerPerceptron(561, nbHiddenNeurons, 6);

        Batch b = new Batch(fileX, fileY, 1000, " ");

        List<Double> errors = mlp.trainNTest(b);
        double error = MultiLayerPerceptron.mean(errors);

        System.out.println(errors);
        System.out.println("\n\tTOTAL ERROR :\t" + (float)error*100 + "%");
    }

}
