package com.example.work.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.work.R;
import com.example.work.database.UserPojo;
import com.example.work.database.UserPojoDao;
import com.example.work.utils.SessionInitializer;
import com.example.work.utils.SharePreference;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {
    private String nameValidate;
    private int currentAge;
    private String profileImg;
    private TextInputEditText name;
    private TextView age;
    private ImageView profileImageView;
    private Uri selectedImage;
    private static final int GALLERY_REQUEST_CODE = 101;
    private UserPojo userPojo;
    private UserPojoDao userPojoDao;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImageView = findViewById(R.id.editProfile);
        name = findViewById(R.id.edit_name);
        age = findViewById(R.id.edit_age);
        MaterialButton edit = findViewById(R.id.edit_btn);
        ImageButton editClickImg = findViewById(R.id.edit_click_image);
        String userId;
        userId = SharePreference.getSharedPref(getApplication(), "userId");

        userPojoDao = ((SessionInitializer) getApplication()).getDaoSession().getUserPojoDao();
        userPojo = userPojoDao.queryBuilder().where(UserPojoDao.Properties.Userid.eq(Integer.valueOf(userId))).build().unique();
       if(userPojo!=null) {
           //un editable
           profileImageView.setEnabled(false);

           //getting existing data
           nameValidate = userPojo.getName();
           currentAge = userPojo.getAge();
           profileImg = userPojo.getConfirmPassword();
           //display existing data

           if(profileImg!=null) {
               Glide.with(EditProfile.this)
                       .load(Uri.parse(profileImg))
                       .into(profileImageView);
           }
           else {
               Log.d("tag ","===========not=========");
           }
           name.setText(nameValidate);
           age.setText(String.valueOf(currentAge));

       }
        editClickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImageView.setEnabled(true);

                if (ContextCompat.checkSelfPermission(Objects.<Context>requireNonNull(EditProfile.this),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
                } else {
                    selectImageFromGallery();
                }


            }
        });

        name.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                name.requestFocus();
                //click edit it will focus on edit text
                if (name.requestFocus()) {
                    //get keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);


                }
                return false;
            }
        });
        //onTouch listener

        age.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    datePicker();
                }
                return false;
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                nameValidate = (Objects.requireNonNull(name.getText())).toString().trim();
                //update profile


                if (selectedImage != null)
                    profileImg = selectedImage.toString();
                userPojo.setConfirmPassword(profileImg);
                userPojo.setAge(currentAge);
                userPojo.setName(nameValidate);
                userPojoDao.update(userPojo);

                Intent intent = new Intent(EditProfile.this, MainActivity.class);

                startActivity(intent);
                Toast.makeText(EditProfile.this, "Updated Profile!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void selectImageFromGallery() {

        Intent i = new Intent();
        i.setAction(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Choose a image"), GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery();
            } else {
                Toast.makeText(EditProfile.this, "permission denied", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED && requestCode == GALLERY_REQUEST_CODE) {
            return;
        }
        if (data != null) {
            selectedImage = data.getData();
            profileImageView.setImageURI(selectedImage);
        }

    }

    private void datePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        int currentDay = myCalendar.get(Calendar.DAY_OF_MONTH);
        final int currentYear = myCalendar.get(Calendar.YEAR);
        int currentMonth = myCalendar.get(Calendar.MONTH);
        //disable future date

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                currentAge = currentYear - year;
                age.setText(String.valueOf(currentAge));

            }
        };
        DatePickerDialog dpDialog = new DatePickerDialog(EditProfile.this, listener, currentYear, currentMonth, currentDay);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpDialog.show();

    }

}
