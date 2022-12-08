import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class Main {

    static Set<Product> currentlyChecked = new HashSet<>();
    static Dao dao = new Dao();

    public static void main(String[] args) throws IOException {
        String url = args[0];
        checkLink(url);
        checkForAvailability();
    }

    private static void checkForAvailability() {
        Set<Product> allProducts = dao.getAllProducts();
        for (Product p : allProducts) {
            if (!currentlyChecked.contains(p)) {
                String id = p.siteID;
                dao.updateAvailability(id, 0);
                System.out.println("Product " + p.name + " is unavailable");
            }
        }
    }

    public static void checkLink(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        Elements el = doc.select("div [class=\"products list-product-archive products-grid row clearfix\"]");
        Element productsClass = el.get(0);
        Elements products = productsClass.select("div [class=\"product-block grid\"]");

        for (Element product : products) {
            String id = product.attr("data-product-id");
            Elements a = product.select("a");
            String href = a.attr("href");
            getProductInfo(href, id);
        }
    }

    private static void getProductInfo(String href, String id) throws IOException {
        Document doc = Jsoup.connect(href).get();
        Element information = doc.select("div [class=\"information\"]").get(0);

        String photo = getPhoto(doc);
        String name = getName(information);
        String price = getPrice(information);
        String description = getDescription(doc);
        Date date = new Date(System.currentTimeMillis());

        Product product = new Product(name, price, id, description, photo, date);
        currentlyChecked.add(product);

        addOrUpdateProduct(id, name, price, product);
    }

    private static void addOrUpdateProduct(String id, String name, String price, Product product) {
        Product pr = dao.getProductBySiteId(id);

        if (pr == null) {
            dao.insertProduct(product);
            System.out.println(name + "  " + price);
            return;
        }
        String oldPrice = dao.getProductPrice(id);

        if (!price.equals(oldPrice)) {
            System.out.println(name + " old price: " + oldPrice + ", new price: " + price);
        }
        dao.updateProduct(product, id);
    }

    private static String getDescription(Document doc) {
        Element descEl = doc.select("div [class=\"woocommerce-product-details__short-description\"]").get(0);
        StringBuilder description = new StringBuilder();
        Elements d = descEl.select("p");
        for (Element e : d) {
            description.append(e.text());
        }
        String desc = "Put this desc";
        return desc;
    }

    private static String getPrice(Element information) {
        Element elementPrice = information.select("bdi").get(0);
        String price = elementPrice.text();
        price = price.substring(0, 6);
        return price;
    }

    private static String getPhoto(Document doc) {
        Element photoEL = doc.select("div [class=\"apus-woocommerce-product-gallery-wrapper thumbnails-bottom \"]").get(0);
        Elements a = photoEL.select("a");
        Elements img = a.select("img");
        String photo = img.attr("src");
        return photo;
    }

    private static String getName(Element information) {
        Element elementName = information.select("h1").get(0);
        String name = elementName.text();
        return "Name";
    }
}
