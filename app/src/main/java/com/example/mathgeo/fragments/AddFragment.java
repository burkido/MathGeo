package com.example.mathgeo.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.mathgeo.LoadingDialog;
import com.example.mathgeo.MainActivity;
import com.example.mathgeo.R;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import static android.app.Activity.RESULT_OK;


public class AddFragment extends Fragment {

    /*public AddFragment() {
        // Required empty public constructor
    }*/

    ImageView close, add_image;

    EditText commentText;

    Uri imageData;

    Bitmap selectedImage;

    Button upload;

    private AdView adView;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;



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
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContext().getContentResolver(), imageData);
                    add_image.setImageBitmap(selectedImage);
                    startActivity(new Intent(AddFragment.this.getContext(), MainActivity.class));
                }else{

                    if (selectedImage != null && !selectedImage.isRecycled())
                    {
                        selectedImage.recycle();
                        selectedImage = null;
                        System.gc();
                    }
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), imageData);
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                   // byte[] byteArray = stream.toByteArray();
                    //selectedImage.recycle();
                }
                add_image.setImageBitmap(selectedImage);
                //selectedImage.recycle();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ViewGroup viewGroup =(ViewGroup) inflater.inflate(R.layout.fragment_add,container,false);


        adView = viewGroup.findViewById(R.id.adView);
        MobileAds.initialize(this.getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final LoadingDialog loadingDialog;
        loadingDialog = new LoadingDialog((Activity) AddFragment.this.getContext());

        // xml implementations...
        close=viewGroup.findViewById(R.id.close);
        add_image=viewGroup.findViewById(R.id.add_image);
        upload = viewGroup.findViewById(R.id.upload_image);
        commentText = viewGroup.findViewById(R.id.add_comment);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddFragment.this.getContext(),MainActivity.class));   //for cancel the share photo procees.
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                }else{
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentToGallery,2);
                }
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageData!=null){
                    loadingDialog.startLoadingDialog();

                    UUID uuid = UUID.randomUUID();
                    final String uniqueID = "Images/" + uuid +".jpg";

                    storageReference.child(uniqueID).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //startActivity(new Intent(AddFragment.this.getContext(),FeedActivity.class));
                            final StorageReference newReference = FirebaseStorage.getInstance().getReference(uniqueID);
                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    final String downloadUrl = uri.toString();
                                    firebaseUser = firebaseAuth.getCurrentUser();
                                    final String uid = firebaseUser.getUid();
                                    final String userEmail = firebaseUser.getEmail();
                                    final String comment = commentText.getText().toString();


                                    final HashMap<String, Object> nick_name = new HashMap<>();
                                    final String[] name = new String[1];

                                    DocumentReference docRef = db.collection("users").document(uid);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {

                                                    name[0] = (String) document.get("Ad");

                                                    nick_name.put("downloadURL",downloadUrl);
                                                    nick_name.put("Date", FieldValue.serverTimestamp());
                                                    nick_name.put("UID", uid);
                                                    nick_name.put("Description",comment);
                                                    nick_name.put("User_e-mail",userEmail);
                                                    nick_name.put("Adı", name[0]);
                                                    nick_name.put("replied",1);

                                                    db.collection("posts").add(nick_name).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            loadingDialog.dismissDialog();
                                                            Intent intent = new Intent(AddFragment.this.getContext(),MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(AddFragment.this.getContext(),"Öngürelemeyen Bir Hata Oluştu. Lütfen Tekrar Deneyiniz.",Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddFragment.this.getContext(),"Fotoğraf Yüklenirken Hata Oluştu. Lütfen Tekrar Deneyiniz",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        return viewGroup;
    }
}