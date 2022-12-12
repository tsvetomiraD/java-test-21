
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Enter link to check");
            return;
        }

        String url = args[0];
        PriceGrabber pg = new PriceGrabber(url);
        pg.checkLink();
        pg.checkForAvailability();
    }
}

class PriceGrabber {
    private static final String folderPath = "C:\\Users\\TD\\test21";
    static Set<String> currentlyChecked = new HashSet<>();
    static Set<Product> allDBProducts = new HashSet<>();
    static Dao dao = new Dao();
    String url;

    final static Logger logger = LogManager.getLogger(PriceGrabber.class.getName());

    public PriceGrabber(String url) {
        this.url = url;
        allDBProducts = dao.getAllProducts();
    }


    public void checkForAvailability() {
        for (Product p : allDBProducts) {
            if (!currentlyChecked.contains(p.id)) {
                String id = p.id;
                dao.updateAvailability(id, 0);
                System.out.println("Product " + p.name + " is unavailable");
            }
        }
    }

    public void checkLink() throws IOException {
        Document doc = Jsoup.connect(url).get();
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
        logger.info(photo);
        addAllPhotos(doc);
        String name = getName(information);
        logger.info(name);
        String price = getPrice(information);
        logger.info(price);
        String description = getDescription(doc);
        logger.info(description);
        Date date = new Date(System.currentTimeMillis());
        logger.info(date);

        Product product = new Product(name, price, id, description, photo, date);
        currentlyChecked.add(id);

        addOrUpdateProduct(id, name, price, product);
    }

    private static void addAllPhotos(Document doc) throws IOException {
        Elements allPhotosClass = doc.select("div [class=\"wrapper-thumbs thumbnails-bottom\"]");
        Elements allPhotos = allPhotosClass.select("div [class=\"thumbs-inner\"]");
        for (Element e : allPhotos) {
            Elements img = e.select("img");
            getImage(img.attr("src"));
        }
    }

    private static void addOrUpdateProduct(String id, String name, String price, Product product) {
        Product oldProduct = findProduct(id);

        if (oldProduct == null) {
            dao.insertProduct(product);
            System.out.println(name + "  " + price);
            return;
        }
        String oldPrice = oldProduct.price;

        if (!price.equals(oldPrice)) {
            System.out.println(name + " old price: " + oldPrice + ", new price: " + price);
        }
        //dao.updateProduct(product, id);
    }

    private static Product findProduct(String id) {
        for (Product p : allDBProducts) {
            if (p.id.equals(id)) {
                return p;
            }
        }
        return null;
    }

    private static String getDescription(Document doc) {
        Element descEl = doc.select("div [class=\"woocommerce-product-details__short-description\"]").get(0);
        StringBuilder description = new StringBuilder();
        Elements d = descEl.select("p");
        for (Element e : d) {
            description.append(e.text());
        }
        return "Put this desc";
    }

    private static String getPrice(Element information) {
        Element elementPrice = information.select("bdi").get(0);
        String price = elementPrice.text();
        return price.substring(0, 6);
    }

    private static String getPhoto(Document doc) throws IOException {
        Element photoEL = doc.select("div [class=\"apus-woocommerce-product-gallery-wrapper thumbnails-bottom \"]").get(0);
        Elements a = photoEL.select("a");
        Elements img = a.select("img");
        getImage(img.attr("src"));
        return img.attr("src");
    }

    private static String getName(Element information) {
        Element elementName = information.select("h1").get(0);
        String name = elementName.text();
        return "Name";
    }

    private static void getImage(String src) throws IOException {
        String name = getImageName(src);

        URL url = new URL(src);
        InputStream in = url.openStream();

        OutputStream out = new BufferedOutputStream(new FileOutputStream(folderPath + name));

        for (int b; (b = in.read()) != -1; ) {
            out.write(b);
        }
        out.close();
        in.close();
    }

    private static String getImageName(String src) {
        int indexname = src.lastIndexOf("/");

        if (indexname == src.length()) {
            src = src.substring(1, indexname);
        }

        indexname = src.lastIndexOf("/");
        String name = src.substring(indexname);
        if (name.contains("?")) {
            name = name.substring(0, name.lastIndexOf("?"));
        }

        return name;
    }
}
