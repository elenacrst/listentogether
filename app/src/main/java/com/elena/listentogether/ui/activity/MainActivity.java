package com.elena.listentogether.ui.activity;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elena.listentogether.R;
import com.elena.listentogether.base.App;
import com.elena.listentogether.model.local.entity.UserEntity;
import com.elena.listentogether.ui.viewmodel.user.UserViewModel;
import com.elena.listentogether.utils.Constants;
import com.elena.listentogether.utils.SharedPrefUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class MainActivity  extends AppCompatActivity {

    private static final int RC_GOOGLE_SIGN_IN = 1;
    private AppCompatEditText mUsernameEditText, mPasswordEditText;
    private static TextView mLoginTextView, mLoginMsgTextView, mFbTextView, mGoogleTextView;//,
     //   mSpotifyTextView;
    private Button mSignUpButton;
    private LoginButton mFbLoginButton;

    private UserEntity userEntity;

    private UserViewModel mUserViewModel;
    @Inject
    Retrofit mRetrofit;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
      //  FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);

        findViews();
        setupFonts();
        hideStatusBar();

        setupViewModel();
        setupGoogleSignIn();
        setupFbSignIn();

    }

    private void setupFbSignIn() {
        // Creating CallbackManager
        mCallbackManager = CallbackManager.Factory.create();
        mFbLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        // Registering CallbackManager with the LoginButton
        mFbLoginButton.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Retrieving access token using the LoginResult
                AccessToken accessToken = loginResult.getAccessToken();
                useLoginInformation(accessToken);
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
            }
        });

    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               // .requestIdToken()
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupViewModel() {
        mUserViewModel = ViewModelProviders.of(this)
                .get(UserViewModel.class);
        ((App) getApplication()).getNetComponent().injectMain(this);//dagger
        mUserViewModel.setRetrofit(mRetrofit);

    }

    private void setupFonts() {
        AssetManager am = getAssets();
        Typeface typefaceLight = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "AvenirLTStd-Light.otf"));

        mUsernameEditText.setTypeface(typefaceLight);
        mPasswordEditText.setTypeface(typefaceLight);
        mLoginMsgTextView.setTypeface(typefaceLight);
        mSignUpButton.setTypeface(typefaceLight);

        Typeface typefaceMedium = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "AvenirLTStd-Medium.otf"));
        mFbTextView.setTypeface(typefaceMedium);
        mGoogleTextView.setTypeface(typefaceMedium);
    //    mSpotifyTextView.setTypeface(typefaceMedium);

        Typeface typefaceRoman = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "AvenirLTStd-Roman.otf"));
        mLoginTextView.setTypeface(typefaceRoman);
    }

    private void findViews() {
        mUsernameEditText = findViewById(R.id.edit_username);
        mPasswordEditText = findViewById(R.id.edit_password);
        mLoginTextView = findViewById(R.id.text_login);
        mLoginMsgTextView = findViewById(R.id.text_login_msg);
        mFbTextView = findViewById(R.id.text_fb);
        mGoogleTextView = findViewById(R.id.text_google);
      //  mSpotifyTextView = findViewById(R.id.text_spotify);
        mSignUpButton = findViewById(R.id.btn_sign_up);
        mFbLoginButton = findViewById(R.id.btn_fb_login);
    }

    public void onSignUp(View view) {
        Intent openRegisterActivity = new Intent(this, RegisterActivity.class);
        startActivity(openRegisterActivity);
    }

    public void onLogin(View view) {
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_TYPE, Constants.PROFILE_TYPE_BASIC);
        userEntity = new UserEntity();
        userEntity.setUsername(mUsernameEditText.getText().toString());
        userEntity.setPassword(mUsernameEditText.getText().toString());
        mUserViewModel.loadUser(this,userEntity);
        mUserViewModel.getUserData().observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(@Nullable UserEntity user) {
                if (user == null || user.getEmail() == null){
                //    Toast.makeText(MainActivity.this, R.string.msg_wrong_credentials, Toast.LENGTH_SHORT).show();
                }else{
                    userEntity = user;
                    onLoginSuccess();
                }
            }
        });
    }

    public void hideStatusBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void onFacebookSignIn(View view) {
        mFbLoginButton.performClick();
    }

    public void onGoogleSignIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case RC_GOOGLE_SIGN_IN:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null){
                            SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
                            sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_TYPE, Constants.PROFILE_TYPE_GOOGLE);

                            userEntity = new UserEntity();
                            userEntity.setUsername(account.getDisplayName());
                            userEntity.setPassword("google");
                            userEntity.setPhone(null);
                            userEntity.setEmail(account.getEmail());
                            userEntity.setCountry(null);
                            userEntity.setAvatar(String.valueOf(account.getPhotoUrl()));
                            userEntity.setCity(null);
                            mUserViewModel.insertUser(this,userEntity);
                            //mUserViewModel.loadUser(this,userEntity);
                            mUserViewModel.getUserData().observe(this, new Observer<UserEntity>() {
                                @Override
                                public void onChanged(@Nullable UserEntity user) {
                                    if (user == null || user.getEmail() == null){
                                       // Toast.makeText(MainActivity.this, R.string.msg_wrong_credentials, Toast.LENGTH_SHORT).show();
                                    }else{
                                        userEntity = user;
                                        onLoginSuccess();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(MainActivity.this, R.string.msg_login_error, Toast.LENGTH_SHORT).show();
                        }

                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w("google-sign-in", "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }

    private void onLoginSuccess(){
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(MainActivity.this);
        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_USERNAME, userEntity.getUsername());
        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_EMAIL, userEntity.getEmail());
        sharedPrefUtils.saveLong(SharedPrefUtils.KEY_PROFILE_ID, userEntity.getId());
        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_CITY, userEntity.getCity());
        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_COUNTRY, userEntity.getCountry());
        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_PHONE, userEntity.getPhone());
        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_AVATAR, userEntity.getAvatar());
        Intent openRoomsActivity = new Intent(MainActivity.this, RoomsActivity.class);
        startActivity(openRoomsActivity);
        finish();
    }

    private void useLoginInformation(AccessToken accessToken) {
        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(MainActivity.this);
                    sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_TYPE, Constants.PROFILE_TYPE_FACEBOOK);
                    userEntity = new UserEntity();
                    String name = object.getString("name");
                    String email = object.getString("email");
                    userEntity.setUsername(name);
                    userEntity.setEmail(email);
                    userEntity.setPassword("facebook");

                    mUserViewModel.insertUser(MainActivity.this,userEntity);
                    //mUserViewModel.loadUser(this,userEntity);
                    mUserViewModel.getUserData().observe(MainActivity.this, new Observer<UserEntity>() {
                        @Override
                        public void onChanged(@Nullable UserEntity user) {
                            if (user == null || user.getEmail() == null){
                                // Toast.makeText(MainActivity.this, R.string.msg_wrong_credentials, Toast.LENGTH_SHORT).show();
                            }else{
                                userEntity = user;
                                onLoginSuccess();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }
}
