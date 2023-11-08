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


/**
 * Esta clase es un adaptador que se utiliza para mostrar una cuadrícula de imágenes.
 * El adaptador se utiliza en la actividad AlbumPhotoActivity para mostrar una cuadrícula de imágenes.
 */
public class MyGridViewAdapter extends BaseAdapter {
    private final Context context;
    private final List<Bitmap> imageList;

    private final List<String> fileNames;


    /**
     * Constructor de la clase MyGridViewAdapter.
     * @param context el contexto en el que se utiliza el adaptador
     * @param imageList la lista de imágenes que se mostrarán
     */
    public MyGridViewAdapter(Context context, List<Bitmap> imageList) {
        this.context = context;
        this.imageList = imageList != null ? imageList : new ArrayList<>();
        this.fileNames = new ArrayList<>();

    }

    /**
     * Añade una imagen a la lista de imágenes.
     * @param image la imagen que se va a añadir
     * @param fileName nombre del archivo de la imagen
     */
    public void add(Bitmap image, String fileName) {
        imageList.add(image);
        fileNames.add(fileName);
    }

    /**
     * Elimina una imagen de la lista de imágenes.
     * @param position la posición de la imagen que se va a eliminar
     */
    public void remove(int position) {
        imageList.remove(position);
        fileNames.remove(position);
    }

    /**
     * Devuelve el nombre del archivo de la imagen en la posición especificada.
     * @param position  la posición de la imagen
     * @return el nombre del archivo de la imagen en la posición especificada
     */
    public String getFileName(int position) {
        return fileNames.get(position);
    }

    /**
     * Devuelve la longitud de la lista de imágenes.
     * @return la longitud de la lista de imágenes
     */
    @Override
    public int getCount() {
        return imageList.size();
    }

    /**
     * Método que devuelve la imagen en la posición especificada de la lista.
     * @param i la posición de la imagen que se va a devolver
     * @return la imagen en la posición especificada de la lista
     */
    @Override
    public Object getItem(int i) {
        return null;
    }

    /**
     * Método que devuelve el ID de la imagen en la posición especificada de la lista.
     * @param i la posición de la imagen cuyo ID se va a devolver
     * @return el ID de la imagen en la posición especificada de la lista
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Método que devuelve la vista que se muestra en la posición especificada de la lista.
     * @param position la posición de la vista que se va a devolver
     * @param convertView la vista que se va a convertir
     * @param parent el ViewGroup en el que se va a inflar la vista
     * @return la vista que se muestra en la posición especificada de la lista
     */
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

    /**
     * Elimina todas las imágenes de la lista.
     */
    public void clear() {
        imageList.clear();
        notifyDataSetChanged();
    }

}

