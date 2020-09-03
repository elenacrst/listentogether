package com.elena.listentogether.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.elena.listentogether.BuildConfig;
import com.elena.listentogether.R;
import com.elena.listentogether.base.App;
import com.elena.listentogether.ui.viewmodel.user.UserViewModel;
import com.elena.listentogether.utils.Constants;
import com.elena.listentogether.utils.ImageEncodingUtils;
import com.elena.listentogether.utils.SharedPrefUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Retrofit;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class ProfileActivity extends AppCompatActivity {

    private static final int RC_GALLERY = 1;
    private static final int RC_CAMERA = 2;

    private CircleImageView mAvatarImageView;
    private TextView mRoomsCountTextView, mEmailTextView, mUsernameTextView;
    private EditText mCountryEditText, mCityEditText, mPhoneEditText;
    private ImageButton mEditCountryButton, mEditCityButton, mEditPhoneButton, mDoneCountryButton, mDoneCityButton, mDonePhoneButton,
        mCancelCountryButton, mCancelCityButton, mCancelPhoneButton;
    private Toolbar mToolbar;

    private SharedPrefUtils mSharedPrefUtils;
    private String mCameraFilePath;
    private UserViewModel mUserViewModel;

    @Inject
    Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mSharedPrefUtils = new SharedPrefUtils(this);

        findViews();
        populateProfile();
     //   hideKeyboard(this);


        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.title_profile);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueIndicator)));
            getSupportActionBar().setElevation(0f);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViewModel();
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
        mUserViewModel.loadUserRoomsCount(this, sharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID));
        mUserViewModel.getmUserRoomsCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mRoomsCountTextView.setText(""+ (integer == null ? String.valueOf(0) : String.valueOf(integer)));
            }
        });
    }

    private void setupViewModel() {
        mUserViewModel = ViewModelProviders.of(this)
                .get(UserViewModel.class);
        ((App) getApplication()).getNetComponent().injectProfile(this);//dagger
        mUserViewModel.setRetrofit(mRetrofit);

    }

    private void populateProfile() {
   /*     if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_AVATAR, ""))){
            Picasso.get().load(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_AVATAR, ""));
        }*/
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_USERNAME, ""))){
            mUsernameTextView.setText(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_USERNAME, ""));
        }
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_EMAIL, ""))){
            mEmailTextView.setText(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_EMAIL, ""));
        }
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_COUNTRY, ""))){
            mCountryEditText.setText(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_COUNTRY, ""));
        }
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_CITY, ""))){
            mCityEditText.setText(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_CITY, ""));
        }
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_PHONE, ""))){
            mPhoneEditText.setText(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_PHONE, ""));
        }
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_AVATAR, ""))){
            ImageEncodingUtils.decodeBase64AndSetImage(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_AVATAR, ""), mAvatarImageView);
        }
    }

    private void findViews() {
        mAvatarImageView = findViewById(R.id.image_avatar);
        mRoomsCountTextView = findViewById(R.id.text_rooms);
        mCountryEditText = findViewById(R.id.text_country);
        mCityEditText = findViewById(R.id.text_city);
        mPhoneEditText = findViewById(R.id.text_phone);
        mEditCountryButton = findViewById(R.id.btn_edit_country);
        mEditCityButton = findViewById(R.id.btn_edit_city);
        mEditPhoneButton = findViewById(R.id.btn_edit_phone);
        mDoneCountryButton = findViewById(R.id.btn_done_country);
        mDoneCityButton = findViewById(R.id.btn_done_city);
        mDonePhoneButton = findViewById(R.id.btn_done_phone);
        mCancelCountryButton = findViewById(R.id.btn_cancel_country);
        mCancelCityButton = findViewById(R.id.btn_cancel_city);
        mCancelPhoneButton = findViewById(R.id.btn_cancel_phone);
        mEmailTextView = findViewById(R.id.text_email);
        mUsernameTextView = findViewById(R.id.text_username);
        mToolbar = findViewById(R.id.toolbar);
    }

    public void onCityEdit(View view) {
        mCityEditText.setBackground(getResources().getDrawable(android.R.drawable.edit_text));
        mCityEditText.setEnabled(true);
        mEditCityButton.setVisibility(View.GONE);
        mCancelCityButton.setVisibility(View.VISIBLE);
        mDoneCityButton.setVisibility(View.VISIBLE);
    }


    public void onPhoneEdit(View view) {
        mPhoneEditText.setBackground(getResources().getDrawable(android.R.drawable.edit_text));
        mPhoneEditText.setEnabled(true);
        mEditPhoneButton.setVisibility(View.GONE);
        mCancelPhoneButton.setVisibility(View.VISIBLE);
        mDonePhoneButton.setVisibility(View.VISIBLE);
    }

    public void onCountryEdit(View view) {
        mCountryEditText.setBackground(getResources().getDrawable(android.R.drawable.edit_text));
        mCountryEditText.setEnabled(true);
        mEditCountryButton.setVisibility(View.GONE);
        mCancelCountryButton.setVisibility(View.VISIBLE);
        mDoneCountryButton.setVisibility(View.VISIBLE);
    }

    public void onCountryCancel(View view) {
        mCountryEditText.setEnabled(false);
        mCountryEditText.setBackground(null);
        mEditCountryButton.setVisibility(View.VISIBLE);
        mCancelCountryButton.setVisibility(View.GONE);
        mDoneCountryButton.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_COUNTRY, ""))){
            mCountryEditText.setText(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_COUNTRY, ""));
        }

    }

    public void onCityCancel(View view) {
        mCityEditText.setEnabled(false);
        mCityEditText.setBackground(null);
        mEditCityButton.setVisibility(View.VISIBLE);
        mCancelCityButton.setVisibility(View.GONE);
        mDoneCityButton.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_CITY, ""))){
            mCityEditText.setText(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_CITY, ""));
        }
    }

    public void onPhoneCancel(View view) {
        mPhoneEditText.setEnabled(false);
        mPhoneEditText.setBackground(null);
        mEditPhoneButton.setVisibility(View.VISIBLE);
        mCancelPhoneButton.setVisibility(View.GONE);
        mDonePhoneButton.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_PHONE, ""))){
            mPhoneEditText.setText(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_PHONE, ""));
        }
    }

    public void onCountryDone(View view) {
        mCountryEditText.setEnabled(false);
        mCountryEditText.setBackground(null);
        mEditCountryButton.setVisibility(View.VISIBLE);
        mCancelCountryButton.setVisibility(View.GONE);
        mDoneCountryButton.setVisibility(View.GONE);
        mSharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_COUNTRY, mCountryEditText.getText().toString());
        mUserViewModel.updateCountry(this, mSharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID), mCountryEditText.getText().toString());
    }

    public void onCityDone(View view) {
        mCityEditText.setEnabled(false);
        mCityEditText.setBackground(null);
        mEditCityButton.setVisibility(View.VISIBLE);
        mCancelCityButton.setVisibility(View.GONE);
        mDoneCityButton.setVisibility(View.GONE);
        mSharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_CITY, mCityEditText.getText().toString());
        mUserViewModel.updateCity(this, mSharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID),
                mCityEditText.getText().toString());
    }

    public void onPhoneDone(View view) {
        mPhoneEditText.setEnabled(false);
        mPhoneEditText.setBackground(null);
        mEditPhoneButton.setVisibility(View.VISIBLE);
        mCancelPhoneButton.setVisibility(View.GONE);
        mDonePhoneButton.setVisibility(View.GONE);
        mSharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_PHONE, mPhoneEditText.getText().toString());
        mUserViewModel.updatePhone(this, mSharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID), mPhoneEditText.getText().toString());
    }

    public void onTakePhoto(View view) {
        selectImage();
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        // Permission is not granted
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                RC_CAMERA);
                    }else{
                        openCamera();
                    }

                }
                else if (options[item].equals("Choose from Gallery")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_GALLERY);
                    }else{
                        openGallery();
                    }


                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new   Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(intent, RC_GALLERY);
    }

    private void openCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",
                    createImageFile())
            );
            startActivityForResult(intent, RC_CAMERA);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_CAMERA) {
                mAvatarImageView.setImageURI(Uri.parse(mCameraFilePath));
                String fileLocation = new String(mCameraFilePath);
                fileLocation = fileLocation.replaceFirst("file://", "");
                String encodedAvatar = ImageEncodingUtils.getBase64String(fileLocation);
                mUserViewModel.updateAvatar(this, mSharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID),
                        encodedAvatar);
                mSharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_AVATAR, encodedAvatar);
            } else if (requestCode == RC_GALLERY) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null,
                        null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                mAvatarImageView.setImageBitmap(thumbnail);
                String encodedAvatar = ImageEncodingUtils.getBase64String(picturePath);
                mUserViewModel.updateAvatar(this, mSharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID),
                        encodedAvatar);
                mSharedPrefUtils.saveString(SharedPrefUtils.KEY_PROFILE_AVATAR, encodedAvatar);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RC_CAMERA:
                if (grantResults.length > 2 && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED
                    && grantResults[2] == PERMISSION_GRANTED){
                    openCamera();
                }
                break;
            case RC_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED){
                    openGallery();
                }
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        /*File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");*/

        File storageDir = new File(Environment.getExternalStorageDirectory(), Constants.APP_DIR);
        if (!storageDir.exists()){
            storageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        mCameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
