package com.example.mathgeo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreen extends AppCompatActivity {


    EditText editTextTextEmailAddress, editTextTextPasswordLogin;

    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextTextPasswordLogin = findViewById(R.id.editTextTextPasswordLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();
        if(firebaseUser!=null) startActivity(new Intent(LoginScreen.this, MainActivity.class));

    }

    public void register(View view){ startActivity(new Intent(LoginScreen.this, RegisterActivity.class)); }

    public void closeApp(View view){ LoginScreen.this.finish();    System.exit(0); }

    public void loginToApp(View view){

        String mail, password;

        mail = editTextTextEmailAddress.getText().toString();   password = editTextTextPasswordLogin.getText().toString();

        // login to app
        firebaseAuth.signInWithEmailAndPassword(mail, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                startActivity(new Intent(LoginScreen.this, MainActivity.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginScreen.this,"Lütfen bilgilerinizi kontrol edin.",Toast.LENGTH_LONG).show();
            }
        });

    }
}