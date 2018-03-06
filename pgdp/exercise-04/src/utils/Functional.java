package utils;

import java.util.ArrayList;
import java.util.Collection;

public class Functional {
    public interface MapFunction<T, U> {
        U map(T obj);
    }
    public static <T, U> ArrayList<U> map(Collection<T> collection, MapFunction<T, U> fn) {
        ArrayList<U> newArray = new ArrayList<>();

        for(T obj : collection) {
            newArray.add(fn.map(obj));
        }

        return newArray;
    }


    public interface ReduceFunction<T, U> {
        T reduce(T accumulator, U obj);
    }
    public static <T, U> U reduce(Collection<T> collection, U initialValue, ReduceFunction<U, T> fn) {
        for (T obj : collection) {
            initialValue = fn.reduce(initialValue, obj);
        }
        return initialValue;
    }
}
