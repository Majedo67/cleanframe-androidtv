package com.example.tv;

import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.widget.Toast;

public class TvInputHelper {

    public static final String INPUT_HDMI1 = "com.droidlogic.tvinput/.services.Hdmi1InputService/HW5";
    public static final String INPUT_HDMI2 = "com.droidlogic.tvinput/.services.Hdmi2InputService/HW6";
    public static final String INPUT_HDMI3 = "com.droidlogic.tvinput/.services.Hdmi3InputService/HW7";
    public static final String INPUT_AV = "com.droidlogic.tvinput/.services.AV1InputService/HW1";
    public static final String INPUT_LIVETV = "com.droidlogic.tvinput/.services.ADTVInputService/HW16";

    public static void switchToInputNumber(Context context, String port) {
        int number;
        try { number = Integer.parseInt(port); } catch (Throwable ignored) { number = 1; }
        if (number < 1 || number > 4) number = 1;

        try {
            TvInputManager manager = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
            if (manager != null) {
                for (TvInputInfo input : manager.getTvInputList()) {
                    String label = String.valueOf(input.loadLabel(context)).toLowerCase();
                    if (label.contains("hdmi") && (label.contains(String.valueOf(number))
                            || label.contains(number == 1 ? "one" : "___never___"))) {
                        switchToInput(context, input.getId(), "HDMI " + number);
                        return;
                    }
                }
            }
        } catch (Throwable ignored) {}

        String fallbackId = number == 1 ? INPUT_HDMI1 : number == 2 ? INPUT_HDMI2 : number == 3 ? INPUT_HDMI3 : null;
        if (fallbackId != null) {
            switchToInput(context, fallbackId, "HDMI " + number);
        } else {
            Toast.makeText(context, "HDMI 4 input was not exposed by the TV", Toast.LENGTH_LONG).show();
        }
    }

    public static void switchToInput(Context context, String inputId, String displayName) {
        try {
            Uri uri = TvContract.buildChannelUriForPassthroughInput(inputId);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable t) {
            // Fallback for live TV or general TV app
            Intent fallback = context.getPackageManager().getLaunchIntentForPackage("com.android.tv");
            if (fallback != null) {
                fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fallback);
            } else {
                Toast.makeText(context, "Cannot switch to " + displayName, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
