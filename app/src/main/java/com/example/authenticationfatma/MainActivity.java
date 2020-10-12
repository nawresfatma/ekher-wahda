package com.example.authenticationfatma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    TextView submit ,textView2;
    EditText email , pwd ,fullname ;
    Button but;
    ProgressBar progress;
    FirebaseAuth mAuth;
    User user;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser userkey;

    private String Email;
    private String Name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress=findViewById(R.id.progressBar);
        //textView2 = findViewById(R.id.textView2);
        fullname=findViewById(R.id.FullName);
        //submit=findViewById(R.id.signUp);
        mAuth = FirebaseAuth.getInstance();
        but = findViewById(R.id.button);
        email =findViewById(R.id.EmailAddress);
        pwd =findViewById(R.id.Password);

        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users");


        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email =email.getText().toString().trim();
                String Pwd = pwd.getText().toString().trim();
                Name =fullname.getText().toString();
                if(Email.isEmpty()){
                    email.setError("Please enter email id");
                    email.requestFocus();
                }

                else if (Pwd.isEmpty()){
                    pwd.setError("please enter your password");
                    pwd.requestFocus();

                }
                else if (Name.isEmpty()) {
                    fullname.setError("please enter your Full name");
                    fullname.requestFocus();
                }
                else if (Pwd.isEmpty() && Email.isEmpty()){
                    Toast.makeText(MainActivity.this, "enter your email and password", Toast.LENGTH_SHORT).show();
                }
                else if( !(Pwd.isEmpty() && Email.isEmpty() && Name.isEmpty())){
                progress.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(Email,Pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "SignUp Unseccessful , please try again", Toast.LENGTH_SHORT).show();
                                progress.setVisibility(View.GONE);

                            }else{
                                userkey = FirebaseAuth.getInstance().getCurrentUser() ;

                                user = new User(Name , Email);

                                myRef.child(userkey.getUid()).setValue(user);

                                startActivity(new Intent(MainActivity.this, HomeActivity.class));


                            }
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this, "Error Ocurred !", Toast.LENGTH_SHORT).show();

                }
            }

    });
      /*  textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
*/
    }


}