import java.sql.Date;

public class Reservation {
    String firstName;
    String lastName;
    String roomCode;
    String bedType;
    Date startDate;
    Date endDate;
    int numChildren;
    int numAdults;

//    public Reservation(String fn, String ln, String rc, String bt, Date sd, Date ed, int nc, int na) {
//        firstName = fn;
//        lastName = ln;
//        roomCode = rc;
//        bedType = bt;
//        startDate = sd;
//        endDate = ed;
//        numChildren = nc;
//        numAdults = na;
//    }
    public String toString() {
        return String.format("%s %s %s %s %s %s %d %d", firstName, lastName, roomCode, bedType, startDate, endDate, numChildren, numAdults);
    }
}
