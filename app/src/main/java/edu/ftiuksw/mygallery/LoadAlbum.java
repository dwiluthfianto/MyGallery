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

public class LoadAlbum extends AsyncTask<String, Void, String> {

    private final MainActivity mainActivity;
    private final ArrayList<HashMap<String, String>> albumList;

    public LoadAlbum(MainActivity mainActivity, ArrayList<HashMap<String, String>> album) {
        this.mainActivity = mainActivity;
        this.albumList = album;
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
        String selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + " OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "='application/pdf')";
        String sortOrder = null;
        Cursor cursorFilesExternal = mainActivity.getContentResolver().query(uriFilesExternal, projection, selection, null, sortOrder);
        Cursor cursorFilesInternal = mainActivity.getContentResolver().query(uriFilesInternal, projection, selection, null, sortOrder);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorFilesExternal, cursorFilesInternal});
        String currentBucketID = "";

        while (cursor.moveToNext()) {
            if (albumList.size() < 10) {
                String bucket_id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                String time = Function.convertToTime(timestamp);
                String count = Function.getCounts(mainActivity.getApplicationContext(), album);
                String mediaType = getMediaTypeFromCursor(cursor);

                if (!currentBucketID.equals(bucket_id)) {
                    // Periksa apakah album dengan nama yang sama sudah ada dalam albumList
                    boolean isAlbumExists = false;
                    for (HashMap<String, String> existingAlbum : albumList) {
                        if (album.equals(existingAlbum.get(Function.KEY_ALBUM))) {
                            isAlbumExists = true;
                            break;
                        }
                    }

                    if (!isAlbumExists) {
                        albumList.add(Function.mappingInbox(album, path, timestamp, time, count, mediaType));
                    }
                    currentBucketID = bucket_id;
                }
            }
        }
        cursor.close();

        Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "desc"));
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        AlbumAdapter adapter = new AlbumAdapter(mainActivity, albumList);
        mainActivity.setAdapter(adapter);
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