package task_09.compiler;

import task_09.Util;
import task_09.arc.Trackable;
import task_09.ast.Declaration;
import task_09.ast.Function;
import task_09.ast.Program;
import task_09.ast.Variable;
import task_09.ast.expression.*;
import task_09.ast.expression.Number;
import task_09.ast.statement.*;
import task_09.interpreter.Interpreter;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static task_09.Util.f;


/*
* The class descriptor cache is where we register all of a program's classes during codegen
*
* It manages these classes and uses their metadata to calculate information about the classes' memory layout
* */
class ClassDescriptorCache {
    // ugh sorry this is a singleton
    static final ClassDescriptorCache shared = new ClassDescriptorCache();

    private final Map<String, ClassDescriptor> classDescriptors = new HashMap<>();


    void register(ClassDescriptor classDescriptor) {
        classDescriptors.put(classDescriptor.classname, classDescriptor);
    }


    boolean classExists(String classname) {
        return classDescriptors.containsKey(classname);
    }


    String superclass(String classname) {
        return classDescriptors.get(classname).superclass;
    }


    boolean hasSuperclass(String classname) {
        return superclass(classname) != null;
    }


    // Check whether a class implements a method
    // This just determines whether the class has its own implementation of the selector,
    // not whether objects of that class respond to that selector
    boolean implementsMethod(String classname, String selector) {
        return Util.containsWhere(classDescriptors.get(classname).functions, fn -> fn.name.equals(selector));
    }


    // get the total size of a class
    // this includes:
    // the vtable
    // - all ivars
    // - all inherited ivars
    int sizeof(String classname) {
        if (!classDescriptors.containsKey(classname)) {
            throw new RuntimeException(f("Unable to find class '%s'. Forward declarations are not supported!", classname));
        }

        ClassDescriptor classDescriptor = classDescriptors.get(classname);

        int size = 1; // vtable

        size += classDescriptor.ivars.size();

        String superclass = classDescriptor.superclass;
        while (superclass != null) {
            ClassDescriptor super_classDescriptor = classDescriptors.get(superclass);
            size += super_classDescriptor.ivars.size();

            superclass = super_classDescriptor.superclass;
        }

        return size;
    }


    // get an object's vtable
    List<String> vtable(String classname) {
        List<String> inheritanceTree = new ArrayList<>();

        while (true) {
            inheritanceTree.add(classname);
            if (hasSuperclass(classname)) {
                classname = superclass(classname);
            } else {
                break;
            }
        }
        classname = inheritanceTree.get(0);

        Collections.reverse(inheritanceTree);

        List<String> vtable = new ArrayList<>();

        for (String classname_ : inheritanceTree) {
            ClassDescriptor classDescriptor = classDescriptors.get(classname_);

            for (Function fn : classDescriptor.functions) {
                if (fn.isInitializer) continue;

                if (!Util.containsWhere(vtable, sel -> sel.endsWith(fn.name))) {
                    vtable.add(fn.mangledName());
                } else {
                    int index = Util.indexWhere(vtable, sel -> sel.endsWith(fn.name));
                    vtable.set(index, fn.mangledName());
                }
            }
        }

        return vtable;
    }


    // get an ivar's offset, relative to the object's vtable
    int getIvarOffset(String classname, String ivarName) {
        if (!hasSuperclass(classname)) {
            return classDescriptors.get(classname).ivarNames.indexOf(ivarName) + 1;
        }

        return classDescriptors.get(classname).ivarNames.indexOf(ivarName) + sizeof(superclass(classname));
    }
}



///////////////////////////////////////////////////////
//                      codegen                      //
///////////////////////////////////////////////////////


public class CodeGenerationVisitor implements Visitor {

    // option to disable arc (automatic reference counting)
    private static final boolean DISABLE_ARC = Trackable.Options.DISABLED = false;

    private final List<String> instructions = new ArrayList<>();

    // additional information about the current scope
    // this is used internally when generating instructions for functions
    private final Scope scope;              // scope of the current function (arguments + local variables)
    private final String functionName;      // name of the current function
    private final Map<String, Integer> globalScopeInfo; // [function_name: #args]


    // counter we use to ensure the labels generated for if/while statements are unique
    private int if_while_counter = 0;
    private void inc_if_while_counter() { if_while_counter++; }

    private static final List<String> builtins = Arrays.asList("length", "alloc");


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


    // codegen that "inherits" basic knowledge about the current program
    // we use this when we need a separate codegen visitor
    CodeGenerationVisitor(CodeGenerationVisitor otherVisitor) {
        this.scope = otherVisitor.scope;
        this.functionName = otherVisitor.functionName;
        this.globalScopeInfo = otherVisitor.globalScopeInfo;

        commonInit();
    }

    CodeGenerationVisitor(List<Declaration> parameters, List<Declaration> localVariables, String functionName, Map<String, Integer> globalScopeInfo) {

        this.scope = new Scope(parameters, localVariables);
        this.functionName = functionName;
        this.globalScopeInfo = globalScopeInfo;

        commonInit();
    }

    private void commonInit() {
        // builtins

        // length is inserted as an inline function every time it's called
        globalScopeInfo.put("length", 1);

        // alloc is inserted as a global function, but only if necessary
        globalScopeInfo.put("alloc", 2);
    }


    @Override
    public void visit(Program program) throws Error {

        handleProgram_preflight(program);

        // Register the classes
        program.classDescriptors.forEach(ClassDescriptorCache.shared::register);

        // run codegen for classes

        for (ClassDescriptor classDescriptor : program.classDescriptors) {
            handleFunctions(classDescriptor.functions);
        }

        // run codegen for global functions
        handleFunctions(program.functions);


        // include the `alloc` function, if necessary
        if (instructions.contains("ldi alloc")) {
            this.instructions.addAll(Arrays.asList(
                    "",
                    "alloc:",
                    "alloc 2",
                    "lds 0",
                    "alloch",
                    "sts 1",
                    "lds -1",
                    "alloch",
                    "sts 2",
                    "lds 2",
                    "ldi 0",
                    "lds 1",
                    "sth",
                    "lds 1",
                    "return 4"
            ));
        }


        // after having parsed the program, we add the `end` label (that's where we jump to after returning from the `main` function)
        this.instructions.add("");
        this.instructions.add("end:");

        // every program needs an entry point
        if (!instructions.contains("main:")) {
            throw new Error("Unable to find entry point. You have to specify a 'main' function");
        }
    }


    // preprocessing of the classes
    // this is where we register all global symbols
    // (this allows us to support accessing yet to be defined symbols w/out forward declarations)
    private void handleProgram_preflight(Program program) throws Error {
        // 1 check for duplicate global symbols
        // we have to run this before processing any of the actual functions bc this
        // functions list also serves as our symbol lookup table when processing function calls

        Set<String> functionNames = new HashSet<>();

        Util.ThrowingConsumer<List<Function>, Error> handleFunctions = functions -> {
            for (Function function : functions) {
                if (functionNames.contains(function.mangledName())) {
                    throw new Visitor.Error("Found duplicate symbol '%s", function.mangledName());
                } else if (builtins.contains(function.name)) {
                    throw new Error("Invalid function name. '%s' is already a builtin!", function.name);
                } else {
                    functionNames.add(function.mangledName());
                    globalScopeInfo.put(function.mangledName(), function.parameters.size());
                }
            }
        };

        handleFunctions.apply(program.functions);


        // java sucks af and that's why we have to use a for loop instead of the much nicer `List<T>::forEach` (the block can't throw)
        for (ClassDescriptor cls : program.classDescriptors) {
            handleFunctions.apply(cls.functions);
        }
    }


    // Generate the instructions for a collection of `Function`s
    // [Q] Why do we need a separate function just for this / how does this work?
    // [A] We call this function multiple times, for different scopes:
    //     1. The global scope (ie all global functions (ie all functions that are not part of a class))
    //     2. The 'local' scope of each class
    private void handleFunctions(List<Function> functions) throws Error {

        // reorder the functions to make sure that main is always first
        // this isn't actually necessary, but IMO makes the code look nicer
        functions = functions
                .stream()
                .sorted((f0, f1) -> f0.name.equals("main") ? -1 : 1)
                .collect(Collectors.toList());

        for (Function function : functions) {

            // List of instructions for the function we're currently processing
            List<String> fn_instructions = new ArrayList<>();

            // create the function's entry point
            fn_instructions.add("");
            fn_instructions.add(f("%s:", function.mangledName()));


            // collect all variables in the current scope (arguments + locally defined ones)

            List<String> scope = new ArrayList<>();

            Consumer<List<Declaration>> handleDecls = decls -> {
                scope.addAll(
                        decls
                                .stream()
                                .map(decl -> decl.names)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList())
                );
            };

            handleDecls.accept(function.parameters);
            handleDecls.accept(function.declarations);


            // [precondition check] no duplicates in local namespace
            if (Util.hasDuplicates(scope)) {
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
                    function.parameters,           // arguments
                    function.declarations,         // declarations
                    function.mangledName(),        // name of the current function
                    globalScopeInfo                // [function_name: #args]
            );

            // determine whether the first statement is a call to the superclasses initializer
            java.util.function.Function<Function, Boolean> fn_firstStatementIsSuperInit = fn -> {
                if (fn.statements.isEmpty()) return false;

                Statement firstStatement = fn.statements.get(0);
                if (!(firstStatement instanceof ExpressionStatement)) return false;

                Expression expression = ((ExpressionStatement) firstStatement).expression;
                if (!(expression instanceof InstanceMethodCall)) return false;

                InstanceMethodCall call = (InstanceMethodCall) expression;
                return call.targetName.equals("super") && call.selector.equals(ClassDescriptorCache.shared.superclass(fn.name));
            };

            boolean firstStatementIsSuperInit = fn_firstStatementIsSuperInit.apply(function);

            {
                CodeGenerationVisitor codegen = new CodeGenerationVisitor(functionCodeGen);
                if (firstStatementIsSuperInit) {
                    Expression superInitCall = ((ExpressionStatement)function.statements.get(0)).expression;
                    // we have to assign to `this` to avoid duplicates on the stack
                    new Assignment("this", superInitCall).accept(codegen);
                    fn_instructions.addAll(codegen.getInstructions());
                }
            }


            if (function.isInitializer) {
                fn_instructions.addAll(functionCodeGen.generateObjectInitializationInstructions(function.name));
            }

            // generate instructions for all of the function's statements
            for (Statement statement : function.statements) {
                if (firstStatementIsSuperInit && statement == function.statements.get(0)) continue;
                statement.accept(functionCodeGen);
            }

            if (function.isInitializer) {
                if (!DISABLE_ARC) {
                    // Problem: the RETURN instruction retains the return value,
                    // meaning that the retain count of the object we just initialized
                    // is now off by +1. we  counteract that by releasing the object once
                    new Variable("this").accept(functionCodeGen);
                    new Asm("RELEASE").accept(functionCodeGen);
                }
                new Return(new Variable("this")).accept(functionCodeGen);
            }

            fn_instructions.addAll(functionCodeGen.getInstructions());

            this.instructions.addAll(fn_instructions);
        }
    }






    // Generate the initialization instructions for an object of the specified class
    // You have to provide the initializer's local stack space (ie #args + #locals)
    List<String> generateObjectInitializationInstructions(String classname) throws Error {

        // `this` is an empty array of the size we're expecting, containing the vtable (also of the size we're expecting) at index 0
        CodeGenerationVisitor codegen_ = new CodeGenerationVisitor(this);

        // fill the vtable

        List<String> vtable_ = ClassDescriptorCache.shared.vtable(classname);

        Expression getVtable = new ArrayElementGetter(new Variable("this"), new Number(0));

        for (int i = 0; i < vtable_.size(); i++) {
            if (!vtable_.get(i).startsWith(classname)) continue;
            new ArrayElementSetter(
                    getVtable,
                    new Number(i),
                    new Asm(f("ldi %s", vtable_.get(i)))
            ).accept(codegen_);
        }

        return new ArrayList<>(codegen_.getInstructions());
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
        boolean isImplicitSelfAssignment = !this.scope.contains(assignment.variableName) && this.scope.contains("this");

        if (isImplicitSelfAssignment) {
            String typeof_self = this.scope.typeof("this");
            new ArrayElementSetter(
                    new Variable("this"),
                    new Number(ClassDescriptorCache.shared.getIvarOffset(typeof_self, assignment.variableName)),
                    assignment.expression
            ).accept(this);

            return;
        }

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
        arrayElementSetter.target.accept(this);

        this.instructions.add("sth");
    }

    public void visit(ExpressionStatement expressionStatement) throws Error {
        expressionStatement.expression.accept(this);
    }




    //
    // EXPRESSIONS
    //


    @Override
    public void visit(ObjectInitialization objectInitialization) throws Error {
        // call the initializer w/ the supplied arguments

        int sizeof_object = ClassDescriptorCache.shared.sizeof(objectInitialization.classname);
        int sizeof_vtable = ClassDescriptorCache.shared.vtable(objectInitialization.classname).size();

        List<Expression> args = new ArrayList<>();
        args.add(new Call("alloc", new Number(sizeof_object), new Number(sizeof_vtable)));
        args.addAll(objectInitialization.parameters);

        new Call(
                objectInitialization.classname + "_init",
                args
        ).accept(this);
    }


    public void visit(InstanceMethodCall instanceMethodCall) throws Error {
        final String target   = instanceMethodCall.targetName.equals("super") ? "this" : instanceMethodCall.targetName;
        final String selector = instanceMethodCall.selector;
        final boolean isSuperCall = instanceMethodCall.targetName.equals("super");

        String typeOfTargetObject = scope.typeof(target);
        if (typeOfTargetObject == null && ClassDescriptorCache.shared.classExists(instanceMethodCall.selector)) {
            typeOfTargetObject = instanceMethodCall.selector;
        }

        if (Arrays.asList("int", "int[]").contains(typeOfTargetObject)) {
            throw new Error("Cannot call a method on primitive type '%s' (target is %s)", typeOfTargetObject, target);
        }

        List<Expression> args = new ArrayList<>();
        args.add(new Variable(target));
        args.addAll(instanceMethodCall.arguments);


        // we first need to push the arguments onto the stack
        Collections.reverse(args);

        for (Expression arg : args) {
            arg.accept(this);
        }

        java.util.function.Function<String, Integer> getIndexOnVtable = typename -> Util.indexWhere(ClassDescriptorCache.shared.vtable(typename), sel -> sel.endsWith(instanceMethodCall.selector));

        int vtableIndex = getIndexOnVtable.apply(typeOfTargetObject);

        // [super init] call
        if (vtableIndex == -1 && ClassDescriptorCache.shared.classExists(instanceMethodCall.selector)) {
            this.instructions.add(f("ldi %s_init", instanceMethodCall.selector));

        } else if (isSuperCall) {

            if (!ClassDescriptorCache.shared.hasSuperclass(typeOfTargetObject)) {
                throw new Error("Calling `super` in a class that doesn't inherit anything");
            }

            String classname = ClassDescriptorCache.shared.superclass(typeOfTargetObject);
            while (true) {
                if (ClassDescriptorCache.shared.implementsMethod(classname, selector)) {
                    break;
                } else {
                    classname = ClassDescriptorCache.shared.superclass(classname);
                }
            }

            this.instructions.add(f("ldi %s_%s", classname, selector));

        } else { // "regular" call
            CodeGenerationVisitor _codeGen = new CodeGenerationVisitor(this);

            // get the vtable
            Expression vtable = new ArrayElementGetter(
                    new Variable(instanceMethodCall.targetName),
                    new Number(0)
            );

            // get the method address from the vtable
            new ArrayElementGetter(vtable, new Number(vtableIndex)).accept(_codeGen);

            this.instructions.addAll(_codeGen.getInstructions()); // TODO do we _really_ need a separate codegen ???
        }

        // we now have the address of the method on the stack, meaning that we can perform the call\
        this.instructions.add(f("call %s", instanceMethodCall.arguments.size() + 1));

        if (instanceMethodCall.unusedReturnValue && !ClassDescriptorCache.shared.classExists(instanceMethodCall.selector)) {
            // Problem:  the return value of the function call is unused, but still pushed on the stack
            // Solution: we need to remove it off the stack

            if (!DISABLE_ARC) {
                // if arc is enabled, removing a value off the stack is easy: we can simply use the RELEASE instruction
                // this actually kills two birds w/ a single stone because - if the unused return value is an object - we also decrease the retain count by 1
                // (keep in mind that the RETURN instruction increases it by 1)
                this.instructions.add("RELEASE");
            } else {
                // if arc is disabled, we remove the value off the stack by simply performing a jump to the next instruction
                // if the return value is -1, we actually jump to the next instruction
                // if the return value is something else, we don't perform the jump and go to the next anyway
                String label = "@" + Util.uniqueRandomNumber();

                this.instructions.add(f("jump %s", label));
                this.instructions.add(label + ":");
            }
        }
    }


    @Override
    public void visit(Variable variable) throws Error {

        boolean isImplicitSelfAccess = !this.scope.contains(variable.name) && this.scope.contains("this");
        if (isImplicitSelfAccess) {
            String typeof_self = this.scope.typeof("this");
            int offset = ClassDescriptorCache.shared.getIvarOffset(typeof_self, variable.name);
            new ArrayElementGetter(new Variable("this"), new Number(offset)).accept(this);
            return;
        }

        // [precondition] the symbol we're trying to access actually exists in the current scope
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

        // increment the counter to get fresh new unique labels for statements nested in the if statements
        inc_if_while_counter();

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

    }


    public void visit(While whileStatement) throws Error {
        int captured_counter = this.if_while_counter;
        java.util.function.Function<String, String> generateLabel = type -> f("%s_while_%02d_%s", this.functionName, captured_counter, type);

        // increment the counter to get fresh new unique labels for statements nested in the if statements
        inc_if_while_counter();

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
    // It also keeps track of every variable's type
    private static class Scope extends ArrayList<String> {

        final int numberOfArguments;

        // [variableName : typename]
        private final Map<String, String> types = new HashMap<>();

        Scope() {
            super();
            this.numberOfArguments = 0;
        }

        Scope(List<Declaration> arguments, List<Declaration> declarations) {
            super();
            this.numberOfArguments = arguments.size();

            Consumer<List<Declaration>> handleVariables = decls -> {
                for (Declaration declaration : decls) {
                    this.addAll(declaration.names);

                    declaration.names.forEach(name -> this.types.put(name, declaration.typename));
                }
            };

            handleVariables.accept(arguments);
            handleVariables.accept(declarations);

        }

        int indexOfVariableWithName(String name) {
            int idx = this.indexOf(name) + 1;

            if (idx <= numberOfArguments) {
                return -(idx - 1);
            } else {
                return idx - numberOfArguments;
            }
        }


        String typeof(String name) {
            return this.types.get(name);
        }
    }
}
