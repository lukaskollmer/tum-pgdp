package task_07.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Declaration {
    private final List<String> names;

    public Declaration(String... names) {
        this.names = Arrays.asList(names);
    }

    public Declaration(List<String> names) {
        this.names = names;
    }


    public List<String> getNames() {
        return names;
    }

    @Override
    public String toString() {
        return String.format("<Declaration names=%s >", this.names);
    }
}
