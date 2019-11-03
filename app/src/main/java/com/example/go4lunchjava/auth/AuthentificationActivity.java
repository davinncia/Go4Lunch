package com.example.go4lunchjava.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.go4lunchjava.MainActivity;
import com.example.go4lunchjava.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthentificationActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SIGN_IN = 1000;
    public static final String TAG = AuthentificationActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private CallbackManager mCallbackManager; //Facebook

    private SignInButton mGoogleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);

        mAuth = FirebaseAuth.getInstance();

        //Sign Out
        //mAuth.getInstance().signOut();

        setUpGoogleSignIn();
        setUpFaceBookSignIn();
    }

    private void setUpGoogleSignIn(){

        mGoogleSignInButton = findViewById(R.id.btn_google_sign_in);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id)) //Bug with strings file??
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);

            }
        });
    }

    private void setUpFaceBookSignIn(){

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton mFacebookLogInButton = findViewById(R.id.facebook_login_button);
        mFacebookLogInButton.setPermissions("email", "public_profile");

        // Callback registration
        mFacebookLogInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "Facebook sign in success");

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.v(TAG, "Facebook log in cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG, "Error occured during Facebook log in", error);
            }
        });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d(AuthentificationActivity.class.getSimpleName(), "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        signInToFireBase(credential);
    }

    private void handleFacebookAccessToken(AccessToken token){
        Log.d(TAG, "handleFacebookAccessToken: " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signInToFireBase(credential);
    }

    private void signInToFireBase(AuthCredential credential){

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // Sign in success, update UI with the signed-in user's information
                    Log.w(AuthentificationActivity.class.getSimpleName(), "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(AuthentificationActivity.class.getSimpleName(), "signInWithCredential:failure", task.getException());
                    Toast.makeText(AuthentificationActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        //GOOGLE
        if (requestCode == REQUEST_CODE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Log.e(AuthentificationActivity.class.getSimpleName(), "Google sign in failed", e);
            }
        }
    }

    private void updateUI(FirebaseUser user){

        if (user != null){
            //Account detected - Straight to main
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
    }
}
