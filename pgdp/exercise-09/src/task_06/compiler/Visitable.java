package task_06.compiler;

public interface Visitable {
    void accept(Visitor visitor) throws Visitor.Error;
}
