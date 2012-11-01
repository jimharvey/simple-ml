package org.simpleml.classify;

import junit.framework.Assert;
import org.junit.Test;
import org.simpleml.struct.LabeledVector;

import java.util.List;

/**
 * @author rasmikun
 */
public class PassiveAggressivePerceptronTest {

    @Test
    public void testPAPerceptron() throws Exception {
        final PassiveAggressivePerceptron perceptron = new PassiveAggressivePerceptron(TestUtil.DATA_SIZE);
        final List<LabeledVector> trainingData = TestUtil.getSimpleTrainingData();

        perceptron.train(trainingData);
        for (LabeledVector vector : trainingData) {
            int predictedLabel = perceptron.classify(vector.getInnerVector());
            Assert.assertEquals(vector.getLabel(), predictedLabel);
        }
    }
}
