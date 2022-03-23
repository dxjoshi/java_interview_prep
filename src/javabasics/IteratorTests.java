package javabasics;

import java.util.ArrayList;
import java.util.List;

public class IteratorTests {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

//      NO exception but result would be undefined
/*
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
            list.remove(i);
        }
*/

//      Throws ConcurrentModification Exception
/*
        for (Integer num : list) {
            System.out.println(num);
            list.remove(num);
        }
*/
    }
}
