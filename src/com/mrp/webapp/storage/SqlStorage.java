package com.mrp.webapp.storage;

import com.mrp.webapp.exception.NotExistStorageException;
import com.mrp.webapp.model.Product;
import com.mrp.webapp.sql.ConnectionFactory;
import com.mrp.webapp.sql.SqlHelper;

import java.sql.*;
import java.util.*;

public class SqlStorage implements Storage {
    public final ConnectionFactory connectionFactory;
    private final SqlHelper sqlHelper;

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        connectionFactory = () -> DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        sqlHelper = new SqlHelper(connectionFactory);
    }

    @Override
    public void clear() {
        sqlHelper.execute("DELETE FROM Product ", PreparedStatement::execute);
    }

    @Override
    public void update(Product Product) {
        sqlHelper.transactionalExecute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE Product SET full_name = ? WHERE uuid = ?")) {
                ps.setString(1, Product.getFullName());
                ps.setString(2, Product.getUuid());
                if (ps.executeUpdate() == 0) {
                    throw new NotExistStorageException(Product.getUuid());
                }
            }
            PreparedStatement ps = conn.prepareStatement("INSERT INTO materials (uuid, name, quantity) VALUES (?,?,?)");
            deleteContacts(Product.getUuid(), conn);
            saveContacts(Product, conn);
            return null;
        });

    }

    @Override
    public void save(Product Product) {
        sqlHelper.transactionalExecute(conn -> {
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Product (uuid, full_name) VALUES (?,?)")) {
                        ps.setString(1, Product.getUuid());
                        ps.setString(2, Product.getFullName());
                        ps.execute();
                    }
                    saveContacts(Product, conn);
                    return null;
                }
        );
    }


    @Override
    public Product get(String uuid) {
        return sqlHelper.execute("    SELECT * FROM Product r " +
                        "    LEFT JOIN contact c " +
                        "    ON r.uuid = c.Product_uuid " +
                        "    WHERE r.uuid =? ",
                ps -> {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new NotExistStorageException(uuid);
                    }
                    Product Product = new Product(uuid, rs.getString("full_name"));
                    do {
                        String value = rs.getString("value");
                        String name = rs.getString("name");
                        if (value != null) {
                            Product.addMaterial(name, Integer.parseInt(value));
                        }
                    } while (rs.next());
                    return Product;
                });
    }

    @Override
    public void delete(String uuid) {
        sqlHelper.execute("DELETE FROM Product r WHERE r.uuid = ?", ps -> {
                    ps.setString(1, uuid);
                    if (ps.executeUpdate() == 0) {
                        throw new NotExistStorageException(uuid);
                    }
                    ps.execute();
                    return null;
                }
        );
    }

    @Override
    public List<Product> getAllSorted() {
        return sqlHelper.execute("SELECT * FROM Product r " +
                "LEFT JOIN contact c " +
                "ON uuid = Product_uuid " +
                "ORDER BY full_name ", ps -> {
            ResultSet rs = ps.executeQuery();
            Map<String, Product> Products = new LinkedHashMap<>();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                if (!Products.containsKey(uuid)) {
                    Products.put(uuid, new Product(uuid, rs.getString("full_name")));
                }
                Products.get(uuid).addMaterial(rs.getString("name"), Integer.parseInt(rs.getString("value")));
            }
            return new ArrayList<>(Products.values());
        });
    }

    @Override
    public int size() {
        return sqlHelper.execute("SELECT COUNT(*) AS count FROM Product", ps -> {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        });
    }

    private void saveContacts(Product Product, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO materials (uuid,name, quantity) " +
                "VALUES (?,?,?)")) {



            

            ps.executeBatch();
        }

    }

    private void deleteContacts(String uuid, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM materials " + "WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.execute();
        }
    }
}
