import org.apache.ibatis.session.*;

import java.io.*;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class Dao {
    SqlSessionFactory factory;

    public Dao() {
        File f = new File("C:\\Users\\TD\\MyBatis\\config.xml");
        try (Reader reader = new FileReader(f)) {
            factory = new SqlSessionFactoryBuilder().build(reader);
            factory.getConfiguration().addMapper(Mapper.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Product> getAllProducts() {
        try (SqlSession session = factory.openSession()) {
            Mapper productMapper = session.getMapper(Mapper.class);
            return productMapper.getAllProducts();
        }
    }

    public int insertProduct(Product product) {
        try (SqlSession session = factory.openSession()) {
            Mapper productMapper = session.getMapper(Mapper.class);
            int res = productMapper.insertProduct(product);
            session.commit();
            return res;
        }
    }

    public int updateProduct(Product product, String id) {
        try (SqlSession session = factory.openSession()) {
            Mapper productMapper = session.getMapper(Mapper.class);
            int res = productMapper.updateProduct(product, id);
            session.commit();
            return res;
        }
    }

    public int updateAvailability(String id, int availability) {
        try (SqlSession session = factory.openSession()) {
            Mapper productMapper = session.getMapper(Mapper.class);
            int res = productMapper.updateAvailability(id, availability);
            session.commit();
            return res;
        }
    }
}
