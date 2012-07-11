package org.simpleml.classify;

import org.simpleml.struct.ArrayVector;
import org.simpleml.struct.LabeledVector;
import org.simpleml.struct.MutableVector;
import org.simpleml.struct.Vector;

import java.util.Iterator;

/**
 * See the paper: Koby Crammer at al. 2006. Online Passive-Aggressive Algorithms.
 *
 * @author rasmikun
 * @author sitfoxfly
 */
public class PassiveAggressivePerceptron implements Classifier {

    public static enum AlgorithmType {
        PA1,
        PA2,
        PA3
    }

    private static final AlgorithmType DEFAULT_ALGORITHM = AlgorithmType.PA2;
    private static final double DEFAULT_AGGRESSIVENESS = 0.5;
    private static final int DEFAULT_NUM_ITERATION = 100;

    private double aggressiveness = DEFAULT_AGGRESSIVENESS;
    private int numIteration = DEFAULT_NUM_ITERATION;
    private AlgorithmType algorithm = DEFAULT_ALGORITHM;

    private MutableVector w;

    public PassiveAggressivePerceptron(int dimension) {
        w = new ArrayVector(dimension);
    }

    private double calcSquaredL2(Vector vector) {
        final Iterator<Vector.Entry> sparseIterator = vector.sparseIterator();
        double squaredL2 = 0d;
        while (sparseIterator.hasNext()) {
            double value = sparseIterator.next().getValue();
            squaredL2 += value * value;
        }
        return squaredL2;
    }

    private double getLR1(Vector vector, double lossValue) {
        return lossValue / calcSquaredL2(vector);
    }

    private double getLR2(LabeledVector labeledVector, double lossValue) {
        return Math.min(aggressiveness, getLR1(labeledVector, lossValue));
    }

    private double getLR3(LabeledVector labeledVector, double lossValue) {
        double squaredL2 = calcSquaredL2(labeledVector);
        return lossValue / (squaredL2 + 0.5 / aggressiveness);
    }

    public void train(Iterable<LabeledVector> data) {
        for (int i = 0; i < numIteration; i++) {
            for (LabeledVector labeledVector : data) {
                double lossValue = Math.max(0d, 1 - labeledVector.getLabel() * w.innerProduct(labeledVector.getInnerVector()));
                double learningRate = 0d;
                switch (algorithm) {
                    case PA1:
                        learningRate = getLR1(labeledVector, lossValue);
                        break;
                    case PA2:
                        learningRate = getLR2(labeledVector, lossValue);
                        break;
                    case PA3:
                        learningRate = getLR3(labeledVector, lossValue);
                        break;
                }
                w.addToThis(labeledVector, learningRate * labeledVector.getLabel());
            }
        }
    }

    @Override
    public int classify(Vector vector) {
        return (int) Math.signum(w.innerProduct(vector));
    }

    public double getAggressiveness() {
        return aggressiveness;
    }

    public void setAggressiveness(double aggressiveness) {
        this.aggressiveness = aggressiveness;
    }

    public void setNumIteration(int numIteration) {
        this.numIteration = numIteration;
    }

    public int getNumIteration() {
        return numIteration;
    }

    public void setAlgorithm(AlgorithmType e) {
        algorithm = e;
    }

    public AlgorithmType getAlgorithm() {
        return algorithm;
    }
}