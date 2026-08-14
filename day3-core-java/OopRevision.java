/*
 * Day 3 — Core Java Revision
 * Demonstrates: classes, objects, constructors, encapsulation,
 * inheritance, polymorphism, abstraction, interfaces.
 *
 * Themed around the PlayConnect domain (Player, Sport, Match)
 * so the concepts map directly onto what we'll build later.
 *
 * Run with: javac OopRevision.java && java OopRevision
 */

// ---------- INTERFACE (Abstraction + contract) ----------
interface Notifiable {
    void notify(String message);
}

// ---------- BASE CLASS (Encapsulation + Constructor) ----------
class User implements Notifiable {
    // private fields -> encapsulation. Nothing outside this class
    // can touch them directly.
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // controlled access via getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Polymorphism target: subclasses will override this
    @Override
    public void notify(String message) {
        System.out.println("[User] " + name + " received: " + message);
    }

    public String describe() {
        return "User: " + name;
    }
}

// ---------- INHERITANCE ----------
// Player IS-A User, but adds sport-specific fields and behavior.
class Player extends User {
    private String sport;
    private String skillLevel;

    public Player(String name, String email, String sport, String skillLevel) {
        super(name, email); // calls User's constructor
        this.sport = sport;
        this.skillLevel = skillLevel;
    }

    public String getSport() {
        return sport;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    // POLYMORPHISM: Player overrides notify() with its own behavior,
    // even though it's called the same way as User.notify()
    @Override
    public void notify(String message) {
        System.out.println("[Player] " + getName() + " (" + sport + ", "
                + skillLevel + ") received: " + message);
    }

    @Override
    public String describe() {
        return "Player: " + getName() + " plays " + sport + " at " + skillLevel + " level";
    }
}

// ---------- ANOTHER CLASS IMPLEMENTING THE SAME INTERFACE ----------
// Match is unrelated to User, but can still be "Notifiable" -
// this is what makes interfaces powerful: unrelated classes,
// shared contract.
class Match implements Notifiable {
    private String title;
    private int maxPlayers;

    public Match(String title, int maxPlayers) {
        this.title = title;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void notify(String message) {
        System.out.println("[Match] \"" + title + "\" update: " + message);
    }

    public String describe() {
        return "Match: " + title + " (max " + maxPlayers + " players)";
    }
}

// ---------- MAIN: puts it all together ----------
public class OopRevision {
    public static void main(String[] args) {

        // Objects created from classes
        User genericUser = new User("Ravi", "ravi@example.com");
        Player player = new Player("Dhanush", "dhanush@example.com", "Cricket", "INTERMEDIATE");
        Match match = new Match("Sunday Cricket", 11);

        System.out.println(genericUser.describe());
        System.out.println(player.describe());
        System.out.println(match.describe());

        System.out.println("\n--- Polymorphism in action ---");
        // Same interface type, different runtime behavior
        Notifiable[] notifiables = { genericUser, player, match };
        for (Notifiable n : notifiables) {
            n.notify("Match starts in 30 minutes!");
        }

        System.out.println("\n--- Encapsulation check ---");
        // player.name would NOT compile - name is private.
        // We must go through the public getter:
        System.out.println("Accessing name via getter: " + player.getName());
    }
}

/*
 * Expected output:
 *
 * User: Ravi
 * Player: Dhanush plays Cricket at INTERMEDIATE level
 * Match: Sunday Cricket (max 11 players)
 *
 * --- Polymorphism in action ---
 * [User] Ravi received: Match starts in 30 minutes!
 * [Player] Dhanush (Cricket, INTERMEDIATE) received: Match starts in 30 minutes!
 * [Match] "Sunday Cricket" update: Match starts in 30 minutes!
 *
 * --- Encapsulation check ---
 * Accessing name via getter: Dhanush
 */
