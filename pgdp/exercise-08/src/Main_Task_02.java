import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main_Task_02 {

    public static void main(String... args) {
        Date event = new Date(2, 10, 0, 30, "Event");
        System.out.format("%s\n", event);


        Date event2 = new Date(2, 10, 45, 15, "title1");


        Timetable tt = new Timetable();

        tt.addDate(event);
        tt.addDate(event2);
        tt.deleteDate(event2);


        System.out.format("tt: %s\n", tt);
    }

    static class Date {
        private int weekday;
        private int starthour;
        private int startmin;
        private int duration; // min
        private String title;

        Date(int weekday, int starthour, int startmin, int duration, String title) {
            this.weekday = weekday;
            this.starthour = starthour;
            this.startmin = startmin;
            this.duration = duration;
            this.title = title;


        }

        public int getWeekday() {
            return weekday;
        }

        public int getStarthour() {
            return starthour;
        }

        public int getStartmin() {
            return startmin;
        }

        public int getDuration() {
            return duration;
        }

        public String getTitle() {
            return title;
        }

        int startHash() {
            return weekday * 10000 + starthour * 100 + startmin;
        }

        @Override
        public String toString() {
            List<String> days = Arrays.asList("Monday", "Tuesday", "wwednesday", "thu", "fri", "sat", "sun");
            return String.format("<Date '%s' duration: %s min start: %02d:%02d (%s)>", title, duration, starthour, startmin, days.get(weekday));
        }


        public boolean isEqualToDate(Date other) {
            return this.title.equals(other.title) &&
                    this.weekday == other.weekday &&
                    this.starthour == other.starthour &&
                    this.startmin == other.startmin &&
                    this.duration == other.duration;
        }
    }


    static class Timetable {
        DateList dates;

        Timetable() {
            this.dates = new DateList(null);

        }

        boolean addDate(Date date) {
            AtomicBoolean hasConflict = new AtomicBoolean(false);

            this.dates.forEach(date1 -> {
                if (date.startHash() <= date1.startHash() + date1.duration) {
                    hasConflict.set(true);
                }
            });

            if (!hasConflict.get()) {
                this.dates = new DateList(date, this.dates);
            }

            return hasConflict.get();
        }

        boolean deleteDate(Date date) {
            return this.dates.delete(date);
        }


        @Override
        public String toString() {
            List<Date> list = dates.asList();
            list.sort(Comparator.comparingInt(Date::startHash));
            return String.format("<Timetable dates: %s>", list);
        }
    }


    static class DateList {
        Date info;
        DateList next;

        DateList(Date info) {
            this.info = info;
            this.next = null;
        }

        DateList(Date info, DateList next) {
            this.info = info;
            this.next = next;
        }


        interface Block {
            void fn(Date date);
        }

        void forEach(Block block) {
            DateList nextList = this;
            while (nextList != null && nextList.info != null) {
                block.fn(nextList.info);
                nextList = nextList.next;
            }
        }


        boolean delete(Date date) {
            DateList list = this;

            if (this.info.isEqualToDate(date)) {
                this.info = null;

                if (this.next != null) {
                    this.info = this.next.info;
                    this.next = this.next.next;
                }

                return true;
            }

            while (list != null && list.info != null && list.next != null && list.next.info != null) {
                if (list.next.info.isEqualToDate(date)) {
                    list.next = list.next.next;
                    return true;
                }
                list = list.next;
            }
            return false;
        }

        List<Date> asList() {
            List<Date> list = new ArrayList<>();
            forEach(list::add);
            return list;
        }

        @Override
        public String toString() {
            return asList().toString();
        }
    }
}
