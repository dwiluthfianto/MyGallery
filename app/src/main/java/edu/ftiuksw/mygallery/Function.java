package edu.ftiuksw.mygallery;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Function {
    static final String KEY_ALBUM = "album_name";
    static final String KEY_PATH = "path";
    static final String KEY_TIMESTAMP = "timestamp";

    static final String KEY_TIME = "time";
    static final String KEY_COUNT = "count";
    static final String KEY_MEDIA_TYPE = "type_media";

    public static  boolean hasPermissions(Context context, String... permissions) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (context != null) && (permissions != null)) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
             return Environment.isExternalStorageManager();
        }
        return true;
    }

    public static HashMap<String, String> mappingInbox(String album, String path, String timestamp, String time, String count, String mediaType) {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_ALBUM, album);
        map.put(KEY_PATH, path);
        map.put(KEY_TIMESTAMP, timestamp);
        map.put(KEY_TIME, time);
        map.put(KEY_COUNT, count);
        map.put(KEY_MEDIA_TYPE, mediaType);
        return map;
    }

    public static String convertToTime(String timestamp){
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
        return  dateFormat.format(date);
    }

    public static String getCounts(Context context, String albumName){
        Uri uriFilesExternal;
        Uri uriFilesInternal;
        uriFilesExternal = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        uriFilesInternal = MediaStore.Files.getContentUri(MediaStore.VOLUME_INTERNAL);

        String[] projection = { MediaStore.Files.FileColumns.DATA };
        String selection = "bucket_display_name=?";
        String sortOrder = null;
        Cursor cursorFilesExternal = context.getContentResolver().query(uriFilesExternal, projection, selection, new String[] {albumName}, sortOrder);
        Cursor cursorFilesInternal = context.getContentResolver().query(uriFilesInternal, projection, selection, new String[] {albumName}, sortOrder);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorFilesExternal, cursorFilesInternal});
        return  cursor.getCount() + " Albums";
    }
}
