package com.example.mathgeo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mathgeo.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatBoxAdapter extends RecyclerView.Adapter<ChatBoxAdapter.PostHolder> {


    ArrayList<String> username, user_profile_photo, last_message;
    private OnChatListener onChatListener1;

    public ChatBoxAdapter(ArrayList<String> username,ArrayList<String> user_profile_photo, ArrayList<String> last_message, OnChatListener onChatListener1){
        this.username = username;
        this.user_profile_photo = user_profile_photo;
        this.last_message = last_message;
        //this.onChatListener1 = onChatListener1;
    }



    // inflate the layout for showing the view for each item.
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.chat_box_recycler_row, parent, false);

        return new PostHolder(view, onChatListener1);
    }





    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.last_message.setText(last_message.get(position));
        holder.username.setText(username.get(position));
        Picasso.get().load(user_profile_photo.get(position)).fit().centerCrop().into(holder.profile_photo);

    }


    @Override
    public int getItemCount() { return username.size(); }


    public class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView profile_photo;
        TextView username, last_message;


        OnChatListener onChatListener;


        public PostHolder(@NonNull View itemView, OnChatListener onChatListener) {
            super(itemView);

            profile_photo = itemView.findViewById(R.id.chat_box_profile_photo);
            username = itemView.findViewById(R.id.chat_box_username);
            //last_message = itemView.findViewById(R.id.last_message);
            this.onChatListener = onChatListener;


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onChatListener.OnChatClick(getAdapterPosition());
        }
    }


    public interface OnChatListener{
        void OnChatClick(int position);
    }
}
