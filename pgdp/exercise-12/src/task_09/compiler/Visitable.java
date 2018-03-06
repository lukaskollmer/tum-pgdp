package task_09.compiler;

public interface Visitable {
    void accept(Visitor visitor) throws Visitor.Error;
}
