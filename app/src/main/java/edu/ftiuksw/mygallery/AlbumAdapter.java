package edu.ftiuksw.mygallery;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AlbumAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public AlbumAdapter(Activity a, ArrayList< HashMap <String, String>> d) {
        activity = a;
        data = d;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder holder = null;

        if(convertView == null) {
            holder = new AlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.album_row, parent, false);

            holder.galleryImage = convertView.findViewById(R.id.galleryImage);
            holder.gallery_count = convertView.findViewById(R.id.gallery_count);
            holder.gallery_title = convertView.findViewById(R.id.gallery_title);
            holder.videoIcon = convertView.findViewById(R.id.videoIcon);

            convertView.setTag(holder);
        }else{
            holder = (AlbumViewHolder) convertView.getTag();
        }

        holder.galleryImage.setId(position);
        holder.gallery_count.setId(position);
        holder.gallery_title.setId(position);
        holder.videoIcon.setId(position);

        HashMap<String, String> image;
        image = data.get(position);
        try{
            holder.gallery_count.setText(image.get(Function.KEY_COUNT));
            holder.gallery_title.setText(image.get(Function.KEY_ALBUM));
            String mediaType = image.get(Function.KEY_MEDIA_TYPE);
            if ("image".equals(mediaType)) {
                // Tampilkan ikon gambar
                Glide.with(activity)
                        .load(new File(image.get(Function.KEY_PATH)))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.galleryImage);
            } else if ("video".equals(mediaType)) {
                Glide.with(activity)
                        .load(new File(image.get(Function.KEY_PATH)))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.galleryImage);
                holder.videoIcon.setImageResource(R.drawable.ic_video_icon);
            } else if ("audio".equals(mediaType)) {
                holder.galleryImage.setImageResource(R.drawable.ic_audio_icon);
            }else if ("pdf".equals(mediaType)) {
                holder.galleryImage.setImageResource(R.drawable.ic_pdf_icon);
            }else{
                holder.galleryImage.setImageResource(R.drawable.ic_file_icon);
            }

        }catch (Exception x) {
            x.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

class AlbumViewHolder{
    ImageView galleryImage;
    TextView gallery_count, gallery_title;
    ImageView videoIcon;
}
