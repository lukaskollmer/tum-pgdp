/*
 * exercise-05/task-06
 *
 * Mensch ärgere dich nicht
 * */

import java.util.ArrayList;

public class Main_Task_06 extends Aerger {

    public static void main(String[] args) {
        // Draw the initial board configuration (all pawns at location -1)
        Game.shared.draw();

        Player currentPlayer = Game.shared.players.get(0);

        // Run forever until we exit the application
        while (true) {

            int randomNumber = MiniJava.dice();

            while (true) {
                // Ask which pawn should be moved, then check if that pawn number is valid
                int pawnNumber = MiniJava.readInt(String.format("[%s] which pawn would you like to move by %s", currentPlayer.color, randomNumber)) - 1;
                boolean isOutOfBounds = pawnNumber < 0 || pawnNumber > 3;


                // only proceed if the pawn number is valid
                // if the pawn number is not valid, the inner while loop runs again and we ask again
                // (i know that this approach with the two nested while loops is super disgusting and I hate myself for this)
                if (!isOutOfBounds) {
                    // Fetch the selected pawn
                    Pawn selectedPawn = currentPlayer.pawns.get(pawnNumber);

                    // Request the projected location (where the selected pawn would be if it were to be moved)
                    int projectedLocation = selectedPawn.projectedLocationAfterAdvancingBy(randomNumber);

                    // Check if there are other pawns at the projected location
                    // If there are other pawns, check if they belong to the current player
                    // special case: we always can move to >39 (garden)
                    boolean destinationCanBeMovedTo = projectedLocation > 39 || !Game.shared.pawnExistsAtLocation(projectedLocation);

                    Pawn potentialEnemyPawnAtProjectedLocation = null;
                    if (!destinationCanBeMovedTo) {
                        potentialEnemyPawnAtProjectedLocation = Game.shared.pawnAtLocation(projectedLocation);
                        if (potentialEnemyPawnAtProjectedLocation.player.equals(currentPlayer)) {
                            potentialEnemyPawnAtProjectedLocation = null;
                        }
                        destinationCanBeMovedTo = !Game.shared.pawnAtLocation(projectedLocation).player.equals(currentPlayer);
                    }

                    // make sure we don't move pawns that are already in the garden
                    boolean pawnCanBeMoved = !selectedPawn.isInGarden();

                    // commit the move if a) the selected pawn can be moved and b) it can be moved to the projected destination
                    if (pawnCanBeMoved && destinationCanBeMovedTo) {
                        // move enemy pawns out of the way, if necessary
                        if (potentialEnemyPawnAtProjectedLocation != null) {
                            potentialEnemyPawnAtProjectedLocation.moveTo(-1);
                        }
                        selectedPawn.advanceBy(randomNumber);

                        // break out of the inner while loop
                        break;
                    }
                }
            }

            // after every move, we look if the current player won the game
            if (currentPlayer.won()) {
                write(String.format("%s won the game", currentPlayer.color));
                System.exit(0);
            }

            // switch the player. this only works when 2 players are playing
            currentPlayer = Game.shared.players.get(1 - currentPlayer.id);
        }
    }


    // A Pawn represents a single movable object on the board.
    // Each player has 4 pawns and all pawns of a single player have the same color
    static class Pawn {

        int location;
        final Player player; // todo should this be a weak reference to avoid retain cycles? does java even have retain cycles?
        final private int offset;

        Pawn(Player player) {
            this.offset = player.id * 10;
            this.player = player;
        }

        // move the pawn to an absolute location. this also redraws the board
        void moveTo(int newLocation) {
            this.location = newLocation;

            Game.shared.draw();
        }

        // Move the pawn relative to its current location. this also redraws the board
        void advanceBy(int distance) {
            this.location = projectedLocationAfterAdvancingBy(distance);

            Game.shared.draw();
        }

        // The new location of the pawn after moving it by `distance`
        // Takes the player's offset into account
        int projectedLocationAfterAdvancingBy(int distance) {
            // create a copy of location bc this function is non-mutating
            int newLocation = location;

            // if the pawn isn't on the board yet, move it to its player's starting location
            if (newLocation == -1) {
                newLocation = this.player.id * 10;
            }

            // If the player started w/ an offset, we have to check for 2 edge cases:
            // 1. moving past 39
            // 2. moving into the garden (since the garden doesn't start after 39, but at offset -1)

            // 1. make sure we smoothly run over 39 if the player started w/ an offset
            if (offset > 0 && newLocation + distance > 39) {
                newLocation = ((newLocation + distance) % 39) - 1; // -1 because we have to account for the fact that the first field has index 0

            // 2. check if the pawn should be moved to the garden
            //
            } else if (newLocation < offset && newLocation + distance >= offset) { // >= bc the entry field is out of bounds
                //System.out.println("case 2");
                newLocation = 43;

            // This is a normal move
            } else if (newLocation <= 39) {
                newLocation += distance;
            } else {
                // if we reach here, the pawn is already in garden, we can safely ignore this
                // since we have another check before actually committing the move
            }


            return newLocation;
        }

        // Check if the pawn is already in the garden
        boolean isInGarden() {
            return location > 39;
        }

        // Get the color of the player associated with the pawn
        Player.Color getColor() {
            return this.player.color;
        }

        public String toString() {
            return String.format("<Pawn color=%s location=%s >", this.getColor(), this.location);
        }
    }

    // A Player represents one of 4 players that participate in the game.
    // The Player class is responsible for managing its pawns
    static class Player {
        enum Color { YELLOW, BLUE, RED, GREEN }

        int id;
        final Color color;
        final ArrayList<Pawn> pawns;

        Player(int playerId) {
            this.color = Color.values()[playerId];
            this.id = playerId;
            this.pawns = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                Pawn pawn = new Pawn(this);
                pawn.location = -1;
                pawns.add(pawn);
            }
        }

        // Check if all pawns are in the garden
        boolean won() {
            for (Pawn pawn : pawns) {
                if (pawn.location <= 39) {
                    return false;
                }
            }
            return true;
        }

        // Compare two players
        boolean equals(Player otherPlayer) {
            return this.id == otherPlayer.id;
        }
    }


    // `Game` is the class managing the board and all participating players
    // Since we can only play one game at a time, the `Game` initializer is private
    // and we use a singleton to access the single global `Game` instance
    static class Game {
        static final Game shared = new Game(2);

        final ArrayList<Player> players;
        private int numberOfPlayers;

        private Game(int numberOfPlayers) {
            if (numberOfPlayers != 1 && numberOfPlayers != 2) {
                System.out.println("Error: You can only play with 1 or 2 players");
                System.exit(1);
            }
            this.numberOfPlayers = numberOfPlayers;
            players = new ArrayList<>(numberOfPlayers);

            for (int i = 0; i < numberOfPlayers; i++) {
                players.add(new Player(i));
            }
        }

        // Check if a pawn exists at `location`
        boolean pawnExistsAtLocation(int location) {
            return pawnAtLocation(location) != null;
        }

        // Get the pawn at the specified location
        // Returns null if the location is empty
        Pawn pawnAtLocation(int location) {
            for (Player player : this.players) {
                for (Pawn pawn : player.pawns) {
                    if (pawn.location == location) {
                        return pawn;
                    }
                }
            }
            return null;
        }

        // Update the board to represent the current locations of the players and their pawns
        void draw() {
            int[][] pawnLocations = new int[2][4]; // 2 players with 4 pawns each

            for (int i = 0; i < numberOfPlayers; i++) {
                ArrayList<Pawn> pawns = this.players.get(i).pawns;
                for (int j = 0; j < pawns.size(); j++) {
                    pawnLocations[i][j] = pawns.get(j).location;
                }
            }
            Aerger.paintField(pawnLocations[0], pawnLocations[1]);
        }
    }
}