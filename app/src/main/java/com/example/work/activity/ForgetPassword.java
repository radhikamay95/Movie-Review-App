package com.example.work.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.example.work.R;
import com.example.work.database.UserPojo;
import com.example.work.database.UserPojoDao;
import com.example.work.utils.SessionInitializer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ForgetPassword extends AppCompatActivity {
    private Button generateOtp;
    private Button submitOtp;
    private Button forgot;
    private PinView pinView;
    private TextInputEditText newPassword;
    private EditText email;
    private TextInputEditText confirmPassword;

    private TextInputLayout newPassLayout;
    private TextInputLayout confirmPassLayout;
    private UserPojo userPojo;
    private UserPojoDao userPojoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forget_password1);
        pinView = findViewById(R.id.firstPinView);
        submitOtp = findViewById(R.id.submit_Otp);
        forgot = findViewById(R.id.forget_btn);
        newPassword = findViewById(R.id.input_new_pass);
        confirmPassword = findViewById(R.id.input_confirm_password);
        newPassLayout = findViewById(R.id.new_pass_layout);
        confirmPassLayout = findViewById(R.id.confirm_password_layout);
        //invisible
        pinView.setVisibility(View.INVISIBLE);
        submitOtp.setVisibility(View.INVISIBLE);
        newPassLayout.setVisibility(View.INVISIBLE);
        confirmPassLayout.setVisibility(View.INVISIBLE);
        forgot.setVisibility(View.INVISIBLE);

        email = findViewById(R.id.email_id);
        generateOtp = findViewById(R.id.gen_Otp);

        userPojoDao = ((SessionInitializer) getApplication()).getDaoSession().getUserPojoDao();

        generateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailIdInput = email.getText().toString();
                userPojo = userPojoDao.queryBuilder().where(UserPojoDao.Properties.Emailid.eq(emailIdInput)).build().unique();
                if (userPojo != null) {
                    String userNameValidation = userPojo.getEmailid();

                    if (userNameValidation.isEmpty() || (!emailIdInput.equals(userNameValidation) || !Patterns.EMAIL_ADDRESS.matcher(userNameValidation).matches())) {
                        email.setError("Invalid UserName");


                    } else {
                        email.setError(null);
                        generateOtp.setVisibility(View.INVISIBLE);
                        email.setVisibility(View.INVISIBLE);
                        pinView.setVisibility(View.VISIBLE);
                        submitOtp.setVisibility(View.VISIBLE);
                    }
                }

            }

        });

        submitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = Objects.requireNonNull(pinView.getText()).toString();
                if (value.equals("3636")) {
                    pinView.setVisibility(View.INVISIBLE);
                    submitOtp.setVisibility(View.INVISIBLE);
                    newPassLayout.setVisibility(View.VISIBLE);
                    confirmPassLayout.setVisibility(View.VISIBLE);
                    forgot.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ForgetPassword.this, "invalid OTP", Toast.LENGTH_SHORT).show();

                }
            }
        });


        forgot.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                setForgot();
            }
        });


    }

    private void setForgot() {
        String passwordValidate = Objects.requireNonNull(newPassword.getText()).toString().trim();
        String confirmPasswordValidate = Objects.requireNonNull(confirmPassword.getText()).toString().trim();
        Intent intent = new Intent(ForgetPassword.this, Login.class);

        //validation
        final String PASSWORD_PATTERN = getString(R.string.PasswordPattern);
        if (!passwordValidate.matches(PASSWORD_PATTERN)) {
            newPassLayout.setError("Password dose not meet[A-z,0-9,@##$$$]");

        } else {
            newPassLayout.setError(null);
        }
        if (confirmPasswordValidate.isEmpty()) {
            confirmPassLayout.setError("password and confirm password not match");

        } else {
            confirmPassword.setError(null);
        }

        //update SharePreference

        userPojo.setPassword(passwordValidate);
        userPojoDao.update(userPojo);

        Toast.makeText(ForgetPassword.this, "Password Changed", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

}
