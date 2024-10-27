package com.example.alarmwars;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView blob1 = findViewById(R.id.blob1);
        ImageView blob2 = findViewById(R.id.blob2);

        // Animation for blob1
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(blob1, "translationY", -50f, 50f);
        animator1.setDuration(3000);
        animator1.setRepeatMode(ObjectAnimator.REVERSE);
        animator1.setRepeatCount(ObjectAnimator.INFINITE);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.start();

        // Animation for blob2
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(blob2, "translationX", -30f, 30f);
        animator2.setDuration(4000);
        animator2.setRepeatMode(ObjectAnimator.REVERSE);
        animator2.setRepeatCount(ObjectAnimator.INFINITE);
        animator2.setInterpolator(new LinearInterpolator());
        animator2.start();
    }
}
