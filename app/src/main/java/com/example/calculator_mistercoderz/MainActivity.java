package com.example.calculator_mistercoderz;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    TextView display;
    String expression = "";

    // Sound variables
    private SoundPool soundPool;
    private int beepSoundId;
    private boolean soundLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        // Initialize sound pool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        // Load beep sound
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            soundLoaded = true;
        });
        beepSoundId = soundPool.load(this, R.raw.beep, 1);
    }

    private void playBeep() {
        if (soundLoaded) {
            soundPool.play(beepSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void onDigitClick(View view) {
        playBeep();
        Button button = (Button) view;
        expression += button.getText().toString();
        display.setText(expression);
    }

    public void onOperatorClick(View view) {
        playBeep();
        Button button = (Button) view;
        expression += button.getText().toString();
        display.setText(expression);
    }

    public void onClearClick(View view) {
        playBeep();
        expression = "";
        display.setText("");
    }

    public void onEqualClick(View view) {
        playBeep();
        try {
            Context rhino = Context.enter();
            rhino.setOptimizationLevel(-1);
            Scriptable scope = rhino.initStandardObjects();
            Object result = rhino.evaluateString(scope, expression, "JavaScript", 1, null);
            display.setText(result.toString());
            expression = result.toString();
        } catch (Exception e) {
            display.setText("Error");
            expression = "";
        } finally {
            Context.exit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}