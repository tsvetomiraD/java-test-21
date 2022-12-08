import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Set;

public interface Mapper {
    @Select("SELECT * FROM products WHERE siteID=#{id}")
    public Product getProductBySiteID(String id);

    @Select("SELECT price FROM products WHERE siteID=#{id}")
    public String getProductPrice(String id);

    @Select("SELECT * FROM products")
    public Set<Product> getAllProducts();

    @Insert("INSERT INTO products(name,price,siteID,description,photo,date) VALUES (#{name},#{price},#{siteID},#{description},#{photo},#{date})")
    public int insertProduct(Product product);

    @Update("UPDATE products SET name={name},price={price},siteID={siteID},description={description},photo={photo},date={date} WHERE siteID = #{siteID}")
    int updateProduct(Product post, String SIteID);

    @Update("UPDATE products SET availability={availability} WHERE siteID = #{siteID}")
    int updateAvailability(String SiteID, int availability);
}
