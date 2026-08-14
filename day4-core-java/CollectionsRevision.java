/*
 * Day 4 — Java Collections
 * Demonstrates: ArrayList, HashMap, HashSet, Queue, Stack, Stream API
 *
 * Themed around PlayConnect player data — the same kind of
 * operations you'll need for the Player Search and Match APIs later.
 *
 * Run with: javac CollectionsRevision.java && java CollectionsRevision
 */

import java.util.*;
import java.util.stream.*;

// Simple data holder for these examples
class Player {
    String id;
    String name;
    String sport;
    String skillLevel;
    double distanceKm;

    public Player(String id, String name, String sport, String skillLevel, double distanceKm) {
        this.id = id;
        this.name = name;
        this.sport = sport;
        this.skillLevel = skillLevel;
        this.distanceKm = distanceKm;
    }

    @Override
    public String toString() {
        return name + " (" + sport + ", " + skillLevel + ", " + distanceKm + "km)";
    }
}

public class CollectionsRevision {
    public static void main(String[] args) {

        // ---------- ArrayList ----------
        // Ordered, allows duplicates. Good for "players who joined this match".
        List<Player> players = new ArrayList<>();
        players.add(new Player("p1", "Dhanush", "Cricket", "INTERMEDIATE", 2.3));
        players.add(new Player("p2", "Ravi", "Cricket", "ADVANCED", 4.7));
        players.add(new Player("p3", "Kiran", "Badminton", "BEGINNER", 1.1));
        players.add(new Player("p4", "Arjun", "Cricket", "PRO", 8.1));
        players.add(new Player("p5", "Meena", "Football", "INTERMEDIATE", 3.4));

        System.out.println("--- ArrayList: all players ---");
        players.forEach(System.out::println);

        // ---------- HashMap ----------
        // Fast lookup by ID — like how you'd fetch a player by ID in the service layer.
        Map<String, Player> playerById = new HashMap<>();
        for (Player p : players) {
            playerById.put(p.id, p);
        }
        System.out.println("\n--- HashMap: lookup by ID 'p3' ---");
        System.out.println(playerById.get("p3"));

        // ---------- HashSet ----------
        // Unique sports played across all players — no duplicates.
        Set<String> uniqueSports = new HashSet<>();
        for (Player p : players) {
            uniqueSports.add(p.sport);
        }
        System.out.println("\n--- HashSet: unique sports offered ---");
        System.out.println(uniqueSports);

        // ---------- Queue ----------
        // FIFO waitlist — first player to join the waitlist is first offered a spot.
        Queue<Player> waitlist = new LinkedList<>();
        waitlist.offer(new Player("p6", "Sana", "Cricket", "INTERMEDIATE", 5.0));
        waitlist.offer(new Player("p7", "Vikram", "Cricket", "BEGINNER", 6.2));
        System.out.println("\n--- Queue: waitlist processing order ---");
        while (!waitlist.isEmpty()) {
            System.out.println("Offering spot to: " + waitlist.poll());
        }

        // ---------- Stack ----------
        // LIFO — e.g. an "undo last action" history for match edits.
        Deque<String> actionHistory = new ArrayDeque<>();
        actionHistory.push("Created match");
        actionHistory.push("Added player Dhanush");
        actionHistory.push("Changed max players to 11");
        System.out.println("\n--- Stack: undoing last 2 actions ---");
        System.out.println("Undo: " + actionHistory.pop());
        System.out.println("Undo: " + actionHistory.pop());

        // ---------- Stream API ----------
        System.out.println("\n--- Stream: Cricket players sorted by distance ---");
        List<String> nearestCricketPlayers = players.stream()
                .filter(p -> p.sport.equals("Cricket"))
                .sorted(Comparator.comparingDouble(p -> p.distanceKm))
                .map(p -> p.name + " (" + p.distanceKm + "km)")
                .collect(Collectors.toList());
        nearestCricketPlayers.forEach(System.out::println);

        System.out.println("\n--- Stream: count players per sport ---");
        Map<String, Long> countBySport = players.stream()
                .collect(Collectors.groupingBy(p -> p.sport, Collectors.counting()));
        System.out.println(countBySport);

        System.out.println("\n--- Stream: skill levels present (distinct) ---");
        players.stream()
                .map(p -> p.skillLevel)
                .distinct()
                .forEach(System.out::println);
    }
}

/*
 * Expected output (order of HashMap/HashSet/groupingBy entries may vary
 * slightly since they're unordered collections — that's normal and expected):
 *
 * --- ArrayList: all players ---
 * Dhanush (Cricket, INTERMEDIATE, 2.3km)
 * Ravi (Cricket, ADVANCED, 4.7km)
 * Kiran (Badminton, BEGINNER, 1.1km)
 * Arjun (Cricket, PRO, 8.1km)
 * Meena (Football, INTERMEDIATE, 3.4km)
 *
 * --- HashMap: lookup by ID 'p3' ---
 * Kiran (Badminton, BEGINNER, 1.1km)
 *
 * --- HashSet: unique sports offered ---
 * [Cricket, Badminton, Football]   (order not guaranteed)
 *
 * --- Queue: waitlist processing order ---
 * Offering spot to: Sana (Cricket, INTERMEDIATE, 5.0km)
 * Offering spot to: Vikram (Cricket, BEGINNER, 6.2km)
 *
 * --- Stack: undoing last 2 actions ---
 * Undo: Changed max players to 11
 * Undo: Added player Dhanush
 *
 * --- Stream: Cricket players sorted by distance ---
 * Dhanush (2.3km)
 * Ravi (4.7km)
 * Arjun (8.1km)
 *
 * --- Stream: count players per sport ---
 * {Football=1, Cricket=3, Badminton=1}   (order not guaranteed)
 *
 * --- Stream: skill levels present (distinct) ---
 * INTERMEDIATE
 * ADVANCED
 * BEGINNER
 * PRO
 */
