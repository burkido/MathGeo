package com.example.mathgeo.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mathgeo.ChatBoxModel;
import com.example.mathgeo.MessageActivity;
import com.example.mathgeo.MessageActivity2;
import com.example.mathgeo.R;
import com.example.mathgeo.adapter.ChatBoxAdapter;
import com.example.mathgeo.adapter.MessageListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MessageFragment extends Fragment implements MessageListAdapter.OnChatBoxListener {

    private RecyclerView recyclerView;

    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;
    DatabaseReference reference;

    ArrayList<ChatBoxModel> chatBoxModel;

    ChatBoxModel model;

    MessageListAdapter messageListAdapter;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_message, container, false);

        chatBoxModel = new ArrayList<>();

        recyclerView = viewGroup.findViewById(R.id.recycler_view_message_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        messageListAdapter = new MessageListAdapter(chatBoxModel,this);
        //chatBoxModel = new ArrayList<>();
        //recyclerView.removeItemDecorationAt(0);
        recyclerView.setAdapter(messageListAdapter);


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        loadUsers();





        return viewGroup;
    }

    private void loadUsers() {

//        reference.child("messageList").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ChatBoxModel model = snapshot.getValue(ChatBoxModel.class);
//                //Log.i("Messages: ", messageModel.toString());
//                chatBoxModel.add(model);
//                messageListAdapter.notifyDataSetChanged();
//                recyclerView.scrollToPosition(chatBoxModel.size()-1);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        reference.child("messageList").child(fuser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                model = snapshot.getValue(ChatBoxModel.class);

                //Log.i("Messages: ", messageModel.toString());
                chatBoxModel.add(model);
                messageListAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatBoxModel.size()-1);
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
    public void OnChatBoxClick(int position) {
        Intent intent = new Intent(MessageFragment.this.getContext(), MessageActivity2.class);
        intent.putExtra("MyModel", model);
        /*intent.putExtra("name", username.get(position));
        intent.putExtra("MyName", username.get(position));
        intent.putExtra("uid", senderUID);  //thismanuıd*/
        startActivity(intent);
    }
}


