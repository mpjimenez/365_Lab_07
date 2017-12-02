# 365_Lab_07

TEAM: Derek Lance and Matt Jimenez
DATABASE: dlance (contains lab7-specific INN tables)

To compile and run:

1. run the script: ./jdbc.sh
this script compiles the program automatically and runs the program with
the correct environment variables set for Derek Lance's database
(requires the jar file to be in the same directory)

OR

2. compile using: javac InnReservations.java Reservation.java
make sure correct environment variables are set:
    HP_JDBC_URL for the url
    HP_JDBC_USER for the username
    HP_JDBC_PW for the password
run the program: java -cp <path to jar file>:. InnReservations

No known bugs.

Deficiencies:
- Functional Requirement: Reservations
    - if the program does not find any exact matches from the database,
        nothing is displayed. We did not implement the "suggest 5 possibilities" functionality based on room similarity.
    - not a deficiency but for clarity: the checkout date is not included as  a day when calculating rates
    for example, a reservation of 2017-01-01 to 2017-01-02 at a nightly rate
    of $175 will be calculated as 175 * 1.18 tax
