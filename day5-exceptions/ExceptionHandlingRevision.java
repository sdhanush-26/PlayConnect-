/*
 * Day 5 — Exception Handling
 * Demonstrates: try/catch/finally, throw, throws, custom exceptions
 *
 * These three custom exceptions are the exact ones called out in the
 * 60-day plan and will be reused for real in the Spring Boot service
 * layer starting Day 11, then wired into @RestControllerAdvice on Day 14.
 *
 * Run with: javac ExceptionHandlingRevision.java && java ExceptionHandlingRevision
 */

import java.util.*;

// ---------- CUSTOM EXCEPTIONS ----------
// Unchecked (extends RuntimeException) so service methods don't need
// "throws" on every signature — this matches how Spring apps typically
// handle domain exceptions, letting @RestControllerAdvice catch them globally.

class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String playerId) {
        super("Player not found with id: " + playerId);
    }
}

class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(String matchId) {
        super("Match not found with id: " + matchId);
    }
}

class InvalidMatchException extends RuntimeException {
    public InvalidMatchException(String reason) {
        super("Invalid match: " + reason);
    }
}

// ---------- SIMPLE DOMAIN CLASSES ----------
class Player {
    String id;
    String name;
    Player(String id, String name) { this.id = id; this.name = name; }
}

class Match {
    String id;
    String title;
    int maxPlayers;
    int currentPlayers;
    Match(String id, String title, int maxPlayers, int currentPlayers) {
        this.id = id;
        this.title = title;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
    }
}

// ---------- SERVICE-LIKE LOGIC THAT THROWS THESE EXCEPTIONS ----------
class PlayerService {
    private Map<String, Player> players = new HashMap<>();

    public PlayerService() {
        players.put("p1", new Player("p1", "Dhanush"));
        players.put("p2", new Player("p2", "Ravi"));
    }

    // "throws" isn't required here since PlayerNotFoundException is
    // unchecked, but callers still need to know it CAN be thrown —
    // that's documented via the exception itself, not the signature.
    public Player getPlayer(String id) {
        Player player = players.get(id);
        if (player == null) {
            throw new PlayerNotFoundException(id);
        }
        return player;
    }
}

class MatchService {
    private Map<String, Match> matches = new HashMap<>();

    public MatchService() {
        matches.put("m1", new Match("m1", "Sunday Cricket", 11, 11)); // full
        matches.put("m2", new Match("m2", "Evening Badminton", 4, 2));
    }

    public Match getMatch(String id) {
        Match match = matches.get(id);
        if (match == null) {
            throw new MatchNotFoundException(id);
        }
        return match;
    }

    public void joinMatch(String matchId) {
        Match match = getMatch(matchId); // can bubble up MatchNotFoundException
        if (match.currentPlayers >= match.maxPlayers) {
            throw new InvalidMatchException("match \"" + match.title + "\" is already full");
        }
        match.currentPlayers++;
        System.out.println("Joined match: " + match.title
                + " (" + match.currentPlayers + "/" + match.maxPlayers + ")");
    }
}

public class ExceptionHandlingRevision {
    public static void main(String[] args) {
        PlayerService playerService = new PlayerService();
        MatchService matchService = new MatchService();

        // ---------- try/catch: player lookup that succeeds ----------
        System.out.println("--- Looking up an existing player ---");
        try {
            Player p = playerService.getPlayer("p1");
            System.out.println("Found: " + p.name);
        } catch (PlayerNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("(lookup attempt finished)");
        }

        // ---------- try/catch: player lookup that fails ----------
        System.out.println("\n--- Looking up a missing player ---");
        try {
            Player p = playerService.getPlayer("p99");
            System.out.println("Found: " + p.name); // never reached
        } catch (PlayerNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("(lookup attempt finished)");
        }

        // ---------- joining a match that succeeds ----------
        System.out.println("\n--- Joining an open match ---");
        try {
            matchService.joinMatch("m2");
        } catch (MatchNotFoundException | InvalidMatchException e) {
            // multi-catch: handle either exception type the same way
            System.out.println("Error: " + e.getMessage());
        }

        // ---------- joining a full match ----------
        System.out.println("\n--- Joining a full match ---");
        try {
            matchService.joinMatch("m1");
        } catch (MatchNotFoundException | InvalidMatchException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ---------- joining a match that doesn't exist ----------
        System.out.println("\n--- Joining a nonexistent match ---");
        try {
            matchService.joinMatch("m404");
        } catch (MatchNotFoundException | InvalidMatchException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

/*
 * Expected output:
 *
 * --- Looking up an existing player ---
 * Found: Dhanush
 * (lookup attempt finished)
 *
 * --- Looking up a missing player ---
 * Error: Player not found with id: p99
 * (lookup attempt finished)
 *
 * --- Joining an open match ---
 * Joined match: Evening Badminton (3/4)
 *
 * --- Joining a full match ---
 * Error: Invalid match: match "Sunday Cricket" is already full
 *
 * --- Joining a nonexistent match ---
 * Error: Match not found with id: m404
 */
