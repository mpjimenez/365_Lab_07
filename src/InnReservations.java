

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

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
      String line = "Enter the number that cooresponds" +
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

   }

   public static void Revenue(String[] envVar) throws SQLException {

   }


   public static Date stringToSqlDate(String date) {
      return Date.valueOf(date);
   }
}

