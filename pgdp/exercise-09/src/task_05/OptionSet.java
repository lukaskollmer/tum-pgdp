package task_05;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * A generic OptionSet
 * */
public class OptionSet<E extends Enum<E> & OptionSet.Enum> extends HashSet<E> {

    // An Enum that can be represented by an OptionSet
    interface Enum {
        int getRawValue();
    }

    // new OptionSet from values
    OptionSet(E... values) {
        super(Arrays.asList(values));
    }


    public int getRawValue() {
        return this.stream().map(e -> e.getRawValue()).reduce(0, (a, b) -> a + b);
    }


    OptionSet<E> inverted(Class<E> cls) {
        OptionSet<E> newSet = new OptionSet<>(cls.getEnumConstants());

        for (E e : this) {
            newSet.remove(e);
        }

        return newSet;
    }


    // get a filtered copy containing all elements annotated w/ the passed annotation class
    OptionSet<E> filtered(Class<? extends Annotation> annotationClass) {

        BiFunction<E, Class<? extends Annotation>, Boolean> hasAnnotation = (enumCase, _annotationClass) -> {
            try {
                return enumCase.getClass().getField(enumCase.name()).isAnnotationPresent(annotationClass);
            } catch (NoSuchFieldException e) {
                return false;
            }
        };

        return this
                .stream()
                .filter(e -> hasAnnotation.apply(e, annotationClass))
                .distinct()
                .collect(Collectors.toCollection(OptionSet::new));
    }


    // check whether the OptionSet contains any of the passed values
    boolean containsAny(E... elements) {
        for (E element : elements) {
            if (this.contains(element)) {
                return true;
            }
        }

        return false;
    }


    // check whether there is an intersection between two OptionSets
    boolean containsAny(OptionSet<E> otherSet) {
        return !this.intersection(otherSet).isEmpty();
    }


    // get the intersection between two OptionSets
    OptionSet<E> intersection(OptionSet<E> otherSet) {
        OptionSet<E> intersection = new OptionSet<>();

        for (E element : this) {
            if (otherSet.contains(element)) {
                intersection.add(element);
            }
        }

        return intersection;
    }
}