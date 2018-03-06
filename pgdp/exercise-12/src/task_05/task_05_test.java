package task_05;

import java.util.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class task_05_test {
    private Random random = new Random();

    @Test
    public void testContains() throws InterruptedException {
        HashSet<Integer> testSet = new HashSet<>();
        int n = 10000;
        for (int i = 0; i < n; i++)
            testSet.add(random.nextInt(20*n));
        BinaryTree<Integer> suchti = new BinaryTree<>();
        for(Integer i : testSet)
            suchti.insert(i);
        for (int i = 0; i < n; i++)
            assertEquals(testSet.contains(i), suchti.contains(i));
    }

    @Test
    public void testContainsRemove() throws InterruptedException {

        //for (int i_ = 0; i_ < 100; i_++) {
        //    System.out.format("\n\n\n\n\n\n\n\n");
        HashSet<Integer> testSet = new HashSet<>();
        int n = 10000;
        for (int i = 0; i < n; i++)
            testSet.add(random.nextInt(20*n));
        BinaryTree<Integer> suchti = new BinaryTree<>();
        for(Integer i : testSet)
            suchti.insert(i);
        for (int i = 0; i < n; i++) {
            int next = random.nextInt(20*n);
            if(testSet.contains(next)) {
                suchti.remove(next);
                testSet.remove(next);
            }
        }
        for (int i = 0; i < n; i++)
            assertEquals(testSet.contains(i), suchti.contains(i));
        //}
    }


    // https://piazza.com/class/j7hxt8rsfjy1gk?cid=4295
    @Test
    public void testRemoveSize() throws InterruptedException {
        int n = 1000;
        BinaryTree<Integer> suchti = new BinaryTree<>();
        List<Integer> liste = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            liste.add(new Integer(i));
        }
        Collections.shuffle(liste);

        for(Integer i: liste) {
            suchti.insert(i);
        }

        int count = 0;
        for(int i = 0; i < n; i++) {
            if(Math.random() < 0.5) {
                suchti.remove(new Integer(i));
                count++;
            }
        }

        int sizeCount = 0;

        for(int i = 0; i < n; i++) {
            if(suchti.contains(i))
                sizeCount++;
        }

        assertEquals(sizeCount, n - count);
    }



    @Test
    public void potentially_new_testContains() throws InterruptedException {
        HashSet<Integer> testSet = new HashSet<>();
        int n = 10000;
        for (int i = 0; i < n; i++)
            testSet.add(random.nextInt(20*n));
        BinaryTree<Integer> suchti = new BinaryTree<>();
        for(Integer i : testSet)
            suchti.insert(i);
        for (int i = 0; i < 20*n; i++)
            assertEquals(testSet.contains(i), suchti.contains(i));
    }

    @Test
    public void potentially_new_testContainsRemove() throws InterruptedException {
        HashSet<Integer> testSet = new HashSet<>();
        int n = 10000;
        for (int i = 0; i < n; i++)
            testSet.add(random.nextInt(20*n));
        BinaryTree<Integer> suchti = new BinaryTree<>();
        for(Integer i : testSet)
            suchti.insert(i);
        for (int i = 0; i < n; i++) {
            int next = random.nextInt(20*n);
            if(testSet.contains(next)) {
                suchti.remove(next);
                testSet.remove(next);
            }
        }
        for (int i = 0; i < 20*n; i++)
            assertEquals(testSet.contains(i), suchti.contains(i));
    }


    @Test
    void test_with_threads() {
        if (true) return;


        BinaryTree<Integer> tree = new BinaryTree<>();

        Arrays.asList(33, 5, 77, 9, 76, 85, 17, 73, 84, 39).forEach(tree::insert);


        Thread t_0 = new Thread(() -> System.out.format("running thread 0\n"));
        Thread t_1 = new Thread(() -> System.out.format("running thread 1\n"));



        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                t_0.run();
                t_1.run();
            }
        }, 1000, 1000);

        while(true){}
    }
}

