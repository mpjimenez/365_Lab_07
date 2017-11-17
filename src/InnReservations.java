import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class InnReservations {
    public static void main(String[] args) throws SQLException {
//        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
//                System.getenv("HP_JDBC_USER"),
//                System.getenv("HP_JDBC_PW"))) {
        try (Connection conn = DriverManager.getConnection(
                args[0], args[1], args[2]
        )) {
            String sql = "select * from lab7_rooms limit 1";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    String roomCode = rs.getString("RoomCode");
                    BigDecimal basePrice = rs.getBigDecimal("basePrice");
                    System.out.format("roomCode: %s\nbasePrice: %f\n", roomCode, basePrice);
                }
            }
        }
    }
}
