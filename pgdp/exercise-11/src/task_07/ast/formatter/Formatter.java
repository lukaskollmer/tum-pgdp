package task_07.ast.formatter;

import task_07.ast.Program;

public class Formatter {

    public static String format(Formattable formattable) {
        // todo this is potentially bad bc we don't use the visitor

        //if (!(formattable instanceof Program)) {
        //    return formattable.toString_code();
        //}

        FormatVisitor formatVisitor = new FormatVisitor();

        formattable.accept(formatVisitor);

        return formatVisitor.getResult();
    }
}
