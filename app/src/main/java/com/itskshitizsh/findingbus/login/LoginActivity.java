package com.itskshitizsh.findingbus.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.itskshitizsh.findingbus.R;
import com.itskshitizsh.findingbus.ui.HomeActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = ".LoginActivity";
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    EditText userPasswordEditText;
    private EditText userNameEditText;

    private boolean doubleBackToExitPressedOnce = false;

    private boolean isPasswordAlertDialogOpen = false;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.lnmiit);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);   // So that keyboard doesn't pop up every time user open app

        userNameEditText = findViewById(R.id.userEmailAddress);
        userPasswordEditText = findViewById(R.id.userPassword);

        findViewById(R.id.logInButton).setOnClickListener(this);
        findViewById(R.id.google_button).setOnClickListener(this);

        progressBar = findViewById(R.id.progress_bar);

        checkNetConnection();

        userPasswordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_ENTER) {
                        logIn();
                    }
                }
                return false;
            }
        });
    }

    private boolean checkNetConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !(networkInfo.isConnected())) {
                Toast.makeText(getApplicationContext(), "Network Unavailable", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.google_button:
                if (checkNetConnection()) {
                    googleSignIn();
                }
                break;
            case R.id.logInButton:
                if (checkNetConnection()) {
                    logIn();
                }
                break;
            default:
                break;
        }
    }

    public void logIn() {
        // LogIn Logic Here!

//        RadioButton termsAndConditions = findViewById(R.id.radioButton);

        if (userNameEditText.getText().toString().isEmpty()) {
            userNameEditText.requestFocus();
            userNameEditText.setError("Enter User Email");

        } else {
            if (userPasswordEditText.getText().toString().isEmpty()) {
                userPasswordEditText.requestFocus();
                userPasswordEditText.setError("Enter Password");
            } else {

                hideKeyboard();
                progressBar.setVisibility(View.VISIBLE);

                String email = userNameEditText.getText().toString().trim();
                String password = userPasswordEditText.getText().toString().trim();

                mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        }

    }

    public void googleSignIn() {
        progressBar.setVisibility(View.VISIBLE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.INVISIBLE);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            showPasswordAlertDialog();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void showPasswordAlertDialog() {

        isPasswordAlertDialogOpen = true;

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("PASSWORD");
        alertDialog.setMessage("Enter Password (Min. 6 characters)");
        alertDialog.setCancelable(false);

        final EditText input = new EditText(LoginActivity.this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);//Text password
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_password_icon);

        alertDialog.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString().trim();
                if (password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Enter valid password!", Toast.LENGTH_SHORT).show();
                    showPasswordAlertDialog();
                } else if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Too short!", Toast.LENGTH_SHORT).show();
                    showPasswordAlertDialog();
                } else {
                    isPasswordAlertDialogOpen = false;
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.updatePassword(password);
                    String username = user.getDisplayName();
                    String userEmail = user.getEmail();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                    finish();
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                isPasswordAlertDialogOpen = false;
            }
        });

        dialog = alertDialog.create();
        dialog.show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        /*
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
        */

        if (isPasswordAlertDialogOpen) {
            dialog.dismiss();
        }
    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_hint_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
