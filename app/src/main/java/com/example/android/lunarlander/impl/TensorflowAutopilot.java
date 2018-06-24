package com.example.android.lunarlander.impl;

import android.content.res.AssetManager;
import android.support.v4.os.TraceCompat;

import com.example.android.lunarlander.Autopilot;
import com.example.android.lunarlander.dto.Action;
import com.example.android.lunarlander.dto.InputDTO;
import com.example.android.lunarlander.dto.OutputDTO;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class TensorflowAutopilot implements Autopilot{

    private TensorFlowInferenceInterface inferenceInterface;

    public TensorflowAutopilot(AssetManager assetManager) {
        inferenceInterface = new TensorFlowInferenceInterface(assetManager, "file:///android_asset/mnist_model_graph.pb");
    }


    @Override
    public OutputDTO getActions(InputDTO input) {
        float[] in = {
                (float)input.x,
                (float)input.y,
                (float)(input.headerAngle > 180 ? input.headerAngle-360 : input.headerAngle),
                (float)input.velocityX,
                (float)input.velocityY,
                (float)input.fuel,
                (float)input.goalX,
                (float)input.goalWidth};

        OutputDTO outputDTO = new OutputDTO();

        TraceCompat.beginSection("autopilot");
        TraceCompat.beginSection("feed");
        inferenceInterface.feed("input", in, new long[]{2 * 4});
        TraceCompat.endSection();

        TraceCompat.beginSection("run");
        inferenceInterface.run(new String[]{"output"}, false);
        TraceCompat.endSection();

        float outputs[] = new float[6];
        TraceCompat.beginSection("fetch");
        inferenceInterface.fetch("output", outputs);
        TraceCompat.endSection();

        int maxIndex = 0;
        for(int i=0; i<outputs.length; i++) {
            if (outputs[i] > outputs[maxIndex]) {
                maxIndex = i;
            }
        }

        if (maxIndex >=3) {
            outputDTO.actions.add(Action.ACCEL);
        }
        if (maxIndex == 1 || maxIndex == 4) {
            outputDTO.actions.add(Action.RIGHT);
        }
        if (maxIndex == 2 || maxIndex == 5) {
            outputDTO.actions.add(Action.LEFT);
        }

        return outputDTO;
    }

    @Override
    public int getPeriod() {
        return 50;
    }
}
