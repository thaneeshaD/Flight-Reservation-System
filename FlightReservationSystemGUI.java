package tickettt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** ---------- Flight Class ---------- **/
class Flight {
    String id, source, destination;
    LocalDate date;
    double price;
    int availableSeats;

    Flight(String id, String source, String destination, LocalDate date, double price, int seats) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.price = price;
        this.availableSeats = seats;
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-10s -> %-12s | %s | ₹%.2f | Seats: %d",
                id, source, destination, date, price, availableSeats);
    }
}

/** ---------- Booking Class ---------- **/
class Booking {
    String bookingId, passengerName, flightId;
    int seatsBooked;
    LocalDateTime createdAt;

    Booking(String bookingId, String passengerName, String flightId, int seatsBooked) {
        this.bookingId = bookingId;
        this.passengerName = passengerName;
        this.flightId = flightId;
        this.seatsBooked = seatsBooked;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-15s | Flight: %-6s | Seats: %-3d | %s",
                bookingId, passengerName, flightId, seatsBooked, createdAt);
    }
}

/** ---------- Flight Database ---------- **/
class FlightDB {
    private static final List<Flight> flights = new ArrayList<>();

    static void seed() {
        if (!flights.isEmpty()) return;
        flights.add(new Flight("AI101", "Bengaluru", "Mumbai", LocalDate.now().plusDays(1), 5499, 10));
        flights.add(new Flight("AI102", "Bengaluru", "Delhi", LocalDate.now().plusDays(2), 6999, 6));
        flights.add(new Flight("6E301", "Bengaluru", "Mumbai", LocalDate.now().plusDays(1), 4799, 4));
        flights.add(new Flight("6E302", "Bengaluru", "Kolkata", LocalDate.now().plusDays(3), 6299, 8));
        flights.add(new Flight("SG501", "Mumbai", "Bengaluru", LocalDate.now().plusDays(1), 5599, 7));
        flights.add(new Flight("VJ777", "Delhi", "Bengaluru", LocalDate.now().plusDays(2), 6599, 5));
    }

    static List<Flight> search(String src, String dst, LocalDate date) {
        List<Flight> res = new ArrayList<>();
        for (Flight f : flights) {
            if (f.source.equalsIgnoreCase(src.trim()) &&
                f.destination.equalsIgnoreCase(dst.trim()) &&
                f.date.equals(date)) {
                res.add(f);
            }
        }
        return res;
    }

    static Flight findById(String id) {
        for (Flight f : flights) {
            if (f.id.equalsIgnoreCase(id.trim())) return f;
        }
        return null;
    }

    static List<Flight> all() {
        return Collections.unmodifiableList(flights);
    }
}

/** ---------- Booking Database ---------- **/
class BookingDB {
    private static final Map<String, Booking> bookings = new LinkedHashMap<>();
    private static int counter = 1000;

    static String nextId() {
        counter++;
        return "B" + counter; // e.g., B1001
    }

    static void add(Booking b) {
        bookings.put(b.bookingId, b);
    }

    static Booking get(String bookingId) {
        return bookings.get(bookingId);
    }

    static Booking remove(String bookingId) {
        return bookings.remove(bookingId);
    }

    static Collection<Booking> all() {
        return bookings.values();
    }
}

/** ---------- Input Helper ---------- **/
class IO {
    static final Scanner sc = new Scanner(System.in);
    static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    static LocalDate readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (dd-MM-yyyy): ");
                return LocalDate.parse(sc.nextLine().trim(), DTF);
            } catch (Exception e) {
                System.out.println("Invalid date format. Try again.");
            }
        }
    }
}

/** ---------- Service Layer ---------- **/
class ReservationService {

    static void searchFlights() {
        String src = IO.readLine("Enter Source City: ");
        String dst = IO.readLine("Enter Destination City: ");
        LocalDate date = IO.readDate("Enter Travel Date");

        List<Flight> found = FlightDB.search(src, dst, date);
        if (found.isEmpty()) {
            System.out.println("\nNo flights found.\n");
            return;
        }
        System.out.println("\nAvailable Flights:");
        found.forEach(System.out::println);
        System.out.println();
    }

    static void bookTicket() {
        String passenger = IO.readLine("Passenger Name: ");
        String flightId = IO.readLine("Enter Flight ID: ");
        Flight f = FlightDB.findById(flightId);

        if (f == null) {
            System.out.println("Flight not found.\n");
            return;
        }

        System.out.println("Selected: " + f);
        int seats = IO.readInt("Seats to book: ");
        if (seats <= 0 || seats > f.availableSeats) {
            System.out.println("Invalid seat count.\n");
            return;
        }

        f.availableSeats -= seats;
        String bookingId = BookingDB.nextId();
        Booking b = new Booking(bookingId, passenger, f.id, seats);
        BookingDB.add(b);

        System.out.printf("\nBooking Confirmed! ID: %s | Total: ₹%.2f%n%n",
                bookingId, f.price * seats);
    }

    static void cancelBooking() {
        String id = IO.readLine("Enter Booking ID: ");
        Booking b = BookingDB.get(id);
        if (b == null) {
            System.out.println("Booking not found.\n");
            return;
        }
        Flight f = FlightDB.findById(b.flightId);
        if (f != null) f.availableSeats += b.seatsBooked;
        BookingDB.remove(id);
        System.out.println("Booking cancelled.\n");
    }

    static void viewAllBookings() {
        if (BookingDB.all().isEmpty()) {
            System.out.println("No bookings yet.\n");
            return;
        }
        BookingDB.all().forEach(System.out::println);
        System.out.println();
    }

    static void listAllFlights() {
        FlightDB.all().forEach(System.out::println);
        System.out.println();
    }
}

/** ---------- Main Menu ---------- **/
public class FlightReservationSystem {
    public static void main(String[] args) {
        FlightDB.seed();
        while (true) {
            System.out.println("==== Flight Reservation System ====");
            System.out.println("1. Search Flights");
            System.out.println("2. Book Ticket");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View All Bookings");
            System.out.println("5. List All Flights");
            System.out.println("0. Exit");

            int choice = IO.readInt("Choose an option: ");
            System.out.println();

            switch (choice) {
                case 1 -> ReservationService.searchFlights();
                case 2 -> ReservationService.bookTicket();
                case 3 -> ReservationService.cancelBooking();
                case 4 -> ReservationService.viewAllBookings();
                case 5 -> ReservationService.listAllFlights();
                case 0 -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid option.\n");
            }
        }
    }
}
