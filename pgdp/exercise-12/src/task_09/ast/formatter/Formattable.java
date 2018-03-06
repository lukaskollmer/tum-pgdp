package task_09.ast.formatter;

public interface Formattable {
    void accept(FormatVisitor formatVisitor);
}
