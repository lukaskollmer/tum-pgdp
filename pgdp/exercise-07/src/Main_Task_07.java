/*
* exercise-07/task-07
*
* helping penguins (facepalms to death)
* */


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Main_Task_07 {
    enum Direction { LEFT, RIGHT, UP, DOWN }


    static PenguinEnclosure enclosure;


    public static void main(String... args) {

        int width = MiniJava.readInt("width");
        int height = MiniJava.readInt("height");

        //enclosure = new PenguinEnclosure(-1256446734);
        enclosure = new PenguinEnclosure(width, height);
        enclosure.draw();


        handleUserInput();
    }

    private static void handleUserInput() {
        while(true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                /* Intentionally left blank */
            }
            int step = PenguinPen.nextStep();
            if (step != PenguinPen.NO_MOVE) {
                // System.out.print(step+",");
                move(step);
            }
        }
    }

    static void move(int direction) {
        move(Direction.values()[direction]);
    }

    static void move(Direction direction) {
        // Get the new location of the zookeeper and make sure he (or she) can actually move there
        // Return if not (eg wall)
        TileLocation newZookeeperLocation = enclosure.zookeeperPosition.advancedBy(1, direction);
        if (!enclosure.canMoveZookeeperToLocation(newZookeeperLocation)) {
            return;
        }

        System.out.format("==========\n");
        log("Spielschritt", enclosure.zookeeperPosition, newZookeeperLocation);

        // Check the extended neighboring fields for penguins and collect them
        for (TileLocation location : newZookeeperLocation.extendedNeighboringLocationsInEnclosure(enclosure)) {
            Penguin penguin = enclosure.penguinAtLocation(location);
            if (penguin == null) {
                continue;
            }

            System.out.format("Did collect %s\n", penguin.location);
            penguin.kill();
        }


        // Update the location of the zookeeper in the enclosure
        // It's important that we do this _before_ updating the locations of the individual penguins
        // because there are checks in the penguin location update code that need the correct location of the zookeeper
        enclosure.moveZookeeper(newZookeeperLocation);
        enclosure.draw();


        // Go through all penguins in the enclosure and tell them to flee, unless they're already caught
        // -[Penguin flee] automatically redraws the board, after a 0.1 second delay

        for (Penguin penguin : enclosure.penguins) {
            if (!penguin.isDestroyed) {
                penguin.flee();
            }
        }

        System.out.format("\n\n\n\n");
    }



    // Log a move event to stdout
    static void log(String event, TileLocation old, TileLocation new_) {
        System.out.format("%s ==> %s %s\n", old, new_, event);
    }








    //
    // TILE LOCATION
    //


    static class TileLocation {
        final int x;
        final int y;

        TileLocation(int x, int y) {
            this.x = x;
            this.y = y;
        }

        TileLocation advancedBy(int distance, Direction direction) {
            switch (direction) {
                case UP:
                    return new TileLocation(x, y - 1);
                case DOWN:
                    return new TileLocation(x, y + 1);
                case LEFT:
                    return new TileLocation(x - 1, y);
                case RIGHT:
                    return new TileLocation(x + 1, y);
            }

            // java sucks and that's why we still need a return statement, even though the switch above is exhaustive
            return null;
        }


        // List of all neighboring locations, excluding diagonal ones
        List<TileLocation> neighboringLocationsInEnclosure(PenguinEnclosure enclosure) {
            // create a list of all potential neighboring tiles
            List<TileLocation> neighboringLocations = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                neighboringLocations.add(this.advancedBy(1, direction));
            }

            // filter out all the ones that make no sense (wall, out of bounds, etc)
            return locationsFilteredToActuallyMakeSenseInTheCurrentEnclosure(neighboringLocations, enclosure);
        }


        // List of all neighboring locations, including diagonal ones
        List<TileLocation> extendedNeighboringLocationsInEnclosure(PenguinEnclosure enclosure) {
            List<TileLocation> neighboringLocations = this.neighboringLocationsInEnclosure(enclosure);
            neighboringLocations.add(this.advancedBy(1, Direction.UP).advancedBy(1, Direction.LEFT));
            neighboringLocations.add(this.advancedBy(1, Direction.UP).advancedBy(1, Direction.RIGHT));
            neighboringLocations.add(this.advancedBy(1, Direction.DOWN).advancedBy(1, Direction.LEFT));
            neighboringLocations.add(this.advancedBy(1, Direction.DOWN).advancedBy(1, Direction.RIGHT));

            return locationsFilteredToActuallyMakeSenseInTheCurrentEnclosure(neighboringLocations, enclosure);
        }


        private List<TileLocation> locationsFilteredToActuallyMakeSenseInTheCurrentEnclosure(List<TileLocation> locations, PenguinEnclosure enclosure) {
            return locations
                    .stream()
                    .filter(loc -> loc.isValidInEnclosure(enclosure) && enclosure.stateOfTileAtLocation(loc) != PenguinEnclosure.EnclosureTileState.WALL)
                    .collect(Collectors.toList());
        }


        // Check whether the location is valid within the bounds of the enclosure
        boolean isValidInEnclosure(PenguinEnclosure enclosure) {
            if (x < 0 || y < 0) {
                return false;
            }

            return (this.x + 1) <= enclosure.width && (this.y + 1) <= enclosure.height;
        }


        // Calculate the distance between two locations
        int distanceToLocation(TileLocation otherLocation) {
            return Math.abs(this.x - otherLocation.x) + Math.abs(this.y - otherLocation.y);
        }


        // Check whether two locations point to the same coordinated
        boolean equals(TileLocation otherLocation) {
            return this.x == otherLocation.x && this.y == otherLocation.y;
        }


        @Override
        public String toString() {
            return String.format("(%02d, %02d)", x, y);
        }
    }












    //
    // ENCLOSURE
    //



    // The PenguinEnclosure class manages the enclosure
    static class PenguinEnclosure {


        // The state of an individual tile on the board
        enum EnclosureTileState {
            WALL(-3),
            FREE(-2),
            OUTSIDE(-1),
            ZOOKEEPER(0),
            PENGUIN_OOO(1),
            PENGUIN_OOI(2),
            PENGUIN_OIO(3),
            PENGUIN_OII(4),
            PENGUIN_IOO(5);

            final int value;

            EnclosureTileState(int value) {
                this.value = value;
            }

            static EnclosureTileState fromRawValue(int rawValue) {
                switch (rawValue) {
                    case -3: return WALL;
                    case -2: return FREE;
                    case -1: return OUTSIDE;
                    case 0:  return ZOOKEEPER;
                    case 1:  return PENGUIN_OOO;
                    case 2:  return PENGUIN_OOI;
                    case 3:  return PENGUIN_OIO;
                    case 4:  return PENGUIN_OII;
                    case 5:  return PENGUIN_IOO;
                }
                return null;
            }

        }



        // backing array containing the raw data of the enclosure
        int[][] backing;

        // Size of the enclosure
        final int width;
        final int height;


        // All Penguins in the enclosure
        final List<Penguin> penguins = new ArrayList<>();

        // Current location of the zookeeper
        private TileLocation zookeeperPosition;


        PenguinEnclosure(int seed) {
            this(24, 17, seed);
        }


        PenguinEnclosure(int width, int height) {
            this(width, height, 0);
        }


        PenguinEnclosure(int width, int height, int seed) {
            this.width = width;
            this.height = height;
            this.backing = PenguinPen.generatePenguinPen(width, height, seed);

            this.zookeeperPosition = new TileLocation(1, 0);


            // Create objects for all penguins and store them in the `penguins` List
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    TileLocation location = new TileLocation(x, y);
                    EnclosureTileState state = stateOfTileAtLocation(location);
                    int val = state.value;
                    if (1 <= val && val <= 5) {
                        Penguin penguin = Penguin.ofKind(state, location, this);
                        this.penguins.add(penguin);
                    }
                }
            }
        }

        // Total number of fields in the enclosure (incl walls)
        int numberOfFields() {
            return width * height;
        }


        // Check whether the zookeeper can be moved to a specific location
        boolean canMoveZookeeperToLocation(TileLocation location) {
            return location.isValidInEnclosure(this) && stateOfTileAtLocation(location) != EnclosureTileState.WALL;
        }


        // Move the zookeeper to the specified location
        void moveZookeeper(TileLocation newPosition) {
            if (!canMoveZookeeperToLocation(newPosition)) {
                return;
            }

            // clear the old zookeeper position
            setAtPosition(zookeeperPosition, EnclosureTileState.FREE);

            // set the new zookeeper position
            setAtPosition(newPosition, EnclosureTileState.ZOOKEEPER);

            this.zookeeperPosition = newPosition;
        }


        // Update the value of the field at the specified location
        // this does not redraw the board
        // returns the old value
        private int setAtPosition(TileLocation position, EnclosureTileState state) {
            return setAtPosition(position, state.value);
        }


        // Update the value of the field at the specified location
        // this does not redraw the board
        // returns the old value
        private int setAtPosition(TileLocation position, int newValue) {
            int previousValue = getAtLocation(position);
            this.backing[position.x][position.y] = newValue;

            return previousValue;
        }

        // Get the value of the field at the specified location
        private int getAtLocation(TileLocation location) {
            return this.backing[location.x][location.y];
        }


        // Get the state of the field at the specified location
        EnclosureTileState stateOfTileAtLocation(TileLocation location) {
            return EnclosureTileState.fromRawValue(getAtLocation(location));
        }


        // returns the penguin at the specified location, null if there is no penguin at that location
        Penguin penguinAtLocation(TileLocation location) {
            for (Penguin penguin : penguins) {
                if (penguin.location.equals(location)) {
                    return penguin;
                }
            }

            return null;
        }


        // Redraw the board
        void draw() {
            PenguinPen.draw(this.backing);
        }
    }







    //
    // PENGUIN
    //

    abstract static class Penguin {
        TileLocation location;
        PenguinEnclosure enclosure;

        // Boolean indicating that the penguin has been collected
        private boolean isDestroyed = false;

        // Tell the penguin to flee
        // return value indicates whether the penguin actually moved location
        abstract boolean flee();

        // collect the penguin
        void kill() {
            enclosure.setAtPosition(location, PenguinEnclosure.EnclosureTileState.FREE);
            isDestroyed = true;
        }

        // walk `distance` steps in `direction`
        boolean walk(int distance, Direction direction) {
            return move(this.location.advancedBy(distance, direction));
        }


        // move to `newLocation`
        // returns true if the current penguin was able to move to the new location, false if it wasn't
        boolean move(TileLocation newLocation) {

            //check if the new location is within reach of the zookeeper and collect the penguin if necessary
            for (TileLocation neighboringLocation : newLocation.extendedNeighboringLocationsInEnclosure(enclosure)) {
                if (enclosure.zookeeperPosition.equals(neighboringLocation)) {
                    this.kill();
                    return true;
                }
            }

            PenguinEnclosure.EnclosureTileState stateAtNewLocation = enclosure.stateOfTileAtLocation(newLocation);


            // only proceed if the new location is a field that we can - in theory - move to
            if (stateAtNewLocation == PenguinEnclosure.EnclosureTileState.ZOOKEEPER ||
                stateAtNewLocation == PenguinEnclosure.EnclosureTileState.WALL) {
                return false;
            }


            // Check if there's already a penguin at the new location and tell it to go away if necessary

            Penguin penguin = enclosure.penguinAtLocation(newLocation);

            if (penguin != null) {
                // check whether the current penguin (`this`) is about to collide w/ the penguin on the field
                // we're trying to walk onto. This can happen if two penguins that are currently doing the right-hand-rule thing
                // are walking towards each other, (The problem being that neither of them can change direction)
                // We solve this by locking them at their current positions
                if (aboutToCollideWithPenguinOfSameKind(penguin)) {
                    ((Penguin_Wechsulin) this).isLockedInCurrentLocation = true;
                    return false;

                    // tell the penguin to flee and return false if it didn't
                } else if (!penguin.flee()) {
                    return false;
                }
            }


            // set the field at the current (old) location to FREE, but cache the previous value
            // this is necessary since the Penguin class is abstract and we don't really know
            // which exact kind of penguin we're dealing with here
            int self_type = this.enclosure.setAtPosition(this.location, PenguinEnclosure.EnclosureTileState.FREE);

            // cache the old location (will be used to log the move to stdout)
            TileLocation oldLocation = this.location;

            // update the location and set the destination field in the enclosure to the type of the penguin
            this.location = newLocation;
            this.enclosure.setAtPosition(this.location, self_type);

            // log the move
            log(String.format("-[%s flee]", this.getType()), oldLocation, location);


            // sleep for 0.1 seconds, then redraw the frame
            sleep(0.1);
            enclosure.draw();

            return true;
        }


        // check whether we're about to collide w/ another penguin of the same kind
        // (eg two of the ones that do the right hand rule thing and that have no other option than
        // to both walk onto the same next field)
        boolean aboutToCollideWithPenguinOfSameKind(Penguin otherPenguin) {

            // make sure that both `this` and `otherPenguin` are of the correct kind, return false otherwise

            boolean self_isWechsulin = this.getClass().equals(Penguin_Wechsulin.class);
            boolean other_isWechsulin = otherPenguin.getClass().equals(Penguin_Wechsulin.class);

            if (!(self_isWechsulin && other_isWechsulin)) {
                return false;
            }

            // Fetch some information about the current move, and return whether the penguins are about to collide

            Direction self_nextDirection = ((Penguin_Wechsulin)this).potentialNextDirection;
            Direction other_nextDirection = ((Penguin_Wechsulin)otherPenguin).potentialNextDirection;

            TileLocation self_location = ((Penguin_Wechsulin)this).location;
            TileLocation other_location = ((Penguin_Wechsulin)otherPenguin).location;



            if (self_nextDirection == null || other_nextDirection == null) {
                return false;
            }

            return self_location.advancedBy(1, self_nextDirection).equals(other_location) || other_location.advancedBy(1, other_nextDirection).equals(self_location);
        }

        // Get the type of the penguin
        String getType() {
            return this.getClass().getName().split("_")[3];
        }


        @Override
        public String toString() {
            return String.format("<Penguin type=%s location=%s>", getType(), location);
        }


        // Get an object representing the penguin at a specific location in the enclosure
        static Penguin ofKind(PenguinEnclosure.EnclosureTileState kind, TileLocation location, PenguinEnclosure enclosure) {
            Penguin penguin;
            switch (kind) {
                case PENGUIN_OOO: penguin = new Penguin_Fauluin(); break;
                case PENGUIN_OOI: penguin = new Penguin_Zufullin(); break;
                case PENGUIN_OIO: penguin = new Penguin_Wechsulin(); break;
                case PENGUIN_OII: penguin = new Penguin_Springuin(); break;
                case PENGUIN_IOO: penguin = new Penguin_Schlauin(); break;
                default: return null;
            }

            penguin.location = location;
            penguin.enclosure = enclosure;

            return penguin;
        }
    }


    // PENGUIN IMPLEMENTATIONS

    static class Penguin_Fauluin extends Penguin {
        @Override
        boolean flee() {
            return false;
        }
    }


    static class Penguin_Zufullin extends Penguin {
        @Override
        boolean flee() {
            // Make sure we don't try the same direction multiple times
            Set<Direction> directionsWeAlreadyTried = new HashSet<>();

            while (directionsWeAlreadyTried.size() < Direction.values().length) {
                Direction randomDirection = Direction.values()[random(3)];

                if (directionsWeAlreadyTried.contains(randomDirection)) {
                    continue;
                }
                directionsWeAlreadyTried.add(randomDirection);

                TileLocation newLocation = location.advancedBy(1, randomDirection);
                if (!newLocation.isValidInEnclosure(enclosure)) {
                    continue;
                }

                // try moving the penguin to the new location
                if (move(newLocation)) {
                    return true;
                }
            }

            return false;
        }
    }


    static class Penguin_Wechsulin extends Penguin {
        private boolean isStillRunningToTheRightUntilWeHitAWall = true;
        Direction potentialNextDirection;
        private Direction lastDirection = Direction.RIGHT;
        boolean isLockedInCurrentLocation = false;

        @Override
        boolean flee() {
            // don't even try moving the penguin if it's locked
            // the reason why we sometimes have to lock these penguins sometimes is explained somewhere above
            if (isLockedInCurrentLocation) {
                return false;
            }


            if (isStillRunningToTheRightUntilWeHitAWall) {
                TileLocation newLocation = location.advancedBy(1, Direction.RIGHT);

                PenguinEnclosure.EnclosureTileState state = enclosure.stateOfTileAtLocation(newLocation);

                if (state == PenguinEnclosure.EnclosureTileState.WALL) {
                    // we finally hit a wall, now we turn left and do the whole right-hand-rule thing
                    isStillRunningToTheRightUntilWeHitAWall = false;
                    lastDirection = Direction.LEFT;
                    return walk_rightHandRule_thisIsTheRealOne();
                } else {
                    // we still didn't reach a wall, so we just move one to the right
                    return move(newLocation);
                }

            } else { // isStillRunningToTheRightUntilWeHitAWall == false
                return walk_rightHandRule_thisIsTheRealOne();
            }
        }



        // move forward following the right-hand-rule, one step at a time
        boolean walk_rightHandRule_thisIsTheRealOne() {
            for (Direction direction : directionOrder.get(lastDirection)) {
                TileLocation nextLocation = location.advancedBy(1, direction);
                if (!nextLocation.isValidInEnclosure(enclosure)) {
                    continue;
                }

                // this is important for collision detection
                potentialNextDirection = direction;

                // only move in that direction if there's still a wall at the expected side
                // we need to run this check to avoid an edge case where the penguin would walk towards
                // the middle of the enclosure, away from the wall
                if (enclosure.stateOfTileAtLocation(nextLocation.advancedBy(1, directionInWhichAWallIsToBeExpected.get(direction))) != PenguinEnclosure.EnclosureTileState.WALL) {
                    return false;
                }

                if (move(nextLocation)) {
                    lastDirection = direction;
                    return true;
                }
            }
            return false;
        }
    }


    static class Penguin_Springuin extends Penguin {
        @Override
        boolean flee() {
            // keep track of the locations we already tried
            // (this could fail for multiple reasons, like walls, the zookeeper or penguins that have nowhere else to go)
            Set<TileLocation> unsuccessfulDestinations = new HashSet<>();

            while (unsuccessfulDestinations.size() < enclosure.numberOfFields()) {

                // Get a random row & column, check if that field is free and jump there if possible
                int row = random(enclosure.height - 1);
                int column = random(enclosure.width - 1);

                TileLocation location = new TileLocation(row, column);

                if (unsuccessfulDestinations.contains(location)) {
                    continue;
                }

                if (location.isValidInEnclosure(enclosure) && enclosure.stateOfTileAtLocation(location) == PenguinEnclosure.EnclosureTileState.FREE) {
                    return move(location);
                }

                unsuccessfulDestinations.add(location);
            }

            return false;
        }
    }


    static class Penguin_Schlauin extends Penguin {
        boolean isFleeing;

        @Override
        boolean flee() {
            // only proceed if we aren't already fleeing. this check is important to prevent recursions
            if (isFleeing) {
                return false;
            }

            isFleeing = true;

            // sort all neighboring tiles by distance to the zookeeper
            List<TileLocation> neighboringFieldsSortedByDistanceToZookeeper = this.location.neighboringLocationsInEnclosure(enclosure)
                    .stream()
                    .sorted((loc1, loc2) -> loc2.distanceToLocation(enclosure.zookeeperPosition) - loc1.distanceToLocation(enclosure.zookeeperPosition))
                    .collect(Collectors.toList());



            // go through the possible destinations and try moving to them
            for (TileLocation location : neighboringFieldsSortedByDistanceToZookeeper) {
                if (location.distanceToLocation(enclosure.zookeeperPosition) > this.location.distanceToLocation(enclosure.zookeeperPosition)) {
                    boolean success = move(location);
                    if (success) {
                        isFleeing = false;
                        return true;
                    }
                }
            }

            isFleeing = false;
            return false;
        }
    }

    // helper method to log the current class and method, following the objc conventions
    static void logm() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        StackTraceElement calling = trace[2];

        System.out.format("-[%s %s]\n", calling.getClassName(), calling.getMethodName());
    }


    // helper method to pause execution for the specified amount of time
    static void sleep(double duration) {
        try {
            Thread.sleep((long) (duration * (double)1000));
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }


    static int random(int max) {
        return random(0, max);
    }

    static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }




    // Direction order for right-hand-rule maze solving
    // the key is the last direction, and the values list the order in which we need to proceed
    static HashMap<Direction, List<Direction>> directionOrder;

    // other stuff
    static HashMap<Direction, Direction> directionInWhichAWallIsToBeExpected;

    static {
        directionOrder = new HashMap<>();

        directionOrder.put(Direction.DOWN,  Arrays.asList(Direction.LEFT,  Direction.DOWN,  Direction.RIGHT, Direction.UP   ));
        directionOrder.put(Direction.UP,    Arrays.asList(Direction.RIGHT, Direction.UP,    Direction.LEFT,  Direction.DOWN ));
        directionOrder.put(Direction.LEFT,  Arrays.asList(Direction.UP,    Direction.LEFT,  Direction.DOWN,  Direction.RIGHT));
        directionOrder.put(Direction.RIGHT, Arrays.asList(Direction.DOWN,  Direction.RIGHT, Direction.UP,    Direction.LEFT ));


        directionInWhichAWallIsToBeExpected = new HashMap<>();
        directionInWhichAWallIsToBeExpected.put(Direction.RIGHT, Direction.DOWN);
        directionInWhichAWallIsToBeExpected.put(Direction.LEFT, Direction.UP);
        directionInWhichAWallIsToBeExpected.put(Direction.UP, Direction.RIGHT);
        directionInWhichAWallIsToBeExpected.put(Direction.DOWN, Direction.LEFT);
    }
}
