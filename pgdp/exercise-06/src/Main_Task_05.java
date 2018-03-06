/*
 * exercise-06/task-05
 *
 * Finding penguins in a maze
 * */

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Main_Task_05 extends Maze {
    public static void main(String[] args) {
        MazeManager maze = new MazeManager(14, 14);
        maze.draw();

        Tile start = maze.tileAtLocation(new TileLocation(0, 1));
        start.setState(Tile.State.PLAYER);

        int numberOfPenguins = walk(start,15);
        MiniJava.write(String.format("Found %s penguin%s!", numberOfPenguins, numberOfPenguins == 1 ? "" : "s"));

    }


    // This is just the entry point for the real walk function
    // Returns the number of found penguins
    static int walk(Tile start, int maxDistance) {
        return walk(start, maxDistance, new PathStep(start.location, null), 0);
    }


    /*
     * Walk recursively through the maze and collect all penguins
     * Returns the number of penguins found withing the specified maximum distance
     *
     * How does this actually work?
     *
     * - the first argument (currentTile) is the tile we just walked to (the one marked as PLAYER)
     * - `maxDistance` specifies the maximum number of steps we are allowed to walk from the starting point
     * - `lastStep` contains information about the previous step we walked before the current one.
     *      Each `PathStep` object holds a reference to its parent (which is the step before that one), which holds a reference to its parent, and so on
     *      This not only allows us to reconstruct the entire path all the way back to the starting point,
     *      but it also allows us - if we ended up in a dead end - to walk back the path until we find somewhere else to branch off into
     *      we also use the length of the reconstructed path to ensure that we remain withing the maximum distance from the starting point
    */
    static int walk(Tile currentTile, int maxDistance, PathStep lastStep, int numberOfFoundPenguins) {

        Tile previousTile = null;
        if (lastStep.parent != null) {
            previousTile = currentTile.maze.tileAtLocation(lastStep.parent.location);
        }

        // Check if there is a penguin and collect it if necessary
        if (currentTile.getState() == Tile.State.PENGUIN) {
            numberOfFoundPenguins++;
        }

        currentTile.setState(Tile.State.PLAYER);
        if (previousTile != null && previousTile.getState() != Tile.State.OLD_PATH_DONE){
            previousTile.setState(Tile.State.OLD_PATH_ACTIVE);
        }



        // Get all neighboring tiles we ca potentially move to
        // This means we filter out tiles we already went to, walls,
        // as well as tiles where none of the 8 surrounding tiles are walls

        List<Tile> potentialNextStepDestinationTiles = currentTile.neighboringTiles()
                .stream()
                // filter the tiles to exclude everything we can't walk to (wall, old path, etc)
                .filter(t -> t.getState() == Tile.State.PENGUIN || t.getState() == Tile.State.FREE)
                // filter out all tiles where none of the 8 surrounding tiles are walls
                .filter(t -> t.extendedNeighboringTiles().stream().filter(t2 -> t2.getState() != Tile.State.WALL).collect(Collectors.toList()).size() != 8)
                .collect(Collectors.toList());

        // only walk to the next tile if
        // a) it exists, and
        // b) we're still below the maximum distance
        //   (we can check that by restoring the path from the current tile back to the starting point)
        if (!potentialNextStepDestinationTiles.isEmpty() && lastStep.path().size() < maxDistance) {
            Tile nextTile = potentialNextStepDestinationTiles.get(0);
            currentTile.setState(Tile.State.OLD_PATH_ACTIVE);
            return walk(nextTile, maxDistance, new PathStep(nextTile.location, lastStep), numberOfFoundPenguins);
        } else {
            // no more options are available, we now have to go back until we find a free tile somewhere along the way

            if (previousTile == null && currentTile.location.row == 0 && currentTile.location.column == 1) {
                // we're back at the beginning
                return numberOfFoundPenguins;
            }

            currentTile.setState(Tile.State.OLD_PATH_DONE);
            return walk(previousTile, maxDistance, lastStep.parent, numberOfFoundPenguins);
        }
    }


    // Class representing a single step in the current path
    private static class PathStep {
        final TileLocation location;
        final PathStep parent;

        PathStep(TileLocation location, PathStep parent) {
            this.location = location;
            this.parent = parent;
        }

        // Recreate a path through the maze
        List<TileLocation> path() {
            List<TileLocation> path = new ArrayList<>();
            PathStep nextStep = this.parent;

            while (nextStep != null) {
                path.add(nextStep.location);

                nextStep = nextStep.parent;
            }

            return path;
        }
    }



    enum Direction { UP, DOWN, LEFT, RIGHT }

    // Class representing the exact location of a tile within a maze
    private static class TileLocation {
        final int row;
        final int column;

        TileLocation(int row, int column) {
            this.row = row;
            this.column = column;
        }

        // Create a new `TileLocation` by advancing `this` by `distance` in the specified direction
        TileLocation advancedBy(int distance, Direction direction) {
            switch (direction) {
                case UP:
                    return new TileLocation(row - distance, column);
                case DOWN:
                    return new TileLocation(row + distance, column);
                case LEFT:
                    return new TileLocation(row, column - distance);
                case RIGHT:
                    return new TileLocation(row, column + distance);
            }

            // java requires this return statement, even though the switch above is already exhaustive
            return null;
        }

        // Check whether a location is withing the bounds of a maze
        boolean isValidInMaze(MazeManager maze) {
            if (row < 0 || column < 0) {
                return false;
            }

            return (this.column + 1) <= maze.numberOfColumns && (this.row + 1) <= maze.numberOfRows;
        }

        // Compare two `TileLocation` objects
        boolean equals(TileLocation obj) {
            return this.column == obj.column && this.row == obj.row;
        }


        @Override
        public String toString() {
            return String.format("<TileLocation row=%s column=%s>", this.row, this.column);
        }
    }

    // Class representing a Tile within a maze
    static class Tile {

        // Enum describing the state of a Tile
        enum State {
            FREE,
            WALL,
            PLAYER,
            OLD_PATH_ACTIVE,
            OLD_PATH_DONE,
            PENGUIN
        }

        final TileLocation location;
        final MazeManager maze;

        Tile(TileLocation location, MazeManager maze) {
            this.location = location;
            this.maze = maze;
        }

        // Get the locations of all neighbors of a tile. This only returns neighbors that share an edge with the current tile
        // (an edge, not a corner)! Each tile can have up to 4 neighbors
        // NOTE: it's not guaranteed that these locations are actually within the bounds of the maze
        List<TileLocation> neighboringTileLocations() {
            List<TileLocation> locations = new ArrayList<>();
            locations.add(this.location.advancedBy(1, Direction.UP));
            locations.add(this.location.advancedBy(1, Direction.DOWN));
            locations.add(this.location.advancedBy(1, Direction.LEFT));
            locations.add(this.location.advancedBy(1, Direction.RIGHT));
            return locations;
        }

        // Get the actual neighboring tiles
        List<Tile> neighboringTiles() {
            return this.locationsToTiles(this.neighboringTileLocations());
        }


        // Get all neighboring tiles, including diagonal ones (that don't share an edge with the current tile, but just a corner)
        List<Tile> extendedNeighboringTiles() {
            List<TileLocation> locations = new ArrayList<>();
            locations.add(this.location.advancedBy(1, Direction.LEFT).advancedBy(1, Direction.UP));
            locations.add(this.location.advancedBy(1, Direction.LEFT).advancedBy(1, Direction.DOWN));
            locations.add(this.location.advancedBy(1, Direction.RIGHT).advancedBy(1, Direction.UP));
            locations.add(this.location.advancedBy(1, Direction.RIGHT).advancedBy(1, Direction.DOWN));

            locations.addAll(this.neighboringTileLocations());

            return this.locationsToTiles(locations);
        }


        // Map an array of `TileLocation`s to the actual tiles in the maze (if they are valid)
        private List<Tile> locationsToTiles(List<TileLocation> locations) {
            return locations.stream().filter(loc -> loc.isValidInMaze(maze)).map(maze::tileAtLocation).collect(Collectors.toList());
        }

        State getState() {
            return State.values()[maze.backingMaze[location.column][location.row]];
        }

        void setState(State newState) {
            this.maze.backingMaze[location.column][location.row] = newState.ordinal();
            this.maze.draw();
        }

        @Override
        public String toString() {
            return String.format("<Tile state=%s row=%s column=%s>", this.getState(), this.location.row, this.location.column);
        }
    }

    // Class managing a maze
    static class MazeManager {
        final int numberOfColumns;
        final int numberOfRows;
        private final int[][] backingMaze;

        MazeManager(int width, int height) {
            this.numberOfColumns = width;
            this.numberOfRows = height;
            //this.backingMaze = Maze.generateStandardPenguinMaze(width, height);
            this.backingMaze = Maze.generatePenguinMaze(width, height);
        }

        // Re-draw the maze displayed on screen
        void draw() {
            Maze.draw(this.backingMaze);
        }

        // Get the tile at the specified location
        Tile tileAtLocation(TileLocation location) {
            return new Tile(location, this);
        }
    }
}
