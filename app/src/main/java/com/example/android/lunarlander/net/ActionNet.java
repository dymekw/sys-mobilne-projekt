package com.example.android.lunarlander.net;

import android.annotation.TargetApi;
import android.os.Build;

import com.example.android.lunarlander.dto.Action;
import com.example.android.lunarlander.dto.InputDTO;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ActionNet {

    private static final int BUFFER_SIZE = 2000;
    private static final int BATCH_SIZE = 32;

    private static final int STATE_SIZE = 8;
    private static final int ACTION_SIZE = 6;
    private static final double GAMMA = 0.95;
    private static final double EPSILON_MIN = 0.25;
    private static final double EPSILON_DECAY = 0.999995;
    private double epsilon = 1.0;

    private Graph graph = new Graph();
    private Session session;
    private List<LearnEntry> memory = new ArrayList<>(BUFFER_SIZE);

    private Random random = new Random();

    @TargetApi(Build.VERSION_CODES.O)
    public void init() throws IOException {
        graph.importGraphDef(Files.readAllBytes(Paths.get("app/src/main/res/model.proto")));
        session = new Session(graph);
        session.runner().addTarget("init").run();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void fit() { //maybe return loss

        if (memory.size() < BATCH_SIZE) {
            return;
        }

        List<LearnEntry> inputs = getRandomLearningEntry(BATCH_SIZE);
        List<InputDTO> inputStates = inputs.stream().map(LearnEntry::getState).collect(Collectors.toList());
        Tensor<Float> input = getStateAsTensor(inputStates);

        float[][] predictedRewards = predict(inputStates);
        for (int i = 0; i < inputs.size(); i++) {
            LearnEntry learnEntry = inputs.get(i);
            Action action = learnEntry.getAction().actions.get(0);

            float reward = learnEntry.getReward();
            float maxFutureReward = getMaxReward(predict(Arrays.asList(learnEntry.getNextState()))[0]);
            reward = (float) (reward + GAMMA * maxFutureReward);

            predictedRewards[i][action.ordinal()] = reward;
        }

        Tensor<Float> output = Tensors.create(predictedRewards);

//        float[][] results = new float[inputs.size()][ACTION_SIZE];
//        Tensor<Float> result =
        session.runner()
                .feed("x", input)
                .feed("y", output)
                .fetch("result")
                .addTarget("train")
                .run().get(0).expect(Float.class);

//        result.copyTo(results);
//        for (float[] vector : results) {
//            System.out.println(Arrays.toString(vector));
//        }

        if (epsilon >= EPSILON_MIN) {
            epsilon = epsilon * EPSILON_DECAY;
        }
    }

    private List<LearnEntry> getRandomLearningEntry(int size) {

        List<LearnEntry> result = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            int index = random.nextInt(memory.size());
            result.add(memory.get(index));
        }
        return result;
    }

    private float getMaxReward(float[] rewards) {
        float max = -Float.MIN_VALUE;
        for (float reward : rewards) {
            if (max < reward) {
                max = reward;
            }
        }
        return max;
    }

    public float[][] predict(List<InputDTO> states) {

        Tensor<Float> input = getStateAsTensor(states);

        Tensor<Float> result = session.runner()
                .feed("x", input)
                .fetch("result")
                .run().get(0).expect(Float.class);

        float[][] results = new float[states.size()][ACTION_SIZE];
        result.copyTo(results);
//        for (float[] vector : results) {
//            System.out.println(Arrays.toString(vector));
//        }

        return results;
    }

    public void remember(LearnEntry input) {
        if (memory.size() == BUFFER_SIZE) {
            memory.remove(0);
        }
        memory.add(input);
    }

    private Tensor<Float> getStateAsTensor(List<InputDTO> states) {

        float[][] inputMatrix = new float[states.size()][STATE_SIZE];

        for (int i = 0; i < states.size(); i++) {
            inputMatrix[i] = getStateAsArray(states.get(i));
        }

        return Tensors.create(inputMatrix);
    }

    private float[] getStateAsArray(InputDTO state) {

        float[] input = new float[STATE_SIZE];
        input[0] = (float) state.x;
        input[1] = (float) state.y;
        input[2] = (float) state.headerAngle;
        input[3] = (float) state.velocityX;
        input[4] = (float) state.velocityY;
        input[5] = (float) state.fuel;
        input[6] = (float) state.goalX;
        input[7] = (float) state.goalWidth;
        return input;
    }

    public double getEpsilon() {
        return epsilon;
    }

}
