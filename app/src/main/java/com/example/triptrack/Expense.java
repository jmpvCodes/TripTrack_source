package com.example.triptrack;

import com.google.firebase.database.annotations.NotNull;

import java.util.Locale;

/**
 * Esta clase representa un documento que se puede cargar en Firebase Storage.
 * Un documento tiene un nombre y una ruta.
 */
public class Expense {

        String uid;
        double amount;
        String concept;

        public Expense(String uid, double amount, String concept) {
            this.amount = amount;
            this.concept = concept;
            this.uid = uid;
        }

        public double getAmount() {
            return amount;
        }

        public String getUid() {
            return uid;
        }

        public String getConcept() {
            return concept;
        }

    }

