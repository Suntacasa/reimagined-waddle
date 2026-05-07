package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JdbcUtils {
    private static final Logger logger = LogManager.getLogger(JdbcUtils.class);

    private final Properties props;
    private Connection instance = null;

    public JdbcUtils(Properties props) {
        this.props = props;
    }

    private Connection getNewConnection() {
        logger.info("Getting new database connection");
        String url = props.getProperty("jdbc.url");
        Connection con = null;
        try {
            con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.error("Error getting connection: {}", e.getMessage());
        }
        return con;
    }

    public Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                instance = getNewConnection();
            }
        } catch (SQLException e) {
            logger.error("Error checking connection: {}", e.getMessage());
            instance = getNewConnection();
        }
        return instance;
    }
}