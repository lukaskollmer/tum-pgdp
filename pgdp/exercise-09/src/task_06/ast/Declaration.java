package task_06.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Declaration {
    private final List<String> names = new ArrayList<>();

    public Declaration(String... names) {
        this.names.addAll(Arrays.asList(names));
    }


    public List<String> getNames() {
        return names;
    }
}
