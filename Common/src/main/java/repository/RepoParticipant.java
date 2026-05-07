package repository;

import domain.Participant;
import utils.JdbcUtils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepoParticipant extends AbstractRepository<Integer, Participant> implements IRepoParticipant<Integer, Participant>{

    private static final Logger logger = Logger.getLogger(RepoParticipant.class.getName());

    public RepoParticipant(Properties props) {
        logger.info("Initializing RepoAngajat");
        this.dbUtils = new JdbcUtils(props);  // AbstractRepository's dbUtils
        initTable();
    }

    private void initTable() {
        logger.info("Initializing Participant table");
        String sql = """
                CREATE TABLE IF NOT EXISTS Participant (
                    participantID INTEGER PRIMARY KEY AUTOINCREMENT,
                    nume          TEXT    NOT NULL,
                    cnp           TEXT    NOT NULL UNIQUE,
                    echipa        TEXT    NOT NULL,
                    cursaID       INTEGER NOT NULL,
                    FOREIGN KEY (cursaID) REFERENCES Cursa(cursaID)
                )""";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute(sql);
            logger.info("Participant table ready");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to create Participant table", e);
        }
    }

    @Override
    public void save(Participant participant) {
        logger.info("Saving: " + participant);
        String sql = "INSERT INTO Participant(nume, cnp, echipa, cursaID) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, participant.getNume());
            ps.setString(2, participant.getCnp());
            ps.setString(3, participant.getEchipa());
            ps.setInt(4, participant.getCursaID());
            ps.executeUpdate();
            logger.info("Saved successfully: " + participant);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving: " + participant, e);
        }
    }

    @Override
    public void delete(Integer id) {
        logger.info("Deleting Participant with id: " + id);
        String sql = "DELETE FROM Participant WHERE participantID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) logger.warning("No Participant found with id: " + id);
            else logger.info("Deleted Participant with id: " + id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting Participant with id: " + id, e);
        }
    }

    @Override
    public void update(Integer id, Participant participant) {
        logger.info("Updating Participant id: " + id + " with: " + participant);
        String sql = "UPDATE Participant SET nume = ?, cnp = ?, echipa = ?, cursaID = ? WHERE participantID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, participant.getNume());
            ps.setString(2, participant.getCnp());
            ps.setString(3, participant.getEchipa());
            ps.setInt(4, participant.getCursaID());
            ps.setInt(5, id);
            int affected = ps.executeUpdate();
            if (affected == 0) logger.warning("No Participant found with id: " + id);
            else logger.info("Updated Participant id: " + id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating Participant id: " + id, e);
        }
    }

    @Override
    public Participant findById(Integer id) {
        logger.info("Finding Participant by id: " + id);
        String sql = "SELECT * FROM Participant WHERE participantID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Participant p = mapRow(rs);
                logger.info("Found: " + p);
                return p;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding Participant by id: " + id, e);
        }
        logger.warning("No Participant found with id: " + id);
        return null;
    }

    @Override
    public List<Participant> getAll() {
        logger.info("Fetching all Participants");
        List<Participant> list = new ArrayList<>();
        String sql = "SELECT * FROM Participant";
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.info("Fetched " + list.size() + " participants");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching all Participants", e);
        }
        return list;
    }

    //extra method needed by Service, search all participants from a given team
    public List<Participant> findByEchipa(String echipa) {
        logger.info("Finding Participants by echipa: " + echipa);
        List<Participant> list = new ArrayList<>();
        String sql = "SELECT * FROM Participant WHERE echipa = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, echipa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.info("Found " + list.size() + " participants in echipa: " + echipa);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding Participants by echipa: " + echipa, e);
        }
        return list;
    }

    private Participant mapRow(ResultSet rs) throws SQLException {
        return new Participant(
                rs.getInt("participantID"),
                rs.getString("nume"),
                rs.getString("cnp"),
                rs.getString("echipa"),
                rs.getInt("cursaID")
        );
    }

    public void save(Participant participant, Connection con) throws SQLException {
        logger.info("Saving with transaction: " + participant);
        String sql = "INSERT INTO Participant(nume, cnp, echipa, cursaID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, participant.getNume());
            ps.setString(2, participant.getCnp());
            ps.setString(3, participant.getEchipa());
            ps.setInt(4, participant.getCursaID());
            ps.executeUpdate();
            logger.info("Saved successfully via transaction: " + participant);
        }
    }
}