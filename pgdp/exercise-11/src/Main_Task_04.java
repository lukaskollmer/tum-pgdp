import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class Main_Task_04 {

    public static void main(String... args) {
        Set<String> set = new Set<>();

        set = set.add("i");
        set = set.add("am");
        set = set.add("the");
        set = set.add("doctor");

        for (String element : set) {
            System.out.format("%s\n", element);
        }

        System.out.format("contains: %s\n", set.contains("i"));

        set = set.remove("i");

        for (String element : set) {
            System.out.format("%s\n", element);
        }

        System.out.format("contains: %s\n", set.contains("i"));


        Set<String> s2 = new Set<>();
        s2 = s2.add("1");
        s2 = s2.add("2");
        s2 = s2.add("3");

        Set<Integer> s3 = new Set<>();
        s3 = s3.add(1);
        s3 = s3.add(2);
        s3 = s3.add(3);

        Set<Integer> s4 = new Set<>();
        s4 = s4.add(1);
        s4 = s4.add(2);
        s4 = s4.add(3);

        System.out.format("equal: %s\n", s3.equals(s4));


        System.out.format("set: %s\n", s4);
    }
}


class List<T> implements Iterable<T> {
    final T element;
    final List<T> next;


    List(T element, List<T> next) {
        this.element = element;
        this.next = next;
    }

    @Override
    public Iterator<T> iterator() {
        List<T> self = this;

        return new Iterator<T>() {
            private List<T> currentCursor = self;

            @Override
            public boolean hasNext() {
                return currentCursor != null && currentCursor.element != null;
            }

            @Override
            public T next() {
                T element = currentCursor.element;
                currentCursor = currentCursor.next;
                return element;
            }
        };
    }
}

class Set<T> implements Iterable<T> {
    final List<T> backing;

    Set() {
        this(new List<>(null, null));
    }

    Set(List<T> list) {
        this.backing = list;
    }


    Set<T> add(T element) {
        if (element == null) throw new NullPointerException();
        if (contains(element)) return this;

        return new Set<>(new List<>(element, this.backing));
    }


    boolean contains(T otherElement) {
        for (T element : backing) {
            if (element.equals(otherElement)) return true;
        }

        return false;
    }


    Set<T> remove(T element) {
        if (element == null) throw new NullPointerException();
        if (!contains(element)) return this;

        // removing the first element
        if (backing.element.equals(element)) {
            return new Set<>(backing.next);
        }

        List<T> newList = null;

        for (T elem : backing) {
            if (!elem.equals(element)) {
                newList = new List<>(elem, newList);
            }
        }


        return new Set<>(newList);
    }


    int size() {
        AtomicInteger _size = new AtomicInteger(0);

        backing.iterator().forEachRemaining(__ -> _size.getAndIncrement());

        return _size.get();
    }


    @Override
    public Iterator<T> iterator() {
        return backing.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;

        if (obj instanceof Set) {
            Set<T> asSet = (Set<T>)obj;
            for (T elem : asSet.backing) {
                if (!contains(elem)) return false;
            }
            return true;
        }

        return false;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        int size = size();
        int idx = 0;

        for (T element : backing) {
            sb.append(element);

            if (idx < size - 1) sb.append(", ");
            idx++;
        }

        return sb.append("}").toString();
    }
}
