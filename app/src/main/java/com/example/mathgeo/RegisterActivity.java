package com.example.mathgeo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "createAccount";

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    EditText edit_text_name, edit_text_lastname, edit_text_email, edit_text_password;

    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        ///
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference();
        ///


        edit_text_name= findViewById(R.id.edit_text_name);
        edit_text_lastname= findViewById(R.id.edit_text_lastname);
        edit_text_email= findViewById(R.id.edit_text_email);
        edit_text_password= findViewById(R.id.edit_text_password);

    }



    public void createAccount(View view){

        final String name=edit_text_name.getText().toString();
        final String lastname=edit_text_lastname.getText().toString();
        final String email = edit_text_email.getText().toString();
        final String password = edit_text_password.getText().toString();





        if(!(validateForm(name, lastname, email, password))){ return; }


        ///
//        databaseReference.child("users").child(name).child("user_name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    //Log.i("qqqqq", name);
//                    //Toast.makeText(RegisterActivity.this,"REAL",Toast.LENGTH_LONG);
//                }
//            }
//        });
        ///


        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(RegisterActivity.this,"Kaydolma işlemi başarıyla gerçekleştirildi.",Toast.LENGTH_LONG).show();

                userID=firebaseAuth.getCurrentUser().getUid();     // taking the recent user ID.

                DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                Map<String, Object> userData = new HashMap<>();

                userData.put("Ad",name);
                userData.put("Soyad", lastname);
                userData.put("email", email);
                userData.put("password", password);


                documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Profile created succesfuly"+ userID);
                    }
                });
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,"Lütfen e-posta adresinizi kontrol ediniz.",Toast.LENGTH_LONG).show();

            }
        });

    }

    private boolean validateForm(String name, String lastname, String mail, String password){

        boolean valid = true;

        if(mail.isEmpty()) {
            valid = false;
            Toast.makeText(RegisterActivity.this, "Lütfen mail adresinizi giriniz", Toast.LENGTH_LONG).show();
            return valid;
        } else if(name.isEmpty()){
            valid = false;
            Toast.makeText(RegisterActivity.this,"Lütfen adınızı giriniz.",Toast.LENGTH_LONG).show();
            return valid;
        }else if(lastname.isEmpty()){
            valid = false;
            Toast.makeText(RegisterActivity.this,"Lütfen soyadaınızı giriniz.",Toast.LENGTH_LONG).show();
            return valid;
        } else if(password.isEmpty()){
            valid = false;
            Toast.makeText(RegisterActivity.this,"Lütfen bir şifre belirleyin.",Toast.LENGTH_LONG).show();
            return valid;
        }else if(password.length()<6){
            valid = false;
            Toast.makeText(RegisterActivity.this,"Şifreniz en az altı haneli olmalıdır.",Toast.LENGTH_LONG).show();
            return valid;
        }  // checking the user profile.

        return valid;
    }

    public void backToLogin(View view){ startActivity(new Intent(RegisterActivity.this,LoginScreen.class)); }

    public void closeApp(View view){ RegisterActivity.this.finish();    System.exit(0); }

}