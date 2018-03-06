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
    private final List<String> globalScope; // global scope (aka all functions, since we don't have global variables)


    // counter we use to ensure the labels generated for if/while statements are unique
    private int if_while_counter = 0;
    private void inc_if_while_counter() { if_while_counter++; }


    public CodeGenerationVisitor() {
        this.instructions.add("ldi main");
        this.instructions.add("call 0");
        this.instructions.add("");
        this.instructions.add("ldi -1");
        this.instructions.add("jump end");

        this.functionName = null;
        this.scope = new Scope();
        this.globalScope = new ArrayList<>();
    }

    // 'internal' codegen that doesn't add the bootstrapping wrapper code and is aware if its scope
    // numberOfArguments is the number of arguments a function is expecting (eg full scope - local vars)
    CodeGenerationVisitor(int numberOfArguments, List<String> arguments, String functionName, List<String> globalScope) {

        this.scope = new Scope(arguments, numberOfArguments);
        this.functionName = functionName;
        this.globalScope = globalScope;
    }


    @Override
    public void visit(Program program) throws Error {
        // This is where we generate the instructions for the program

        // 1. some checks

        // 1.1 check for duplicate global symbols
        // we have to run this before processing any of the actual nodes bc this
        // functions list also serves as our symbol lookup table when processing function calls

        Set<String> functionNames = new HashSet<>();

        for (Function function : program.getFunctions()) {
            if (functionNames.contains(function.getName())) {
                throw new Visitor.Error("Found duplicate symbol '%s", function.getName());
            } else {
                functionNames.add(function.getName());
            }
        }

        // 1.2 make sure there is a `main` function (since this will be our entry point)
        if (!functionNames.contains("main")) {
            throw new Error("Unable to find entry point. You have to specify a 'main' function");
        }

        // reorder the functions to make sure that main is always first
        // this isn't actually necessary, but IMO makes the code look nicer
        List<Function> functions = program.getFunctions()
                .stream()
                .sorted((f0, f1) -> f0.getName().equals("main") ? -1 : 1)
                .collect(Collectors.toList());

        for (Function function : functions) {

            // List of instructions for the function we're currently processing
            List<String> fn_instructions = new ArrayList<>();

            // create the function's entry point
            fn_instructions.add("");
            fn_instructions.add(f("%s:", function.getName()));

            // collect all variables in the current scope (parameters + locally defined ones)
            List<String> scope = new ArrayList<>();
            scope.addAll(function.getParameters());
            scope.addAll(
                    function.getDeclarations()
                            .stream()
                            .map(Declaration::getNames)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList())
            );

            // [precondition check] no duplicates in local namespace
            if (hasDuplicates(scope)) {
                throw new Visitor.Error("Found duplicate symbol in method '%s'", functionName);
            }

            // [precondition check] no duplicates in local and global namespace
            for (String local_symbol_name : scope) {
                if (functionNames.contains(local_symbol_name)) {
                    throw new Visitor.Error("Found duplicate symbol in method '%s'", functionName);
                }
            }

            // allocate space on the stack for local variables
            // since variable declarations are an array of arrays of variable names,
            // implementing this this way (getting the total scope size and subtracting #args)
            // makes the most sense
            int numberOfLocalVariables = scope.size() - function.getParameters().size();
            fn_instructions.add(f("alloc %s", numberOfLocalVariables));


            // we need a new visitor bc we need to pass some info about the function's scope
            CodeGenerationVisitor functionCodeGen = new CodeGenerationVisitor(
                    function.getParameters().size(),    // #arguments
                    scope,                              // names of local variables (including arguments)
                    function.getName(),                 // name of the current function
                    new ArrayList<>(functionNames)      // names of all functions
            );

            // generate instructions for all of the function's statements
            for (Statement statement : function.getStatements()) {
                statement.accept(functionCodeGen);
            }

            // add the generated instructions to the other instructions we generated earlier
            fn_instructions.addAll(functionCodeGen.getInstructions());

            this.instructions.addAll(fn_instructions);
        }

        // after having parsed all functions of the program, we add the `end` label (that's where we jump to after returning from the `main` function)
        this.instructions.add("");
        this.instructions.add("end:");
    }




    //
    // STATEMENTS
    //


    public void visit(Return returnStatement) throws Error {
        returnStatement.getExpression().accept(this);

        int returnFreeCount = this.scope.size();

        this.instructions.add(f("return %s", returnFreeCount));

    }


    public void visit(Asm asmStatement) throws Error {
        this.instructions.add(asmStatement.getInstruction());
    }


    public void visit(Assignment assignment) throws Error {
        assignment.getExpression().accept(this);

        int index = this.scope.indexOfVariableWithName(assignment.getVariableName());

        this.instructions.add(f("sts %s", index));
    }


    public void visit(Read readStatement) throws Error {
        new Assignment(
                readStatement.getVariableName(),
                new Asm("in")
        ).accept(this);
    }


    public void visit(Write writeStatement) throws Error {
        writeStatement.getExpression().accept(this);
        this.instructions.add("out");
    }




    //
    // EXPRESSIONS
    //



    @Override
    public void visit(Variable variable) throws Error {
        // [precondition] the function we're trying to call actually exists
        if (!this.scope.contains(variable.getName())) {
            throw new Error("Unable to find symbol '%s'", variable.getName());
        }

        // this is where we somehow need to access the variable and make it available to the assembly program
        // how are we doing this?
        // we check which one it is (ie we get its index in the current scope)
        // then fetch that one off the frame and push it onto the stack after we're done (not sure when that is or how we would even find out)

        int index = this.scope.indexOfVariableWithName(variable.getName());
        this.instructions.add(f("lds %s", index));
    }




    @Override
    public void visit(Number number) throws Error {
        Consumer<Integer> ldi = n -> this.instructions.add(f("ldi %s", n));

        int value = number.getValue();

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
        binaryExpression.getRhs().accept(this);
        binaryExpression.getLhs().accept(this);

        // add the binop's instruction to the instructions list
        this.instructions.add(binaryExpression.getBinop().instruction);
    }

    public void visit(Unary unaryExpression) throws Error {
        // unary is always `input * -1`
        new Binary(
                unaryExpression.getExpression(),
                Binary.Binop.MULTIPLICATION,
                new Number(-1)
        ).accept(this);
    }


    @Override
    public void visit(Call call) throws Error {
        // [precondition] the function we're trying to call actually exists
        if (!this.globalScope.contains(call.getFunctionName())) {
            throw new Error("Unable to find symbol '%s'", call.getFunctionName());
        }

        // 1. push the arguments onto the stack, in reverse order
        for (ListIterator<Expression> iterator = call.getArguments().listIterator(call.getArguments().size()); iterator.hasPrevious(); ) {
            Expression arg = iterator.previous();
            arg.accept(this);
        }

        // 2. push the destination label onto the stack
        this.instructions.add(f("ldi %s", call.getFunctionName()));

        // 3. call w/ the passed number of arguments
        this.instructions.add(f("call %s", call.getArguments().size()));
    }


    public void visit(Composite composite) throws Error {
        for (Statement statement : composite.getStatements()) {
            statement.accept(this);
        }
    }



    @Override
    public void visit(If ifStatement) throws Error {
        int captured_counter = this.if_while_counter;
        java.util.function.Function<String, String> generateIfLabel = type -> f("%s_if_%02d_body_%s", this.functionName, captured_counter, type);

        boolean hasElseBranch = ifStatement.getElseBranch() != null;

        // 1. handle the condition
        ifStatement.getCondition().accept(this);

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
        ifStatement.getThenBranch().accept(this);
        this.instructions.add("ldi -1");
        this.instructions.add(f("jump %s", generateIfLabel.apply("end")));
        this.instructions.add("");

        // 5. handle the else branch
        if (hasElseBranch) {
            this.instructions.add(generateIfLabel.apply("else") + ":");
            ifStatement.getElseBranch().accept(this);
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
        whileStatement.getCondition().accept(this);
        this.instructions.add("");

        // 2. jump to the body, if the condition evaluated to true
        this.instructions.add(f("jump %s", generateLabel.apply("body")));

        // 3. jump to the end of the while statement if the condition didn't evaluate to true
        this.instructions.add("ldi -1");
        this.instructions.add(f("jump %s", generateLabel.apply("end")));
        this.instructions.add("");

        // 4. handle the body
        this.instructions.add(generateLabel.apply("body") + ":");
        whileStatement.getBody().accept(this);
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
        condition.getLhs().accept(this);
        condition.getRhs().accept(this);

        // the last two values on the stack are now one of these options:
        // -1, -1   (lhs: true  | rhs: true )
        // -1,  0   (lhs: true  | rhs: false)
        //  0, -1   (lhs: false | rhs: true )
        //  0,  0   (lhs: false | rhs: false)

        // we now add the last two entries on the stack
        // if the result is -2, both are true
        // if the result is -1, one of them is true
        // if the result is  0, both are false

        int expectedResult = condition.getOperator() == Condition.Binary.Bbinop.AND ? -2 : -1;

        this.instructions.add("add");
        this.instructions.add(f("ldi %s", expectedResult));
        this.instructions.add("eq");
    }


    @Override
    public void visit(Condition.Comparison condition) throws Error {
        // rhs first bc the stack is LIFO
        condition.getRhs().accept(this);
        condition.getLhs().accept(this);

        this.instructions.addAll(condition.getComparator().instructions);

    }


    @Override
    public void visit(Condition.Unary condition) throws Error {
        condition.getCondition().accept(this);

        this.instructions.add("NOT");
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
