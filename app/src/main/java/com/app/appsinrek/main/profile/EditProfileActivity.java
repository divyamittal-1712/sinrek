package com.app.appsinrek.main.profile;

import static com.app.appsinrek.utils.AppConstants.TAG_CHECK_USERNAME;
import static com.app.appsinrek.utils.AppConstants.TAG_EDIT_PROFILE;
import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;
import static com.app.appsinrek.utils.CommonUtils.encodeEmoji;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityEditProfileBinding;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.User;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.utils.spinnerdatepicker.DatePicker;
import com.app.appsinrek.utils.spinnerdatepicker.DatePickerDialog;
import com.app.appsinrek.utils.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;
import com.fitness.modval.interfaces.AuthListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity implements AuthListener, DatePickerDialog.OnDateSetListener, DatePickerDialog.OnDateCancelListener {
    private int CAMERA_PERMISSION_CODE = 100;
    ActivityEditProfileBinding bi;
    Activity mActivity = this;
    MainViewModel mainViewModel;
    String path;
    private SimpleDateFormat simpleDateFormat;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        mainViewModel.setMainListener(this);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.US);
        setSupportActionBar(bi.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bi.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.nav_save) {
                if (bi.tilName.getText().toString().isEmpty()) {
                    UtilityHelperKt.toast(mActivity, "Name is Empty");
                } else if (bi.tilUsername.getText().toString().isEmpty()) {
                    UtilityHelperKt.toast(mActivity, "Username is Empty");
                }
                else {
                    if (bi.tilUsername.getText().toString().equals(mainViewModel.getStorage().readUserInfo().getUsername())) {
                        saveCall();
                        return true;
                    }
                    mainViewModel.checkUsernameExist(bi.tilUsername.getText().toString());
                }
            }
            return true;
        });
        bi.tilUsername.setText("@" + mainViewModel.getStorage().readUserInfo().getUsername());
        bi.tilName.setText(mainViewModel.getStorage().readUserInfo().getFullName());
        bi.tilEmail.setText(mainViewModel.getStorage().readUserInfo().getEmail());
        bi.tilPhone.setText(mainViewModel.getStorage().readUserInfo().getPhone());
        UtilityHelperKt.hide(bi.tilPhone);
        UtilityHelperKt.hide(bi.tilAddress);
        UtilityHelperKt.hide(bi.relationship);
        UtilityHelperKt.hide(bi.gender);
        UtilityHelperKt.hide(bi.tilEmail);
        UtilityHelperKt.hide(bi.education);
        bi.tilAddress.setText(mainViewModel.getStorage().readUserInfo().getAddress());
        String bioText = decodeEmoji(mainViewModel.getStorage().readUserInfo().getBio());
        bi.tilBio.setText(bioText);
        bi.tilBirthday.setText(mainViewModel.getStorage().readUserInfo().getDob());
        if(mainViewModel.getStorage().readUserInfo().getDob() != null && !mainViewModel.getStorage().readUserInfo().getDob().isEmpty()){
            bi.closeDbo.setVisibility(View.VISIBLE);
        }
        bi.tilLink.setText(mainViewModel.getStorage().readUserInfo().getWebsite());

        String[] genders = getResources().getStringArray(R.array.gender);
        List<String> gendersList = new ArrayList<String>(Arrays.asList(genders));
        if (gendersList.contains(mainViewModel.getStorage().readUserInfo().getGender())) {
            int pos = gendersList.indexOf(mainViewModel.getStorage().readUserInfo().getGender());
            if (pos >= 0) {
                bi.gender.setSelection(pos);
            }
        }
        String[] educations = getResources().getStringArray(R.array.education);
        List<String> educationList = new ArrayList<String>(Arrays.asList(educations));
        if (educationList.contains(mainViewModel.getStorage().readUserInfo().getEducation())) {
            int pos = educationList.indexOf(mainViewModel.getStorage().readUserInfo().getEducation());
            if (pos >= 0) {
                bi.education.setSelection(pos);
            }
        }
        bi.tilBirthday.setOnClickListener(view -> {
            showDate(1980, 0, 1, R.style.DatePickerSpinner);
        });
        bi.profileImage.setOnClickListener(view -> {
            showBottomSheet();
        });
        bi.selectImage.setOnClickListener(view -> {
            showBottomSheet();
        });
        if (!mainViewModel.getUser().getImage().isEmpty())
            Glide.with(mActivity)
                    .load(API_LINKS.BASE_IMAGE_URL + mainViewModel.getUser().getImage())
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .error(R.drawable.user2)
                    .into(bi.profileImage);
        bi.closeDbo.setOnClickListener(view -> {
            bi.tilBirthday.setText(null);
            bi.closeDbo.setVisibility(View.GONE);
        });


    }

    private void saveCall() {
        HashMap<Object, Object> body = new HashMap();
        body.put("user_id", mainViewModel.getUser().getId());
        body.put("full_name", bi.tilName.getText().toString());
        body.put("username", (bi.tilUsername.getText().toString().replaceFirst("@", "")));
        body.put("email", UtilityHelperKt.getText(mainViewModel.getUser().getEmail()));
        body.put("gender", UtilityHelperKt.getText(mainViewModel.getUser().getGender()));
        body.put("website", bi.tilLink.getText().toString());
        body.put("bio", encodeEmoji(bi.tilBio.getText().toString()));
        body.put("phone", UtilityHelperKt.getText(mainViewModel.getUser().getPhone()));
        body.put("instagram_username", bi.instagramUsername.getText().toString());
        body.put("twitter_username", bi.twitterUsername.getText().toString());
        body.put("dob", bi.tilBirthday.getText().toString());
        body.put("dob", bi.tilBirthday.getText().toString());
        body.put("education", UtilityHelperKt.getText(mainViewModel.getUser().getEducation()));
        body.put("address", UtilityHelperKt.getText(mainViewModel.getUser().getAddress()));
        if (path != null)
            body.put("image", encodeImage(path));
        mainViewModel.editProfile(body);
    }

    /**Activity result handler after image crop from both case either camera or gallery**/
    private final ActivityResultLauncher<Intent> activityResultCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Uri uri = UCrop.getOutput(result.getData());
            path = uri.getPath();
            bi.profileImage.setImageURI(uri);
        }
    });

    /**Activity result handler on image capture from both case either camera and gallery**/
    private final ActivityResultLauncher<Intent> activityResultImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            Uri uri = result.getData().getData();
            Uri outputUri = Uri.fromFile(new File(getTempDirectoryPath() + "/" + System.currentTimeMillis() + "-cropped.jpg"));
            UCrop.Options options = new UCrop.Options();
            options.setHideBottomControls(true);
           Intent intent_crop =  UCrop.of(uri, outputUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(800, 800)
                    .withOptions(options)
                    .getIntent(this);
            activityResultCrop.launch(intent_crop);
        } else if (result.getResultCode() == com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR) {
            Toast.makeText(mActivity, com.github.dhaval2404.imagepicker.ImagePicker.getError(result.getData()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mActivity, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    });

    /**@Method to show action bar at bottom for show two button camera and gallery**/
    private void showBottomSheet() {
        final BottomSheetDialog bottomSheet = new BottomSheetDialog(mActivity, R.style.bottomSheetStyle);
        bottomSheet.setContentView(R.layout.fragment_bottomsheet);
        FloatingActionButton image = bottomSheet.findViewById(R.id.image);
        FloatingActionButton video = bottomSheet.findViewById(R.id.video);
        TextView second_btn_text = bottomSheet.findViewById(R.id.second_btn_text);
        second_btn_text.setText("Camera");
        bottomSheet.show();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
                takePhotoFromGallery();
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
                checkPermission(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},  CAMERA_PERMISSION_CODE);
            }
        });
    }

    private void takePhotoFromGallery() {
        Intent iGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultImage.launch(iGallery);
    }

    private void takePhotoFromCamera() {
        Intent iGallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultImage.launch(iGallery);
    }

    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if (tag.equals(TAG_EDIT_PROFILE)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<User>() {
                        }.getType();
                        User model = gson.fromJson(gson.toJsonTree(responseData.getMsg()).getAsJsonObject().getAsJsonObject("User").toString(), type);
                        Toast.makeText(this, "Profile Update Successfully", Toast.LENGTH_LONG).show();
                        mainViewModel.getStorage().writeUserInfo(model);
                        mainViewModel.setUser(model);
                        finish();

                    } else {
                        Toast.makeText(this, responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }

            });
        }
        if (tag.equals(TAG_CHECK_USERNAME)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200 && responseData.getMsg().equals("ok")) {
                        saveCall();

                    } else {
                        Toast.makeText(this, responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }

            });
        }
    }

    @Override
    public void onFailure(@NonNull String message) {
        bi.progressCircular.setVisibility(View.GONE);
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        bi.tilBirthday.setText(simpleDateFormat.format(calendar.getTime()));
        if(simpleDateFormat.format(calendar.getTime()) != null && !simpleDateFormat.format(calendar.getTime()).isEmpty()){
            bi.closeDbo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCancelled(DatePicker view) {
    }

    private String getTempDirectoryPath() {
        File cache = null;
        cache = getCacheDir();
        cache.mkdirs();
        return cache.getAbsolutePath();
    }

    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, -1);
        Calendar max = new GregorianCalendar();
        max.add(Calendar.YEAR, -1);
        Calendar min = new GregorianCalendar();
        min.add(Calendar.YEAR, -100);
        new SpinnerDatePickerDialogBuilder()
                .context(EditProfileActivity.this)
                .callback(EditProfileActivity.this)
                .onCancel(EditProfileActivity.this)
                .spinnerTheme(spinnerTheme)
                .maxDate(max.get(Calendar.YEAR), max.get(Calendar.MONTH), max.get(Calendar.DAY_OF_MONTH))
                .minDate(min.get(Calendar.YEAR), min.get(Calendar.MONTH), min.get(Calendar.DAY_OF_MONTH))
                .defaultDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .build().show();
    }

    public void checkPermission(String[] permissions, int requestCode)
    {

        if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
        else if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[0]}, requestCode);
        }
        else if (ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {permissions[1]}, requestCode);
        }
        else {
            takePhotoFromCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (CAMERA_PERMISSION_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoFromCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}