package edu.ftiuksw.mygallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AlbumActivity extends AppCompatActivity {
    private GridView albumGridView;
    private final ArrayList<HashMap<String, String>> albumList = new ArrayList<>();
    LoadInAlbum loadInAlbumTask;
    private String albumName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumGridView = findViewById(R.id.albumGridView);

        albumName = getIntent().getStringExtra("albumName");
        setTitle(albumName);

        loadInAlbumTask = new LoadInAlbum(this, albumList, albumName);
        loadInAlbumTask.execute();
    }

    public void setAdapter(SingleAlbumAdapter adapter) {
        albumGridView.setAdapter(adapter);
        albumGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Dapatkan path file media dari album yang dipilih
                String mediaFilePath = albumList.get(position).get(Function.KEY_PATH);
                File filePath = new File(albumList.get(position).get(Function.KEY_PATH));
                Uri mediaUri = FileProvider.getUriForFile(AlbumActivity.this, AlbumActivity.this.getApplicationContext().getPackageName() + ".provider", filePath);
                // Tentukan tipe media berdasarkan ekstensi file (misalnya, audio atau video)
                String mediaType = getMediaTypeFromFilePath(mediaFilePath);

                if (mediaType.equals("audio")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mediaUri, "audio/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(intent);
                } else if (mediaType.equals("video")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mediaUri, "video/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(intent);
                } else if (mediaType.equals("image")) {
                    Intent intent = new Intent(AlbumActivity.this, GalleryPreview.class);
                    intent.putExtra("path", mediaFilePath);
                    startActivity(intent);
                } else if (mediaType.equals("pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mediaUri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(intent);
                }
            }
        });
    }
    private String getMediaTypeFromFilePath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "unknown";
        }

        String lowerCasePath = filePath.toLowerCase();

        if (lowerCasePath.endsWith(".mp3") || lowerCasePath.endsWith(".wav") || lowerCasePath.endsWith(".ogg")) {
            return "audio";
        } else if (lowerCasePath.endsWith(".mp4") || lowerCasePath.endsWith(".3gp") || lowerCasePath.endsWith(".avi")|| lowerCasePath.endsWith(".mkv")) {
            return "video";
        } else if (lowerCasePath.endsWith(".jpg")|| lowerCasePath.endsWith(".jpeg") || lowerCasePath.endsWith(".png")){
            return "image";
        }else if (lowerCasePath.endsWith(".pdf")){
            return "pdf";
        } else{
            return "Unknown";
        }
    }
}