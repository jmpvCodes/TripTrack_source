package com.example.triptrack;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * Esta clase se utiliza para crear un círculo personalizado que se puede utilizar como fondo para un texto.
 * Se utiliza en la clase CustomLineBackgroundSpan para crear un círculo que se puede utilizar como fondo para un texto.
 * El círculo se puede personalizar con un radio, un color y un desplazamiento en el eje X y el eje Y.
 */
public class CustomCircleSpan implements LineBackgroundSpan {
    private final float radius; //radio del círculo
    private final int color; //color del círculo
    private final float offsetX; //desplazamiento en el eje X
    private final float offsetY; //desplazamiento en el eje Y

    /**
     * Constructor de la clase CustomCircleSpan.
     *
     * @param radius  radio del círculo
     * @param color   color del círculo
     * @param offsetX desplazamiento en el eje X
     * @param offsetY desplazamiento en el eje Y
     */
    public CustomCircleSpan(float radius, int color, float offsetX, float offsetY) {
        this.radius = radius;
        this.color = color;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Método que se llama para dibujar el fondo del texto.
     *
     * @param canvas   el canvas en el que se dibuja el fondo del texto
     * @param paint    el pincel utilizado para dibujar el fondo del texto
     * @param left     la posición del borde izquierdo del fondo del texto
     * @param right    la posición del borde derecho del fondo del texto
     * @param top      la posición del borde superior del fondo del texto
     * @param baseline la posición de la línea base del fondo del texto
     * @param bottom   la posición del borde inferior del fondo del texto
     * @param text     el texto que se está dibujando
     * @param start    el índice del primer carácter del texto que se está dibujando
     * @param end      el índice del último carácter del texto que se está dibujando
     * @param lnum     el número de línea en el que se está dibujando el texto
     */
    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom,
                               CharSequence text, int start, int end, int lnum) {
        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }
        canvas.drawCircle((float) (left + right) / 2 + offsetX, bottom - radius + offsetY, radius, paint); //dibujar el círculo
        paint.setColor(oldColor);
    }
}


