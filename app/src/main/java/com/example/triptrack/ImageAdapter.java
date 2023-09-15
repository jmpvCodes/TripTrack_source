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
import java.util.*;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private File[] files;
    private Set<Integer> selectedPositions = new HashSet<>();
    private boolean selectionMode = false;
    private List<Bitmap> imageList;

    private List<String> fileNames;

    public ImageAdapter(Context context) {
        this.context = context;
        this.imageList = new ArrayList<>();
        this.fileNames = new ArrayList<>();
        this.files = new File[0]; // Inicializar files como una matriz vacía
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
        return files != null ? files.length : 0; // Aquí debes devolver la longitud de 'files', no de 'imageList'
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
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250)); // Aumentar el ancho y el alto para hacer que las imágenes se vean más grandes
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        // Verificar si imageList tiene un elemento en la posición dada antes de intentar acceder a él
        if (position < imageList.size()) {
            imageView.setImageBitmap(imageList.get(position));
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

    public void add(Bitmap image, String fileName) {
        imageList.add(image);
        fileNames.add(fileName);

        // Agregar el archivo a files
        File file = new File(fileName);
        files = Arrays.copyOf(files, files.length + 1);
        files[files.length - 1] = file;
    }

}
