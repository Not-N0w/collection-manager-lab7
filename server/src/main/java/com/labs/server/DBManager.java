package com.labs.server;

import com.labs.common.DataContainer;
import com.labs.common.SHAConverter;
import com.labs.common.core.*;
import com.labs.common.user.User;
import com.labs.server.queryBuilder.DBQuery;
import com.labs.server.queryBuilder.JoinQuery;
import com.labs.server.queryBuilder.QueryType;
import com.labs.server.queryBuilder.WhereQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;

public class DBManager {
    private Connection dbConnection;
    private static final Logger logger = LoggerFactory.getLogger(DBManager.class);
    private Long userId;
    DBManager() {}

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    private Connection getNewConnection() throws SQLException {
        String dbName = System.getenv("NAME_DB");
        String dbUser = System.getenv("USER_DB");
        String dbPassword = System.getenv("PASSWORD_DB");
        String dbHost = System.getenv("HOST_DB");

        String url = "jdbc:postgresql://%s:5432/%s".formatted(dbHost, dbName);

        return DriverManager.getConnection(url, dbUser, dbPassword);
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

    private User findUser(User user) throws Exception {
        if (dbConnection == null) return null;
        WhereQuery whereQuery = new WhereQuery("login = ?");
        DBQuery dbQuery = new DBQuery(QueryType.SELECT, "users", List.of(), List.of(whereQuery), "*");

        String query = dbQuery.toString();

        try (PreparedStatement ps = dbConnection.prepareStatement(query)) {
            ps.setString(1, user.getLogin());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String passwordHash = SHAConverter.convert(user.getPasswordHash());
                if(!rs.getString("password_hash").equals(passwordHash)) {
                    throw new Exception("Wrong password");
                }
                user.setId(rs.getLong("id"));
                user.setVerified(true);
            }
        } catch (SQLException e) {
            logger.error("SQLException", e);
            return null;
        }


        return user;
    }



    private User addUser(User user) {
        if (dbConnection == null) return null;

        WhereQuery val1 = new WhereQuery("?");
        WhereQuery val2 = new WhereQuery("?");
        DBQuery dbQuery = new DBQuery(QueryType.INSERT,
                "users", List.of(), List.of(val1, val2),
                "login", "password_hash");

        String query = dbQuery.toString();

        try {
            PreparedStatement ps = dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, SHAConverter.convert(user.getPasswordHash()));
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
        User newUser;
        try {
            newUser = findUser(user);
        } catch (Exception e) {
            logger.error("Error verifying user (Wrong password)", e);
            result.add("status", "error");
            result.add("message", "Wrong password");
            return result;
        }
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
                    user.setPasswordHash(null);
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
        if(dbConnection == null) return false;
        try { dbConnection.close(); }
        catch (Exception e)
        { return false; }
        return true;
    }

    public DataContainer processDBRequest(DataContainer request) {
        logger.info("Processing request");
        return userProcess((User)request.get("User"), (request.get("type").equals("user-add") ? true : false));
    }

    public boolean addTicket(Ticket ticket) {
        if (dbConnection == null || ticket == null) return false;

        try {
            dbConnection.setAutoCommit(false);

            WhereQuery val1 = new WhereQuery("?");

            DBQuery dbQuery = new DBQuery(QueryType.INSERT,
                    "locations", List.of(), List.of(val1, val1, val1),
                    "x", "y", "z");
            dbQuery.setMore("RETURNING id");

            String locationQuery = dbQuery.toString();
            PreparedStatement locationStmt = dbConnection.prepareStatement(locationQuery);
            locationStmt.setFloat(1, ticket.person().getLocation().getX());
            locationStmt.setFloat(2, ticket.person().getLocation().getY());
            locationStmt.setLong(3, ticket.person().getLocation().getZ());
            ResultSet locationRs = locationStmt.executeQuery();
            locationRs.next();
            int locationId = locationRs.getInt(1);


            DBQuery dbPersonQuery = new DBQuery(QueryType.INSERT,
                    "persons", List.of(), List.of(val1, val1, val1, val1),
                    "birthday", "weigth", "passport_id", "location_id");
            dbQuery.setMore("RETURNING id");

            String personQuery = dbPersonQuery.toString();
            PreparedStatement personStmt = dbConnection.prepareStatement(personQuery);
            personStmt.setDate(1, java.sql.Date.valueOf(ticket.person().getBirthday()));
            personStmt.setDouble(2, ticket.person().getWeight());
            personStmt.setString(3, ticket.person().getPassportID());
            personStmt.setInt(4, locationId);
            ResultSet personRs = personStmt.executeQuery();
            personRs.next();
            int personId = personRs.getInt(1);

            DBQuery dbCoordinatesQuery = new DBQuery(QueryType.INSERT,
                    "coordinates", List.of(), List.of(val1, val1),
                    "x", "y");
            dbQuery.setMore("RETURNING id");

            String coordinatesQuery = dbCoordinatesQuery.toString();
            PreparedStatement coordStmt = dbConnection.prepareStatement(coordinatesQuery);
            coordStmt.setInt(1, ticket.coordinates().getX());
            coordStmt.setFloat(2, ticket.coordinates().getY());
            ResultSet coordRs = coordStmt.executeQuery();
            coordRs.next();
            int coordinatesId = coordRs.getInt(1);

            DBQuery dbTicketQuery = new DBQuery(QueryType.INSERT,
                    "tickets", List.of(), List.of(val1, val1, val1, val1, val1, val1, val1),
                    "name", "coordinates_id", "price", "refundable", "type", "person_id", "user_id");
            dbQuery.setMore("RETURNING id");

            String ticketQuery = dbTicketQuery.toString();
            PreparedStatement ticketStmt = dbConnection.prepareStatement(ticketQuery);
            ticketStmt.setString(1, ticket.name());
            ticketStmt.setInt(2, coordinatesId);
            ticketStmt.setInt(3, ticket.price());
            ticketStmt.setBoolean(4, ticket.refundable());
            ticketStmt.setObject(5, ticket.type().name(), java.sql.Types.OTHER);
            ticketStmt.setInt(6, personId);
            ticketStmt.setLong(7, ticket.getOwnerId());
            ResultSet ticketRs = ticketStmt.executeQuery();

            if (ticketRs.next()) {
                long ticketId = ticketRs.getLong(1);
                ticket.setId(ticketId);
                logger.info("Ticket added successfully with ID: " + ticketId);
            }

            dbConnection.commit();
        } catch (SQLException e) {
            logger.error("SQLException during addTicket", e);
            try {
                dbConnection.rollback();
            } catch (SQLException rollbackEx) {
                logger.error("Rollback failed", rollbackEx);
            }
            return false;
        } finally {
            try {
                dbConnection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Failed to reset autocommit", e);
            }
        }
        return true;

    }
    public boolean updateTicket(Long ticketId, Ticket ticket) throws Exception {
        if (dbConnection == null) {
            throw new Exception("DB disconnected");
        }

        String findUserQuery = "SELECT * FROM tickets WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(findUserQuery)) {
            stmt.setLong(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) throw new Exception("No such ticket");
            else if(rs.getLong("user_id") != userId) throw new Exception("Permission denied");
        }

        Location location = ticket.getPerson().getLocation();
        String insertLocation = "INSERT INTO locations (x, y, z) VALUES (?, ?, ?) RETURNING id";
        long locationId;
        try (PreparedStatement stmt = dbConnection.prepareStatement(insertLocation)) {
            stmt.setFloat(1, location.getX());
            stmt.setFloat(2, location.getY());
            stmt.setLong(3, location.getZ());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) throw new Exception("Failed to insert location");
            locationId = rs.getLong(1);
        }

        Person person = ticket.getPerson();
        String insertPerson = "INSERT INTO persons (birthday, weight, passport_id, location_id) VALUES (?, ?, ?, ?) RETURNING id";
        long personId;
        try (PreparedStatement stmt = dbConnection.prepareStatement(insertPerson)) {
            stmt.setDate(1, java.sql.Date.valueOf(person.getBirthday()));
            stmt.setDouble(2, person.getWeight());
            stmt.setString(3, person.getPassportID());
            stmt.setLong(4, locationId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) throw new Exception("Failed to insert person");
            personId = rs.getLong(1);
        }

        Coordinates coordinates = ticket.getCoordinates();
        String insertCoordinates = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";
        long coordinatesId;
        try (PreparedStatement stmt = dbConnection.prepareStatement(insertCoordinates)) {
            stmt.setInt(1, coordinates.getX());
            stmt.setFloat(2, coordinates.getY());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) throw new Exception("Failed to insert coordinates");
            coordinatesId = rs.getLong(1);
        }

        String updateTicket = "UPDATE tickets SET name = ?, coordinates_id = ?, price = ?, refundable = ?, type = ?, person_id = ? WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(updateTicket)) {
            stmt.setString(1, ticket.getName());
            stmt.setLong(2, coordinatesId);
            stmt.setInt(3, ticket.getPrice());
            stmt.setBoolean(4, ticket.getRefundable());
            stmt.setObject(5, ticket.getType().name(), java.sql.Types.OTHER);
            stmt.setLong(6, personId);
            stmt.setLong(7, ticketId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public TreeSet<Ticket> getCollection() {
        TreeSet<Ticket> tickets = new TreeSet<>();

        JoinQuery joinQuery1 = new JoinQuery(
                "JOIN",
                "persons",
                "persons.id",
                "tickets.person_id"
        );

        JoinQuery joinQuery2 = new JoinQuery(
                "JOIN",
                "coordinates",
                "coordinates.id",
                "tickets.coordinates_id"
        );

        JoinQuery joinQuery3 = new JoinQuery(
                "JOIN",
                "locations",
                "locations.id",
                "persons.location_id"
        );

        String[] columns = {
                "tickets.id",
                "name",
                "price",
                "refundable",
                "type",
                "user_id",
                "coordinates.x AS coordinates_x",
                "coordinates.y AS coordinates_y",
                "persons.birthday AS person_birthday",
                "persons.weight AS person_weight",
                "persons.passport_id AS person_passport_id",
                "locations.x AS locations_x",
                "locations.y AS locations_y",
                "locations.z AS locations_z"
        };

        DBQuery dbQuery = new DBQuery(
                QueryType.SELECT,
                "tickets",
                List.of(joinQuery1, joinQuery2, joinQuery3),
                List.of(),
                columns
        );

        String selectTickets = dbQuery.toString();
        try {
            Statement stmt = dbConnection.createStatement();
            var rs = stmt.executeQuery(selectTickets);
            while (rs.next()) {
                Coordinates coordinates = new Coordinates(rs.getInt("coordinates_x"), rs.getFloat("coordinates_y"));
                Location location = new Location(rs.getFloat("locations_x"), rs.getFloat("locations_y"), rs.getLong("locations_z") );

                Person person = new Person();
                person.setBirthday(LocalDate.parse(rs.getString("person_birthday")));
                person.setWeight(rs.getDouble("person_weight"));
                person.setPassportID(rs.getString("person_passport_id"));
                person.setLocation(location);

                Ticket ticket = new Ticket();
                ticket.setId(rs.getLong("id"));
                ticket.setName(rs.getString("name"));
                ticket.setPrice(rs.getInt("price"));
                ticket.setOwnerId(rs.getLong("user_id"));
                ticket.setRfundable(rs.getBoolean("refundable"));
                ticket.setType(TicketType.valueOf(rs.getString("type")));
                ticket.setCoordinates(coordinates);
                ticket.setPerson(person);

                tickets.add(ticket);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tickets;
    }

    public boolean deleteTicket(Long ticketId) throws Exception {
        if (dbConnection == null) {
            throw new Exception("DB disconnected");
        }

        String findUserQuery = "SELECT * FROM tickets WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(findUserQuery)) {
            stmt.setLong(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) throw new Exception("No such ticket");
            else if(rs.getLong("user_id") != userId) throw new Exception("Permission denied");
        }


        String deleteTicket = "DELETE * FROM tickets WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(deleteTicket)) {
            stmt.setLong(1, ticketId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    public boolean clear() throws Exception {
        if (dbConnection == null) {
            throw new Exception("DB disconnected");
        }

        String findUserQuery = "TRUNCATE TABLE tickets";
        try (PreparedStatement stmt = dbConnection.prepareStatement(findUserQuery)) {
            ResultSet rs = stmt.executeQuery();
            return true;
        }
    }

}
