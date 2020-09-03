package com.elena.listentogether.ui.activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.elena.listentogether.R;
import com.elena.listentogether.base.App;
import com.elena.listentogether.model.local.entity.UserEntity;
import com.elena.listentogether.ui.viewmodel.user.UserViewModel;
import com.elena.listentogether.utils.SharedPrefUtils;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUsernameEditText, mPasswordEditText, mConfirmPasswordEditText, mEmailEditText;

    private UserEntity mUserEntity;
    private UserViewModel mUserViewModel;
    @Inject
    Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();
        setupViewModel();
    }

    private void findViews() {
        mUsernameEditText  = findViewById(R.id.edit_username);
        mPasswordEditText = findViewById(R.id.edit_password);
        mConfirmPasswordEditText = findViewById(R.id.edit_confirm_pass);
        mEmailEditText = findViewById(R.id.edit_mail);
    }

    public void onLogin(View view) {

        Intent openLoginActivity = new Intent(this, MainActivity.class);
        startActivity(openLoginActivity);
    }

    public void onSignUp(View view) {
        boolean valid = true;
        if (TextUtils.isEmpty(mUsernameEditText.getText().toString())){
            valid = false;
            mUsernameEditText.setHintTextColor(getResources().getColor(R.color.googleRed));
            Toast.makeText(this, R.string.msg_complete_fields, Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(mPasswordEditText.getText().toString())){
            valid = false;
            mPasswordEditText.setHintTextColor(getResources().getColor(R.color.googleRed));
            Toast.makeText(this, R.string.msg_complete_fields, Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(mConfirmPasswordEditText.getText().toString())){
            valid = false;
            mConfirmPasswordEditText.setHintTextColor(getResources().getColor(R.color.googleRed));
            Toast.makeText(this, R.string.msg_complete_fields, Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(mEmailEditText.getText().toString())){
            valid = false;
            mEmailEditText.setHintTextColor(getResources().getColor(R.color.googleRed));
            Toast.makeText(this, R.string.msg_complete_fields, Toast.LENGTH_SHORT).show();
        }
        if (!TextUtils.isEmpty(mPasswordEditText.getText().toString()) && !TextUtils.isEmpty(mConfirmPasswordEditText.getText().toString())
            && !mPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString())){
            mPasswordEditText.setHintTextColor(getResources().getColor(R.color.googleRed));
            mConfirmPasswordEditText.setHintTextColor(getResources().getColor(R.color.googleRed));
            mPasswordEditText.setText("");
            mConfirmPasswordEditText.setText("");
            Toast.makeText(this, R.string.msg_passwords_mismatch, Toast.LENGTH_SHORT).show();
        }
        if (valid){
            mUserEntity = new UserEntity();
            mUserEntity.setAvatar(null);
            mUserEntity.setCity(null);
            mUserEntity.setCountry(null);
            mUserEntity.setEmail(mEmailEditText.getText().toString());
            mUserEntity.setPassword(mPasswordEditText.getText().toString());
            mUserEntity.setPhone(null);
            mUserEntity.setUsername(mUsernameEditText.getText().toString());

            mUserViewModel.insertUser(this,mUserEntity);
            mUserViewModel.getUserData().observe(this, new Observer<UserEntity>() {
                @Override
                public void onChanged(@Nullable UserEntity userEntity) {
                    if (userEntity != null){
                        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(RegisterActivity.this);
                        sharedPrefUtils.saveLong(SharedPrefUtils.KEY_PROFILE_ID, userEntity.getId());
                        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_AVATAR, null);
                        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_CITY, null);
                        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_PHONE, null);
                        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_COUNTRY, null);
                        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_EMAIL, userEntity.getEmail());
                        sharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_USERNAME, userEntity.getUsername());
                    }

                }
            });

            Intent openRoomsActivity = new Intent(this, RoomsActivity.class);
            openRoomsActivity.putExtra("email", mUserEntity.getEmail());
            openRoomsActivity.putExtra("username", mUserEntity.getUsername());
            startActivity(openRoomsActivity);
        }
    }

    private void setupViewModel() {
        mUserViewModel = ViewModelProviders.of(this)
                .get(UserViewModel.class);
        ((App) getApplication()).getNetComponent().injectRegister(this);//dagger
        mUserViewModel.setRetrofit(mRetrofit);
    }
}
