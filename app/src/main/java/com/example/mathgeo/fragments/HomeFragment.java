package com.example.mathgeo.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mathgeo.ChatBoxModel;
import com.example.mathgeo.FullscreenActivity1;
import com.example.mathgeo.R;
import com.example.mathgeo.adapter.FeedRecycleAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Map;


public class HomeFragment extends Fragment implements FeedRecycleAdapter.OnImageListener {



    public HomeFragment() {
        // Required empty public constructor
    }


   // private static final String KEY_NAME = "Ad";


    FirebaseAuth mAuth, mAuth2, mAuth3;

    FirebaseUser firebaseUser, firebaseUser2;

    private FirebaseFirestore db;

    String myEmail;

    RecyclerView recyclerView;

    ArrayList<String> userCommentList;
    ArrayList<String> userNameList;
    ArrayList<String> userQuestionList;
    ArrayList<String> userProfilePhotoList;
    ArrayList<String> userUIDList;
    ArrayList<String> userRepliedList;



    FeedRecycleAdapter feedRecycleAdapter;


    private AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                // Inflate the layout for this fragment

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home,container,false);


//        adView = new AdView(this.getContext());
//        adView.setAdSize(AdSize.SMART_BANNER);
//        adView.setVisibility(View.VISIBLE);
//        AdRequest.Builder builder = new AdRequest.Builder();
//        adView.setBackgroundColor(0xff000000);
//        adView.setAdUnitId("ca-app-pub-2051889947393127/8794870340");
//        adView.loadAd(builder.build());


//        adView = viewGroup.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);

        mAuth = FirebaseAuth.getInstance();
        mAuth2 = FirebaseAuth.getInstance();
        mAuth3 = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        firebaseUser = mAuth3.getCurrentUser();
        myEmail = firebaseUser.getEmail();

        recyclerView = viewGroup.findViewById(R.id.recycler_view);

        userCommentList = new ArrayList<>();
        userNameList = new ArrayList<>();
        userQuestionList = new ArrayList<>();
        userProfilePhotoList = new ArrayList<>();
        userUIDList = new ArrayList<>();
        userRepliedList = new ArrayList<>();


        getData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        feedRecycleAdapter = new FeedRecycleAdapter(userNameList, userCommentList, userQuestionList, userProfilePhotoList, userUIDList, this, userRepliedList);
        recyclerView.setAdapter(feedRecycleAdapter);


        return viewGroup;
    }

    public void getData() {

        CollectionReference collectionReference = db.collection("posts");
        collectionReference.orderBy("Date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    return;
                }

                if(queryDocumentSnapshots !=null){
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        Map<String, Object> data = snapshot.getData();

                       // assert data != null;
                        final String description = (String) data.get("Description");
                        final String name = (String) data.get("Adı");
                        final String downloadUrl = (String) data.get("downloadURL");
                        String uid = (String) data.get("UID");
                        String email = (String) data.get("User_e-mail");

                        userCommentList.add(description);
                        userNameList.add(name);
                        userQuestionList.add(downloadUrl);
                        userUIDList.add(uid);

                        if(myEmail.equals(email)){
                            userRepliedList.add("0");
                        }else{
                            userRepliedList.add("1");
                        }

                        final String[] profile_photo_url = new String[1];
                       // assert uid != null;
                        DocumentReference docRef = db.collection("profile_photos").document(uid);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    assert document != null;
                                    if (document.exists()) {

                                        profile_photo_url[0] = (String) document.get("downloadProfilePhotoURL");
//                                        userCommentList.add(description);
//                                        userNameList.add(name);
//                                        userQuestionList.add(downloadUrl);
                                        userProfilePhotoList.add(profile_photo_url[0]);
                                        feedRecycleAdapter.notifyDataSetChanged();

                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void OnImageListener(int position) {

        Intent intent = new Intent(getActivity(), FullscreenActivity1.class);
        intent.putExtra("image", userQuestionList.get(position));
        startActivity(intent);
    }
}