package com.elena.listentogether.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elena.listentogether.R;
import com.elena.listentogether.base.App;
import com.elena.listentogether.data.local.entity.UserEntity;
import com.elena.listentogether.ui.viewmodel.user.UserViewModel;
import com.elena.listentogether.utils.SharedPrefUtils;

import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class MainActivity  extends AppCompatActivity {

    private AppCompatEditText mUsernameEditText, mPasswordEditText;
    private static TextView mLoginTextView, mLoginMsgTextView, mFbTextView, mGoogleTextView;//,
     //   mSpotifyTextView;
    private Button mSignUpButton;

    private UserEntity userEntity;

    private UserViewModel mUserViewModel;
    @Inject
    Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setupFonts();
        hideStatusBar();

        setupViewModel();

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
    }

    public void onSignUp(View view) {
        Intent openRegisterActivity = new Intent(this, RegisterActivity.class);
        startActivity(openRegisterActivity);
    }

    public void onLogin(View view) {
        userEntity = new UserEntity();
        userEntity.setUsername(mUsernameEditText.getText().toString());
        userEntity.setPassword(mUsernameEditText.getText().toString());

        mUserViewModel.loadUser(this,userEntity);
        mUserViewModel.getUserData().observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(@Nullable UserEntity userEntity) {
                Log.wtf("found","user "+userEntity.getEmail()+" "+userEntity.getUsername());
                if (userEntity.getEmail() == null){
                    Toast.makeText(MainActivity.this, R.string.msg_wrong_credentials, Toast.LENGTH_SHORT).show();
                }else{
                    //login success
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
                }

            }
        });


    }

    public void hideStatusBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

}
