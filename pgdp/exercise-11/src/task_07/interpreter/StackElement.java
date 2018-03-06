package task_07.interpreter;


import task_07.Util;

// in c++ land, this would be a union
public class StackElement<T, U> {

    public final T element1;
    public final U element2;

    private StackElement(T element1, U element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public static <T, U> StackElement<T, U> withElement1(T element1) {
        return new StackElement<>(element1, null);
    }

    public static <T, U> StackElement<T, U> withElement2(U element2) {
        return new StackElement<>(null, element2);
    }


    @Override
    public String toString() {
        return Util.nullCoalescing(element1, element2).toString();
    }
}
