package task_07.compiler;

import task_07.ast.*;
import task_07.ast.expression.*;
import task_07.ast.expression.Number;
import task_07.ast.statement.*;
import task_07.interpreter.Interpreter;

import static task_07.Util.f;   // String.format

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;



public class CodeGenerationVisitor implements Visitor {

    private final List<String> instructions = new ArrayList<>();

    // additional information about the current scope
    // this is used internally when generating instructions for functions
    private final Scope scope;              // scope of the current function (parameters + local variables)
    private final String functionName;      // name of the current function
    private final Map<String, Integer> globalScopeInfo; // [function_name: #args]


    // counter we use to ensure the labels generated for if/while statements are unique
    private int if_while_counter = 0;
    private void inc_if_while_counter() { if_while_counter++; }

    static private List<String> asm_length = Arrays.asList(
            // TODO inline this instead of making it a function!
            "",
            "length:",
            "alloc 0",
            "ldi -1",
            "lds 0",
            "ldh",
            "return 1"
    );


    public CodeGenerationVisitor() {
        this.instructions.add("ldi main");
        this.instructions.add("call 0");
        this.instructions.add("");
        this.instructions.add("ldi -1");
        this.instructions.add("jump end");

        this.functionName = null;
        this.scope = new Scope();
        this.globalScopeInfo = new HashMap<>();

        commonInit();
    }

    // 'internal' codegen that doesn't add the bootstrapping wrapper code and is aware if its scope
    // numberOfArguments is the number of arguments a function is expecting (eg full scope - local vars)
    CodeGenerationVisitor(int numberOfArguments, List<String> arguments, String functionName, Map<String, Integer> globalScopeInfo) {

        this.scope = new Scope(arguments, numberOfArguments);
        this.functionName = functionName;
        this.globalScopeInfo = globalScopeInfo;

        commonInit();
    }

    private void commonInit() {
        globalScopeInfo.put("length", 1);
    }


    @Override
    public void visit(Program program) throws Error {
        // This is where we generate the instructions for the program

        // 1. some checks

        // 1.1 check for duplicate global symbols
        // we have to run this before processing any of the actual nodes bc this
        // functions list also serves as our symbol lookup table when processing function calls

        Set<String> functionNames = new HashSet<>();
        Map<String, Integer> globalScopeInfo = new HashMap<>();

        for (Function function : program.functions) {
            if (functionNames.contains(function.name)) {
                throw new Visitor.Error("Found duplicate symbol '%s", function.name);
            } else {
                functionNames.add(function.name);
                globalScopeInfo.put(function.name, function.parameters.size());
            }
        }

        // 1.2 make sure there is a `main` function (since this will be our entry point)
        if (!functionNames.contains("main")) {
            throw new Error("Unable to find entry point. You have to specify a 'main' function");
        }

        if (functionNames.contains("length")) {
            throw new Error("Invalid function name. 'length' is already a builtin!");
        }

        // reorder the functions to make sure that main is always first
        // this isn't actually necessary, but IMO makes the code look nicer
        List<Function> functions = program.functions
                .stream()
                .sorted((f0, f1) -> f0.name.equals("main") ? -1 : 1)
                .collect(Collectors.toList());

        for (Function function : functions) {

            // List of instructions for the function we're currently processing
            List<String> fn_instructions = new ArrayList<>();

            // create the function's entry point
            fn_instructions.add("");
            fn_instructions.add(f("%s:", function.name));

            // collect all variables in the current scope (parameters + locally defined ones)
            List<String> scope = new ArrayList<>();
            scope.addAll(function.parameters);
            scope.addAll(
                    function.declarations
                            .stream()
                            .map(decl -> decl.names)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList())
            );

            // [precondition check] no duplicates in local namespace
            if (hasDuplicates(scope)) {
                throw new Visitor.Error("Found duplicate symbol in method '%s'", functionName);
            }


            // allocate space on the stack for local variables
            // since variable declarations are an array of arrays of variable names,
            // implementing this this way (getting the total scope size and subtracting #args)
            // makes the most sense
            int numberOfLocalVariables = scope.size() - function.parameters.size();
            fn_instructions.add(f("alloc %s", numberOfLocalVariables));

            // we need a new visitor bc we need to pass some info about the function's scope
            CodeGenerationVisitor functionCodeGen = new CodeGenerationVisitor(
                    function.parameters.size(),    // #arguments
                    scope,                         // names of local variables (including arguments)
                    function.name,                 // name of the current function
                    globalScopeInfo                // [function_name: #args]
            );

            // generate instructions for all of the function's statements
            for (Statement statement : function.statements) {
                statement.accept(functionCodeGen);
            }

            // add the generated instructions to the other instructions we generated earlier
            fn_instructions.addAll(functionCodeGen.getInstructions());

            this.instructions.addAll(fn_instructions);
        }

        // after having parsed all functions of the program, we add the `end` label (that's where we jump to after returning from the `main` function)
        this.instructions.add("");
        this.instructions.add("end:");


        // include the length function, if necessary

        //java.util.function.Function<List<String>, Boolean> callsLength = _instructions -> {
        //    for (String instruction : _instructions) {
        //        if (instruction.equals("ldi length")) return true;
        //    }
        //
        //    return false;
        //};

        //if (callsLength.apply(this.instructions)) {
        //    this.instructions.addAll(5, asm_length);
        //}
    }




    //
    // STATEMENTS
    //


    public void visit(Return returnStatement) throws Error {
        returnStatement.expression.accept(this);

        int returnFreeCount = this.scope.size();

        this.instructions.add(f("return %s", returnFreeCount));

    }


    public void visit(Asm asmStatement) throws Error {
        this.instructions.add(asmStatement.instruction);
    }


    public void visit(Assignment assignment) throws Error {
        assignment.expression.accept(this);

        int index = this.scope.indexOfVariableWithName(assignment.variableName);

        this.instructions.add(f("sts %s", index));
    }


    public void visit(Read readStatement) throws Error {
        new Assignment(
                readStatement.variableName,
                new Asm("in")
        ).accept(this);
    }


    public void visit(Write writeStatement) throws Error {
        writeStatement.expression.accept(this);
        this.instructions.add("out");
    }


    public void visit(ArrayElementGetter arrayElementGetter) throws Error {
        arrayElementGetter.elementOffsetExpression.accept(this);
        arrayElementGetter.targetObjectExpression.accept(this);

        this.instructions.add("ldh");
    }

    public void visit(ArrayElementSetter arrayElementSetter) throws Error {
        arrayElementSetter.assignedValueExpression.accept(this);
        arrayElementSetter.offsetExpression.accept(this);

        int index = this.scope.indexOfVariableWithName(arrayElementSetter.variableName);
        this.instructions.add(f("lds %s", index));

        this.instructions.add("sth");
    }




    //
    // EXPRESSIONS
    //



    @Override
    public void visit(Variable variable) throws Error {
        // [precondition] the function we're trying to call actually exists
        if (!this.scope.contains(variable.name)) {
            throw new Error("Unable to find symbol '%s'", variable.name);
        }

        // this is where we somehow need to access the variable and make it available to the assembly program
        // how are we doing this?
        // we check which one it is (ie we get its index in the current scope)
        // then fetch that one off the frame and push it onto the stack after we're done (not sure when that is or how we would even find out)

        int index = this.scope.indexOfVariableWithName(variable.name);
        this.instructions.add(f("lds %s", index));
    }




    @Override
    public void visit(Number number) throws Error {
        Consumer<Integer> ldi = n -> this.instructions.add(f("ldi %s", n));

        int value = number.value;

        if ((short)value == value) {
            ldi.accept(value);
            return;
        }

        // problem:  the value cannot be represented w/ a short, meaning it's > 16 bits
        // solution: we split it up into multiple chunks, push those individually,
        //           shift them left and OR them to "restore" the original number
        // this works w positive numbers as well as negative numbers (btw that's why we split everything
        //   into 8 bit chunks instead of 16 bit. if the lower 16 bits started w/ a 1,
        //   this could be interpreted as a negative number which would fuck up the entire thing)

        // get a binary representation of the number, padded w/ 0s until its 32 characters long
        String binaryRepresentation = String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0');

        // split it up into an array of substrings, each 8 characters long
        List<String> strings = new ArrayList<>();
        int index = 0;
        while (index < binaryRepresentation.length()) {
            strings.add(binaryRepresentation.substring(index, Math.min(index + 8, binaryRepresentation.length())));
            index += 8;
        }

        Collections.reverse(strings);

        ldi.accept(0);

        for (int i = 0; i < strings.size(); i++) {
            int val = Integer.parseInt(strings.get(i), 2);
            ldi.accept(val);
            this.instructions.add(f("shl %s", i * 8));
            this.instructions.add("or");
        }
    }


    @Override
    public void visit(Binary binaryExpression) throws Error {

        // rhs first bc the stack is LIFO
        binaryExpression.rhs.accept(this);
        binaryExpression.lhs.accept(this);

        // add the binop's instruction to the instructions list
        this.instructions.add(binaryExpression.binop.instruction);
    }

    public void visit(Unary unaryExpression) throws Error {
        // unary is always `input * -1`
        new Binary(
                unaryExpression.expression,
                Binary.Binop.MULTIPLICATION,
                new Number(-1)
        ).accept(this);
    }


    @Override
    public void visit(Call call) throws Error {
        // [precondition check] the function we're trying to call actually exists
        if (!this.globalScopeInfo.keySet().contains(call.functionName)) {
            throw new Error("Unable to find symbol '%s'", call.functionName);
        }

        // [precondition check] correct number of arguments
        int expectedNumberOfArguments = this.globalScopeInfo.get(call.functionName);
        if (expectedNumberOfArguments != call.arguments.size()) {
            throw new Error("Incorrect number of arguments passed to function '%s' (expected %s, got %s)", call.functionName, expectedNumberOfArguments, call.arguments.size());
        }

        // 1. push the arguments onto the stack, in reverse order
        for (ListIterator<Expression> iterator = call.arguments.listIterator(call.arguments.size()); iterator.hasPrevious(); ) {
            Expression arg = iterator.previous();
            arg.accept(this);
        }

        // 2. push the destination label onto the stack
        this.instructions.add(f("ldi %s", call.functionName));

        // 3. call w/ the passed number of arguments
        this.instructions.add(f("call %s", call.arguments.size()));
    }


    public void visit(Composite composite) throws Error {
        for (Statement statement : composite.statements) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(ArrayLength_inline arrayLength) throws Error {
        this.instructions.add("ldi -1");
        arrayLength.targetPointerExpression.accept(this);

        this.instructions.add("ldh");
    }

    @Override
    public void visit(If ifStatement) throws Error {
        int captured_counter = this.if_while_counter;
        java.util.function.Function<String, String> generateIfLabel = type -> f("%s_if_%02d_body_%s", this.functionName, captured_counter, type);

        boolean hasElseBranch = ifStatement.elseBranch != null;

        // 1. handle the condition
        ifStatement.condition.accept(this);

        // 2. handle if jump
        this.instructions.add(f("jump %s", generateIfLabel.apply("main")));
        this.instructions.add("");


        // 3. handle the else jump
        String elseLabel = generateIfLabel.apply(hasElseBranch ? "else" : "end");
        this.instructions.add("ldi -1");
        this.instructions.add(f("jump %s", elseLabel));
        this.instructions.add("");


        // 4. handle the if branch
        this.instructions.add(generateIfLabel.apply("main") + ":");
        ifStatement.thenBranch.accept(this);
        this.instructions.add("ldi -1");
        this.instructions.add(f("jump %s", generateIfLabel.apply("end")));
        this.instructions.add("");

        // 5. handle the else branch
        if (hasElseBranch) {
            this.instructions.add(generateIfLabel.apply("else") + ":");
            ifStatement.elseBranch.accept(this);
            this.instructions.add(f("jump %s", generateIfLabel.apply("end")));
            this.instructions.add("");
        }

        // 6. handle the end of the if statement
        this.instructions.add(generateIfLabel.apply("end") + ":");


        // at the end!
        inc_if_while_counter();
    }


    public void visit(While whileStatement) throws Error {
        int captured_counter = this.if_while_counter;
        java.util.function.Function<String, String> generateLabel = type -> f("%s_while_%02d_%s", this.functionName, captured_counter, type);

        // 1. handle the condition
        this.instructions.add(generateLabel.apply("cond") + ":");
        whileStatement.condition.accept(this);
        this.instructions.add("");

        // 2. jump to the body, if the condition evaluated to true
        this.instructions.add(f("jump %s", generateLabel.apply("body")));

        // 3. jump to the end of the while statement if the condition didn't evaluate to true
        this.instructions.add("ldi -1");
        this.instructions.add(f("jump %s", generateLabel.apply("end")));
        this.instructions.add("");

        // 4. handle the body
        this.instructions.add(generateLabel.apply("body") + ":");
        whileStatement.body.accept(this);
        this.instructions.add("ldi -1");
        this.instructions.add(f("jump %s", generateLabel.apply("cond")));
        this.instructions.add("");

        // 5. handle the end of the while statement
        this.instructions.add(generateLabel.apply("end") + ":");



        // at the end!
        inc_if_while_counter();
    }


    @Override
    public void visit(Condition.True condition) throws Error {
        this.instructions.add("ldi -1");
    }


    @Override
    public void visit(Condition.False condition) throws Error {
        this.instructions.add("ldi 0");
    }


    @Override
    public void visit(Condition.Binary condition) throws Error {
        // evaluate both conditions (lhs and rhs), order doesn't matter
        condition.lhs.accept(this);
        condition.rhs.accept(this);

        // the last two values on the stack are now one of these options:
        // -1, -1   (lhs: true  | rhs: true )
        // -1,  0   (lhs: true  | rhs: false)
        //  0, -1   (lhs: false | rhs: true )
        //  0,  0   (lhs: false | rhs: false)

        // we now add the last two entries on the stack
        // if the result is -2, both are true
        // if the result is -1, one of them is true
        // if the result is  0, both are false

        int expectedResult = condition.operator == Condition.Binary.Bbinop.AND ? -2 : -1;

        this.instructions.add("add");
        this.instructions.add(f("ldi %s", expectedResult));
        this.instructions.add("eq");
    }


    @Override
    public void visit(Condition.Comparison condition) throws Error {
        // rhs first bc the stack is LIFO
        condition.rhs.accept(this);
        condition.lhs.accept(this);

        this.instructions.addAll(condition.comparator.instructions);

    }


    @Override
    public void visit(Condition.Unary condition) throws Error {
        condition.condition.accept(this);

        this.instructions.add("NOT");
    }

    @Override
    public void visit(ArrayCreation arrayCreation) throws Error {
        arrayCreation.sizeExpression.accept(this);

        instructions.add("alloch");
    }


    //
    // ACCESSING THE GENERATED INSTRUCTIONS



    // get the generated instructions, as an `int[]` array of opcodes that can be passed to an interpreter
    public int[] getProgram() {
        return Interpreter.parse(this.getSourceCode());
    }

    // get the generated instructions, separated by line breaks
    public String getSourceCode() {
        return String.join("\n", instructions);
    }

    // get the generated instructions
    public List<String> getInstructions() {
        return this.instructions;
    }




    //
    // UTILS
    //

    // Scope is a context-aware ArrayList used for getting a variable's offset from the current stack/frame pointer
    private static class Scope extends ArrayList<String> {

        final int numberOfArguments;

        Scope() {
            super();
            this.numberOfArguments = 0;
        }

        Scope(List<String> scope, int numberOfArguments) {
            super();
            this.numberOfArguments = numberOfArguments;
            this.addAll(scope);
        }

        int indexOfVariableWithName(String name) {
            int idx = this.indexOf(name) + 1;

            if (idx <= numberOfArguments) {
                return -(idx - 1);
            } else {
                return idx - numberOfArguments;
            }
        }
    }


    static <T> boolean hasDuplicates(List<T> list) {
        List<T> copy = new ArrayList<>(list);

        while (!copy.isEmpty()) {
            T element = copy.remove(0);

            for (T remainingElement : copy) {
                if (remainingElement.equals(element) || remainingElement == element) {
                    return true;
                }
            }
        }

        return false;
    }
}
