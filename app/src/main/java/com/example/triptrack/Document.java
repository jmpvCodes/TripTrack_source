package com.example.triptrack;

/**
 * Esta clase representa un documento que se puede cargar en Firebase Storage.
 * Un documento tiene un nombre y una ruta.
 */
public class Document {
    private String name; //nombre del documento
    private String path; //ruta del documento

    /**
     * Constructor de la clase Document.
     *
     * @param name nombre del documento
     * @param path ruta del documento
     */
    public Document(String name, String path) {
        this.name = name;
        this.path = path;
    }

    /**
     * Método que devuelve el nombre del documento.
     *
     * @return el nombre del documento
     */
    public String getName() {
        return name;
    }

    /**
     * Método que establece el nombre del documento.
     *
     * @param name el nombre del documento
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método que devuelve la ruta del documento.
     *
     * @return la ruta del documento
     */
    public String getPath() {
        return path;
    }

    /**
     * Método que establece la ruta del documento.
     *
     * @param path la ruta del documento
     */
    public void setPath(String path) {
        this.path = path;
    }
}

