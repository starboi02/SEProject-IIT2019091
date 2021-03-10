package com.example.anomalydetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    TextView login_btn;
    FirebaseAuth mAuth;
    EditText email,pass;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth= FirebaseAuth.getInstance();

        login_btn=findViewById(R.id.login_txt);
        email=findViewById(R.id.email);
        pass=findViewById(R.id.password);
        btn=findViewById(R.id.btn_register);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().matches("") && !pass.getText().toString().matches("")){
                    register(email.getText().toString(),pass.getText().toString());
                }
                else if( !pass.getText().toString().matches("") && pass.getText().toString().length()<6){
                    Toast.makeText(RegisterActivity.this,"Password should be at least 6 characters",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Email/Password cannot be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public void register(String emailAd,String password){
        mAuth.createUserWithEmailAndPassword(emailAd, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Auth", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(RegisterActivity.this,"Authentication Successful.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent =  new Intent(RegisterActivity.this,MainActivity.class);
                            intent.putExtra("name","");

                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Auth", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent =  new Intent(RegisterActivity.this,MainActivity.class);
            intent.putExtra("name","");
            startActivity(intent);
            finish();
        }
    }

}