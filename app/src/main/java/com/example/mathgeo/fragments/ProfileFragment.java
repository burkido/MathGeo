package com.example.mathgeo.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mathgeo.LoginScreen;
import com.example.mathgeo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    FirebaseAuth mAuth;

    FirebaseUser firebaseUser, user_pp;

    FirebaseFirestore firebaseFirestore;

    FirebaseStorage firebaseStorage;

    StorageReference storageReference;

    CircleImageView circleImageView;

    Uri imageData;

    Bitmap selectedImage;

    Button log_out, upload_profile_photo;

    TextView email_profile;




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
                    circleImageView.setImageBitmap(selectedImage);
                    startActivity(new Intent(ProfileFragment.this.getContext(), HomeFragment.class));
                }else{
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), imageData);
                }
                circleImageView.setImageBitmap(selectedImage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_profile, container, false);

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_profile,container,false);


        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();

        circleImageView = viewGroup.findViewById(R.id.image_view_profile);
        log_out = viewGroup.findViewById(R.id.log_out_profile);
        upload_profile_photo = viewGroup.findViewById(R.id.change_profile_photo);
        email_profile = viewGroup.findViewById(R.id.email_profile);

        firebaseUser = mAuth.getCurrentUser();
        final String email = firebaseUser.getEmail();
        email_profile.setText(email);




        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                } else {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentToGallery, 2);
                }
            }
        });

        upload_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageData != null) {

                    UUID uuid = UUID.randomUUID();
                    final String uniqueID = "Profile_Photos/" + uuid + ".jpg";

                    storageReference.child(uniqueID).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //startActivity(new Intent(AddFragment.this.getContext(),FeedActivity.class));
                            StorageReference newReference = FirebaseStorage.getInstance().getReference(uniqueID);
                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String downloadUrl = uri.toString();
                                    firebaseUser = mAuth.getCurrentUser();
                                    final String uid = firebaseUser.getUid();


                                    DocumentReference documentReference = firebaseFirestore.collection("profile_photos").document(uid);
                                    HashMap<String, Object> postDatas = new HashMap<>();

                                    postDatas.put("UID", uid);
                                    postDatas.put("downloadProfilePhotoURL", downloadUrl);

                                    documentReference.set(postDatas).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProfileFragment.this.getContext(),"Profil Fotoğrafı Başarıyla Güncellendi", Toast.LENGTH_LONG);
                                            //Log.d(TAG,"Profile created succesfuly"+ userID);
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileFragment.this.getContext(), "Fotoğraf Yüklenirken Hata Oluştu Ltüfen Tekrar Deneyiniz", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(ProfileFragment.this.getContext(), LoginScreen.class));
            }
        });

        return viewGroup;
    }
}