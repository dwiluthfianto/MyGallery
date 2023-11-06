package edu.ftiuksw.mygallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.io.File;

public class GalleryPreview extends AppCompatActivity {
    ImageView galleryPreview;
    String path;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_preview);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        galleryPreview = findViewById(R.id.GalleryPreviewTag);
        Glide.with(GalleryPreview.this).load(new File(path)).into(galleryPreview);
    }
}
