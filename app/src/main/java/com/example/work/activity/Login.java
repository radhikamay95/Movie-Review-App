package com.example.work.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.work.R;
import com.example.work.database.UserPojo;
import com.example.work.database.UserPojoDao;
import com.example.work.utils.SessionInitializer;
import com.example.work.utils.SharePreference;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private TextInputLayout passwordLayout;
    private TextInputEditText userNameEdit;
    private TextInputEditText password;
    // Session Manager Class
    private static final String LOGGED_IN = "logged";

    private ActivityOptions options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (SharePreference.getSharedPref(getApplicationContext(), LOGGED_IN).equals(LOGGED_IN)) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        LinearLayout registerHere;
        TextView forgetPassword;
        Button loginButton;


        passwordLayout = findViewById(R.id.password_layout);
        userNameEdit = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forgetPassword = findViewById(R.id.forget_password);
        loginButton = findViewById(R.id.btn_login);
        registerHere = findViewById(R.id.register_layout);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (validateLogin()) {
                    Toast.makeText(Login.this, "Logged in ", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Login.this, MainActivity.class);
                    SharePreference.setSharedPref(getApplicationContext(), LOGGED_IN, LOGGED_IN);
                    startActivity(i);
                } else {
                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerHere.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.enter_from_right, R.anim.exit_to_left);
                startActivity(intent, options.toBundle());
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgetPassword.class);
                options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.enter_from_right, R.anim.exit_to_left);
                startActivity(intent, options.toBundle());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean validateLogin() {
        boolean valid = true;

        String userLogin = Objects.requireNonNull(userNameEdit.getText()).toString().trim();
        String passLogin = Objects.requireNonNull(password.getText()).toString().trim();
        UserPojoDao userPojoDao;

        UserPojo userPojo;
        String userName;
        String userPass;
        //getting user data -- email id
        userPojoDao = ((SessionInitializer) getApplication()).getDaoSession().getUserPojoDao();
        userPojo = userPojoDao.queryBuilder().where(UserPojoDao.Properties.Emailid.eq(userLogin)).build().unique();

        if (userPojo != null) {
            userName = userPojo.getEmailid();
            Log.e("tag------", userName);
            userPass = userPojo.getPassword();
            Log.e("tag----------", userPass);

            String userID=userPojo.getUserid().toString();

            SharePreference.setSharedPref(getApplication(), "userId",userID);

            if (userName.isEmpty() || (!userLogin.equals(userName) || !Patterns.EMAIL_ADDRESS.matcher(userName).matches())) {
                this.userNameEdit.setError("Invalid UserName");
                valid = false;

            } else {
                this.userNameEdit.setError(null);
            }
            if ((!passLogin.equals(userPass))) {
                passwordLayout.setError("Invalid Password");
                valid = false;
            } else {
                password.setError(null);
            }
        } else {
            Toast.makeText(Login.this, "Wrong Credential", Toast.LENGTH_SHORT).show();
        }

        return (valid);
    }
}
