package com.example.triptrack;

/**
 * Esta clase representa un documento que se puede cargar en Firebase Storage.
 * Un documento tiene un nombre y una ruta.
 */
public class Expense {

        String uid;
        double amount;
        String concept;

    /**
     * Método constructor de la clase.
     * Crea un gasto.
     * @param uid ID del gasto
     * @param amount cantidad del gasto
     * @param concept concepto del gasto
     */
        public Expense(String uid, double amount, String concept) {
            this.amount = amount;
            this.concept = concept;
            this.uid = uid;
        }

    /**
     * Método para obtener la cantidad del gasto.
     * @return cantidad del gasto
     */
    public double getAmount() {
            return amount;
        }

    /**
     *  Método para obtener el ID del gasto.
     * @return ID del gasto
     */
    public String getUid() {
            return uid;
        }

    /**
     * Método para obtener el concepto del gasto.
     * @return concepto del gasto
     */
    public String getConcept() {
            return concept;
        }

    }

