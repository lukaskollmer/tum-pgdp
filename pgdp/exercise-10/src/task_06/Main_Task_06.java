package task_06;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Main_Task_06 {
    public static void main(String... args) {
        System.out.format("main\n");
    }


    @Test
    public void findAPath() {
        Eisscholle[] eisschollen;
        List<Seeweg> seewege;


        eisschollen = new Eisscholle[6];
        seewege = new LinkedList<>();

        for (char c = 'A'; c <= 'F'; c++) {
            eisschollen[c - 'A'] = new Eisscholle("" + c);
        }


        seewege.add(new Seeweg(10, eisschollen[0], eisschollen[1]));
        seewege.add(new Seeweg(12, eisschollen[1], eisschollen[3]));
        seewege.add(new Seeweg(15, eisschollen[0], eisschollen[2]));
        seewege.add(new Seeweg(10, eisschollen[2], eisschollen[4]));
        seewege.add(new Seeweg(1, eisschollen[3], eisschollen[5]));
        seewege.add(new Seeweg(5, eisschollen[5], eisschollen[4]));
        seewege.add(new Seeweg(2, eisschollen[3], eisschollen[4]));
        seewege.add(new Seeweg(15, eisschollen[1], eisschollen[5]));

        List<Eisscholle> schollen = Seerettung.findeWeg(eisschollen, seewege, 0, 4);
        assertEquals(4, schollen.size());
        assertEquals(eisschollen[0], schollen.get(0));
        assertEquals(eisschollen[1], schollen.get(1));
        assertEquals(eisschollen[3], schollen.get(2));
        assertEquals(eisschollen[4], schollen.get(3));
    }
}
