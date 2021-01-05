package solution.testing.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class MatrixUtil {

    public static int[][] concurrentMultiply0(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        class ColumnMultiplyResult {
            private final int col;
            private final int[] columnC;

            private ColumnMultiplyResult(int col, int[] columnC) {
                this.col = col;
                this.columnC = columnC;
            }
        }

        final CompletionService<ColumnMultiplyResult> completionService = new ExecutorCompletionService<>(executor);

        for (int j = 0; j < matrixSize; j++) {
            final int col = j;
            final int[] columnB = new int[matrixSize];
            for (int k = 0; k < matrixSize; k++) {
                columnB[k] = matrixB[k][col];
            }
            completionService.submit(() -> {
                final int[] columnC = new int[matrixSize];

                for (int row = 0; row < matrixSize; row++) {
                    final int[] rowA = matrixA[row];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += rowA[k] * columnB[k];
                    }
                    columnC[row] = sum;
                }
                return new ColumnMultiplyResult(col, columnC);
            });
        }

        for (int i = 0; i < matrixSize; i++) {
            ColumnMultiplyResult res = completionService.take().get();
            for (int k = 0; k < matrixSize; k++) {
                matrixC[k][res.col] = res.columnC[k];
            }
        }

        return matrixC;
    }

    public static int[][] concurrentMultiply1(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][];

        final int[][] matrixBT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[i][j] = matrixB[j][i];
            }
        }

        List<Callable<Void>> tasks = new ArrayList<>(matrixSize);
        for (int i = 0; i < matrixSize; i++) {
            final int row = i;
            tasks.add(() -> {
                final int[] rowC = new int[matrixSize];
                for (int col = 0; col < matrixSize; col++) {
                    final int[] rowA = matrixA[row];
                    final int[] columnB = matrixBT[col];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += rowA[k] * columnB[k];
                    }
                    rowC[col] = sum;
                }
                matrixC[row] = rowC;
                return null;
            });
        }
        executor.invokeAll(tasks);
        return matrixC;
    }

    //optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply0(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }


    public static int[][] singleThreadMultiply1(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[][] matrixBT = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixBT[j][k];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] singleThreadMultiply2(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int colB = 0; colB < matrixSize; colB++) {
            final int[] columnB = new int[matrixSize];
            for (int rowB = 0; rowB < matrixSize; rowB++) {
                columnB[rowB] = matrixB[rowB][colB];
            }

            for (int rowA = 0; rowA < matrixSize; rowA++) {
                int sum = 0;
                final int[] rowMatrixA = matrixA[rowA];
                for (int k = 0; k < matrixSize; k++) {
                    sum += rowMatrixA[k] * columnB[k];
                }
                matrixC[rowA][colB] = sum;
            }
        }
        return matrixC;

    }

    //самый быстрый однопоточный вариант
    public static int[][] singleThreadMultiply3(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int row = 0; row < matrixSize; row++) {
            final int[] rowA = matrixA[row];
            final int[] rowC = matrixC[row];

            for (int idx = 0; idx < matrixSize; idx++) {
                final int elA = rowA[idx];
                final int[] rowB = matrixB[idx];
                for (int col = 0; col < matrixSize; col++) {
                    rowC[col] += elA * rowB[col];
                }
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
