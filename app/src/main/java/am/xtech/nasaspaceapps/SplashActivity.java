package am.xtech.nasaspaceapps;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import am.xtech.nasaspaceapps.Utils.Methods;
import com.google.android.gms.vision.text.Line;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {

    private ImageView splachRocket;
    private ImageView splashNasaLogo;
    private ImageView splashFireImageView;
    private TextView splashTextView;
    private LinearLayout rocketContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "am.xtech.nasaspaceapps",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        prepareViews();

        setupAnimation();

        setupFinishSplash();
    }

    private void setupFinishSplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        }, 2000);
    }

    private void setupAnimation() {
        AnimationSet snowMov1 = new AnimationSet(true);
        RotateAnimation rotate1 = new RotateAnimation(0,-45, Animation.RELATIVE_TO_SELF,0.5f , Animation.RELATIVE_TO_SELF,0.5f );
        rotate1.setInterpolator(new LinearInterpolator());
        rotate1.setFillEnabled(true);

        rotate1.setFillAfter(true);
        rotate1.setDuration(500);
        splachRocket.setAnimation(rotate1);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Matrix matrix = new Matrix();
//                splachRocket.setScaleType(ImageView.ScaleType.MATRIX);   //required
//                matrix.postRotate((float) -45, 0.5f, 0.5f);
//                splachRocket.setImageMatrix(matrix);
//            }
//        }, 300);

        TranslateAnimation trans1 =  new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.5f);;
        trans1.setDuration(1500);
//        snowMov1.addAnimation(trans1);
        rocketContent.setAnimation(trans1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splachRocket.setVisibility(View.GONE);
            }
        }, 1450);

//        TranslateAnimation trans2 =  new TranslateAnimation(
//                Animation.RELATIVE_TO_PARENT, 0.0f,
//                Animation.RELATIVE_TO_PARENT, 0.0f,
//                Animation.RELATIVE_TO_PARENT, 0.0f,
//                Animation.RELATIVE_TO_PARENT, -1.5f);;
//        trans1.setDuration(1000);
//        splashNasaLogo.setAnimation(trans2);

        Animation anim = new ScaleAnimation(
                1f, 0f, // Start and end values for the X axis scaling
                1f, 0f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(1500);
        splashNasaLogo.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashFireImageView.setVisibility(View.VISIBLE);
                Animation anim = new ScaleAnimation(
                        0f, 1f, // Start and end values for the X axis scaling
                        0f, 1f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                anim.setFillAfter(true); // Needed to keep the result of the animation
                anim.setDuration(500);
                splashFireImageView.startAnimation(anim);
                int colorFrom = Color.parseColor("#00ffffff");
                int colorTo = Color.parseColor("#ffffffff");
                Methods.colorAnimator(splashTextView, colorFrom, colorTo);
            }
        }, 1000);
    }

    private void prepareViews() {
        splachRocket = (ImageView) findViewById(R.id.splachRocket);
        splashNasaLogo = (ImageView) findViewById(R.id.splashNasaLogo);
        splashFireImageView = (ImageView) findViewById(R.id.splashFireImageView);
        splashTextView = (TextView) findViewById(R.id.splashTextView);
        rocketContent = (LinearLayout) findViewById(R.id.rocketContent);
    }
}
