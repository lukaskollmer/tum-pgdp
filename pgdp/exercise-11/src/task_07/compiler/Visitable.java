package task_07.compiler;

public interface Visitable {
    void accept(Visitor visitor) throws Visitor.Error;
}
