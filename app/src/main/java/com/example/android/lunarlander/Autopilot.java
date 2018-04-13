package com.example.android.lunarlander;

import com.example.android.lunarlander.dto.InputDTO;
import com.example.android.lunarlander.dto.OutputDTO;

public interface Autopilot {

    default int getPeriod() {
        return 1500;
    };

    OutputDTO getActions(InputDTO input);
}
