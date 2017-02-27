package com.example.poiz.fridgetablet.data;

/**
 * Created by poiz on 26.08.2016.
 */

/**
 * Class to Hold information about a Categorie
 */
public class Categorie {
    String categorieName;
    int categorieID;

    /**
     * Consturctor
     * @param categorieName name of the categorie
     * @param categorieID id of the categorie
     */
    public Categorie(String categorieName, int categorieID) {
        this.categorieName = categorieName;
        this.categorieID = categorieID;
    }

    /**
     * returns the Name of the Categorie
     * @return name of the Categorie
     */
    public String getCategorieName() {
        return categorieName;
    }

    /**
     * Sets the Name of the Categorie
     * @param categorieName  of the Categorie
     */
    public void setCategorieName(String categorieName) {
        this.categorieName = categorieName;
    }
}
