package GUI;

/**
 * Simple utility class for debugging.
 */
public class Util {
    public static void print2DIntArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(String.format("%d\t\t\t", array[i][j]));
            }

            System.out.print("\n");
        }

        System.out.println("\n\n");
    }

    public static void print2DCharArray(char[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(String.format("%s\t\t\t", array[i][j]));
            }

            System.out.print("\n");
        }

        System.out.println("\n\n");
    }
}
