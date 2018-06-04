package com.example.android.lunarlander.net;

import com.example.android.lunarlander.dto.InputDTO;
import com.example.android.lunarlander.dto.OutputDTO;

public final class LearnEntry {

    private InputDTO state;
    private OutputDTO action;
    private float reward;
    private InputDTO nextState;

    public static LearnEntry create(InputDTO previousState, OutputDTO action, float reward, InputDTO currentState) {
        return new LearnEntry(previousState, action, reward, currentState);
    }

    public LearnEntry(InputDTO state, OutputDTO action, float reward, InputDTO nextState) {
        this.state = state;
        this.action = action;
        this.reward = reward;
        this.nextState = nextState;
    }

    public InputDTO getState() {
        return state;
    }

    public OutputDTO getAction() {
        return action;
    }

    public float getReward() {
        return reward;
    }

    public InputDTO getNextState() {
        return nextState;
    }
}
