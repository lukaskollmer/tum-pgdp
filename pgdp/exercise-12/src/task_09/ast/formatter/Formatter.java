package task_09.ast.formatter;

public class Formatter {

    public static String format(Formattable formattable) {
        FormatVisitor formatVisitor = new FormatVisitor();
        formattable.accept(formatVisitor);

        return formatVisitor.getResult();
    }
}
