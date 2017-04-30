package am.xtech.nasaspaceapps;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import am.xtech.nasaspaceapps.Models.FireAddResponse;
import am.xtech.nasaspaceapps.Models.FireModel;
import am.xtech.nasaspaceapps.Models.GetFiresResponse;
import am.xtech.nasaspaceapps.Models.NasaFireModel;
import am.xtech.nasaspaceapps.Services.FetchAddressIntentService;
import am.xtech.nasaspaceapps.Utils.AppUtils;
import am.xtech.nasaspaceapps.Utils.Constants;
import am.xtech.nasaspaceapps.Utils.ImagePickerComment;
import am.xtech.nasaspaceapps.Utils.Methods;
import am.xtech.nasaspaceapps.Utils.VideoPicker;
import am.xtech.nasaspaceapps.Utils.WebServiceClient;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.internal.ShareFeedContent;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private SharedPreferences preferences;
    private GoogleMap mMap;
    private RelativeLayout takePhotoButton;
    private RelativeLayout loading_content;
    private RelativeLayout videoContentMain;
    private int width, height;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private AddressResultReceiver mResultReceiver;

    private TextView addressTextView;
    private LinearLayout shareFireContent;
    private LinearLayout logoutButton;
    private ImageButton nasaButton;
    private boolean nasaIsActive = false;
    private ImageButton rocketButton;
    private ImageButton weatherButton;
    private ImageButton fbShareButton;
    private ImageButton playVideoButton;
    private ImageButton camerActionButton;
    private ImageButton videoActionButton;
    private ImageView captureddImageView;
    private RelativeLayout closePopUpButton;
    private RelativeLayout actionContent;
    private RelativeLayout touch_content;
    private ImageButton socaialSharingButton;
    private EditText descriptionEditText;
    private VideoView videoView;

    private SharedPreferences sharedpreferences;

    private Uri sharingFile;

    boolean activeChangeMap = true;
    private double mLat, mLong;
    private String descriptionForUpload;
    private Bitmap shareBitmap;

    private boolean isVideo = false;
    private String uploadAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(Constants.My_PREFERENCES, Context.MODE_PRIVATE);
        SupportMapFragment fragment;
        fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        Methods.checkAndRequestPermissions(MainActivity.this);

        preferences = getSharedPreferences(Constants.APP_NAME, MODE_PRIVATE);

        prepareViews();
        measureViews();
        setClicks();
        setupLocationResultReceiver();
        setupShareDialog();
    }

    private void setupRocketAnimation()
    {
        AnimationSet snowMov1 = new AnimationSet(true);
        RotateAnimation rotate1 = new RotateAnimation(0,-45, Animation.RELATIVE_TO_SELF,0.5f , Animation.RELATIVE_TO_SELF,0.5f );
        rotate1.setDuration(300);
        snowMov1.addAnimation(rotate1);
        TranslateAnimation trans1 =  new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.5f);;
        trans1.setFillEnabled(true);
        trans1.setFillAfter(true);
        trans1.setDuration(700);
        snowMov1.addAnimation(trans1);
        rocketButton.setAnimation(snowMov1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rocketButton.setVisibility(View.GONE);
                loading_content.setVisibility(View.VISIBLE);
                descriptionForUpload = descriptionEditText.getText().toString();
                uploadAddress = addressTextView.getText().toString();
                new AsyncAddFire().execute();
            }
        }, 800);
    }

    @Override
    public void onBackPressed() {
        if(popIsOpen)
            pinPopupCloseAnimation();
        else
            super.onBackPressed();
    }

    private void measureViews() {
        ViewTreeObserver vto = shareFireContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams rllpPinPopup = (RelativeLayout.LayoutParams) shareFireContent.getLayoutParams();
                rllpPinPopup.bottomMargin = -shareFireContent.getMeasuredHeight(); // use topmargin for the y-property, left margin for the x-property of your view
                shareFireContent.setLayoutParams(rllpPinPopup);
            }
        });
        ViewTreeObserver vto1 = actionContent.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams rllpPinPopup = (RelativeLayout.LayoutParams) actionContent.getLayoutParams();
                rllpPinPopup.bottomMargin = -actionContent.getMeasuredHeight(); // use topmargin for the y-property, left margin for the x-property of your view
                actionContent.setLayoutParams(rllpPinPopup);
            }
        });
    }
    boolean popIsOpen = false;
    public void pinPopupOpenAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(shareFireContent, "translationY", 0, -shareFireContent.getMeasuredHeight());
        objectAnimator.setDuration(500);
        objectAnimator.start();
        popIsOpen = true;
        rocketButton.setVisibility(View.VISIBLE);
    }

    public void pinPopupCloseAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(shareFireContent, "translationY", -shareFireContent.getMeasuredHeight(), 0);
        objectAnimator.setDuration(500);
        objectAnimator.start();
        popIsOpen = false;
        rocketButton.setVisibility(View.GONE);
        descriptionEditText.setText("");
    }
    boolean popActionsIsOpen = false;
    public void actionsPopupOpenAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(actionContent, "translationY", 0, -actionContent.getMeasuredHeight());
        objectAnimator.setDuration(200);
        objectAnimator.start();
        popActionsIsOpen = true;
        touch_content.setVisibility(View.VISIBLE);
    }

    public void actionsPopupCloseAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(actionContent, "translationY", -actionContent.getMeasuredHeight(), 0);
        objectAnimator.setDuration(200);
        objectAnimator.start();
        popActionsIsOpen = false;
        touch_content.setVisibility(View.GONE);
    }
    private void prepareViews() {
        takePhotoButton = (RelativeLayout) findViewById(R.id.takePhotoButton);
        loading_content = (RelativeLayout) findViewById(R.id.loading_content);
        videoContentMain = (RelativeLayout) findViewById(R.id.videoContentMain);
        touch_content = (RelativeLayout) findViewById(R.id.touch_content);
        actionContent = (RelativeLayout) findViewById(R.id.actionContent);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        nasaButton = (ImageButton) findViewById(R.id.nasaButton);
        rocketButton = (ImageButton) findViewById(R.id.rocketButton);
        weatherButton = (ImageButton) findViewById(R.id.weatherButton);
        fbShareButton = (ImageButton) findViewById(R.id.fbShareButton);
        camerActionButton = (ImageButton) findViewById(R.id.camerActionButton);
        videoActionButton = (ImageButton) findViewById(R.id.videoActionButton);
        socaialSharingButton = (ImageButton) findViewById(R.id.socaialSharingButton);
        shareFireContent = (LinearLayout) findViewById(R.id.shareFireContent);
        logoutButton = (LinearLayout) findViewById(R.id.logoutButton);
        captureddImageView = (ImageView) findViewById(R.id.captureddImageView);
        closePopUpButton = (RelativeLayout) findViewById(R.id.closePopUpButton);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        videoView = (VideoView) findViewById(R.id.videoView);
        playVideoButton = (ImageButton) findViewById(R.id.playVideoButton);
    }
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private void setupShareDialog()
    {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private static final int PICK_VIDEO_ID = 298;
    private void setClicks() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("loginSTATUS", false);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_right_slide, R.anim.right_left_slide);
                finish();
            }
        });
        playVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imagePath));
                intent.setDataAndType(Uri.parse(imagePath), "video/mp4");
                startActivity(intent);
            }
        });
        touch_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(popActionsIsOpen)
                    actionsPopupCloseAnimation();
                return false;
            }
        });
        fbShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isVideo)
                {
                    ShareVideo video = new ShareVideo.Builder()
                            .setLocalUrl(FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", new File(imagePath)))
                            .build();
                    ShareContent content = new ShareVideoContent.Builder()
                            .setVideo(video)
                            .build();

                    shareDialog.show(content);
                }
                else {
                    String str = descriptionEditText.getText().toString();
                    if (str == null)
                        str = "";
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(shareBitmap)
                            .setCaption(str)
                            .build();
                    ShareContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();

                    shareDialog.show(content);
                }
            }
        });
        videoActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsPopupCloseAnimation();
                boolean hasPermissionWR = (ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermissionWR) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
                }
                else {
                    Intent chooseImageIntent = VideoPicker.getPickImageIntent(MainActivity.this, "sharedFireImage.mp4");
                    startActivityForResult(chooseImageIntent, PICK_VIDEO_ID);
                }
            }
        });
        camerActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsPopupCloseAnimation();
                dispatchTakePictureIntent();
            }
        });
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!popActionsIsOpen)
                    actionsPopupOpenAnimation();

            }
        });
        rocketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupRocketAnimation();
            }
        });
        closePopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinPopupCloseAnimation();
                View viewd = MainActivity.this.getCurrentFocus();
                if (viewd != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewd.getWindowToken(), 0);
                }
            }
        });
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewd = MainActivity.this.getCurrentFocus();
                if (viewd != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewd.getWindowToken(), 0);
                }
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        socaialSharingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    if (descriptionEditText.getText() != null && descriptionEditText.getText().toString().length() > 0)
                        sendIntent.putExtra(Intent.EXTRA_TEXT, descriptionEditText.getText().toString());
                    sendIntent.putExtra(Intent.EXTRA_STREAM, sharingFile);
                    if(!isVideo)
                    sendIntent.setType("text/image");
                    else
                    sendIntent.setType("text/video");
                    startActivity(sendIntent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        nasaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nasaIsActive)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Now you will get Nasa's fires data all over the world during last 24 hours. Change map position to see fires in different places. ");

                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                        }
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                    Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(Color.parseColor("#ee6a22"));
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.parseColor("#ee6a22"));
                    nasaButton.setBackgroundResource(R.drawable.nasa_active);
                    nasaIsActive = true;
                    if(!processingIsActive && nasaIsActive)
                        retrieveFileFromUrl();
                }
                else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Now you will get the app data and you can see all added fires by users");

                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                        }
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                    Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(Color.parseColor("#ee6a22"));
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.parseColor("#ee6a22"));
                    nasaButton.setBackgroundResource(R.drawable.nasa_not_active);
                    nasaIsActive = false;
                    mMap.clear();
                    new AsyncDashboard().execute();
                }
            }
        });
    }
    private static final int PICK_IMAGE_ID = 238;
    static final int REQUEST_WRITE_STORAGE = 182;
    String imagePath = "";
    private void dispatchTakePictureIntent() {
        boolean hasPermissionWR = (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionWR) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
        else {
            Intent chooseImageIntent = ImagePickerComment.getPickImageIntent(this, "sharedFireImage.jpg", true);
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            buildGoogleApiClient();
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(MainActivity.this, "The app was not allowed to write to your storage.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_ID && resultCode == RESULT_OK)
        {
            try {
                Bitmap bitmap = ImagePickerComment.getImageFromResult(this, resultCode, data);
                if (ImagePickerComment.isCamera) {
                    File f = ImagePickerComment.getTempFile(MainActivity.this);
                    imagePath = Constants.GLOB_IMAGE_SHARING = f.getAbsolutePath();
                    shareBitmap = bitmap;
                    Constants.GLOB_IMG_P = bitmap;
                    sharingFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", f);;
                    captureddImageView.setImageBitmap(bitmap);
                    pinPopupOpenAnimation();
                    rocketButton.setVisibility(View.VISIBLE);
                    Constants.fromVideo = isVideo = false;
                    videoContentMain.setVisibility(View.GONE);
                    if(bitmap.getWidth() > 1200 || bitmap.getHeight() > 1200)
                        bitmap = Methods.scaleDown(bitmap, 1200, true);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    try {
                        FileOutputStream fOS = new FileOutputStream(imagePath);
                        BufferedOutputStream bOS = new BufferedOutputStream(fOS);
                        bOS.write(bytes.toByteArray());
                        bOS.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //end new
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(requestCode == PICK_VIDEO_ID && resultCode == RESULT_OK) {
            try {
                File f = VideoPicker.getTempFile(MainActivity.this);
                Constants.GLOB_IMAGE_L = imagePath = Constants.GLOB_IMAGE_SHARING = f.getAbsolutePath();
                videoView.setVideoPath(imagePath);
                pinPopupOpenAnimation();
                videoContentMain.setVisibility(View.VISIBLE);
                Constants.fromVideo = isVideo = true;
                rocketButton.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(MainActivity.this, data);
                LatLng latLong;


                latLong = place.getLatLng();
                setupMapFromResult(latLong);


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(MainActivity.this, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    private void setupMapFromResult(LatLng latLong) {


        // TODO call location based filter




        //mLocationText.setText(place.getName() + "");


        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        if(latLong != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(14f).tilt(0).build();
            mMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }

    LatLng mCenterLatLong;
    int poi = 0;
    boolean processingIsActive = false;
    boolean fielStreamed = false;
    ArrayList<NasaFireModel> latLngList;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mCenterLatLong = mMap.getCameraPosition().target;
        Constants.W_GLOB_LAT = mCenterLatLong.latitude;
        Constants.W_GLOB_LONG = mCenterLatLong.longitude;
        new AsyncDashboard().execute();
        if(!processingIsActive && nasaIsActive)
        retrieveFileFromUrl();
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;

//                mMap.clear();
                    if(!processingIsActive && nasaIsActive)
                    retrieveFileFromUrl();

                try {

                    mLat = Constants.W_GLOB_LAT = mCenterLatLong.latitude;
                    mLong = Constants.W_GLOB_LONG = mCenterLatLong.longitude;
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    startIntentService(mLocation);

//                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(nasaIsActive) {
                    int pos = Integer.parseInt(marker.getTag().toString());
                    Constants.GLOB_FIRE_SEL = latLngList.get(pos);
                    Intent intent = new Intent(MainActivity.this, AboutNasaFireActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_in_mina);
                }
                else
                {
                    FireModel thisFire = myBugsHashMap.get(marker.getId());
                    Constants.GLOB_DATE = thisFire.getCreated_at();
                    Constants.GLOB_DESC = thisFire.getDescription();
                    Constants.GLOB_ADDRESS = thisFire.getAddress();
                    if(thisFire.getImage_url() != null)
                        Constants.GLOB_IMAGE_L = thisFire.getImage_url();
                    else
                        Constants.GLOB_IMAGE_L = thisFire.getVideo_url();
                    Constants.GLOB_LAT = thisFire.getLat();
                    Constants.GLOB_LONG = thisFire.getLon();
                    Constants.GLOB_USER_ID = thisFire.getAuthor();
                    Constants.GLOB_USER_NAME = thisFire.getUsername();
                    Intent intent = new Intent(MainActivity.this, AboutAppFiresActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_in_mina);
                }
                return false;
            }
        });
    }

    private void retrieveFileFromUrl() {
        //VNP14IMGTDL_NRT
        processingIsActive = true;

        mMap.clear();
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        if(preferences.contains("FILENAME") && preferences.contains("DOWNDATE") && preferences.getInt("DOWNDATE", 0) == dayOfMonth)
        {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        try {

                if(!fielStreamed)

                    {
                        InputStream instream = new FileInputStream(preferences.getString("FILENAME", ""));
                        InputStreamReader inputreader = new InputStreamReader(instream);
                        BufferedReader reader = new BufferedReader(inputreader);
                        latLngList = new ArrayList<NasaFireModel>();
                        String line = "";

                        while ((line = reader.readLine()) != null) // Read until end of file
                        {
                            try {
                                double lat = Double.parseDouble(line.split(",")[0]);
                                double lon = Double.parseDouble(line.split(",")[1]);
                                String bright_ti4 = line.split(",")[2];
                                String scan = line.split(",")[3];
                                String track = line.split(",")[4];
                                String acq_date = line.split(",")[5];
                                String acq_time = line.split(",")[6];
                                String satellite = line.split(",")[7];
                                String confidence = line.split(",")[8];
                                String version = line.split(",")[9];
                                String bright_ti5 = line.split(",")[10];
                                String frp = line.split(",")[11];
                                String daynight1 = line.split(",")[12];
//                            if (Math.abs(lat - mCenterLatLong.latitude) < 4 && Math.abs(lon - mCenterLatLong.longitude) < 4) {
                                latLngList.add(new NasaFireModel(lat, lon, bright_ti4, scan,
                                        track, acq_date, acq_time, satellite, confidence,
                                        version, bright_ti5, frp, daynight1));
//                            }
                            } catch (OutOfMemoryError e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        loading_content.setVisibility(View.GONE);
                    }

                    // Add them to map
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int k = 0;
                                    for (
                                            int i = 0; i < latLngList.size(); i++)

                                    {
                                        try {

                                            NasaFireModel pos = latLngList.get(i);
                                            if (k < 50 && Math.abs(pos.getLatitude() - mCenterLatLong.latitude) < 2 && Math.abs(pos.getLongitude() - mCenterLatLong.longitude) < 2) {
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(pos.getLatitude(), pos.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.fire_small))).setTag(i);
                                                k++;
                                            }// Don't necessarily need title
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (k > 50)
                                            break;
                                    }

                                }
                            });


                    processingIsActive =false;
                    fielStreamed =true;
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                            processingIsActive = false;
                        }
                    }

                });
            thread.start();

        }
        else {
            loading_content.setVisibility(View.VISIBLE);
            new DownloadKmlFile(Constants.WORLD).execute();
        }
    }


    private class DownloadKmlFile extends AsyncTask<String, Void, byte[]> {
        private final String mUrl;

        public DownloadKmlFile(String url) {
            mUrl = url;
        }

        protected byte[] doInBackground(String... params) {

            try {
                InputStream is =  new URL(mUrl).openStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return buffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(byte[] byteArr) {
            try {
                if(!fielStreamed)
                {
                try {
                    File iFile = new File(getExternalCacheDir(), "24hourscaching.csv");
                    iFile.getParentFile().mkdirs();
                    FileOutputStream fOS = new FileOutputStream(iFile);
                    BufferedOutputStream bOS = new BufferedOutputStream(fOS);
                    bOS.write(byteArr);
                    bOS.close();
                    Calendar cal = Calendar.getInstance();
                    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("FILENAME", iFile.getAbsolutePath());
                    editor.putInt("DOWNDATE", dayOfMonth);
                    editor.apply();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(byteArr != null) {
                    ///latitude,longitude,bright_ti4,scan,track,acq_date,acq_time,satellite,confidence,version,bright_ti5,frp,daynight
                    InputStream instream = new ByteArrayInputStream(byteArr);
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader reader = new BufferedReader(inputreader);
                    latLngList = new ArrayList<NasaFireModel>();
                    String line = "";

                    while ((line = reader.readLine()) != null) // Read until end of file
                    {
                        try {
                            double lat = Double.parseDouble(line.split(",")[0]);
                            double lon = Double.parseDouble(line.split(",")[1]);
                            String bright_ti4 = line.split(",")[2];
                            String scan = line.split(",")[3];
                            String track = line.split(",")[4];
                            String acq_date = line.split(",")[5];
                            String acq_time = line.split(",")[6];
                            String satellite = line.split(",")[7];
                            String confidence = line.split(",")[8];
                            String version = line.split(",")[9];
                            String bright_ti5 = line.split(",")[10];
                            String frp = line.split(",")[11];
                            String daynight = line.split(",")[12];
//                            if (Math.abs(lat - mCenterLatLong.latitude) < 4 && Math.abs(lon - mCenterLatLong.longitude) < 4) {
                            latLngList.add(new NasaFireModel(lat, lon, bright_ti4, scan,
                                    track, acq_date, acq_time, satellite, confidence,
                                    version, bright_ti5, frp, daynight));
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

// Add them to map
                    int k = 0;
                    for (int i = 0;i < latLngList.size(); i++) {
                            try {
                                NasaFireModel pos = latLngList.get(i);
                                if (k < 50 && Math.abs(pos.getLatitude() - mCenterLatLong.latitude) < 4 && Math.abs(pos.getLongitude() - mCenterLatLong.longitude) < 4) {
                                    mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(pos.getLatitude(), pos.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.fire_small))).setTag(i);
                                    k++;
                                }// Don't necessarily need title
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(k > 50)
                                break;
                    }

                    processingIsActive = false;
                    fielStreamed = true;
                    loading_content.setVisibility(View.GONE);
                }
            }catch (Exception e) {
                e.printStackTrace();
                processingIsActive = false;
                loading_content.setVisibility(View.GONE);
            }
        }
    }






    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
//        try {
//            Constants.globUserLatitude = mLat = mLastLocation.getLatitude();
//            Constants.globUserLongitude = mLong = mLastLocation.getLongitude();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (mLastLocation != null) {
            if(activeChangeMap) {
                changeMap(mLastLocation);
                activeChangeMap = false;
            }
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
//            if (location != null)
//                changeMap(location);
//            if(location != null) {
//                Constants.globUserLatitude = mLat = location.getLatitude();
//                Constants.globUserLongitude = mLong = location.getLongitude();
//            }
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, MainActivity.this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(14f).tilt(0).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            startIntentService(location);


        } else {
            Toast.makeText(MainActivity.this,
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }


    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mCountryOutput;
    protected String mStateOutput;

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(android.os.Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);
            mCountryOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_COUNTRY);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        String out = "";
        if (mCountryOutput != null && !mCountryOutput.equals("null") && mCountryOutput.length() > 0)
            out += mCountryOutput + ", ";
        if (mCityOutput != null && !mCityOutput.equals("null") && mCityOutput.length() > 0)
            out += mCityOutput + ", ";
        if (mStateOutput != null && !mStateOutput.equals("null") && mStateOutput.length() > 0)
            out += mStateOutput;
        addressTextView.setText(out);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");

                addressTextView.setText(out);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(MainActivity.this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }


    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(MainActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
    private void setupLocationResultReceiver() {
        mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(MainActivity.this)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("Error getting location");
                dialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.parseColor("#ee6a22"));
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.parseColor("#ee6a22"));
            }
            buildGoogleApiClient();
        } else {
//            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }
    }
    private class AsyncAddFire extends AsyncTask<String, Void, FireAddResponse> {

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected FireAddResponse doInBackground(final String... args) {
            try {
                FireAddResponse addBugObject = WebServiceClient.addFireTask(mLat, mLong, descriptionForUpload, imagePath, uploadAddress,  isVideo);
                return addBugObject;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final FireAddResponse getObject) {

            if (getObject != null) {
                if (getObject.isError()) {

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                    //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Error");

                    // Setting Dialog Message
                    alertDialog.setMessage("Something went wrong");
                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                    Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(Color.parseColor("#4997dd"));
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.parseColor("#4997dd"));
                } else {
                    Constants.GLOB_DATE = "12.04.96 12:23";
                    Constants.GLOB_DESC = descriptionEditText.getText().toString();
                    Constants.GLOB_LAT = mLat;
                    Constants.GLOB_LONG = mLong;
                    Constants.GLOB_ADDRESS = uploadAddress;
                    Intent intent = new Intent(MainActivity.this, AboutAppFiresActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_in_mina);
                    pinPopupCloseAnimation();
                    Constants.fromFinishedMain = true;
                    finish();
                }
                loading_content.setVisibility(View.GONE);
            }
        }
    }
    private class AsyncDashboard extends AsyncTask<String, Void, GetFiresResponse> {

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected GetFiresResponse doInBackground(final String... args) {
            try {
                Log.d(Constants.APP_NAME, "Bugs : get");
                GetFiresResponse dashboardObject = WebServiceClient.firesTask();
                return dashboardObject;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final GetFiresResponse getObject) {

            if (getObject != null) {
                if (getObject.isError()) {

                    Toast.makeText(MainActivity.this, "Fires getting error", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(Constants.APP_NAME, "Bugs : get");
                    mMap.clear();
                    disasters = getObject.getDisasters();
                    setupMarkers();
                }
            }
        }
    }
    private ArrayList<FireModel> disasters = new ArrayList<>();
    private HashMap<String, FireModel> myBugsHashMap = new HashMap<String, FireModel>();
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    public void setupMarkers() {


        if (disasters.size() > 0) {
            for (int i = 0; i < disasters.size(); i++)
                try {
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting the position for the marker
                    markerOptions.position(new LatLng(disasters.get(i).getLat(), disasters.get(i).getLon()));

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker

                    FireModel thisBug = disasters.get(i);

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire_small));

                    Marker mark = mMap.addMarker(markerOptions);

                    markers.add(mark);

                    myBugsHashMap.put(mark.getId(), disasters.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

    }
}
