package com.example.mathgeo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mathgeo.ChatBoxModel;
import com.example.mathgeo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.PostHolder> {

    ArrayList<ChatBoxModel> chatListModel;
   private OnChatBoxListener onChatBoxListener1;


    public MessageListAdapter(ArrayList<ChatBoxModel> chatListModelArrayList, OnChatBoxListener onChatListener1) {
        this.chatListModel = chatListModelArrayList;
        this.onChatBoxListener1 = onChatListener1;

    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.chat_box_recycler_row, parent, false);

        return new PostHolder(view, onChatBoxListener1);

    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        ChatBoxModel model = chatListModel.get(position);

        holder.username.setText(chatListModel.get(position).getUsername());
        Picasso.get().load(model.getReceiverID()).placeholder(R.drawable.ic_person).into(holder.imageView);

    }


    @Override
    public int getItemCount() { return chatListModel.size(); }


    public class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView username;
        CircleImageView imageView;
        OnChatBoxListener onChatBoxListener;

        public PostHolder(@NonNull View itemView, OnChatBoxListener onChatListener) {
            super(itemView);

            username = itemView.findViewById(R.id.chat_box_username);
            imageView = itemView.findViewById(R.id.chat_box_profile_photo);
            this.onChatBoxListener = onChatListener;

            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {
            onChatBoxListener.OnChatBoxClick(getAdapterPosition());
        }
    }

    public interface OnChatBoxListener{
        void OnChatBoxClick(int position);
    }
}
