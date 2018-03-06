/*
*
* exercise-08/task-06
*
* parsing a java program to check if the syntax is valid
* */


//
// PSA: this is the worst code ever.
// it's incredibly badly structured, really difficult to read
// and understand and tbh i still can't believe that it actually works
//


import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class Main_Task_06 {

    // set to true for verbose logging
    static private boolean DEBUG = false;

    public static void main(String... args) {

        String input;

        if (args.length == 1) {
            String path = Paths.get(System.getProperty("user.dir"), args[0]).toString();
            input = Utils.readFile(path);
        } else {
            input = String.join("\n", Utils.readMultipleLines("enter a minijava program, line by line. end input w/ two newlines\n", 2));
        }

        Parser parser = new Parser(input);
        System.out.format("is valid: %s\n", parser.isValid());
    }


    // split `input` by one or more one-character delimiters, but keep the delimiters in the resulting array
    // this internally uses a regex, but you do not need to escape the delimiters
    static List<String> split(String input, String... delimiters) {

        // Create a regex pattern for splitting by a specific character
        Function<String, String> regexPattern = delimiter -> String.format("((?<=\\%s)|(?=\\%s))", delimiter, delimiter);

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


    public static class Parser {

        private String code;

        private String[] tokens;


        Parser(String code) {
            this.code = code;
        }


        boolean isValid() {

            boolean success;

            try {
                this.lex();
                success = this.parse();
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }

            return success;
        }


        private void lex() {
            this.tokens = split(this.code, " ", "-", "+", ",", ";", "(", ")", "{", "}", "\n")
                    .stream()
                    .filter(token -> !token.equals(" ") && !token.equals("\n"))
                    .toArray(String[]::new);

            for (int i = 0; i < tokens.length; i++) {
                log("%02d: %s\n", i, tokens[i]);
            }
        }


        private boolean parse() {
            logm();

            int position = parseProgram(tokens);
            log("pos at end: %s (of %s)\n", position, tokens.length);

            return position != -1;

        }

        // log the name of the calling method
        static void logm() {
            if (!DEBUG) return;
            System.out.format("\n\n\n==== %s ====\n\n", Thread.currentThread().getStackTrace()[2].getMethodName());
        }



        // check whether `name` is valid, according to the minijava rules
        static boolean isValidName(String name) {
            log("checking if '%s' is a valid name\n", name);
            return name.matches("[A-Za-z]+(?:[A-Za-z]|\\d)*");
        }

        // check whether `declaration` is valid, according to the minijava rules
        static boolean isValidVariableDeclaration(String declaration) {
            String name_regex = "[A-Za-z]+(?:[A-Za-z]|\\d)*";
            String full_regex = String.format("int %s(?:, %s)*;", name_regex, name_regex);

            return declaration.matches(full_regex);
        }



        public static int parseProgram(String[] program) {
            int position = 0;

            while (position < program.length) {
                int oldPosition = position;

                position = parseDecl(program, position);

                if (position == -1) {
                    position = oldPosition;
                    break;
                }
            }

            while (position < program.length && position != -1) {
                position = parseStatement(program, position);
            }

            return position;
        }





        public static int parseDecl(String[] program, int from) {
            //logm();

            String token = program[from];

            // variable declarations always start w/ the type
            // and we only allow integers
            if (!token.equals("int")) {
                return -1;
            }

            // walk forward and collect tokens until we reach a semicolon

            String fullDeclaration = token;

            while (true) {
                String nextToken = program[++from];

                if (isValidName(nextToken)) {
                    fullDeclaration += " ";
                }

                fullDeclaration += nextToken;

                if (nextToken.equals(";")) {
                    break;
                }
            }

            if (!isValidVariableDeclaration(fullDeclaration)) {
                return -1;
            }

            return ++from;
        }



        public static int parseStatement(String[] program, int from) {
            logm();
            String token = program[from];


            /*
            *
            *
            * [x]   = ;
            * [x] | { <stmt>* }
            * [x] | <name> = <expr>;
            * [x] | <name> = read();
            * [x] | write(<expr>);
            * [x] | if (<cond>) <stmt>
            * [x] | if (<cond>) <stmt> else <stmt>
            * [x] | while(<cond>) <stmt>
            *
            * */

            if (token.equals(";")) {
                return ++from;
            }

            if (token.equals("{")) {
                int endOfOneOrMoreStatements = from+1;
                while (true) {
                    endOfOneOrMoreStatements = parseStatement(program, endOfOneOrMoreStatements);

                    //log("endOfOneOrMoreStatements: %s (%s)\n", endOfOneOrMoreStatements, program[endOfOneOrMoreStatements]);

                    if (endOfOneOrMoreStatements == -1) {
                        log("error parsing statements after '{'. passed from: %s (%s)\n", from+1, program[from+1]);
                        return -1;
                    }

                    if (program[endOfOneOrMoreStatements].equals("}")) {
                        log("found a closing curly brace at %s\n", endOfOneOrMoreStatements);
                        return ++endOfOneOrMoreStatements;
                    }
                }
            }

            if (isValidName(token) && program[from + 1].equals("=")) {
                log("is assignment\n");

                boolean isReadAssignment =
                        program[from + 2].equals("read") &&
                        program[from + 3].equals("(")    &&
                        program[from + 4].equals(")")    &&
                        program[from + 5].equals(";");

                if (isReadAssignment) {
                    return from + 6;
                }

                log("is assignment from expression (%s)\n", program[from + 2]);
                int endOfExpressionThatAssignsToCurrentToken = parseExpression(program, from + 2);
                if (endOfExpressionThatAssignsToCurrentToken == -1) {
                    return -1;
                } else if (program[endOfExpressionThatAssignsToCurrentToken].equals(";")) {
                    return ++endOfExpressionThatAssignsToCurrentToken;
                }
            }

            // statement is some token that's followed by an opening parentheses
            // the only options for this are `read`, `write`, `while` and `if` [`else`]
            if (program[from + 1].equals("(")) {
                if (token.equals("write")) {
                    int writeExpressionEnd = parseExpression(program, from + 2);
                    if (writeExpressionEnd != -1) {
                        boolean isWellFormedWriteExpression =
                                program[writeExpressionEnd].equals(")") &&
                                program[writeExpressionEnd+1].equals(";");
                        if (isWellFormedWriteExpression) {
                            return writeExpressionEnd + 2;
                        }
                        // malformed write call
                        return -1;
                    }

                } else if (token.equals("while") || token.equals("if")) {
                    //log("%s statement w/ condition starting at #%s (%s)\n", token, from + 2, program[from + 2]);
                    int conditionEndIndex = parseCondition(program, from + 2);
                    //log("end of condition of %s statement starting at %s (%s): %s (%s)\n", token, from + 2, program[from+2], conditionEndIndex, program[conditionEndIndex]);
                    if (conditionEndIndex == -1) {
                        return -1;
                    }

                    if (program[conditionEndIndex].equals(")")) {
                        //log("parsing %s statement body (starting at %s (%s))\n", token, conditionEndIndex+1, program[conditionEndIndex+1]);

                        // check if the if/while statement body is empty
                        // there's no point in trying to parse statements if we already know that there are none
                        int bodyStatementEnd;
                        if (program[conditionEndIndex+2].equals("}")) {
                            bodyStatementEnd = conditionEndIndex + 3;
                        } else {
                            bodyStatementEnd = parseStatement(program, conditionEndIndex + 1);
                        }

                        //log("end of %s statement: %s (%s)\n", token, bodyStatementEnd, 0);//program[bodyStatementEnd]);

                        // if we're currently parsing an if statement, check whether it has an else clause
                        // and parse that as well if necessary
                        if (token.equals("if") && bodyStatementEnd != -1 && bodyStatementEnd < program.length) {

                            if (program[bodyStatementEnd].equals("else")) {
                                return parseStatement(program, bodyStatementEnd + 1);
                            }

                        }

                        return bodyStatementEnd;
                    }
                }
            }


            log("unhandled statement token: '%s'\n", token);
            unhandledToken(token);

            return -1;
        }




        public static int parseExpression(String[] program, int from) {
            logm();
            //String token = program[from];


            /*
            * [x] <number>
            * [x] | <name>
            * [x] | (<expr>)             (kinda, i guess)
            * [x] | <unop> <expr>
            * [x] | <expr> <binop> <expr>
            *
            * */

            log("token: %s\n", program[from]);

            int end_ = parseNumber(program, from);
            if (end_ == -1) {
                end_ = parseName(program, from);
            }

            if (end_ == -1) {
                log("end_ == -1\n");
                int endOfPotentialUnop = parseUnop(program, from);
                if (endOfPotentialUnop == from+1) {
                    // unop (eg `-something`)

                    int endOfExpressionFollowingTheUnop = parseExpression(program, endOfPotentialUnop);
                    log("end of expressionAfterUnop: %s (%s)\n", endOfExpressionFollowingTheUnop, program[endOfExpressionFollowingTheUnop]);
                    return endOfExpressionFollowingTheUnop;
                } else {
                    // token is neither a number, nor a name, nor an unop
                    // the onlty remaining option is that we're dealing w/ another expression wrapped in parens
                    if (program[from].equals("(")) {
                        int endOfExpressionWrappedInParentheses = parseExpression(program, from+1);
                        log("end of (expr): %s (%s)\n", endOfExpressionWrappedInParentheses, program[endOfExpressionWrappedInParentheses]);

                        // return if there is no binop after the closing parens
                        // otherwise, we set end_ to the token after the closing parens
                        // (this will be picked up below for ckecking for a potential binop
                        if (program[endOfExpressionWrappedInParentheses].equals(")") && parseBinop(program, endOfExpressionWrappedInParentheses+1) == -1) {
                            return ++endOfExpressionWrappedInParentheses;
                        } else {
                            end_ = ++endOfExpressionWrappedInParentheses;
                        }
                    }
                }
            }

            //log("end_: %s (%s)\n", end_, program[end_]);
            log("end_: %s\n", end_);

            if (end_ != -1) {
                int endOfPotentialFollowingBinop = parseBinop(program, end_);
                if (endOfPotentialFollowingBinop != -1) {
                    int endOfSecondExpressionFollowingTheBinop = parseExpression(program, endOfPotentialFollowingBinop);
                    log("end of exp following binop: %s (%s)\n", endOfSecondExpressionFollowingTheBinop, program[endOfSecondExpressionFollowingTheBinop]);
                    // this is actually already the index of the next token after the expression
                    return endOfSecondExpressionFollowingTheBinop;
                }
                log("will return end_ %s (%s)\n", end_, program[end_]);
                return end_; // coment doesnt make sense // we have an expression followed by an binop, but no secondary expression following the binop
            }


            //unhandledToken(program[from]);
            //throw new RuntimeException();

            return -1;
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

        public static int parseCondition(String[] program, int from) {
            logm();
            log("parsing condition starting at %s (%s)\n", from, program[from]);

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

            String token = program[from];

            if (token.equals("true") || token.equals("false")) {
                return ++from;
            }

            if (token.equals("(")) {
                // ( <cond> )
                int end = parseCondition(program, from + 1);
                log("end of (<cond>): %s (%s)\n", end, program[end]);
                return ++end;
            }

            int endOfLhsExpression = -1;

            if (parseBunop(program, from) != -1) {
                int endOfBunopCondition = parseCondition(program, from+1);
                if (endOfBunopCondition == -1) {
                    return -1;
                }

                log("end of bunop: %s (%s)\n", endOfBunopCondition, program[endOfBunopCondition]);
                log("bunop followed by bbinop: %s\n", parseBbinop(program, endOfBunopCondition));

                boolean followedByBbinop = parseBbinop(program, endOfBunopCondition) != -1;

                if (followedByBbinop) {
                    endOfLhsExpression = endOfBunopCondition;
                    log("IS FOLLOWED BY BBINOP\n");
                } else {
                    return ++endOfBunopCondition;
                }
            }

            if (endOfLhsExpression == -1) {
                // endOfLhsExpression in this case is the end of the left side of the left side of the comparison
                // NOT the end of the left hand side of the comparison
                // eg: (i % 5 == 0 || i % 6 == 0)
                // `endOfLhsExpression` is the index of the 5
                endOfLhsExpression = parseExpression(program, from);

                if (endOfLhsExpression != -1) {
                    int endOfComparatorAfterLhsExpression = parseComp(program, endOfLhsExpression);
                    if (endOfComparatorAfterLhsExpression == -1) {
                        return -1;
                    }

                    int endOfRhsExpressionAfterComparatorAfterLhsExpression = parseExpression(program, endOfComparatorAfterLhsExpression);
                    if (endOfRhsExpressionAfterComparatorAfterLhsExpression == -1) {
                        return -1;
                    } else {
                        endOfLhsExpression = endOfRhsExpressionAfterComparatorAfterLhsExpression;
                    }
                }

            }

            log("end of lhs expression: %s (%s)\n", endOfLhsExpression, program[endOfLhsExpression]);
            log("might be a bbinop starting at %s (%s)\n", endOfLhsExpression, program[endOfLhsExpression]);;

            boolean isBbinop = parseBbinop(program, endOfLhsExpression) != -1;

            if (isBbinop) {
                log("OMG THIS IS IT\n");
                return parseCondition(program, endOfLhsExpression + 1);
            } else {
                return endOfLhsExpression;
            }
        }



        public static int parseNumber(String[] program, int from) {
            return program[from].matches("[0-9]+") ? ++from : -1;
        }

        public static int parseName(String[] program, int from) {
            return isValidName(program[from]) ? ++from : -1;
        }

        public static int parseType(String[] program, int from) {
            return program[from].equals("int") ? ++from : -1;
        }

        public static int parseComp(String[] program, int from) {
            return Arrays.asList("==", "!=", "<=", "<", ">=", ">").contains(program[from]) ? ++from : -1;
        }


        public static int parseUnop(String[] program, int from) {
            return program[from].equals("-") ? ++from : -1;
        }

        public static int parseBunop(String[] program, int from) {
            return program[from].equals("!") ? ++from : -1;
        }


        public static int parseBinop(String[] program, int from) {
            return Arrays.asList("-", "+", "*", "/", "%").contains(program[from]) ? ++from : -1;
        }

        public static int parseBbinop(String[] program, int from) {
            return Arrays.asList("&&", "||").contains(program[from]) ? ++from : -1;
        }

    }



    static class Utils {
        static String readFile(String path) {
            StringBuilder contents = new StringBuilder();
            try {
                Scanner scanner = new Scanner(new FileReader(path));

                while (scanner.hasNextLine()) {
                    contents.append(scanner.nextLine());
                    contents.append("\n");
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            return contents.toString();
        }


        // Read multiple lines from stdin
        static List<String> readMultipleLines(String prompt, int numberOfEmptyLinesRequiredToReturn) {
            System.out.print(prompt);

            List<String> input = new ArrayList<>();
            Scanner scanner = new Scanner(System.in);

            int numberOfEmptyLines = 0;

            while (numberOfEmptyLines < numberOfEmptyLinesRequiredToReturn) {
                String nextLine = scanner.nextLine();
                input.add(nextLine);

                numberOfEmptyLines += nextLine.length() == 0 ? 1 : 0;
            }

            return input;
        }
    }
}
