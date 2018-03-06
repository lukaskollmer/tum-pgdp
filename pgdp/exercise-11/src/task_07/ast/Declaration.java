package task_07.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Declaration {
    public final List<String> names;

    public Declaration(String... names) {
        this.names = Arrays.asList(names);
    }

    public Declaration(List<String> names) {
        this.names = names;
    }


    @Override
    public String toString() {
        return String.format("<Declaration names=%s >", this.names);
    }
}
