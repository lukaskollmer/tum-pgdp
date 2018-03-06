

import java.util.*;


/*
* Note: This is not a very good implementation of this right hand maze thing. It'll run forever,
* even if the maze doesn't have a solution
* */

public class Main_Task_04 {
    public static void main(String[] args) {
        MazeManager maze = new MazeManager(10, 10);

        maze.draw();

        Tile start = maze.tileAtLocation(new TileLocation(0, 1));
        Tile end   = maze.tileAtLocation(new TileLocation(maze.numberOfRows - 2, maze.numberOfColumns - 1));

        start.setState(Tile.State.PLAYER); // mark the entry tile as well

        walk(maze, start, end, Direction.DOWN);
    }






    private enum Direction { UP, DOWN, LEFT, RIGHT }

    private static class TileLocation {
        final int row;
        final int column;

        TileLocation(int row, int column) {
            this.row = row;
            this.column = column;
        }

        TileLocation advancedBy(int distance, Direction direction) {
            switch (direction) {
                case DOWN:
                    return new TileLocation(row + distance, column);
                case LEFT:
                    return new TileLocation(row, column - distance);
                case UP:
                    return new TileLocation(row - distance, column);
                case RIGHT:
                    return new TileLocation(row, column + distance);
            }

            return null; // not really seeing why this is necessary, the switch is already exhaustive
        }

        boolean isValidInMaze(MazeManager maze) {
            if (row < 0 || column < 0) {
                return false;
            }

            return (this.column + 1) <= maze.numberOfColumns && (this.row + 1) <= maze.numberOfRows;
        }

        boolean equals(TileLocation obj) {
            return this.column == obj.column && this.row == obj.row;
        }


        @Override
        public String toString() {
            return String.format("<TileLocation row=%s column=%s>", this.row, this.column);
        }
    }

    private static class Tile {
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

        // Get all neighbors of a tile. This only returns neighbors that share an edge with the current tile
        // (an edge, not a corner)! Each tile can have up to 4 neighbors
        /*List<Tile> neighboringTiles() {
            List<TileLocation> locations = new ArrayList<>();
            locations.add(this.location.advancedBy(1, Direction.UP));
            locations.add(this.location.advancedBy(1, Direction.DOWN));
            locations.add(this.location.advancedBy(1, Direction.LEFT));
            locations.add(this.location.advancedBy(1, Direction.RIGHT));
            return locations.stream().filter(loc -> loc.isValidInMaze(this.maze)).map(loc -> new Tile(loc, this.maze)).collect(Collectors.toList());
        }*/

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

    private static class MazeManager {
        final int numberOfColumns;
        final int numberOfRows;
        private final int[][] backingMaze;

        MazeManager(int width, int height) {
            this.numberOfColumns = width;
            this.numberOfRows = height;
            this.backingMaze = Maze.generateMaze(width, height);
        }

        void draw() {
            Maze.draw(this.backingMaze);
        }

        Tile tileAtLocation(TileLocation location) {
            return new Tile(location, this);
        }
    }

    static void walk(MazeManager maze, Tile current, Tile destination, Direction lastDirection) {
        if (current.location.equals(destination.location)) {
            MiniJava.write("done");
            return;
        }

        for (Direction direction : directionOrder.get(lastDirection)) {
            TileLocation nextLocation = current.location.advancedBy(1, direction);
            if (nextLocation.isValidInMaze(maze)) {
                Tile nextTile = maze.tileAtLocation(nextLocation);
                if (nextTile.getState() != Tile.State.WALL) {

                    sleep(nextTile.location.equals(destination.location) ? 0 : (long)0.1);

                    nextTile.setState(Tile.State.PLAYER);
                    walk(maze, nextTile, destination, direction);
                    return;
                }
            }
        }

    }

    static void sleep(long duration) {
        try {
            Thread.sleep(duration * 1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }



    static HashMap<Direction, List<Direction>> directionOrder;

    static {
        directionOrder = new HashMap<>();

        directionOrder.put(Direction.DOWN,  Arrays.asList(Direction.LEFT,  Direction.DOWN,  Direction.RIGHT, Direction.UP   ));
        directionOrder.put(Direction.UP,    Arrays.asList(Direction.RIGHT, Direction.UP,    Direction.LEFT,  Direction.DOWN ));
        directionOrder.put(Direction.LEFT,  Arrays.asList(Direction.UP,    Direction.LEFT,  Direction.DOWN,  Direction.RIGHT));
        directionOrder.put(Direction.RIGHT, Arrays.asList(Direction.DOWN,  Direction.RIGHT, Direction.UP,    Direction.LEFT ));

    }

}
