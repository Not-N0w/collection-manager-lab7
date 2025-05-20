package com.labs.server;

import com.labs.common.DataContainer;
import com.labs.common.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DBManager {
    private Connection dbConnection;
    private static final Logger logger = LoggerFactory.getLogger(DBManager.class);
    DBManager() {

    }

    private Connection getNewConnection() throws SQLException {
        String url = "jdbc:postgresql://postgres:5432/ticketControllerDB";
        String user = "postgres";
        String passwd = "postgres";
        return DriverManager.getConnection(url, user, passwd);
    }

    public boolean connect() {
        try {
            dbConnection = getNewConnection();
        } catch (SQLException e) {
            logger.error("Error connecting to PostgreSQL database", e);
            return false;
        }
        logger.info("Connected to PostgreSQL database");
        return true;
    }
    private boolean checkConnection() {
        return dbConnection != null;
    }

    private User findUser(User user) {
        if (dbConnection == null) return null;

        String query = "SELECT * FROM users WHERE login = ? AND password_hash = ?";

        try (PreparedStatement ps = dbConnection.prepareStatement(query)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setVerified(true);
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException", e);
            return null;
        }

        return user;
    }



    private User addUser(User user) {
        if (dbConnection == null) return null;

        String query = "INSERT INTO users (login, password_hash) VALUES (?, ?);";
        try {
            PreparedStatement ps = dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
                user.setVerified(true);
                logger.info("User added successfully. Returning verified user.");
            } else {
                logger.info("Returning not verified user.");
                return user;
            }

        } catch (SQLException e) {
            logger.error("SQLException", e);
            return null;
        }
        return user;
    }


    public DataContainer userProcess(User user, boolean needAdd) {
        logger.info("Processing user-request");

        DataContainer result = new DataContainer();
        result.add("from", "SERVER");
        logger.info("Trying to verify user");

        User newUser = findUser(user);
        if (newUser == null) {
            result.add("status", "error");
            result.add("message", "Error while asking database");
            logger.info("Error while asking database");
            return result;
        } else if (!newUser.isVerified()) {
            if (!needAdd) {
                result.add("User", user);
                result.add("status", "ok");
                logger.info("User not found, so not verified");
                return result;
            }
            else {
                logger.info("Adding new user");

                newUser = addUser(newUser);
                if (newUser == null) {
                    result.add("status", "error");
                    result.add("message", "Error while asking database");
                    logger.info("Error while asking database");

                    return result;
                }
                else {
                    result.add("User", user);
                    result.add("status", "ok");
                    logger.info("New user added");

                }
            }
        }
        else {
            result.add("status", "ok");
            result.add("message", "User verified");
            result.add("User", newUser);
        }
        logger.info("User verified");

        return result;
    }

    public boolean disconnect() {
        try { dbConnection.close(); }
        catch (Exception e)
        { return false; }
        return true;
    }

    public DataContainer processDBRequest(DataContainer request) {
        logger.info("Processing request");
        return userProcess((User)request.get("User"), (request.get("type").equals("user-add") ? true : false));
    }

}
