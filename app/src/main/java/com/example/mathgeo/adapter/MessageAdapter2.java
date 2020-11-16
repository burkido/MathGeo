package com.example.mathgeo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathgeo.ChatBoxModel;
import com.example.mathgeo.MessageModel;
import com.example.mathgeo.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class MessageAdapter2 extends RecyclerView.Adapter<MessageAdapter2.PostHolder> {

    Activity activity;

    String username;

    Context context;

    ArrayList<MessageModel> messageModelList;

    Boolean state;

    ArrayList<ChatBoxModel> chatBoxModel;

    int view_send = 1, view_receive = 2;

    public MessageAdapter2(Context context, ArrayList<MessageModel> messageModelList, Activity activity, ArrayList<ChatBoxModel> chatBoxModel) {
        this.context = context;
        this.messageModelList = messageModelList;
        this.activity = activity;
        this.chatBoxModel = chatBoxModel;
        state = false;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType == view_send) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.send_layout, parent, false);
            return new PostHolder(view);
        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.receive_layout, parent, false);
            return new PostHolder(view);
        }
    }

    // pozisyondaki viewı oluşturur.
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, final int position) {

        MessageModel model = messageModelList.get(position);
        String message_type = model.getType();

        if(message_type.equals("text")){
            //holder.showMessage.setText(messageModelList.get(position).getText());
            holder.imageView.setVisibility(View.INVISIBLE);
        }else{
            holder.showMessage.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(model.getText()).placeholder(R.drawable.ic_person).into(holder.imageView);
        }
    }


    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        TextView showMessage;
        ImageView imageView;


        public PostHolder(@NonNull View itemView) {
            super(itemView);

            if(state){
                showMessage = itemView.findViewById(R.id.send_text);
                imageView = itemView.findViewById(R.id.send_image);
                //imageView.setVisibility(View.INVISIBLE);
            }else{
                showMessage = itemView.findViewById(R.id.receive_text);
                imageView = itemView.findViewById(R.id.send_image);

            }
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (messageModelList.get(position).getFrom().equals(chatBoxModel.get(position).getUsername())) {
            state = true;
            return view_send;
        }else{
            state = false;
            return view_receive;
        }
    }
}
