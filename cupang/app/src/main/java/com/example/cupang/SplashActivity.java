package com.example.cupang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 5500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(0, 0);

        ImageView logoImage = findViewById(R.id.logoImage);
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvStoreName = findViewById(R.id.tvStoreName);
        TextView tvDescription = findViewById(R.id.Description);
        View circleView = findViewById(R.id.circle);

        View[] viewsToAnimate = new View[]{circleView, logoImage, tvWelcome, tvStoreName, tvDescription};

        for (View view : viewsToAnimate) {
            view.setAlpha(0f);
            view.setTranslationY(100f);
            view.setScaleX(0.7f);
            view.setScaleY(0.7f);
        }

        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < viewsToAnimate.length; i++) {
            final View view = viewsToAnimate[i];
            handler.postDelayed(() -> {
                animateViewWithSpring(view);
            }, 500 * i);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(SplashActivity.this, Home.class);
            } else {
                intent = new Intent(SplashActivity.this, firstpage.class);
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }

    private void animateViewWithSpring(View view) {
        view.animate().alpha(1f).setDuration(400).start();

        SpringAnimation animY = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0);
        animY.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        animY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animY.start();

        SpringAnimation animX = new SpringAnimation(view, DynamicAnimation.SCALE_X, 1f);
        animX.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        animX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animX.start();

        SpringAnimation animScaleY = new SpringAnimation(view, DynamicAnimation.SCALE_Y, 1f);
        animScaleY.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        animScaleY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animScaleY.start();
    }
}