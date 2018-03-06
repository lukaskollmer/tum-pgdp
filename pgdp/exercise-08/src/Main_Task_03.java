import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main_Task_03 {
    public static void main(String... args) {
        System.out.println("main");
    }


    public static class Set<T> {
        private List<T> backing;

        private final int size;

        Set() {
            this(0);
        }

        Set(int size) {
            this.size = size;
            this.backing = new ArrayList<>(size);
        }

        Set(Set<T> otherSet, T newElement) {
            this(otherSet.size + 1);

            this.backing.addAll(otherSet.backing);
            this.backing.add(newElement);
        }

        // check if contains
        boolean isElement(T obj) {
            return contains(obj);
        }


        // check if `this` is a superset of `otherSet`
        boolean superset(Set<T> otherSet) {
            for (T obj: otherSet.backing) {
                if (!this.contains(obj)) {
                    return false;
               }
            }

            return true;
        }


        boolean isEqual(Set<T> otherSet) {
            if (this.size != otherSet.size) {
                return false;
            }

            for (T obj : this.backing) {
                if (!otherSet.contains(obj)) {
                    return false;
                }
            }

            return true;
        }


        boolean contains(T element) {
            for (T obj: backing) {
                if (obj.equals(element)) {
                    return true;
                }
            }

            return false;
        }



        Set<T> add(T newElement) {
            if (contains(newElement)) {
                return this;
            }
            return new Set<>(this, newElement);
        }

        @Override
        public String toString() {
            return String.format("<ImmutableSet %s>", this.backing);
        }
    }
}
