package task_07.ast;

import task_07.ast.expression.*;
import task_07.ast.expression.Number;
import task_07.ast.statement.*;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;


/*
* Use the Parser class to turn a String containing a valid MiniJava program into an AST
* */


// all of this is one giant clusterfuck,
// but it works, which is all that matters


public class Parser {

    //
    // Utils
    //

    // set to true for verbose logging
    static public boolean DEBUG = false;

    // log the name of the calling method
    static void logm() {
        if (!DEBUG) return;
        System.out.format("\n\n\n==== %s ====\n\n", Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    static void unhandledToken(String token) {
        String caller = Thread.currentThread().getStackTrace()[2].getMethodName();
        throw new RuntimeException(String.format("[UNHANDLED TOKEN] caller: '%s', token: '%s'\n", caller, token));
    }

    static void log(String format, Object... args) {
        if (!DEBUG) return;
        String message = String.format(format, args);
        System.out.format("[%s] %s", Thread.currentThread().getStackTrace()[2].getMethodName(), message);
    }



    //
    // Tokenizer
    //

    private String code;
    private String[] tokens;


    private int currentPosition;


    public Parser(String code) {
        this.code = code;
    }



    // split the input source code into tokens
    private void lex() {
        // firstly, we get rid of all comments (aka lines starting w/ two forward slashes)
        // TODO what about somehow keeping these comments and putting them into the assembly code we'll generate later?
        // that'd be pretty sweet

        String codeWithoutComments = Arrays.stream(this.code.split("\n"))
                .filter(line -> !line.startsWith("//"))
                .collect(Collectors.joining("\n"));

        // now, we split the code into individual tokens
        this.tokens = Utils.split(codeWithoutComments, " ", "-", "+", "*", "/", ",", ";", "(", ")", "{", "}", "[", "]", "\n")
                .stream()
                .filter(token -> !token.equals(" ") && !token.equals("\n"))
                .toArray(String[]::new);

        for (int i = 0; i < tokens.length; i++) {
            log("%02d: %s\n", i, tokens[i]);
        }
    }


    public Program parse() {
        this.lex();
        return parseProgram();
    }


    static List<String> types = Arrays.asList("int", "int[]");



    // check whether `name` is valid, according to the minijava rules
    static boolean isValidName(String name) {
        log("checking if '%s' is a valid name\n", name);
        return name.matches("[A-Za-z]+(?:[A-Za-z]|\\d)*");
    }

    // check whether `declaration` is valid, according to the minijava rules
    static boolean isValidVariableDeclaration(String declaration) {
        String name_regex = "[A-Za-z]+(?:[A-Za-z]|\\d)*";
        String full_regex = String.format("int(?:\\[\\])? %s(?:, %s)*;", name_regex, name_regex);

        return declaration.matches(full_regex);
    }



    // program aka function*
    Program parseProgram() {

        List<Function> functions = new ArrayList<>();

        while (currentPosition < tokens.length) { // we have to stop after processing the last function
            Function fn = parseFunction();

            if (fn == null) {
                break;
            } else {
                functions.add(fn);
            }
        }

        log("functions: %s\n", functions);

        return new Program(functions);

        //int position = 0;
        //while (position < program.length && position != -1) {
        //    position = parseFunction(program, position);
        //    System.out.format("position after parsing function: %s\n", position);
        //}
        //return position;
    }


    static boolean isValidFunctionSignature(String signature) {
        String intTypeFollowedByValidIdentifier = "int(?:\\[\\])? (?:[A-Za-z]+(?:[A-Za-z]|\\d)*)";

        String regex = String.format("%s\\((?:(?:%s)(?:,[ ]*%s)*)?\\)",
                intTypeFollowedByValidIdentifier,
                intTypeFollowedByValidIdentifier,
                intTypeFollowedByValidIdentifier
        );

        return signature.matches(regex);
    }


    // TODO return type!!!
    Function parseFunction() {
        // function regex

        // HOW?
        // 1. 'recreate' the function signature
        // 2. walk through it and check that return type, name, argtypes, argnames, etc are all valid



        // signature is in the form of `int FN_NAME(int arg0,int arg1...)`
        String signature = "";

        while (true) {
            String nextToken = this.tokens[currentPosition++];

            signature += nextToken;

            if (nextToken.equals(")")) {
                break;
            } else if (types.contains(nextToken)) {
                signature += " ";
            }
        }

        if (signature.contains("int []")) {
            signature = signature.replace("int []", "int[] ");
        }


        if (!isValidFunctionSignature(signature)) {
            log("'%s' is not a valid function signature!\n", signature);
            return null;
        }





        // end of function
        // in order to create the `ast.Function` object, we need additional information:
        // the function name, as well as the names of its parameters
        // we fetch that info using a regex (this is really simple since `int` is our only type)

        Pattern pattern = Pattern.compile("int(?:\\[\\])? ([A-Za-z]+(?:[A-Za-z]|\\d)*)");
        Matcher matcher = pattern.matcher(signature);


        String functionName = null;
        List<String> parameterNames = new ArrayList<>();

        int iteration = 0;
        while (matcher.find()) {
            // first iteration gets the function name, all following iterations get the parameter names
            String symbolName = matcher.group(1);

            if (iteration == 0) {
                functionName = symbolName;
            } else {
                parameterNames.add(symbolName);
            }

            iteration++;
        }


        log("function_name: %s, parameters: %s\n", functionName, parameterNames);





        if (!this.tokens[currentPosition].equals("{")) {
            // function body is wrapped in curly braces
            return null;
        }

        currentPosition +=1 ; // increment the position by one to jump into the function body

        // next, we parse the contents of the function body
        // a function body consists of zero or more declarations, followed by zero or more stataments

        List<Declaration> declarations = new ArrayList<>();

        while (true) {
            Declaration declaration = parseDecl();

            if (declaration != null) {
                declarations.add(declaration);
            } else {
                break;
            }
        }

        log("decls until %s (%s)\n", currentPosition, tokens[currentPosition]);
        log("declarations: %s\n", declarations);


        List<Statement> statements = new ArrayList<>();
        while (true) {
            Statement statement = parseStatement();

            if (statement != null) {
                statements.add(statement);
            } else {
                break;
            }
        }


        if (tokens[currentPosition].equals("}")) {
            currentPosition++;
            return new Function(functionName, parameterNames, declarations, statements);
        } else {
            return null;
        }
    }




    Declaration parseDecl() {
        logm();

        String token = tokens[currentPosition];

        log("token: %s pos: %s\n", token, currentPosition);

        // variable declarations always start w/ the type
        // and we only allow integers
        if (!types.contains(token)) {
            return null;
        }

        // walk forward and collect tokens until we reach a semicolon

        String fullDeclaration = token;

        while (true) {
            String nextToken = tokens[++currentPosition];

            if (isValidName(nextToken)) {
                fullDeclaration += " ";
            }

            fullDeclaration += nextToken;

            if (nextToken.equals(";")) {
                break;
            }
        }

        if (!isValidVariableDeclaration(fullDeclaration)) {
            return null;
        }

        // remove the typename at the beginning of the declaration.
        // this makes extracting the individual variable names w/ a regex a lot easier
        fullDeclaration = fullDeclaration
                .replace("int ", "")
                .replace("int[] ", "");

        Pattern pattern = Pattern.compile("([A-Za-z]+(?:[A-Za-z]|\\d)*)");
        Matcher matcher = pattern.matcher(fullDeclaration);

        List<String> variableNames = new ArrayList<>();
        while (matcher.find()) {
            variableNames.add(matcher.group(0));
        }

        log("FULL DECL: %s\n", fullDeclaration);

        currentPosition++;

        log("will return decl w/ names %s\n", variableNames);
        return new Declaration(variableNames);
    }









    Statement parseStatement() {
        if (currentPosition >= tokens.length - 1) return null;

        logm();
        String token = tokens[currentPosition];

        log("token: %s, from: %s\n", token, currentPosition);


        /*
         *
         * [x]   = ;
         * [x] | { <stmt>* }
         * [x] | <name> = <expr>;
         * [x] | <name>[<expr>] = <expr>;
         * [x] | <name> = read();
         * [x] | write(<expr>);
         * [x] | if (<cond>) <stmt>
         * [x] | if (<cond>) <stmt> else <stmt>
         * [x] | while(<cond>) <stmt>
         * [x] | return <expr>
         *
         * */

        if (token.equals("asm")) {
            currentPosition += 2;
            String asm_instruction = tokens[currentPosition++]
                    .replace("\"", "")
                    .replace("_", " ");

            currentPosition += 2;

            return new Asm(asm_instruction);
        }

        if (token.equals(";")) {
            // do nothing, but increment the position
            currentPosition++;

            // returning an empty asm is basically a noop
            return new Asm("");
        }

        if (token.equals("{")) {
            log("OPENING CURLY BRACE OH MY GOD\n");
            currentPosition += 1;


            List<Statement> statements = new ArrayList<>();

            while (true) {
                Statement statement = parseStatement();

                if (statement == null) {
                    return null;
                }

                statements.add(statement);

                if (tokens[currentPosition].equals("}")) {
                    currentPosition++;
                    return new Composite(statements);
                }
            }

        }


        if (isValidName(token) && tokens[currentPosition + 1].equals("=")) {
            // variable assignment!

            boolean isReadAssignment =
                            tokens[currentPosition + 2].equals("read") &&
                            tokens[currentPosition + 3].equals("(")    &&
                            tokens[currentPosition + 4].equals(")")    &&
                            tokens[currentPosition + 5].equals(";");

            if (isReadAssignment) {
                currentPosition += 6;
                return new Read(token);
            }

            // increment by 2 to jump to the assignment expression (2 bc there's a '=' before)
            currentPosition += 2;

            Expression expressionThatAssignsToCurrentToken = parseExpression();
            log("assigning expr: %s\n", expressionThatAssignsToCurrentToken);

            if (expressionThatAssignsToCurrentToken != null && tokens[currentPosition].equals(";")) {
                log("will return new assignment to '%s' (pos at %s (%s))\n", token, currentPosition, tokens[currentPosition]);
                this.currentPosition++; // inc bc it currently points at the EOL semicolon
                return new Assignment(token, expressionThatAssignsToCurrentToken);
            } else {
                log("assigning expr is null %s (%s)\n", currentPosition, tokens[currentPosition]);
            }
        }

        if (isValidName(token) && tokens[currentPosition + 1].equals("[")) {
            // array element assignment
            log("array element assignment!\n");

            currentPosition += 2; // jump to the expression constructing the offset of the array element we're assigning to

            Expression arrayOffsetExpression = parseExpression();

            currentPosition += 1; // jump to after the closing bracket

            if (!tokens[currentPosition].equals("=")) {
                return null;
            }

            currentPosition += 1;

            Expression assignedValue = parseExpression();


            if (!tokens[currentPosition].equals(";")) {
                return null;
            }

            currentPosition += 1; // jump to after the closing semicolon

            return new ArrayElementSetter(token, arrayOffsetExpression, assignedValue);
        }




        // statement is some token that's followed by an opening parentheses
        // the only options for this are `read`, `write`, `while` and `if` [`else`]
        if (tokens[currentPosition + 1].equals("(")) {
            if (token.equals("write")) {

                currentPosition += 2;
                Expression writeEpression = parseExpression();

                if (writeEpression != null) {
                    boolean isWellFormedWriteExpression =
                                    tokens[currentPosition].equals(")") &&
                                    tokens[currentPosition + 1].equals(";");

                    if (isWellFormedWriteExpression) {
                        currentPosition += 2;
                        return new Write(writeEpression);
                    } else {
                        return null;
                    }
                }

            } else if (token.equals("while") || token.equals("if")) {

                log("if/else statement\n");

                currentPosition += 2; // '{while|if} ('
                Condition condition = parseCondition();
                if (condition == null) {
                    log("condition is null\n");
                    return null;
                }

                if (tokens[currentPosition].equals(")")) {
                    // we now need to get the body of the statement

                    log("will get body of if/else statement\n");

                    // optimization: we first check whether the body is empty
                    Statement body_statement;
                    Statement potentialElseStatement = null;
                    if (tokens[currentPosition + 2].equals("}")) {
                        body_statement = new Asm("");

                    } else {
                        // no empty body
                        currentPosition += 1;
                        body_statement = parseStatement();
                    }


                    // if we're currently parsing an if statement, check whether it has an else clause
                    // and parse that as well if necessary
                    if (token.equals("if") && body_statement != null && currentPosition < tokens.length) {
                        if (tokens[currentPosition].equals("else")) {
                            currentPosition += 1;
                            potentialElseStatement = parseStatement();
                        }
                    }

                    if (token.equals("while")) {
                        return new While(condition, body_statement);
                    } else { // if
                        return new If(condition, body_statement, potentialElseStatement);
                    }


                }
            }
        }

        if (token.equals("return")) {
            log("RETURN STATEMENT\n");
            currentPosition += 1;
            Expression returnExpression = parseExpression();

            if (returnExpression != null && tokens[currentPosition].equals(";")) {
                currentPosition++;
                return new Return(returnExpression);
            } else {
                return null;
            }
        }

        log("unhandled statement token: '%s'\n", token);

        return null;
    }




    Expression parseExpression() {
        logm();

        /*
         * [x] <number>
         * [x] | <name>
         * [x] | new int [<expr>]
         * [x] | <expr>[<expr>]
         * [x] | <name>(ε|<expr>(, <expr>)*)
         * [x] | (<expr>)
         * [x] | <unop> <expr>
         * [x] | <expr> <binop> <expr>
         *
         * */

        log("token: %s (at %s)\n", tokens[currentPosition], currentPosition);



        boolean isArrayCreation =
                tokens[currentPosition].equals("new") &&
                        tokens[currentPosition + 1].equals("int") &&
                        tokens[currentPosition + 2].equals("[");

        if (isArrayCreation) {
            currentPosition += 3; // jump over 'new', 'int' and the opening bracket
            Expression arraySizeExpression = parseExpression();
            currentPosition += 1; // jump over the closing bracket
            return new ArrayCreation(arraySizeExpression);

        }

        Expression expression;

        expression = parseNumber();
        if (expression == null) {
            expression = parseName();
        }


        if (expression instanceof Variable && tokens[currentPosition].equals("(")) {
            log("FUNCTION CALL\n");
            // name, followed by an opening parentheses -> function call
            // 3 possible cases:
            //   - no parameters
            //   - a single parameter ('<expr>')
            //   - multiple parameters ('<expr>, <expr>*')

            // FIXME this is not good code
            // update: who cares as long as it works
            String functionName = ((Variable)expression).name;
            log("function name: %s\n", functionName);

            if (tokens[currentPosition + 1].equals(")")) {
                // no parameters
                currentPosition += 1;
                expression = new Call(functionName);
            }

            List<Expression> parameters = new ArrayList<>();

            currentPosition += 1; // jump over the function's opening paren
            // go through the individual parameter expressions
            while (true) {
                Expression parameter = parseExpression();

                if (parameter != null) {
                    parameters.add(parameter);
                } else {
                    log("param is null; pos: %s (%s)\n", currentPosition, tokens[currentPosition]);
                    // the fact that the parameter is null DOES NOT mean that we
                    // actually reached the end of the parameters. It's also null when we encounter a comma bc that's not a valid expression
                    if (tokens[currentPosition].equals(",")) {
                        currentPosition += 1;
                    } else {
                        break;
                    }
                }
            }

            log("parameters: %s\n", parameters);

            // end of fetching parameters
            // increment by one if we're currently at the function call's closing paren
            // (this happens when we pass multiple parameters)
            if (tokens[currentPosition].equals(")")) {
                log("did reach end of function call\n");
                currentPosition += 1;

                // inline array length access, instead of calling a separate function
                // why? implementing `length` as a builtin function would include a lot of runtime overhead
                // which we can avoid by simply inserting these instructions inline
                if (functionName.equals("length") && parameters.size() == 1) {
                    expression = new ArrayLength_inline(parameters.get(0));
                } else {
                    expression = new Call(functionName, parameters);
                }
            }
        }

        if (expression != null && tokens[currentPosition].equals("[")) {
            // accessing an array element

            currentPosition += 1; // jump over the opening bracket
            Expression offsetExpression = parseExpression();
            currentPosition += 1; // jump over the closing bracket

            expression = new ArrayElementGetter(expression, offsetExpression);
        }



        if (expression == null) {
            if (tokens[currentPosition].equals("-")) { // or currentPosition + 1 ???
                currentPosition += 1;

                Expression expressionFollowingTheUnop = parseExpression();

                if (expressionFollowingTheUnop != null) {
                    log("unop detected. will return %s\n", expressionFollowingTheUnop);
                    log("after unop: %s (%s)\n", currentPosition, tokens[currentPosition]);
                    expression = new Unary(Unary.Unop.MINUS, expressionFollowingTheUnop);
                } else {
                    return null;
                }
            } else {
                // token is neither a number, nor a name, nor an unop
                // the only remaining option is that we're dealing w/ another expression wrapped in parens
                if (tokens[currentPosition].equals("(")) {
                    currentPosition +=1 ;
                    Expression expressionWrappedInParens = parseExpression();
                    log("expression wrapped in parens: %s\n", expressionWrappedInParens);

                    // set currentPosition to the token after the closing parens
                    currentPosition += 1;

                    // pass the found expression on, we'll check for a potential binop below
                    expression = expressionWrappedInParens;
                }
            }

        }

        if (expression != null) {
            log("expression is not null, looking for potential binop (expr: %s)\n", expression);
            Binary.Binop pot_binop = parseBinop();
            if (pot_binop != null) {
                Expression secondExpressionFollowingTheBinop = parseExpression();

                return new Binary(expression, pot_binop, secondExpressionFollowingTheBinop);
            } else {
                log("no binop found after expression, returning the expression (%s)\n", expression);
                log("pos when returning non-binop expr: %s (%s)\n", currentPosition, tokens[currentPosition]);
                return expression;
            }
        }

        return null;
    }


    Condition parseCondition() {
        logm();
        log("parsing condition starting at %s (%s)\n", currentPosition, tokens[currentPosition]);

        /*
         *
         *
         * [x] true | false
         * [x] | (<cond>)
         * [x] | <expr> <comp> <expr>
         * [x] | <bunop> (<cond>)
         * [x] | <cond> <bbinop> <cond>
         *
         * */

        String token = tokens[currentPosition];

        if (token.equals("true")) {
            currentPosition += 1;
            return new Condition.True();
        }

        if (token.equals("false")) {
            currentPosition += 1;
            return new Condition.False();
        }


        if (token.equals("(")) {

            currentPosition += 1;   // skip the opening paren
            Condition condition = parseCondition();
            currentPosition += 1;   // skip the closing paren

            return condition;
        }


        if (parseBunop() != null) {
            log("it's a bunop!\n");
            currentPosition += 1;
            Condition bunopCondition = parseCondition();

            if (bunopCondition == null) {
                log("bunop condition is null\n");
                return null;
            }

            if (parseBbinop() != null) {
                // bunop followed by bbinop
                log("IS FOLLOWED BY BBINOP\n");
            } else {
                log("about to return bunop node %s (%s)\n", currentPosition, tokens[currentPosition]);
                currentPosition += 1;
                return new Condition.Unary(Condition.Unary.Bunop.NOT, bunopCondition);
            }
        }


        log("about to check whether it's a comparison. pos: %s (%s)\n", currentPosition, tokens[currentPosition]);
        Condition.Comparison comparison = null;


        Expression lhsExpression = parseExpression();
        log("lhs expr: %s\n", lhsExpression);

        log("pos after getting lhs: %s (%s)\n", currentPosition, tokens[currentPosition]);

        if (lhsExpression != null) {
            Condition.Comparison.Comparator comparator = parseComp();
            log("comparator: %s\n", comparator);
            if (comparator == null) {
                return null;
            }


            Expression rhsExpression = parseExpression();
            log("rhs expr: %s\n", rhsExpression);
            if (rhsExpression == null) {
                return null;
            }

            comparison = new Condition.Comparison(lhsExpression, comparator, rhsExpression);

            if (tokens[currentPosition].equals(")")) {
                log("pos before returning comparison: %s (%s)\n", currentPosition, tokens[currentPosition]);
                return comparison;
            }

        }


        Condition.Binary.Bbinop bbinop = parseBbinop();

        if (bbinop != null) {
            return new Condition.Binary(comparison, bbinop, parseCondition());
        } else {
            return null;
        }


    }


    Number parseNumber() {
        logm();

        String number = tokens[currentPosition];
        if (number.matches("[0-9]+")) {
            currentPosition += 1;
            return new Number(Integer.parseInt(number));
        }

        return null;
    }


    Variable parseName() {
        logm();

        String name = tokens[currentPosition];
        if (isValidName(name)) {
            currentPosition += 1;
            return new Variable(name);
        }

        return null;
    }


    Condition.Comparison.Comparator parseComp() {
        logm();

        Map<String, Condition.Comparison.Comparator> mappedComparators = new HashMap<>();

        mappedComparators.put("==", Condition.Comparison.Comparator.EQUALS);
        mappedComparators.put("!=", Condition.Comparison.Comparator.NOT_EQUALS);
        mappedComparators.put("<=", Condition.Comparison.Comparator.LESS_EQUAL);
        mappedComparators.put("<",  Condition.Comparison.Comparator.LESS);
        mappedComparators.put(">=", Condition.Comparison.Comparator.GREATER_EQUAL);
        mappedComparators.put(">",  Condition.Comparison.Comparator.GREATER);


        Condition.Comparison.Comparator comparator = mappedComparators.get(tokens[currentPosition]);
        if (comparator != null) {
            currentPosition += 1;
        }

        log("will return %s\n", comparator);
        return comparator;
    }


    Condition.Unary.Bunop parseBunop() {
        logm();

        if (tokens[currentPosition].equals("!")) {
            currentPosition += 1;
            return Condition.Unary.Bunop.NOT;
        }

        return null;
    }


    Binary.Binop parseBinop() {
        logm();

        Map<String, Binary.Binop> mappedBinops = new HashMap<>();

        mappedBinops.put("-", Binary.Binop.SUBTRACTION);
        mappedBinops.put("+", Binary.Binop.ADDITION);
        mappedBinops.put("*", Binary.Binop.MULTIPLICATION);
        mappedBinops.put("/", Binary.Binop.DIVISION);
        mappedBinops.put("%", Binary.Binop.MODULO);

        Binary.Binop binop = mappedBinops.get(tokens[currentPosition]);

        if (binop != null) {
            currentPosition += 1;
        }

        log("will return binop %s\n", binop);
        return binop;
    }

    Condition.Binary.Bbinop parseBbinop() {
        logm();

        Map<String, Condition.Binary.Bbinop> mappedBbinops = new HashMap<>();

        mappedBbinops.put("&&", Condition.Binary.Bbinop.AND);
        mappedBbinops.put("||", Condition.Binary.Bbinop.OR);


        Condition.Binary.Bbinop bbinop = mappedBbinops.get(tokens[currentPosition]);
        if (bbinop != null) {
            currentPosition += 1;
        }

        log("will return bbinop %s\n", bbinop);
        return bbinop;
    }




    static private  class Utils {
        // split `input` by one or more one-character delimiters, but keep the delimiters in the resulting array
        // this internally uses a regex, but you do not need to escape the delimiters
        public static List<String> split(String input, String... delimiters) {

            // Create a regex pattern for splitting by a specific character
            java.util.function.Function<String, String> regexPattern = delimiter -> String.format("((?<=\\%s)|(?=\\%s))", delimiter, delimiter);

            List<List<String>> components = new ArrayList<>();

            for (String delimiter : delimiters) {
                if (components.isEmpty()) {
                    for (String s : input.split(regexPattern.apply(delimiter))) {
                        components.add(Collections.singletonList(s));
                    }
                } else {
                    components = components
                            .stream()
                            .map(sublist -> sublist
                                    .stream()
                                    .map(s -> Arrays.asList(s.split(regexPattern.apply(delimiter))))
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList()))
                            .collect(Collectors.toList());
                }
            }

            return components
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
    }


}
