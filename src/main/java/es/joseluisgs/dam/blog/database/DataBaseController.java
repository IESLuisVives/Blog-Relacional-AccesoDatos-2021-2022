package es.joseluisgs.dam.blog.database;

import es.joseluisgs.dam.blog.utils.ApplicationProperties;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;

public class DataBaseController {
    private static DataBaseController controller;
    private String serverUrl;
    private String serverPort;
    private String dataBaseName;
    private String user;
    private String password;
    /*
    Tipos de Driver
    SQLite: "org.sqlite.JDBC";
    MySQL: "com.mysql.jdbc.Driver"
    MariaDB: com.mysql.cj.jdbc.Driver
     */
    private String jdbcDriver;
    // Para manejar las conexiones y respuestas de las mismas
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private DataBaseController() {
        // System.out.println("Mi nombre es: " + this.nombre);
        initConfig();
    }

    public static DataBaseController getInstance() {
        if (controller == null) {
            controller = new DataBaseController();
        }
        return controller;
    }

    private void initConfig() {
        // Leemos los datos de la base de datos que pueden estar en
        // porperties o en .env
        // imaginemos que el usuario y pasword estaán en .env y el resto en application.properties
        ApplicationProperties properties = new ApplicationProperties();
        serverUrl = properties.readProperty("database.server.url");
        serverPort = properties.readProperty("database.server.port");
        dataBaseName = properties.readProperty("database.name");
        jdbcDriver = properties.readProperty("database.jdbc.driver");
        Dotenv dotenv = Dotenv.load();
        user = dotenv.get("DATABASE_USER");
        password = dotenv.get("DATABASE_PASSWORD");

    }

    // Método para abrir la Conexion la Base de Datos
    public void open() {
        try {
            //String url = "jdbc:sqlite:"+this.ruta+this.bbdd; //MySQL jdbc:mysql://localhost/prueba", "root", "1daw"
            String url = "jdbc:mariadb://" + this.serverUrl + ":" + this.serverPort + "/" + this.dataBaseName + "";
            // System.out.println(url);
            // Obtenemos la conexión
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Error al abrir Conexión: " + e.getMessage());
        }
    }

    public void close() {
        try {
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar Conexión: " + e.getMessage());
        }
    }

    private ResultSet executeQuery(String querySQL, Object... params) throws SQLException {
        preparedStatement = connection.prepareStatement(querySQL);
        // Vamos a pasarle los parametros usando preparedStatement
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeQuery();
    }

    public ResultSet select(String querySQL, Object... params) {
        try {
            resultSet = executeQuery(querySQL, params);
        } catch (SQLException e) {
            System.err.println("Error al consultar BD" + e.getMessage());
        } finally {
            return resultSet;
        }
    }

    public ResultSet select(String querySQL, int limit, int offset, Object... params) {
        try {
            String query = querySQL + " LIMIT " + limit + " OFFSET " + offset;
            resultSet = executeQuery(query, params);
        } catch (SQLException e) {
            System.err.println("Error al consultar BD con paginación" + e.getMessage());
        } finally {
            return resultSet;
        }
    }

    public ResultSet insert(String insertSQL, Object... params) {
        try {
            // Con return generated keys obtenemos las claves generadas
            preparedStatement = connection.prepareStatement(insertSQL, preparedStatement.RETURN_GENERATED_KEYS);
            // Vamos a pasarle los parametros usando preparedStatement
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            preparedStatement.executeUpdate();
            return preparedStatement.getGeneratedKeys();
        } catch (SQLException e) {
            System.err.println("Error al insertar BD" + e.getMessage());
        }
        return null;
    }

    public int update(String updateSQL, Object... params) {
        int res = -1;
        try {
            res = updateQuery(updateSQL, params);
        } catch (SQLException e) {
            System.err.println("Error al actualizar BD" + e.getMessage());
        } finally {
            return res;
        }
    }

    public int delete(String deleteSQL, Object... params) {
        int res = -1;
        try {
            res = updateQuery(deleteSQL, params);
        } catch (SQLException e) {
            System.err.println("Error al eliminar BD" + e.getMessage());
        } finally {
            return res;
        }
    }

    private int updateQuery(String genericSQL, Object... params) throws SQLException {
        int res = -1;
        // Con return generated keys obtenemos las claves generadas
        preparedStatement = connection.prepareStatement(genericSQL);
        // Vamos a pasarle los parametros usando preparedStatement
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeUpdate();
    }

    public void initData(String sqlFile) throws FileNotFoundException {
        ScriptRunner sr = new ScriptRunner(connection);
        Reader reader = new BufferedReader(new FileReader(sqlFile));
        sr.runScript(reader);
    }
}
