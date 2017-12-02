import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;

import java.util.List;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

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
              catch(IOException e) {
                 e.printStackTrace();
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
            // creating PreparedStatement 
            String sql = "select * from lab7_rooms where bedType = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            
            // insert parameter into preparedStatement
            preparedStatement.setString(1, "King");
            
            //execute preparedStatement
            try(ResultSet rs = preparedStatement.executeQuery()){
            
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
         
            // creating PreparedStatement 
            String sql = "select * from lab7_rooms where bedType = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            
            // insert parameter into preparedStatement
            preparedStatement.setString(1, "King");
            
            //execute preparedStatement
            try(ResultSet rs = preparedStatement.executeQuery()){
            
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
   }          
   

   public static void Reservations(String[] envVar) throws SQLException {
			
      Scanner reader = new Scanner(System.in);
      Reservation reservation = new Reservation();
      List<Object> params = new ArrayList<>();

      System.out.println("ENTER YOUR FIRST NAME: ");
      reservation.firstName = reader.nextLine();

      System.out.println("ENTER YOUR LAST NAME: ");
      reservation.lastName = reader.nextLine();

      System.out.println("ENTER YOUR DESIRED ROOM CODE (ANY FOR NO PREFERENCE): ");
      reservation.roomCode = reader.nextLine();
      if (!reservation.roomCode.equalsIgnoreCase("any")) {
         params.add(reservation.roomCode);
      }

      System.out.println("ENTER YOUR DESIRED BED TYPE (ANY FOR NO PREFERENCE): ");
      reservation.bedType = reader.nextLine();
      if (!reservation.bedType.equalsIgnoreCase("any")) {
         params.add(reservation.bedType);
      }

      System.out.println("ENTER DESIRED CHECKIN DATE (format: yyyy-MM-dd): ");
      reservation.startDate = stringToSqlDate(reader.nextLine());
      params.add(reservation.startDate);

      System.out.println("ENTER DESIRED CHECKOUT DATE (format: yyyy-MM-dd): ");
      reservation.endDate = stringToSqlDate(reader.nextLine());
      params.add(reservation.endDate);

      params.add(reservation.startDate);
      params.add(reservation.endDate);

      System.out.println("ENTER NUMBER OF CHILDREN: ");
      reservation.numChildren = Integer.parseInt(reader.nextLine());

      System.out.println("ENTER NUMBER OF ADULTS: ");
      reservation.numAdults = Integer.parseInt(reader.nextLine());

      params.add(reservation.numAdults + reservation.numChildren);

      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
              System.getenv("HP_JDBC_USER"),
              System.getenv("HP_JDBC_PW"))) {

         StringJoiner joiner = new StringJoiner(" ");
         joiner.add("select * from lab7_rooms where");
         if (!reservation.roomCode.equalsIgnoreCase("any")) {
            joiner.add("roomCode = ? and");
         }
         if (!reservation.bedType.equalsIgnoreCase("any")) {
            joiner.add("bedType = ? and");
         }
         joiner.add("roomCode not in (select room from lab7_reservations where");
         joiner.add("(? between checkin and checkout - interval 1 day) or");
         joiner.add("(? between checkin + interval 1 day and checkout) or");
         joiner.add("(? < checkin and ? > checkout))");
         joiner.add("and maxOcc >= ?");

         try (PreparedStatement statement = conn.prepareStatement(joiner.toString())) {
            int i = 1;
            for (Object param : params) {
               statement.setObject(i++, param);
            }

            try (ResultSet rs = statement.executeQuery()) {
               System.out.format("MATCHING OPEN ROOMS BETWEEN %s AND %s:\n",
                       reservation.startDate.toString(), reservation.endDate.toString());
               System.out.println("----------------------------------");
               int c = 0;
               String[] roomCodes = new String[10];
               String[] roomNames = new String[10];
               String[] bedTypes = new String[10];
               Double[] basePrices = new Double[10];

               while (rs.next()) {
                  System.out.format("OPTION: %d\n", c);

                  roomCodes[c] = rs.getString("roomCode");
                  roomNames[c] = rs.getString("roomName");
                  bedTypes[c] = rs.getString("bedType");
                  basePrices[c] = rs.getDouble("basePrice");

                  System.out.println("ROOM CODE: " + roomCodes[c]);
                  System.out.println("ROOM NAME: " + roomNames[c]);
                  System.out.println("BEDS: " + rs.getInt("beds"));
                  System.out.println("BED TYPE: " + bedTypes[c]);
                  System.out.println("MAX OCCUPANCY: " + rs.getInt("maxOcc"));
                  System.out.println("BASE PRICE: " + basePrices[c]);
                  System.out.println("DECOR: " + rs.getString("decor"));
                  c++;
               }
               System.out.println("----------------------------------");
               System.out.println("ENTER AN OPTION NUMBER TO BOOK (OR CANCEL TO CANCEL)");
               String choice = reader.nextLine();
               if (!choice.equalsIgnoreCase("cancel")) {
                  int index = Integer.parseInt(choice);
                  System.out.println("BOOKING CONFIRMATION: ");
                  System.out.println("----------------------------------");
                  System.out.println("NAME: " + reservation.firstName + " " + reservation.lastName);
                  System.out.println("ROOM CODE: " + roomCodes[index]);
                  System.out.println("ROOM NAME: " + roomNames[index]);
                  System.out.println("BED TYPE: " + bedTypes[index]);
                  System.out.format("DATE: %s TO %s\n", reservation.startDate, reservation.endDate);
                  System.out.println("ADULTS: " + reservation.numAdults);
                  System.out.println("CHILDREN: " + reservation.numChildren);

                  int[] days = getWeekdaysBetweenTwoDates(reservation.startDate, reservation.endDate);
                  double cost = days[0] * basePrices[index];
                  cost += days[1] * basePrices[index] * 1.1;
                  cost *= 1.18;
                  System.out.format("TOTAL COST: %.2f\n", cost);
                  System.out.println("----------------------------------");
                  System.out.println("CONFIRM OR CANCEL: ");
                  String input = reader.nextLine();

                  if (input.equalsIgnoreCase("confirm")) {
                     List<Object> parameters = new ArrayList<>();
                     parameters.add(System.currentTimeMillis() / 1000);
                     parameters.add(roomCodes[index]);
                     parameters.add(reservation.startDate);
                     parameters.add(reservation.endDate);
                     parameters.add(basePrices[index]);
                     parameters.add(reservation.lastName);
                     parameters.add(reservation.firstName);
                     parameters.add(reservation.numAdults);
                     parameters.add(reservation.numChildren);

                     String query = "insert into lab7_reservations (code, room, checkin, checkout, rate, lastname, firstname, adults, kids) values ";
                     query += "(?, ?, ?, ?, ?, ?, ?, ?, ?)";

                     try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        int count = 1;
                        for (Object param : parameters) {
                           stmt.setObject(count++, param);
                        }
                        boolean exRes = stmt.execute();
                        if (exRes) {
                           System.out.println("ERROR BOOKING RESERVATION.");
                        }
                        else {
                           System.out.println("RESERVATION BOOKED SUCCESSFULLY.");
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static int[] getWeekdaysBetweenTwoDates(Date startDate, Date endDate) {
      Calendar startCal = Calendar.getInstance();
      startCal.setTime(startDate);
      int[] weekdaysWeekends = new int[2];

      Calendar endCal = Calendar.getInstance();
      endCal.setTime(endDate);

      int workDays = 0;

      if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
         int[] array = {0, 0};
         return array;
      }

      if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
         startCal.setTime(endDate);
         endCal.setTime(startDate);
      }

      do {
         if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            ++workDays;
         }
         startCal.add(Calendar.DAY_OF_MONTH, 1);
      } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date
      weekdaysWeekends[0] = workDays;
      weekdaysWeekends[1] = (int) getDifferenceDays(startDate, endDate) - workDays;
      return weekdaysWeekends;
   }

   private static long getDifferenceDays(Date d1, Date d2) {
      long diff = d2.getTime() - d1.getTime();
      return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
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

   public static void Revenue(String[] envVar) throws SQLException, IOException {
      String query = new String(Files.readAllBytes(Paths.get("lab7-revenue-query.txt")));

      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
              System.getenv("HP_JDBC_USER"),
              System.getenv("HP_JDBC_PW"))) {

         try (PreparedStatement statement = conn.prepareStatement(query)) {
            System.out.println("\n\tJan\tFeb\tMar\tApr\tMay\tJun\tJul\tAug\tSep\tOct\tNov\tDec\tYearTotal");
            try (ResultSet rs = statement.executeQuery()) {
               while (rs.next()) {
                  String room = rs.getString("room");
                  if (room == null) {
                     System.out.print("TOTAL");
                  }
                  else {
                     System.out.print(room);
                  }
                  System.out.print("\t" + rs.getInt("Jan"));
                  System.out.print("\t" + rs.getInt("Feb"));
                  System.out.print("\t" + rs.getInt("Mar"));
                  System.out.print("\t" + rs.getInt("Apr"));
                  System.out.print("\t" + rs.getInt("May"));
                  System.out.print("\t" + rs.getInt("Jun"));
                  System.out.print("\t" + rs.getInt("Jul"));
                  System.out.print("\t" + rs.getInt("Aug"));
                  System.out.print("\t" + rs.getInt("Sep"));
                  System.out.print("\t" + rs.getInt("Oct"));
                  System.out.print("\t" + rs.getInt("Nov"));
                  System.out.print("\t" + rs.getInt("Dec"));
                  System.out.println("\t" + rs.getInt("YearlyRevenue"));
                  System.out.println();
               }
            }
         }
      }
   }


   public static Date stringToSqlDate(String date) {
      return Date.valueOf(date);
   }

}

