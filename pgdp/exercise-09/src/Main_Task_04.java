

public class Main_Task_04 {
    public static void main(String... args) {

        DLL<String> list = new DLL<>();


        list.add("hello");
        list.add("world");

        list.add(1, ", ");


        String s = list.remove(1);

        System.out.format("removed: %s\n", s);

        System.out.format("list: %s\n", list);



        list.add("i");
        list.add("am");
        list.add("the");
        list.add("doctor");

        list.shiftLeft(2);


        System.out.format("\n\nlist: %s\n", list);

    }


    static class DLL<T> {

        int size;

        DLL<T> prev;
        T   obj;
        DLL<T> next;


        DLL() {
        }

        int size() {
            return size;
        }


        void add(T newElement) {
            DLL next = this;

            while (next.next != null) {
                next = next.next;
            }

            next.next = new DLL();
            next.next.prev = next;
            next.obj = newElement;

            size++;
        }


        void add(int index, T newElement) {
            DLL<T> next = atIndex(index);

            DLL<T> newEntry = new DLL<>();
            newEntry.obj = newElement;
            newEntry.next = next.next;
            newEntry.prev = next;

            next.next = newEntry;
        }


        T remove(int index) {
            DLL<T> next = atIndex(index);

            T val = next.next.obj;
            next.next = next.next.next;
            next.next.prev = next.next;

            return val;
        }


        void shiftLeft(int index) {
            DLL<T> next = atIndex(index);

            this.obj = next.obj;
            this.next = next.next;
            next.prev = this;
        }

        DLL<T> atIndex(int index) {
            DLL<T> next = this;

            for (int i = 0; i < index; i++) {
                next = next.next;
            }

            return next;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<DLL items=(\n");

            DLL next = this;

            while (next.next != null) {
                builder.append(String.format("  %s\n", next.obj));
                next = next.next;
            }

            builder.append(">");

            return builder.toString();
        }
    }
}
