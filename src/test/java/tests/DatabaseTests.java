package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import utils.DBUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTests extends BaseTest {
    @Test
    public void testCreateAndRead() throws SQLException {
        String createQuery = "INSERT INTO users (id, name, email) VALUES (1, 'Okuhle Mbewu', 'om@gmail.com');";
        DBUtils.create(createQuery);

        String readQuery = "SELECT * FROM users WHERE id = 1;";
        ResultSet rs = DBUtils.read(readQuery);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("name"), "Okuhle Mbewu");
    }

    @Test
    public void testUpdate() throws SQLException {
        String updateQuery = "UPDATE users SET email = 'om@gmail.com' WHERE id = 1;";
        DBUtils.update(updateQuery);

        String readQuery = "SELECT * FROM users WHERE id = 1;";
        ResultSet rs = DBUtils.read(readQuery);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("email"), "om@gmail.com");
    }

    @Test
    public void testDelete() throws SQLException {
        String deleteQuery = "DELETE FROM users WHERE id = 1;";
        DBUtils.delete(deleteQuery);

        String readQuery = "SELECT * FROM users WHERE id = 1;";
        ResultSet rs = DBUtils.read(readQuery);
        Assert.assertFalse(rs.next());
    }
}
