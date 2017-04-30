package am.xtech.nasaspaceapps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;

import am.xtech.nasaspaceapps.Models.LoginResponse;
import am.xtech.nasaspaceapps.Utils.CheckNetwork;
import am.xtech.nasaspaceapps.Utils.Constants;
import am.xtech.nasaspaceapps.Utils.Methods;
import am.xtech.nasaspaceapps.Utils.WebServiceClient;

public class AuthActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;

    private ImageButton fb_fake_btn;
    private ImageView authFire;

    private String id = "", name = "", gender, email = "", birthday, facebookToken = "";
    private CallbackManager callbackManager ;

    private boolean loginStatus;
    private boolean internetIsConnected = true;

    private RelativeLayout loadingContentAuth;

    int animationT = 0;
    boolean isAnimRight = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDisplaySizes();
        setupFacebook();
        setContentView(R.layout.activity_auth);
        Methods.checkAndRequestPermissions(this);
        prepareObjects();
        loadViews();
        setActivityClickListeners();
        checkNetworkAndAutoLogin();

        setupFireAnim();

    }

    private void setupFireAnim() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Matrix matrix = new Matrix();
                if(isAnimRight) {
                    matrix.preScale(-1.0f, 1.0f);
                    isAnimRight = false;
                }
                else
                {
                    matrix.preScale(1.0f, 1.0f);
                    isAnimRight = true;
                }

                final Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fire_large);
                final Bitmap mirroredBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                authFire.setImageBitmap(mirroredBitmap);
                setupFireAnim();
            }
        }, 70);
    }

    private void setupFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
    }
    private void prepareObjects() {
        sharedpreferences = getSharedPreferences(Constants.My_PREFERENCES, Context.MODE_PRIVATE);
        loginStatus = sharedpreferences.getBoolean("loginSTATUS", false);
    }
    private void checkNetworkAndAutoLogin() {
        if(!CheckNetwork.isInternetAvailable(this))
        {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuthActivity.this);

            //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

            // Setting Dialog Title
            alertDialog.setTitle("No Internet Connection");

            // Setting Dialog Message
            alertDialog.setMessage("Please connect your internet");
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                            startActivity(intent);
                        }
                    });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    internetIsConnected = false;
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(Color.parseColor("#4997dd"));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(Color.parseColor("#4997dd"));
        }
        else
        {
            internetIsConnected = true;
            if(loginStatus)
            {
                Constants.USER_NAME = sharedpreferences.getString("userNAME", "");
                Constants.USER_EMAIL = sharedpreferences.getString("userEMAIL", "");
                Constants.FACEBOOK_ACCES_TOKEN = sharedpreferences.getString("FACEBOOK_ACCES_TOKEN", "");
                String userId = sharedpreferences.getString("userID", "");
                Constants.USER_ID = userId;
                Constants.USER_IMAGE = "https://graph.facebook.com/" + userId + "/picture?type=large";

                if(Constants.USER_ID != null && Constants.USER_NAME != null && Constants.USER_NAME.length() > 0 && Constants.USER_ID.length() > 0)
                {
                    loadingContentAuth.setVisibility(View.VISIBLE);
                    new AsyncLoginAuth().execute();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    int width;
    int height;
    private void getDisplaySizes() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }
    public void loadViews()
    {
        fb_fake_btn = (ImageButton) findViewById(R.id.fb_fake_btn);
        loadingContentAuth = (RelativeLayout) findViewById(R.id.loadingContentAuth);
        authFire = (ImageView) findViewById(R.id.authFire);

        ViewGroup.LayoutParams vGLP = fb_fake_btn.getLayoutParams();
        vGLP.width = vGLP.height = (int) (width / 5.5);
        fb_fake_btn.setLayoutParams(vGLP);
    }
    public void setActivityClickListeners()
    {

        fb_fake_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingContentAuth.setVisibility(View.VISIBLE);
                LoginManager.getInstance().logInWithReadPermissions(AuthActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>()
                        {
                            @Override
                            public void onSuccess(LoginResult loginResult)
                            {
                                // App code

                                final String accessToken = loginResult.getAccessToken()
                                        .getToken();
                                Log.i("accessToken", accessToken);
                                facebookToken = accessToken;

                                GraphRequest request = GraphRequest.newMeRequest(
                                        loginResult.getAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {@Override
                                        public void onCompleted(JSONObject object,
                                                                GraphResponse response) {

                                            Log.i("LoginActivity",
                                                    response.toString());
                                            try {
                                                id = object.getString("id");
                                                name = object.getString("name");
                                                if(object.has("email"))
                                                    email = object.getString("email");
                                                Log.d(Constants.APP_NAME, email);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            new AsyncLoginAuth().execute();
                                            SharedPreferences.Editor editor = sharedpreferences.edit();

                                            if(id != null && id.length() > 0) {
                                                editor.putString("userID", id);
                                                Constants.USER_ID = id;
                                                Constants.USER_IMAGE = "https://graph.facebook.com/" + id + "/picture?type=large";
                                            }
                                            if(name != null && name.length() > 0) {
                                                editor.putString("userNAME", name);
                                                Constants.USER_NAME = name;
                                            }
                                            if(email != null && email.length() > 0) {
                                                editor.putString("userEMAIL", email);
                                                Constants.USER_EMAIL = email;
                                            }
                                            if(facebookToken != null && facebookToken.length() > 0)
                                            {
                                                Constants.FACEBOOK_ACCES_TOKEN = accessToken;
                                                editor.putString("FACEBOOK_ACCES_TOKEN", accessToken);
                                            }
                                            editor.putBoolean("loginSTATUS", true);
                                            editor.apply();


                                        }
                                        });

                                Bundle parameters = new Bundle();
                                parameters.putString("fields",
                                        "id,name,email");
                                request.setParameters(parameters);
                                request.executeAsync();

                            }

                            @Override
                            public void onCancel()
                            {
                                // App code
                                loadingContentAuth.setVisibility(View.GONE);
                                Log.d("CANCEL", "CANCEL");
                            }

                            @Override
                            public void onError(FacebookException exception)
                            {
                                // App code
                                loadingContentAuth.setVisibility(View.GONE);
                                Log.e("ERROR", exception.toString());
                            }
                        });
            }
        });
    }
    private class AsyncLoginAuth extends AsyncTask<String, Void, LoginResponse> {

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected LoginResponse doInBackground(final String... args) {
            try {

                LoginResponse loginObject = WebServiceClient.loginTask(Constants.USER_ID, Constants.USER_NAME);
                return loginObject;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final LoginResponse getObject) {

            if (getObject != null) {
                if (getObject.isError()) {

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuthActivity.this);

                        //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Something went wrong");

                        // Setting Positive "Yes" Button
                        alertDialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new AsyncLoginAuth().execute();
                                        loadingContentAuth.setVisibility(View.VISIBLE);
                                    }
                                });
                        AlertDialog alert = alertDialog.create();
                        alert.show();
                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(Color.parseColor("#4997dd"));
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.parseColor("#4997dd"));

                } else {
                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }
            }
            loadingContentAuth.setVisibility(View.GONE);
        }
    }
}
