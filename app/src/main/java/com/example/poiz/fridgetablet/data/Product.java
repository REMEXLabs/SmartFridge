package com.example.poiz.fridgetablet.data;

import com.example.poiz.fridgetablet.util.Util;

import java.util.Comparator;
import java.util.Date;

/**
 * Class to hold informations about a Product
 */
public class Product{
    private int id;
    private String name;
    private int catID;
    private Date storeDate;
    private Date expDate;
    private int daysLeft;
    Util util = new Util();
    private int storeid;
    private int shelflife;

    /**
     * constructor
     * @param id id of the product in the database
     * @param name name of the product
     * @param catID id of the categorie a product belongs to
     * @param shelflife amount of days a product is edible
     *
     */

    public Product(){}

    public Product(int id, String name, int catID, int shelflife) {
        this.id = id;
        this.name = name;
        this.catID = catID;
        this.shelflife = shelflife;

    }
    public Product(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor
     * @param id id of the product in the database
     * @param name name of the product
     * @param catID id of the categorie a product belongs to
     * @param shelflife amount of days a product is edible
     * @param storeDate date a product was stored
     * @param expDate expiredate of a product
     * @param storeid id of a product in the table of all stored products in database
     */
    public Product(int id, String name, int catID, int shelflife, Date storeDate, Date expDate, int storeid) {
        this.id = id;
        this.name = name;
        this.catID = catID;
        this.shelflife = shelflife;
        this.storeDate = storeDate;

        this.expDate = expDate;
        daysLeft = util.getDateDiff(util.getTodaysDate(),expDate);
        this.storeid = storeid;
    }

    /**
     * returns the Expiredate of the Product
     * @return the expiredate of the product
     */
    public Date getExpDate() {
        return expDate;
    }

    /**
     * Sets the Expiredate of the Product
     * @param expDate the expiredate of the product
     */
    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    /**
     * returns the Date a Product was stored
     * @return the Date a Product was stored
     */
    public Date getStoreDate() {
        return storeDate;
    }

    /**
     * sets the Date a Product was stored
     * @param storeDate the Date a Product was stored
     */
    public void setStoreDate(Date storeDate) {
        this.storeDate = storeDate;
    }

    /**
     * returns the StoreID of the Product
     * @return every Product wich is stored has a StoreID
     */
    public int getStoreid() {
        return storeid;
    }

    /**
     * sets the StoreID of the Product
     * @param storeid every Product wich is stored has a StoreID
     */
    public void setStoreid(int storeid) {
        this.storeid = storeid;
    }



    /**
     * returns the Days wich are left for the Product to reach the expiredate
     * @return days left until the expiredate is reached
     */
    public int getDaysLeft() {
        return daysLeft;
    }

    /**
     * returns the ID of the product
     * @return id of the product in databse
     */
    public int getId() {
        return id;
    }

    /**
     * sets the id of the Procduct
     * @param id  id of the product in databse
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * returns the amount of Days a Product is edible after is was stored
     * @return  the amount of Days a Product is edible after is was stored
     */
    public int getShelflife() {
        return shelflife;
    }

    /**
     * sets the amount of Days a Product is edible after is was stored
     * @param shelflife the amount of Days a Product is edible after is was stored
     */
    public void setShelflife(int shelflife) {
        this.shelflife = shelflife;
    }

    /**
     * returs the ID of the Categorie a product is in
     * @return id of a the categorie a product belongs to
     */
    public int getCatID() {
        return catID;
    }

    /**
     * sets the ID of the Categorie a product is in
     * @param catID id of a the categorie a product belongs to
     */
    public void setCatID(int catID) {
        this.catID = catID;
    }

    /**
     * returns the Name of a Product
     * @return name of a prodcut
     */
    public String getName() {
        return name;
    }

    /**
     * sets the Name of a Procut
     * @param name Name of a Procut
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Comparator to sort Objects by StoreDate
     *
     */
   public static class ProductStoreDateComparator implements Comparator<Product> {
        public int compare(Product prod1, Product prod2) {
            return prod1.getStoreDate().compareTo(prod2.getStoreDate());
        }
    }

    /**
     * Comparator to sort Objects by Expiredate
     */
    public static class ProductExpDateComparator implements Comparator<Product> {
        public int compare(Product prod1, Product prod2) {
            return prod1.getExpDate().compareTo(prod2.getExpDate());
        }
    }

    /**
     * Comparator to sort Objects by Name
     */
    public static class ProductNameComparator implements Comparator<Product> {
        public int compare(Product prod1, Product prod2) {
            return prod1.getName().compareTo(prod2.getName());
        }
    }

}
