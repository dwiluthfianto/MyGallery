package edu.ftiuksw.mygallery;

import static android.os.Build.VERSION.SDK_INT;

import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LoadInAlbum extends AsyncTask<String, Void, String> {

    private final AlbumActivity albumActivity;
    private final ArrayList<HashMap<String, String>> albumList;
    String albumName;
    public LoadInAlbum(AlbumActivity albumActivity, ArrayList<HashMap<String, String>> album, String albumName) {
        this.albumActivity = albumActivity;
        this.albumList = album;
        this.albumName = albumName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        albumList.clear();
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri uriFilesExternal;
        Uri uriFilesInternal;
        uriFilesExternal = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        uriFilesInternal = MediaStore.Files.getContentUri(MediaStore.VOLUME_INTERNAL);
        String[] projection = { MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.BUCKET_ID };
        String selection = "bucket_display_name=?";
        String sortOrder = null;
        Cursor cursorFilesExternal = albumActivity.getContentResolver().query(uriFilesExternal, projection, selection, new String[] {albumName}, sortOrder);
        Cursor cursorFilesInternal = albumActivity.getContentResolver().query(uriFilesInternal, projection, selection, new String[] {albumName}, sortOrder);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorFilesExternal, cursorFilesInternal});

        while (cursor.moveToNext()) {
            if (albumList.size() < 10) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                String time = Function.convertToTime(timestamp);
                String count = Function.getCounts(albumActivity.getApplicationContext(), album);
                String mediaType = getMediaTypeFromCursor(cursor);
                albumList.add(Function.mappingInbox(album, path, timestamp, time, count, mediaType));
            }
        }
        cursor.close();

        Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "desc"));
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        SingleAlbumAdapter adapter = new SingleAlbumAdapter(albumActivity, albumList);
        albumActivity.setAdapter(adapter);
    }

    private String getMediaTypeFromCursor(Cursor cursor) {
        String mediaType = "unknown";

        String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
        if (data != null) {
            if (data.endsWith(".jpg") || data.endsWith(".jpeg") || data.endsWith(".png") || data.endsWith(".gif")) {
                mediaType = "image";
            } else if (data.endsWith(".mp4") || data.endsWith(".3gp") || data.endsWith(".avi")) {
                mediaType = "video";
            } else if (data.endsWith(".mp3") || data.endsWith(".wav") || data.endsWith(".ogg")) {
                mediaType = "audio";
            }else if (data.endsWith(".pdf")) {
                mediaType = "pdf";
            }
        }

        return mediaType;
    }
}