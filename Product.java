import java.sql.Date;

class Product {
    String name;
    String price;
    String id;
    String description;
    String photo;
    Date date;

    Product(){}

    Product(String name, String price, String siteID, String description, String photo, Date date) {
        this.name = name;
        this.price = price;
        this.id = siteID;
        this.description = description;
        this.photo = photo;
        this.date = date;
    }
}
