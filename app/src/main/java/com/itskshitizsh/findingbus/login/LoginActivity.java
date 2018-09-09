package com.itskshitizsh.findingbus.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.itskshitizsh.findingbus.R;
import com.itskshitizsh.findingbus.ui.HomeActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.lnmiit);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);   // So that keyboard doesn't pop up every time user open app

        findViewById(R.id.logInButton).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            //   case R.id.signUpButton:
            //            signUp();
            //      Toast.makeText(getApplicationContext(),"Need to Add Function!",Toast.LENGTH_SHORT).show();
            //    break;
            case R.id.logInButton:
                logIn();
                break;
            default:
                break;
        }
    }

    public void logIn() {
        // LogIn Logic Here!
        EditText userNameEditText = findViewById(R.id.userEmailAddress);
        EditText userPasswordEditText = findViewById(R.id.userPassword);

        RadioButton termsAndConditions = findViewById(R.id.radioButton);

        if (userNameEditText.getText().toString().isEmpty()) {
            userNameEditText.requestFocus();
            userNameEditText.setError("Enter User Email");

        } else {
            if (userPasswordEditText.getText().toString().isEmpty()) {
                userPasswordEditText.requestFocus();
                userPasswordEditText.setError("Enter Password");
            } else {
                if (!termsAndConditions.isChecked()) {
                    termsAndConditions.requestFocus();
                    Toast.makeText(getApplicationContext(), "Agree to Terms and Conditions!", Toast.LENGTH_SHORT).show();
                } else {
                    // Rest Code Here!
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            }
        }

    }

}
