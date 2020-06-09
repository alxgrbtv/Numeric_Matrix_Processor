package processor

import java.util.*
import kotlin.math.pow

const val mainMenu = """
        1. Add matrices
        2. Multiply matrix to a constant
        3. Multiply matrices
        4. Transpose matrix
        5. Calculate a determinant
        6. Inverse matrix
        0. Exit
        Your choice: 
    """
const val transpositionMenu = """
        1. Main diagonal
        2. Side diagonal
        3. Vertical line
        4. Horizontal line
        Your choice: 
    """

val scanner: Scanner = Scanner(System.`in`).useLocale(Locale.US)

data class Matrix(val rows: Int, val columns: Int) {
    private val elements = Array(rows) { DoubleArray(columns) }

    operator fun get(row: Int, column: Int): Double = elements[row][column]

    operator fun set(row: Int, column: Int, value: Double) { elements[row][column] = value }

    override fun toString(): String {
        return elements.joinToString("\n") {
            it.joinToString(" ") {
                value -> "%.4f".format(Locale.US, value)
            }
        }
    }

    fun readElements() {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                this[i, j] = scanner.nextDouble()
            }
        }
    }

    fun multiplyByNumber(multiplier: Double): Matrix {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                this[i, j] = this[i, j] * multiplier
            }
        }
        return this
    }

    fun transpose(type: Int): Matrix {
        val matrix: (Int, Int) -> Double = when (type) {
            1 -> { row, column -> this[column, row] }
            2 -> { row, column -> this[rows - column - 1, columns - row - 1] }
            3 -> { row, column -> this[row, columns - column - 1] }
            4 -> { row, column -> this[rows - row - 1, column] }
            else -> throw IllegalArgumentException()
        }
        val (tRows, tColumns) = if (type in 1..2) Pair(columns, rows) else Pair(rows, columns)
        val tMatrix = Matrix(tRows, tColumns)
        for (i in 0 until tRows) {
            for (j in 0 until tColumns) {
                tMatrix[i, j] = matrix(i, j)
            }
        }
        return tMatrix
    }
}

fun main() {
    do {
        getMenu(mainMenu)
        val action = scanner.nextInt()
        when (action) {
            1 -> runSummation()
            2 -> runMultiplicationByNumber()
            3 -> runMultiplication()
            4 -> runTransposition()
            5 -> runCalcDeterminant()
            6 -> runInverseMatrix()
        }
    } while (action != 0)
}

fun runInverseMatrix() {
    val matrix = getMatrix()
    val determinant = calculateDeterminant(matrix)
    val tAdj = getAdjointMatrix(matrix).transpose(1)
    println("The matrix inversion result is: ")
    val result = tAdj.multiplyByNumber(1 / determinant)
    println("$result\n")
}

fun getAdjointMatrix(matrix: Matrix): Matrix {
    val adjointMatrix = Matrix(matrix.rows, matrix.columns)
    for (row in 0 until matrix.rows) {
        for (column in 0 until matrix.columns) {
            val minorMatrix = getMinorMatrix(matrix, row, column)
            adjointMatrix[row, column] = (-1.00).pow(row + column) * calculateDeterminant(minorMatrix)
        }
    }
    return adjointMatrix
}

fun runCalcDeterminant() {
    val matrix = getMatrix()
    println("Determinant of the matrix is:")
    val result = calculateDeterminant(matrix)
    println("$result\n")
}

fun getMinorMatrix(matrix: Matrix, itRow: Int, itColumn: Int): Matrix {
    val minorMatrix = Matrix(matrix.rows - 1, matrix.columns - 1)
    var mColumn = 0
    var mRow = 0
    for (row in 0 until matrix.rows) {
        for (column in 0 until matrix.columns) {
            if (row != itRow && column != itColumn) {
                minorMatrix[mRow, mColumn++] = matrix[row, column]
                if (mColumn == matrix.columns - 1) {
                    mColumn = 0
                    mRow ++
                }
            }
        }
    }
    return minorMatrix
}

fun calculateDeterminant(matrix: Matrix): Double {
    if (matrix.columns == 1) return matrix[0, 0]
    var determinant = 0.0
    var sign = 1
    for (column in 0 until matrix.columns) {
        val minorMatrix = getMinorMatrix(matrix, 0, column)
        determinant += sign * matrix[0, column] * calculateDeterminant(minorMatrix)
        sign = -sign
    }
    return determinant
}

fun getMenu(menuText: String) { print(menuText.trimIndent()) }

fun runTransposition() {
    getMenu(transpositionMenu)
    val type = scanner.nextInt()
    val matrix = getMatrix()
    println("The transposition result is:")
    val result = matrix.transpose(type)
    println("$result\n")
}

fun runMultiplication() {
    val firstMatrix = getMatrix()
    val secondMatrix = getMatrix()
    println("The multiplication result is:")
    val result = multiplyMatrices(firstMatrix, secondMatrix)
    println("$result\n")
}

fun runMultiplicationByNumber() {
    val matrix = getMatrix()
    println("Enter a multiplier:")
    val multiplier = scanner.nextDouble()
    println("The multiplication result is:")
    val result = matrix.multiplyByNumber(multiplier)
    println("$result\n")
}

fun runSummation() {
    val firstMatrix = getMatrix()
    val secondMatrix = getMatrix()
    println("The summation result is:")
    val result = summarizeMatrices(firstMatrix, secondMatrix)
    println("$result\n")
}

fun getMatrix(): Matrix {
    print("Enter size of matrix: ")
    val (rows, columns) = Pair(scanner.nextInt(), scanner.nextInt())
    println("Enter matrix elements:")
    val matrix = Matrix(rows, columns)
    matrix.readElements()
    return matrix
}

fun multiplyMatrices(firstMatrix: Matrix, secondMatrix: Matrix): Matrix {
    val matrix = Matrix(firstMatrix.rows, secondMatrix.columns)
    for (i in 0 until firstMatrix.rows) {
        for (j in 0 until secondMatrix.columns) {
            for (k in 0 until firstMatrix.columns) {
                matrix[i, j] += firstMatrix[i, k] * secondMatrix[k, j]
            }
        }
    }
    return matrix
}

fun summarizeMatrices(firstMatrix: Matrix, secondMatrix: Matrix): Matrix {
    val matrix = Matrix(firstMatrix.rows, firstMatrix.columns)
    for (i in 0 until firstMatrix.rows) {
        for (j in 0 until firstMatrix.columns) {
            matrix[i, j] = firstMatrix[i, j] + secondMatrix[i, j]
        }
    }
    return matrix
}