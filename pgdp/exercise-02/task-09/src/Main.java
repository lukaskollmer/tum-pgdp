/*
 * exercise-02/task-09
 * Generating a LaTeX formatted table
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-11-04
 */


public class Main extends MiniJava {

    public static void main(String[] args) {

        int size = 0;
        while (size <= 0) {
            size = readInt("Which size should the table have?");
        }


        // Create the column alignment string ('l' repeated `size` times)
        String alignment = "";
        int x = 1;
        while (x <= size) {
            alignment += "l";
            x++;
        }


        // Print the first line of the LaTeX table
        System.out.format("\\begin{tabular}{%s}\n", alignment);

        int row = 1;
        while (row <= size) {
            int column = 1;
            while (column <= size) {
                // Print the value for the current cell
                System.out.print((int) Math.pow(row, column - 1));

                // Print the LaTeX column separator ('&')
                // But only if this is not the last column
                // If it is the last column, we print '\\' (LaTeX new row indicator)
                System.out.print(column != size ? " & " : " \\\\");

                column++;
            }

            // Print a newline at the end of each row
            System.out.print("\n");

            row++;
        }

        // Print the last line of the LaTeX table
        System.out.println("\\end{tabular}");
    }
}
