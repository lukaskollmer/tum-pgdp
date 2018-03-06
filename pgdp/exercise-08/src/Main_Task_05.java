/*
* exercise-07/task-05
*
* Worstorage
*
* */


import java.util.Arrays;
import java.util.function.*;


public class Main_Task_05 {


    public static void main(String... args) {

        Penguin p_2 = new Penguin(2);
        Penguin p_5 = new Penguin(5);
        Penguin p_1 = new Penguin(1);
        Penguin p_4 = new Penguin(4);
        Penguin p_8 = new Penguin(8);
        Penguin p_3 = new Penguin(3);
        Penguin p_8_2 = new Penguin(8);
        Penguin p_9 = new Penguin(9);
        Penguin p_10 = new Penguin(10);
        Penguin p_neg_100 = new Penguin(-100);

        Penguin p_50_ext = new Penguin(50);

        Worstorage storage = new Worstorage();

        storage.add(p_2);
        storage.add(p_5);
        storage.add(p_1);
        storage.add(p_4);
        storage.add(p_8);
        storage.add(p_8_2);
        storage.add(p_9);
        storage.add(p_3);
        storage.add(p_10);
        storage.add(p_neg_100);

        System.out.format("penguinTree:\n%s\n", storage.toString_levels());


        System.out.format("exists: %s\n", storage.find(p_50_ext));

        storage.remove(p_8);


        System.out.format("ps:    %s\n", storage);
        System.out.format("count: %s\n", Arrays.toString(storage.count));

        System.out.format("%s\n", storage.toString_levels());
    }












    static class Worstorage {
        private Penguin[] ps;
        private int[] count;

        Worstorage() {
            this.ps = new Penguin[1];
            this.count = new int[]{0};
        }


        void add(Penguin penguin) {

            BinaryTreePosition positionOfNewElement = getPositionForPenguin(penguin, PositionSearchOption.INSERT);

            boolean needsResizing = positionOfNewElement.level >= this.count.length;

            if (needsResizing) {
                this.ps = resize(this.ps, len -> len * 2 + 1);
                this.count = resize(this.count, len -> len + 1);
            }

            this.ps[positionOfNewElement.index] = penguin;
            this.count[positionOfNewElement.level] += 1;
        }





        boolean find(Penguin penguin) {
            //System.out.format("\n\n\n-[Worstorage_2 find]\n");
            return getPositionForPenguin(penguin, PositionSearchOption.LOCATE) != null;
        }






        void remove(Penguin penguin) {
            BinaryTreePosition position = getPositionForPenguin(penguin, PositionSearchOption.LOCATE_LAST);

            if (position == null) {
                //trying to delete non-existent penguin
                return;
            }

            Runnable adjustSizesIfNecessary = () -> {
                if (this.count[this.count.length - 1] == 0) {
                    this.ps = resize(this.ps, len -> (len - 1) / 2);
                    this.count = resize(this.count, len -> len - 1);
                }
            };


            if (position.level == this.count.length - 1) {
                // we're removing an element at the bottom of the tree,
                // meaning that we don't need to take care of potential children
                this.ps[position.index] = null;
                this.count[position.level] -= 1;

                adjustSizesIfNecessary.run();
                return;
            }

            Consumer<BinaryTreePosition> removeElementAtPosition = pos -> {
                setPenguinAtPosition(pos, null);
                this.count[pos.level] -= 1;
            };


            // removing an element somewhere in the middle of the tree
            // we need to take potential children into account

            removeElementAtPosition.accept(position);

            traverse(position, (pos, element) -> {
                removeElementAtPosition.accept(pos);

                add(element);
            });


            adjustSizesIfNecessary.run();
        }





        //
        // traversal
        //


        interface TraversalHandler extends BiConsumer<BinaryTreePosition, Penguin> { }

        void traverse(BinaryTreePosition position, TraversalHandler handler) {

            if (position.level >= this.count.length - 1) {
                return;
            }


            BinaryTreePosition lhs_child_position = Utils.positionOfChild(position, Utils.Direction.LEFT);
            BinaryTreePosition rhs_child_position = Utils.positionOfChild(position, Utils.Direction.RIGHT);


            if (penguinExistsAtPosition(lhs_child_position)) {
                handler.accept(lhs_child_position, getPenguinAtPosition(lhs_child_position));
            }

            if (penguinExistsAtPosition(rhs_child_position)) {
                handler.accept(rhs_child_position, getPenguinAtPosition(rhs_child_position));
            }

            traverse(lhs_child_position, handler);
            traverse(rhs_child_position, handler);

        }






        //
        // Array resizing
        // (specialized for `int[]` and `Penguin[]`)
        //

        interface SizeCalculator {
            int newSize(int previousSize);
        }

        int[] resize(int[] array, SizeCalculator calculator) {
            int newSize = calculator.newSize(array.length);
            int[] newArray = new int[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize < array.length ? newSize : array.length);
            return newArray;
        }

        Penguin[] resize(Penguin[] array, SizeCalculator calculator) {
            int newSize = calculator.newSize(array.length);
            Penguin[] newArray = new Penguin[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize < array.length ? newSize : array.length);
            return newArray;
        }







        private static class BinaryTreePosition {
            final int index;      // index in the entire binary tree (counting row by row and element by element)
            final int level;      // the level in the binary tree
            final int position;   // x-position on the node's level

            BinaryTreePosition(int index, int level, int position) {
                this.index = index;
                this.level = level;
                this.position = position;
            }

            @Override
            public String toString() {
                return String.format("<BinaryTreePosition index=%s level=%s position=%s>", index, level, position);
            }
        }


        enum PositionSearchOption {
            INSERT,      // get a free position to insert an element
            LOCATE,      // get the position of an element's first occurrence
            LOCATE_LAST, // get the position of an element's last occurrence
        }

        private BinaryTreePosition getPositionForPenguin(Penguin penguin, PositionSearchOption searchOption) {


            // used when searchOption == LOCATE_LAST;
            BinaryTreePosition locate_lastPosition = null;

            int level = 0;
            int position = 0;

            while (true) {
                int index = Utils.indexForLevelAndPosition(level, position);

                Penguin temp_penguin = getPenguinAtIndex(index);

                if (temp_penguin == null) {
                    if (searchOption == PositionSearchOption.INSERT) {
                        return new BinaryTreePosition(index, level, position);
                    } else if (searchOption == PositionSearchOption.LOCATE) {
                        return null;
                    } else if (searchOption == PositionSearchOption.LOCATE_LAST) {
                        return locate_lastPosition;
                    }
                }


                switch (compare(temp_penguin, penguin)) {
                    case Same:
                        if (searchOption == PositionSearchOption.LOCATE) {
                            return new BinaryTreePosition(index, level, position);
                        } else if (searchOption == PositionSearchOption.LOCATE_LAST) {
                            locate_lastPosition = new BinaryTreePosition(index, level, position);
                        }
                        // [[fallthrough]]
                    case Ascending:
                        level++;
                        position = position * 2 + 1;
                        break;
                    case Descending:
                        level++;
                        position = position * 2;
                        break;
                    default:
                        break;
                }

            }
        }


        Penguin getPenguinAtIndex(int index) {
            try {
                return ps[index];
            } catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
        }

        Penguin getPenguinAtPosition(BinaryTreePosition position) {
            return getPenguinAtIndex(position.index);
        }

        boolean penguinExistsAtPosition(BinaryTreePosition position) {
            return getPenguinAtPosition(position) != null;
        }


        Penguin setPenguinAtPosition(BinaryTreePosition position, Penguin penguin) {
            Penguin oldValue = getPenguinAtPosition(position);
            this.ps[position.index] = penguin;
            return oldValue;
        }






        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            for (Penguin penguin : ps) {
                stringBuilder.append(String.format("%s,", penguin == null ? "~" : penguin.cuddliness)); // todo remove the ~
            }

            return stringBuilder.toString();
        }


        // return a string representation of the individual levels
        String toString_levels() {
            StringBuilder stringBuilder = new StringBuilder();

            for (int level = 0; level < count.length; level++) {
                int startIndex = Utils.getNumberOfNodesUpToLevel(level - 1);
                int endIndex = startIndex + Utils.getNumberOfNodesOnLevel(level);

                Penguin[] subrange = Arrays.copyOfRange(this.ps, startIndex, endIndex);

                StringBuilder stringBuilder1 = new StringBuilder();
                stringBuilder1.append("[");

                for (int i = 0; i < subrange.length; i++) {
                    Penguin penguin = subrange[i];
                    stringBuilder1.append(String.format("%s", penguin == null ? "~" : penguin.cuddliness));

                    if (i != subrange.length-1) {
                        stringBuilder1.append(", ");
                    }
                }
                stringBuilder1.append("]\n");

                stringBuilder.append(stringBuilder1);
            }

            return stringBuilder.toString();
        }





        // BinaryTree utils
        static class Utils {

            // get the number of nodes in all levels between 0 and level
            // eg: for level 1 this would return 3 (1 node in level 0 + 2 in level 1)
            static int getNumberOfNodesUpToLevel(int level) {
                if (level < 0) {
                    return 0;
                } else if (level == 0) {
                    return 1;
                }

                return (int) (Math.pow(2, level) + getNumberOfNodesUpToLevel(level - 1));
            }


            static int getNumberOfNodesOnLevel(int level) {
                return (int) Math.pow(2, level);
            }


            // get the total number of nodes in the tree (including empty ones)
            static int getTotalNumberOfNodes(int maximumDepth) {
                return (int) (Math.pow(2, maximumDepth + 1) - 1);
            }


            // position within a level is 0-based
            static int indexForLevelAndPosition(int level, int position) {
                if (level == 0 && position == 0) return 0; // not really necessary

                int lvl = 0;
                int index = 0;

                while (lvl < level) {
                    index += Utils.getNumberOfNodesOnLevel(lvl);
                    lvl++;
                }

                index += position+1;

                return index - 1; // arrays start at 0!
            }

            enum Direction { LEFT, RIGHT }

            static BinaryTreePosition positionOfChild(BinaryTreePosition position, Direction direction) {
                int newLevel = position.level + 1;
                int newPosition = position.position * 2 + (direction == Direction.RIGHT ? 1 : 0);
                int newIndex = indexForLevelAndPosition(newLevel, newPosition);

                return new BinaryTreePosition(newIndex, newLevel, newPosition);
            }
        }
    }







    static class Penguin implements Comparable<Penguin> {

        private int cuddliness;

        public Penguin(int cuddliness) {
            this.cuddliness = cuddliness;
        }

        public int getCuddliness() {
            return this.cuddliness;
        }

        public void setCuddliness(int cuddliness) {
            this.cuddliness = cuddliness;
        }

        // Note: this class has a natural ordering that is inconsistent with equals.
        public int compareTo(Penguin other) {
            int oc = other.cuddliness;
            if (cuddliness < oc)
                return -1;
            if (cuddliness > oc)
                return 1;
            return 0;
        }


        @Override
        public String toString() {
            return String.format("<Penguin c=%s>", this.cuddliness);
        }
    }

    //
    // Comparisons
    //

    enum ComparisonResult {
        Ascending,
        Same,
        Descending,
        Undefined
    }


    // compare two values
    static <T extends Comparable<T>> ComparisonResult compare(T left, T right) {
        if (left == null || right == null) {
            return ComparisonResult.Undefined;
        }

        int cmp = left.compareTo(right);
        if (cmp < 0) {
            return ComparisonResult.Ascending;
        } else if (cmp == 0) {
            return ComparisonResult.Same;
        } else {
            return ComparisonResult.Descending;
        }
    }


}
