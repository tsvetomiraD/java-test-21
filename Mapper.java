import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Set;

public interface Mapper {
    @Select("SELECT * FROM products")
    public Set<Product> getAllProducts();

    @Insert("INSERT INTO products(name,price,id,description,photo,date) VALUES (#{name},#{price},#{id},#{description},#{photo},#{date})")
    public int insertProduct(Product product);

    @Update("UPDATE products SET name=#{name},price=#{price},description=#{description},photo=#{photo},date=#{date} WHERE id = #{id}")
    int updateProduct(Product post, String SIteID);

    @Update("UPDATE products SET availability=#{availability} WHERE id = #{id}")
    int updateAvailability(String SiteID, int availability);
}
