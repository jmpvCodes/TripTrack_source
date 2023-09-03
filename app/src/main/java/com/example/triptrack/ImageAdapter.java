package com.example.triptrack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private File[] files;
    private Set<Integer> selectedPositions = new HashSet<>();
    private boolean selectionMode = false;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public void setFiles(File[] files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        if (!selectionMode) {
            selectedPositions.clear();
        }
        notifyDataSetChanged();
    }

    public Set<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    @Override
    public int getCount() {
        return files != null ? files.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return files != null ? files[position] : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        // Cambiar el fondo de las vistas de las fotos seleccionadas
        if (selectedPositions.contains(position)) {
            imageView.setBackgroundColor(Color.LTGRAY);
        } else {
            imageView.setBackgroundColor(Color.TRANSPARENT);
        }

        File file = files[position];
        if (file.getName().endsWith(".mp4")) {
            // Mostrar un video
            Bitmap thumbnail = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                try {
                    thumbnail = ThumbnailUtils.createVideoThumbnail(file, new Size(640, 480), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                thumbnail = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
            }
            imageView.setImageBitmap(thumbnail);
        } else {
            // Mostrar una imagen
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }

        return imageView;
    }
}