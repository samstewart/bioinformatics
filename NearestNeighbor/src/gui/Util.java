package gui;

/**
 * Simple utility class for debugging.
 */
public class Util {
    public static void print2DIntArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(String.format("%d\t", array[i][j]));
            }

            System.out.print("\n");
        }

        System.out.println("\n\n");
    }

    public static void print2DDoubleArray(double[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(String.format("%.5f\t\t", array[i][j]));
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
