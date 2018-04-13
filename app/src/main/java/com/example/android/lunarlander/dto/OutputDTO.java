package com.example.android.lunarlander.dto;

import java.util.LinkedList;
import java.util.List;

public class OutputDTO {
    public List<Action> actions = new LinkedList<>();

    @Override
    public String toString() {
        String a = "";
        for (Action action:actions) {
            a += action.toString() + " ";
        }
        return "OutputDTO{" +
                "actions=" + a +
                '}';
    }
}
