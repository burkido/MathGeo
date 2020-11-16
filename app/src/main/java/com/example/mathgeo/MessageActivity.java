package com.example.mathgeo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mathgeo.adapter.MessageAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity  {

    FirebaseDatabase firebaseDatabase;

    DatabaseReference reference, reference2;

    FirebaseFirestore db;

    FirebaseUser firebaseUser;

    private StorageReference storageReference;

    FirebaseStorage firebaseStorage;

    FirebaseAuth mAuth, firebaseAuth;

    TextView chatUserName;

    EditText messageContent;

    ImageView sendMessage, send_image;

    String name, uid, MyUID, key;

    String myName, getMyName;

    RecyclerView chatRecyclerView;

    Uri imageData;

    Bitmap selectedImage;

    MessageAdapter messageAdapter;

    ArrayList<MessageModel> messageModelList;

    ArrayList<String> image_message;

    AdView adView;

    String photoURL1;

    String uiddd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        define();

        adView = findViewById(R.id.adView);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    private void load_messages() {
        loadMessages();
    }

    private void define() {


        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();

        reference = firebaseDatabase.getReference();

        reference2 = firebaseDatabase.getReference();

        chatUserName = findViewById(R.id.chat_username);

        messageContent = findViewById(R.id.message_for_send);

        sendMessage = findViewById(R.id.send_btn);

        send_image = findViewById(R.id.send_image);

        messageModelList = new ArrayList<>();

        image_message = new ArrayList<>();

//        Intent intent = getIntent();
////        ChatBoxModel chatBoxModel = (ChatBoxModel) intent.getSerializableExtra("MyModel");
////        //System.out.println("MODEL " + chatBoxModel.username);
////        name = chatBoxModel.username;
////        uiddd = chatBoxModel.senderID;
////        System.out.println(name);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            getMyName = extras.getString("MyName");
            uid = extras.getString("uid");
            chatUserName.setText(name);
            //Log.i("(MessageActivity) name:", name);
           // Log.i("(MessageActivity) uid: ", uid);

            //The key argument here must match that used in the other activity
        }

        firebaseUser = mAuth.getCurrentUser();
        MyUID = firebaseUser.getUid();
        DocumentReference docRef = db.collection("users").document(MyUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        myName= (String) document.get("Ad");
                        load_messages();
                    }
                }
            }
        });


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageForSending = messageContent.getText().toString();
                messageContent.setText("");
                export(messageForSending);

            }
        });


        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                }else{
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentToGallery,2);
                    //System.out.println("went to the activity");
                }
            }
        });

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MessageActivity.this,1);
        chatRecyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(MessageActivity.this, messageModelList,MessageActivity.this, name);
        chatRecyclerView.setAdapter(messageAdapter);
    }

    private void export(final String messageForSending) {

        key = reference.child("messages").child(name).child(myName).push().getKey();
        final HashMap<String, Object> messageMap = new HashMap();
        messageMap.put("text", messageForSending);
        messageMap.put("from",name);
        messageMap.put("type", "text");

        final HashMap<String, Object> messageList = new HashMap();
        messageList.put("senderID", uid);
        messageList.put("receiverID", MyUID);
        messageList.put("username", name);
//        Log.i("(EXPORT)myName",myName);
//        Log.i("(EXPORT)name",name);


        firebaseUser = mAuth.getCurrentUser();
        final String uidd = firebaseUser.getUid();
        System.out.println(uidd);
//        final DocumentReference documentReference = db.collection("messages").document(uiddd);
        final DocumentReference documentReference1 = db.collection("messages").document(MyUID);
        final Map<String, Object> messageData = new HashMap<>();

//        DocumentReference docRef = db.collection("profile_photos").document(uidd);
//        DocumentReference docRef2 = db.collection("profile_photos").document(MyUID);
//
//        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot snapshot2 = task.getResult();
//                    photoURL1 = (String) snapshot2.get("downloadProfilePhotoURL");
//            }
//        }
//        });


//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot snapshot = task.getResult();
//                    String photoURL = (String) snapshot.get("downloadProfilePhotoURL");
//
//
//                    messageData.put("receiverProfilePhoto", photoURL);
//                    messageData.put("senderProfilePhoto", photoURL1);
//                    messageData.put("sender", myName);
//                    messageData.put("receiverName", name);
//                    messageData.put("lastMessage", messageForSending);
//                    messageData.put("Date", FieldValue.serverTimestamp());
//                    messageData.put("senderUID", MyUID);
//                    messageData.put("receiverUID", uid);
//
//                    //ikisiin ismi aynı mı diye bak
//                   // DocumentReference documentReference = db.collection("messages").document(uidd);
//
//                    documentReference.set(messageData).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            documentReference.update("lastMessage", messageForSending);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MessageActivity.this, "Mesajlar Yüklenirken Hata Oluştu", Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                    documentReference1.set(messageData).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            documentReference1.update("lastMessage", messageForSending);
//                        }
//                    });
//
//
//                    //documentReference1.update("lastMessage", messageForSending);
//                }
//            }
//        });

        reference.child("messages").child(myName).child(name).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child("messages").child(name).child(myName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //documentReference.update("lastMessage", messageForSending);
                        }
                    });
                }
            }
        });

        //System.out.println(MyUID + uiddd);
        reference2.child("messageList").child(MyUID).child(key).setValue(messageList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference2.child("messageList").child(uid).child(key).setValue(messageList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            }
        });


//        reference2.child("messageList").child(MyUID).child(key).setValue(messageList).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//
//
//                    reference2.child("messageList").child(MyUID).child(uid).child(key).setValue(messageList).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                        }
//                    });
//                }
//            }
//        });
    }

    private void loadMessages(){

        reference.child("messages").child(name).child(myName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                MessageModel messageModel = snapshot.getValue(MessageModel.class);
                //Log.i("Messages: ", messageModel.toString());
                messageModelList.add(messageModel);
                messageAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messageModelList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent toGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(toGallery);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==2 && resultCode == RESULT_OK && data!=null){   //resultCode = if chose the photo   data = if there is a photo
            imageData = data.getData();

            try {
                if(Build.VERSION.SDK_INT >=28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageData);
                    //add_image.setImageBitmap(selectedImage);
                }else{
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                }
                startActivity(new Intent(MessageActivity.this, MainActivity.class));
                System.out.println("image data" + imageData.toString());
                upload_storage();
                //add_image.setImageBitmap(selectedImage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upload_storage() {

        if(imageData!=null){

            UUID uuid = UUID.randomUUID();
            final String uniqueID = "Images/" + uuid +"message.jpg";

            storageReference.child(uniqueID).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final StorageReference newReference = FirebaseStorage.getInstance().getReference(uniqueID);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            final String downloadUrl = uri.toString();
                            System.out.println("message photo link:" + downloadUrl);


                            final String key = reference.child("messages").child(name).child(myName).push().getKey();

                            final HashMap<String, Object> messageMap = new HashMap();
                            messageMap.put("text", downloadUrl);
                            messageMap.put("from",name);
                            messageMap.put("type","image");

                            reference.child("messages").child(myName).child(name).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        reference.child("messages").child(name).child(myName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this,"Fotoğraf Yüklenirken Hata Oluştu. Lütfen Tekrar Deneyiniz",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}