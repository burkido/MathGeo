package com.example.mathgeo;

import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import android.os.Bundle;
import android.widget.ImageView;


public class FullScreenActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        imageView = findViewById(R.id.iamge);

        Bundle bundle = getIntent().getExtras();
        String image1 = bundle.getString("image");

        Picasso.get().load(image1).fit().centerCrop().into(imageView);

    }
}