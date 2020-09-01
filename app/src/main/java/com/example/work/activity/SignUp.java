package com.example.work.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.work.R;
import com.example.work.database.UserPojo;
import com.example.work.database.UserPojoDao;
import com.example.work.utils.SessionInitializer;
import com.example.work.utils.SharePreference;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Objects;


public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;

    private Button signUpButton;
    private TextView age;
    private ImageView profileImageView;

    private int currentAge;
    private String genderItem;

    //user global variable
    private String nameValidate;
    private String emailValidate;
    private String passwordValidate;
    private static final int GALLERY_REQUEST_CODE = 101;
    private Uri selectedImage;
    private UserPojoDao userPojoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Spinner spinner;


        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
        profileImageView = findViewById(R.id.edit_profile);
        name = findViewById(R.id.edit_name);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        confirmPassword = findViewById(R.id.input_confirm_password);

        age = findViewById(R.id.get_date);
        spinner = findViewById(R.id.gender);
        signUpButton = findViewById(R.id.edit_btn);


        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        spinner.setOnItemSelectedListener(this);

        //gender
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gender_list));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        /* greenDao */

         userPojoDao= ((SessionInitializer)getApplication()).getDaoSession().getUserPojoDao();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Log.d("tag", "SignUp Success");
                signUp();


            }
        });


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Objects.<Context>requireNonNull(SignUp.this),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
                } else {
                    selectImageFromGallery();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery();
            } else {
                Toast.makeText(SignUp.this, "permission denied", Toast.LENGTH_SHORT)
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
            assert profileImageView != null;


        }

    }

    private void selectImageFromGallery() {

        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Choose a image"), GALLERY_REQUEST_CODE);

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
        DatePickerDialog dpDialog = new DatePickerDialog(SignUp.this, listener, currentYear, currentMonth, currentDay);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void signUp() {


        if (!validate()) {
            onSignUpFailed();
            return;
        } else {
            onSignUpSuccess();
        }

        signUpButton.setEnabled(true);

        final ProgressDialog progressDialog = new ProgressDialog(SignUp.this,
                R.style.MyProgressDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        Intent intent = new Intent(SignUp.this, MainActivity.class);

        //greenDao database inserting
        UserPojo userPojo = new UserPojo();
        userPojo.setName(nameValidate);
        userPojo.setEmailid(emailValidate);
        userPojo.setPassword(passwordValidate);
        userPojo.setGender(genderItem);
        userPojo.setAge(currentAge);

        if (selectedImage != null)
            userPojo.setConfirmPassword(selectedImage.toString());

        userPojoDao.insert(userPojo);


        SharePreference.setSharedPref(getApplicationContext(), "logged", "logged");

        progressDialog.dismiss();

        startActivity(intent);
        finish();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean
    validate() {
        boolean valid = true;

        final String PASSWORD_PATTERN = getString(R.string.PasswordPattern);

        //this is for image storing
         String confirmPasswordValidate;

        nameValidate = Objects.requireNonNull(name.getText()).toString().trim();
        emailValidate = Objects.requireNonNull(email.getText()).toString().trim();
        passwordValidate = Objects.requireNonNull(password.getText()).toString().trim();
        confirmPasswordValidate = Objects.requireNonNull(confirmPassword.getText()).toString().trim();


        if (nameValidate.isEmpty() || nameValidate.length() < 3) {
            name.setError("at least 3 characters");
            valid = false;
        } else {
            name.setError(null);
        }

        if (emailValidate.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailValidate).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (!passwordValidate.matches(PASSWORD_PATTERN)) {
            passwordLayout.setError("Password dose not meet[A-z,0-9,@##$$$]");
            valid = false;
        } else {
            password.setError(null);
        }
        if (confirmPasswordValidate.isEmpty()) {
            confirmPasswordLayout.setError("password and confirm password not match");
            valid = false;
        } else {
            confirmPassword.setError(null);
        }
        if ((genderItem.isEmpty())) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if ((currentAge == 0) || currentAge < -1) {
            age.setError("age invalid");
            valid = false;
        } else {
            age.setError(null);
        }
        if (selectedImage == null) {
            Toast.makeText(this, "please select profile picture", Toast.LENGTH_SHORT).show();
            valid = false;
        }


        return (valid);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onSignUpSuccess() {
        signUpButton.setEnabled(true);
        if (validate()) {
            Toast.makeText(SignUp.this, "Sign-Up Success", Toast.LENGTH_SHORT).show();
            signUpButton.setEnabled(true);
        }

    }

    private void onSignUpFailed() {
        Toast.makeText(SignUp.this, "Sign-Up  Failed", Toast.LENGTH_SHORT).show();
        signUpButton.setEnabled(true);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        genderItem = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // empty method
    }


}
