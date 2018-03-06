package task_09.ast;

import java.util.Arrays;
import java.util.List;

public class Declaration {

    public final String typename;
    public final List<String> names;


    public Declaration(String typename, List<String> names) {
        this.typename = typename;
        this.names = names;
    }


    // legacy api, assuming all variables are integers
    public Declaration(String... names) {
        this.typename = "int";
        this.names = Arrays.asList(names);
    }

    // get the name of the first declared variable
    public String getName() {
        return names.get(0);

    }


    @Override
    public String toString() {
        return String.format("<Declaration type=%s names=%s >", this.typename, this.names);
    }
}
