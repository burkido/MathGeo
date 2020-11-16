package com.example.mathgeo.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathgeo.ChatBoxModel;
import com.example.mathgeo.MessageActivity;
import com.example.mathgeo.R;
import com.example.mathgeo.fragments.MessageFragment;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class FeedRecycleAdapter extends RecyclerView.Adapter<FeedRecycleAdapter.PostHolder> {

    private ArrayList<String> userNameList;
    private ArrayList<String> userCommentList;
    private ArrayList<String> userQuestionList;
    private ArrayList<String> userProfilePhotoList;
    private ArrayList<String> userUIDList;
    private ArrayList<String> userRepliedList;
    private OnImageListener onImageListener;



    public FeedRecycleAdapter(ArrayList<String> userNameList, ArrayList<String> userCommentList, ArrayList<String> userQuestionList, ArrayList<String> userProfilePhotoList,
                              ArrayList<String> userUIDList,OnImageListener onImageListener, ArrayList<String> userRepliedList){
        this.userNameList = userNameList;
        this.userCommentList = userCommentList;
        this.userQuestionList = userQuestionList;
        this.userProfilePhotoList = userProfilePhotoList;
        this.userUIDList = userUIDList;
        this.onImageListener = onImageListener;
        this.userRepliedList = userRepliedList;

    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.recycler_row, parent,false);

        return new PostHolder(view, onImageListener);
    }


    // pozisyondaki viewı oluşturur.
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, final int position) {

        holder.commentText.setText(userCommentList.get(position));
        holder.userName.setText(userNameList.get(position));





        holder.ifAnswer.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),MessageActivity.class);
                intent.putExtra("name", userNameList.get(position));
                intent.putExtra("uid", userUIDList.get(position));
                v.getContext().startActivity(intent);
            }
        });

        //Picasso.get().load(userProfilePhotoList.get(position)).into(holder.circleImageView);
        Picasso.get().load(userQuestionList.get(position)).fit().centerCrop().into(holder.question);

        if(userRepliedList.get(position).equals("0")){
            holder.switchCompat.setVisibility(View.VISIBLE);
        }else {
            holder.switchCompat.setVisibility(View.INVISIBLE);
        }

        holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    changeToAnswered();
                    holder.ifAnswer.setText("Cevaplandı");
                }else{
                    holder.ifAnswer.setText("Cevap Bekliyor");
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return userQuestionList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView question;

        ImageView circleImageView;

        TextView userName, commentText, ifAnswer, uid;

        SwitchCompat switchCompat;

        RelativeLayout relativeLayout;

        OnImageListener onImageListener;


        public PostHolder(@NonNull View itemView, OnImageListener onImageListener) {
            super(itemView);

            //uid = itemView.findViewById(R.id.commentText);
            question = itemView.findViewById(R.id.question);
            circleImageView = itemView.findViewById(R.id.profile_image);
            //circleImageView.setImageResource(R.drawable.ic_person);
            userName = itemView.findViewById(R.id.profile_name);
            commentText = itemView.findViewById(R.id.commentText);
            ifAnswer = itemView.findViewById(R.id.answered);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            switchCompat = itemView.findViewById(R.id.switch1);
            this.onImageListener = onImageListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onImageListener.OnImageListener(getAdapterPosition());
        }


    }
    public interface OnImageListener{

        void OnImageListener(int position);
    }


    private void changeToAnswered() {












    }
}
