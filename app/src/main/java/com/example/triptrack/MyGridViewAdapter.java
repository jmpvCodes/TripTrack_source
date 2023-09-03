package com.example.triptrack;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<Bitmap> imageList;

    private List<String> fileNames;


    public MyGridViewAdapter(Context context, List<Bitmap> imageList) {
        this.context = context;
        this.imageList = imageList != null ? imageList : new ArrayList<Bitmap>();
        this.fileNames = new ArrayList<>();

    }
    public void add(Bitmap image, String fileName) {
        imageList.add(image);
        fileNames.add(fileName);
    }

    public void remove(int position) {
        imageList.remove(position);
        fileNames.remove(position);
    }

    public String getFileName(int position) {
        return fileNames.get(position);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }


    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250)); // Aumentar el ancho y el alto para hacer que las imágenes se vean más grandes
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(imageList.get(position));
        return imageView;
    }


    public void clear() {
        imageList.clear();
        notifyDataSetChanged();
    }
}

