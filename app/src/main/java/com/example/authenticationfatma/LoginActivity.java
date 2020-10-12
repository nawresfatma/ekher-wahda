package com.example.authenticationfatma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.annotations.Nullable;


public class LoginActivity extends AppCompatActivity {
   private CallbackManager callbackManager ;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private  String TAG = "LoginActivity";
    private int RC_SIGN_IN = 1;
    LoginButton loginFacebook ;
    private AccessTokenTracker accessTokenTracker;
    private static String Tag="Facebook authentication";
    TextView submit ,textView2;
    EditText email , pwd ;
    Button but2;
    FirebaseAuth mAuth;
    ProgressBar progress2;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInButton = findViewById(R.id.signInButton);
        progress2 = findViewById(R.id.progressBar2);
        submit = findViewById(R.id.signIn);
        textView2 = findViewById(R.id.textView);

        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        loginFacebook=findViewById(R.id.login_button);
        loginFacebook.setReadPermissions("email","public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(Tag,"OnSuccess"+ loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(Tag,"OnCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Tag,"OnError" + error);

            }
        });
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user1=firebaseAuth.getCurrentUser();
                if (user1!=null){
                    updateUI(user1);
                }
                else {
                    updateUI(null);
                }
                }

            };

        accessTokenTracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken==null){
                    mAuth.signOut();
                }

            }
        };

        but2 = (Button) findViewById(R.id.button2);
        email = (EditText) findViewById(R.id.EmailAddress);
        pwd = (EditText) findViewById(R.id.Password);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Toast.makeText(LoginActivity.this, "you are logged in !", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                }
            }
        };
    but2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String Email =email.getText().toString();
            String Pwd = pwd.getText().toString();
            if(Email.isEmpty()){
                email.setError("Please enter email id");
                email.requestFocus();
            }else if (Pwd.isEmpty()){
                pwd.setError("please enter your password");
                pwd.requestFocus();
            }
            else if (Pwd.isEmpty() && Email.isEmpty() ){
                Toast.makeText(LoginActivity.this, "enter your email and password", Toast.LENGTH_SHORT).show();
            }
            else if( !(Pwd.isEmpty() && Email.isEmpty() )) {
                progress2.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(Email,Pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            progress2.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "login error , please login again!", Toast.LENGTH_SHORT).show();


                        }else{
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                        }
                    }
                });
            }else {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));


                        }
                    }
    });
       textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void handleFacebookToken(AccessToken token) {
        Log.d(Tag,"handleFacebookToken"+token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(Tag,"signin with crendetial : successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }else{

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }


    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(LoginActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(LoginActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }
    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }
        else{
            Toast.makeText(LoginActivity.this, "acc failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateUI(FirebaseUser fUser){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            account.getPhotoUrl();

            Toast.makeText(LoginActivity.this,personName + personEmail ,Toast.LENGTH_SHORT).show();
            if(fUser!=null){
                if(fUser.getPhotoUrl() !=null){
                    String photoUrl = fUser.getPhotoUrl().toString();
                }
            }
        }


    }



}

