import java.sql.Date;

class Product {
    String name;
    String price;
    String siteID;
    String description;
    String photo;
    Date date;

    Product(String name, String price, String siteID, String description, String photo, Date date) {
        this.name = name;
        this.price = price;
        this.siteID = siteID;
        this.description = description;
        this.photo = photo;
        this.date = date;
    }
}
