package repository;

import domain.Cursa;
import utils.JdbcUtils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepoCursa extends AbstractRepository<Integer, Cursa> implements IRepoCursa<Integer, Cursa>{

    private static final Logger logger = Logger.getLogger(RepoCursa.class.getName());

    public RepoCursa(Properties props) {
        logger.info("Initializing RepoAngajat");
        this.dbUtils = new JdbcUtils(props);  // AbstractRepository's dbUtils
        initTable();
    }

    private void initTable() {
        logger.info("Initializing Cursa table");
        String sql = """
                CREATE TABLE IF NOT EXISTS Cursa (
                    cursaID         INTEGER PRIMARY KEY AUTOINCREMENT,
                    capacitateMotor TEXT    NOT NULL,
                    nrParticipanti  INTEGER NOT NULL DEFAULT 0
                )""";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute(sql);
            logger.info("Cursa table ready");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to create Cursa table", e);
        }
    }

    @Override
    public void save(Cursa cursa) {
        logger.info("Saving: " + cursa);
        String sql = "INSERT INTO Cursa(capacitateMotor, nrParticipanti) VALUES (?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cursa.getCapacitateMotor());
            ps.setInt(2, cursa.getNrParticipanti());
            ps.executeUpdate();
            logger.info("Saved successfully: " + cursa);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving: " + cursa, e);
        }
    }

    @Override
    public void delete(Integer id) {
        logger.info("Deleting Cursa with id: " + id);
        String sql = "DELETE FROM Cursa WHERE cursaID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) logger.warning("No Cursa found with id: " + id);
            else logger.info("Deleted Cursa with id: " + id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting Cursa with id: " + id, e);
        }
    }

    @Override
    public void update(Integer id, Cursa cursa) {
        logger.info("Updating Cursa id: " + id + " with: " + cursa);
        String sql = "UPDATE Cursa SET capacitateMotor = ?, nrParticipanti = ? WHERE cursaID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cursa.getCapacitateMotor());
            ps.setInt(2, cursa.getNrParticipanti());
            ps.setInt(3, id);
            int affected = ps.executeUpdate();
            if (affected == 0) logger.warning("No Cursa found with id: " + id);
            else logger.info("Updated Cursa id: " + id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating Cursa id: " + id, e);
        }
    }

    public void update(Integer id, Cursa cursa, Connection con) throws SQLException {
        logger.info("Updating Cursa (transactional) id: " + id + " to " + cursa.getNrParticipanti() + " participants");

        String sql = "UPDATE Cursa SET nrParticipanti = ? WHERE cursaID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cursa.getNrParticipanti());
            ps.setInt(2, id);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                logger.warning("No Cursa found with id: " + id + " during transaction.");
            } else {
                logger.info("Updated Cursa (transactional) id: " + id);
            }
        }
    }

    @Override
    public Cursa findById(Integer id) {
        logger.info("Finding Cursa by id: " + id);
        String sql = "SELECT * FROM Cursa WHERE cursaID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Cursa c = new Cursa(
                        rs.getInt("cursaID"),
                        rs.getString("capacitateMotor"),
                        rs.getInt("nrParticipanti")
                );
                logger.info("Found: " + c);
                return c;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding Cursa by id: " + id, e);
        }
        logger.warning("No Cursa found with id: " + id);
        return null;
    }

    @Override
    public List<Cursa> getAll() {
        logger.info("Fetching all Curse");
        List<Cursa> list = new ArrayList<>();
        String sql = "SELECT * FROM Cursa";
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Cursa(
                        rs.getInt("cursaID"),
                        rs.getString("capacitateMotor"),
                        rs.getInt("nrParticipanti")
                ));
            }
            logger.info("Fetched " + list.size() + " curse");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching all Curse", e);
        }
        return list;
    }

    //extra method needed by Service, increment participant count when someone registers
    public void incrementParticipanti(Integer cursaID) {
        logger.info("Incrementing nrParticipanti for cursaID: " + cursaID);
        String sql = "UPDATE Cursa SET nrParticipanti = nrParticipanti + 1 WHERE cursaID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cursaID);
            int affected = ps.executeUpdate();
            if (affected == 0) logger.warning("No Cursa found with id: " + cursaID);
            else logger.info("Incremented nrParticipanti for cursaID: " + cursaID);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error incrementing nrParticipanti for cursaID: " + cursaID, e);
        }
    }
}