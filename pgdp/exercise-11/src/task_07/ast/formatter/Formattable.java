package task_07.ast.formatter;

public interface Formattable {
    void accept(FormatVisitor formatVisitor);
}
