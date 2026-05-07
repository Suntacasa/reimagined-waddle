package repository;

import domain.Angajat;
import utils.JdbcUtils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepoAngajat extends AbstractRepository<Integer, Angajat> implements IRepoAngajat<Integer, Angajat>{

    private static final Logger logger = Logger.getLogger(RepoAngajat.class.getName());

    public RepoAngajat(Properties props) {
        logger.info("Initializing RepoAngajat");
        this.dbUtils = new JdbcUtils(props);
        initTable();
    }

    private void initTable() {
        logger.info("Initializing Angajat table");
        String sql = """
                CREATE TABLE IF NOT EXISTS Angajat (
                    angajatID INTEGER PRIMARY KEY AUTOINCREMENT,
                    username  TEXT NOT NULL UNIQUE,
                    password  TEXT NOT NULL
                )""";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute(sql);
            logger.info("Angajat table ready");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to create Angajat table", e);
        }
    }

    @Override
    public void save(Angajat angajat) {
        logger.info("Saving: " + angajat);
        String sql = "INSERT INTO Angajat(username, password) VALUES (?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, angajat.getUsername());
            ps.setString(2, angajat.getPassword());
            ps.executeUpdate();
            logger.info("Saved successfully: " + angajat);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving: " + angajat, e);
        }
    }

    @Override
    public void delete(Integer id) {
        logger.info("Deleting Angajat with id: " + id);
        String sql = "DELETE FROM Angajat WHERE angajatID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) logger.warning("No Angajat found with id: " + id);
            else logger.info("Deleted Angajat with id: " + id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting Angajat with id: " + id, e);
        }
    }

    @Override
    public void update(Integer id, Angajat angajat) {
        logger.info("Updating Angajat id: " + id + " with: " + angajat);
        String sql = "UPDATE Angajat SET username = ?, password = ? WHERE angajatID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, angajat.getUsername());
            ps.setString(2, angajat.getPassword());
            ps.setInt(3, id);
            int affected = ps.executeUpdate();
            if (affected == 0) logger.warning("No Angajat found with id: " + id);
            else logger.info("Updated Angajat id: " + id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating Angajat id: " + id, e);
        }
    }

    @Override
    public Angajat findById(Integer id) {
        logger.info("Finding Angajat by id: " + id);
        String sql = "SELECT * FROM Angajat WHERE angajatID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Angajat a = new Angajat(
                        rs.getInt("angajatID"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                logger.info("Found: " + a);
                return a;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding Angajat by id: " + id, e);
        }
        logger.warning("No Angajat found with id: " + id);
        return null;
    }

    @Override
    public List<Angajat> getAll() {
        logger.info("Fetching all Angajati");
        List<Angajat> list = new ArrayList<>();
        String sql = "SELECT * FROM Angajat";
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Angajat(
                        rs.getInt("angajatID"),
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }
            logger.info("Fetched " + list.size() + " angajati");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching all Angajati", e);
        }
        return list;
    }

    //extra method needed by Service for login
    public Angajat findByUsername(String username) {
        logger.info("Finding Angajat by username: " + username);
        String sql = "SELECT * FROM Angajat WHERE username = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Angajat a = new Angajat(
                        rs.getInt("angajatID"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                logger.info("Found: " + a);
                return a;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding Angajat by username: " + username, e);
        }
        logger.warning("No Angajat found with username: " + username);
        return null;
    }

    public Angajat findByUsernameAndPassword(String username, String password) {
        logger.info("Finding Angajat by username and password");
        String sql = "SELECT * FROM Angajat WHERE username = ? AND password = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Angajat a = new Angajat(
                        rs.getInt("angajatID"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                logger.info("Found: " + a);
                return a;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding Angajat by username and password", e);
        }
        logger.warning("No Angajat found with provided credentials");
        return null;
    }
}