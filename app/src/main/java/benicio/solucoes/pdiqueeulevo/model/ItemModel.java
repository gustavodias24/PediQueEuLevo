package benicio.solucoes.pdiqueeulevo.model;

public class ItemModel {
        String title;
        String description;
        String picture_url;
        float quantity;
        String currency_id = "BRL";
        double unit_price;

    public ItemModel(String title, String description, String picture_url, float quantity, double unit_price) {
        this.title = title;
        this.description = description;
        this.picture_url = picture_url;
        this.quantity = quantity;
        this.unit_price = unit_price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }
}
