

import java.math.BigDecimal;
import java.sql.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;

public class InnReservations {

   public static void main(String[] args)  {
  
      Scanner input = new Scanner(System.in);
      String option = "A";
   
      while (!option.equals("5")) {

         printMenu();
         option = input.nextLine();
      
         switch(option) {
            case "1":
            try {
            RoomRates(args);
            } catch (SQLException e) {
               System.err.println("SQLException: " + e.getMessage());
              }
            break;

            case "2":
            try {
               Reservations(args);
            } catch (SQLException e) {
               System.err.println("SQLException: " + e.getMessage());
              }
            break;
   
            case "3":
            try {
               DetailedReservationInfo(args);
            } catch (SQLException e) {
               System.err.println("SQLException: " + e.getMessage());
              }
            break;

            case "4":
            try {
              Revenue(args);
            } catch (SQLException e) {
               System.err.println("SQLException: " + e.getMessage());
              }
            break;
   
         }
      }
   }

   public static void printMenu() {
      String line = "Enter the number that corresponds" +
                    "to your desired action\n\n";

      System.out.println("Rooms and Rates [1]");
      System.out.println("Reservations [2]");
      System.out.println("Detailed Reservation Information [3]");
      System.out.println("Revenue [4]");
      System.out.println("Quit [5]\n"); 
   }

   public static void RoomRates(String[] envVar) throws SQLException {
      if (envVar.length != 3) {
         try (Connection conn = DriverManager.getConnection(
                                                System.getenv("HP_JDBC_URL"),
                                                System.getenv("HP_JDBC_USER"),
                                                System.getenv("HP_JDBC_PW"))) {

            String sql = "select * from lab7_rooms limit 2";
            try (Statement stmt = conn.createStatement();
               ResultSet rs = stmt.executeQuery(sql)) {
            
               while (rs.next()) {
                  String roomCode = rs.getString("RoomCode");
                  BigDecimal basePrice = rs.getBigDecimal("basePrice");
                  System.out.format("roomCode: %s\nbasePrice: %f\n",
                                       roomCode, basePrice);
                  System.out.print("\n");
               } 
            }
         }
      }
      
      else {
         try (Connection conn = DriverManager.getConnection(
                                              envVar[0], envVar[1], envVar[2])) {
         
            String sql = "select * from lab7_rooms limit 2";
            try (Statement stmt = conn.createStatement();
               ResultSet rs = stmt.executeQuery(sql)) {
            
               while (rs.next()) {
                  String roomCode = rs.getString("RoomCode");
                  BigDecimal basePrice = rs.getBigDecimal("basePrice");
                  System.out.format("roomCode: %s\nbasePrice: %f\n",
                                       roomCode, basePrice);
               } 
            }
         }
      }
   }          
   

   public static void Reservations(String[] envVar) throws SQLException {
      Scanner reader = new Scanner(System.in);
      Reservation reservation = new Reservation();

      System.out.println("ENTER YOUR FIRST NAME: ");
      reservation.firstName = reader.nextLine();

      System.out.println("ENTER YOUR LAST NAME: ");
      reservation.lastName = reader.nextLine();

      System.out.println("ENTER YOUR DESIRED ROOM CODE: ");
      reservation.roomCode = reader.nextLine();

      System.out.println("ENTER YOUR DESIRED BED TYPE: ");
      reservation.bedType = reader.nextLine();

      System.out.println("ENTER DESIRED CHECKIN DATE (format: yyyy-MM-dd): ");
      reservation.startDate = stringToSqlDate(reader.nextLine());

      System.out.println("ENTER DESIRED CHECKOUT DATE (format: yyyy-MM-dd): ");
      reservation.endDate = stringToSqlDate(reader.nextLine());

      System.out.println("ENTER NUMBER OF CHILDREN: ");
      reservation.numChildren = Integer.parseInt(reader.nextLine());

      System.out.println("ENTER NUMBER OF ADULTS: ");
      reservation.numAdults = Integer.parseInt(reader.nextLine());

      System.out.println(reservation);
   }

   public static void DetailedReservationInfo(String[] envVar) throws SQLException {
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
              System.getenv("HP_JDBC_USER"),
              System.getenv("HP_JDBC_PW"))) {



         boolean hasCondition = false;
         Scanner reader = new Scanner(System.in);
         System.out.println("RESERVATION LOOKUP (LEAVE ENTRY BLANK TO INDICATE ANY)\n");
         System.out.println("FIRST NAME: ");
         String firstName = reader.nextLine();
         hasCondition = hasCondition || firstName.length() > 0;

         System.out.println("LAST NAME: ");
         String lastName = reader.nextLine();
         hasCondition = hasCondition || lastName.length() > 0;

         System.out.println("START DATE: ");
         String startDate = reader.nextLine();
         hasCondition = hasCondition || startDate.length() > 0;

         System.out.println("END DATE: ");
         String endDate = reader.nextLine();
         hasCondition = hasCondition || endDate.length() > 0;

         System.out.println("ROOM CODE: ");
         String roomCode = reader.nextLine();
         hasCondition = hasCondition || roomCode.length() > 0;

         System.out.println("RESERVATION CODE: ");
         String reservationCode = reader.nextLine();
         hasCondition = hasCondition || reservationCode.length() > 0;

         String query = "select lab7_rooms.roomName, lab7_reservations.* from lab7_reservations inner join lab7_rooms on room = roomCode";
         if (hasCondition) {
            query += " where ";
         }

         List<Object> params = new ArrayList<>();
         StringJoiner sj = new StringJoiner(" AND ");

         if (firstName.length() > 0) {
            if (firstName.contains("%") || firstName.contains("_")) {
               sj.add("firstName like ?");
            }
            else {
               sj.add("firstName = ?");
            }
            params.add(firstName);
         }

         if (lastName.length() > 0) {
            if (lastName.contains("%") || lastName.contains("_")) {
               sj.add("lastName like ?");
            }
            else {
               sj.add("lastName = ?");
            }
            params.add(lastName);
         }
         if (startDate.length() > 0) {
            sj.add("CheckIn >= ?");
            params.add(stringToSqlDate(startDate));
         }
         if (endDate.length() > 0) {
            sj.add("Checkout <= ?");
            params.add(stringToSqlDate(endDate));
         }
         if (roomCode.length() > 0) {
            if (roomCode.contains("%") || roomCode.contains("_")) {
               sj.add("room like ?");
            }
            else {
               sj.add("room = ?");
            }
            params.add(roomCode);
         }
         if (reservationCode.length() > 0) {
            if (reservationCode.contains("%") || reservationCode.contains("_")) {
               sj.add("code like ?");
               params.add(reservationCode);
            }
            else {
               sj.add("code = ?");
               params.add(Integer.parseInt(reservationCode));
            }
         }

         try (PreparedStatement statement = conn.prepareStatement(query + sj)) {
            int i = 1;
            for (Object param : params) {
               statement.setObject(i++, param);
            }

            try (ResultSet rs = statement.executeQuery()) {
               System.out.println("MATCHING RESULTS:\n----------------------------------");
               while (rs.next()) {
                  System.out.println("ROOM NAME: " + rs.getString("roomName"));
                  System.out.println("CODE: " + rs.getString("code"));
                  System.out.println("ROOM: " + rs.getString("room"));
                  System.out.println("CHECK IN: " + rs.getString("CheckIn"));
                  System.out.println("CHECK OUT: " + rs.getString("Checkout"));
                  System.out.println("RATE: " + rs.getDouble("Rate"));
                  System.out.println("LAST NAME: " + rs.getString("LastName"));
                  System.out.println("FIRST NAME: " + rs.getString("FirstName"));
                  System.out.println("ADULTS: " + rs.getInt("adults"));
                  System.out.println("KIDS: " + rs.getInt("kids") + "\n----------------------------------");
               }
               System.out.println();
            }
         }
      }
   }

   public static void Revenue(String[] envVar) throws SQLException {

   }


   public static Date stringToSqlDate(String date) {
      return Date.valueOf(date);
   }
}

