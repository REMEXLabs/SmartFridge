package com.example.poiz.fridgetablet.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * class to hold information about a Shoppinglist
 */
public class ShoppingList {

    private int id;
    private String name;
    private Date dateOfCreation;
    private int productCount;
    List<Product> products = new ArrayList<Product>();



    /**
     * returns the ID of the Shoppinglist
     * @return Integer
     */
    public int getId() {
        return id;
    }

    /**
     * sets the ID of the Shoppinglist
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Constructor
     * @param name
     * @param id
     * @param dateOfCreation
     * @param products
     */
    public ShoppingList(String name, int id, Date dateOfCreation, List<Product> products) {
        this.name = name;
        this.dateOfCreation = dateOfCreation;
        this.products = products;
        this.id = id;
        productCount = products.size();
    }

    /**
     * Constructor
     * @param id
     * @param name
     * @param dateOfCreation
     */
    public ShoppingList(int id, String name, Date dateOfCreation) {
        this.name = name;
        this.dateOfCreation = dateOfCreation;
        this.id = id;

    }

    /**
     * sets the amount of Products wich are Stored in the SHoppinglist
     * @param productCount
     */
    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    /**
     * returns the Amount of products wich are Stored in the SHoppinglist
     * @return integer
     */
    public int getProductCount() {
        return productCount;
    }

    /**
     * returns the Name of the Shoppinglist
     * @return String
     */
    public String getName() {

        return name;
    }


    /**
     * Sets the Name of the Shoppinglist
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the Date when the Shoppinglist was Created
     * @return Date
     */
    public Date getDateOfCreation() {
        return dateOfCreation;
    }


    /**
     * Sets the Date when the Shoppinglist was created
     * @param dateOfCreation
     */
    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    /**
     * returns a List of all Products wich are stored in the Shoppinglist
     * @return
     */
    public List<Product> getProducts() {
        productCount = products.size();
        return products;
    }

    /**
     *Sets a List of all Products wich are stored in the Shoppinglist
     * @param products
     */
    public void setProducts(List<Product> products) {
        this.products = products;
        productCount = products.size();

    }

}