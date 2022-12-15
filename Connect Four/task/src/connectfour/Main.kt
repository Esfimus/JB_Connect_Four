package connectfour

var boardSize1: Int = 0
var boardSize2: Int = 0
var name1 = ""
var name2 = ""
var boardMatrix = mutableListOf<MutableList<Char>>()
var freeCellList = mutableListOf<Int>()
var endCommand: Boolean = false
var gameFinished: Boolean = false
var gamesNumber: Int = 0
var player1Score = 0
var player2Score = 0

/**
 * Asks for two players' names and for board size (5..9 rows). Checks and outputs the correctness of input values
 */
fun introduction() {
    // both players introductions
    println("Connect Four")
    println("First player's name:")
    name1 = readln()
    println("Second player's name:")
    name2 = readln()
    // checking board size input according to the requirements
    do {
        try {
            println("""
                Set the board dimensions (Rows x Columns)
                Press Enter for default (6 x 7)
            """.trimIndent())
            val boardInput = readln()
            // default board size for empty input
            if (boardInput.isEmpty()) {
                boardSize1 = 6
                boardSize2 = 7
                break
            }
            // cleaning input from spaces and tabs
            val regex = Regex("\\s+")
            val boardCleanArray = boardInput.split(regex)
            val boardString = boardCleanArray.joinToString("").lowercase()
            val boardArrayFinal = boardString.split("x")
            // checking input
            if (boardArrayFinal.size < 2) {
                throw Exception("Invalid input")
            }
            if (boardArrayFinal[0].toInt() !in 5..9) {
                println("Board rows should be from 5 to 9")
            } else if (boardArrayFinal[1].toInt() !in 5..9) {
                println("Board columns should be from 5 to 9")
            }
            else {
                boardSize1 = boardArrayFinal[0].toInt()
                boardSize2 = boardArrayFinal[1].toInt()
                break
            }
        } catch (e: Exception) {
            println("Invalid input")
        }
    } while(true)
    // single or multiple games selection
    do {
        try {
            println("""
                Do you want to play single or multiple games?
                For a single game, input 1 or press Enter
                Input a number of games:
            """.trimIndent())
            val gamesInput = readln()
            if (gamesInput.isEmpty() || gamesInput.toInt() == 1) {
                gamesNumber = 1
                break
            } else if (gamesInput.toInt() > 1) {
                gamesNumber = gamesInput.toInt()
                break
            } else {
                throw Exception("Invalid input")
            }
        } catch (e: Exception) {
            println("Invalid input")
        }
    } while(true)
    println("$name1 VS $name2")
    println("$boardSize1 X $boardSize2 board")
    if (gamesNumber == 1) {
        println("Single game")
    } else {
        println("Total $gamesNumber games")
    }
    // creating a board matrix according to the approved size
    boardMatrix = MutableList(boardSize1) { MutableList(boardSize2) { ' ' } }
    // creating a list of free cells to further check for fullness
    freeCellList = MutableList(boardSize2) { boardSize1 }
}

/**
 * Displays the game board according to its size and formatting
 */
fun displayBoard(matrix: MutableList<MutableList<Char>>) {
    val horizontalSize = matrix[0].size
    val verticalSize = matrix.size
    // displaying column numbers
    for (x in 0 until horizontalSize) {
        print(" ${x + 1}")
    }
    println()
    // displaying the board
    for (y in 0 until verticalSize) {
        for (x in 0..horizontalSize) {
            if (x == 0) {
                print("|")
            } else {
                print("${matrix[y][x - 1]}|")
            }
        }
        println()
    }
    for (i in 0 until horizontalSize * 2 + 1) {
        print("=")
    }
    println()
}

/**
 * Analysing user input and placing the appropriate symbol to the mentioned column
 */
fun gameMove(symbol: Char, player: String, matrix: MutableList<MutableList<Char>>) {
    do {
        try {
            // user input and 'end' command
            println("$player's turn:")
            val userInput = readln()
            if (userInput == "end") {
                endCommand = true
                return
            }
            val columnSelected = userInput.toInt()
            // filtering valid column numbers
            if (columnSelected in 1..matrix[0].size) {
                // filtering full columns
                if (freeCellList[columnSelected - 1] > 0) {
                    // checking for empty cell from below
                    for (y in matrix.size downTo 1) {
                        if (matrix[y - 1][columnSelected - 1] == ' ') {
                            matrix[y - 1][columnSelected - 1] = symbol
                            freeCellList[columnSelected - 1] -= 1
                            displayBoard(boardMatrix)
                            return
                        }
                    }
                } else {
                    println("Column $columnSelected is full")
                }
            } else {
                println("The column number is out of range (1 - ${matrix[0].size})")
            }
        } catch (e: Exception) {
            println("Incorrect column number")
        }
    } while(true)
}

/**
 * Defines which player deserves the increase of score
 */
fun playerScoreNomination(player: String) {
    if (player == name1) {
        player1Score += 2
    } else {
        player2Score += 2
    }
}

/**
 * Checks every move of the last player for winning sequence or for a draw state
 */
fun gameStateCheck(symbol: Char, player: String, matrix: MutableList<MutableList<Char>>, freeCellList: MutableList<Int>) {
    var winSequence = 0
    // checking if the board is full
    if (freeCellList.sum() == 0) {
        println("It is a draw")
        player1Score++
        player2Score++
        gameFinished = true
    } else {
        // iterating through evey cell
        for (y in matrix.indices) {
            for (x in matrix[0].indices) {
                // checking cell value
                if (matrix[y][x] != ' ') {
                    // checking row length for the right diagonal direction
                    if (x + 3 < matrix[0].size && y + 3 < matrix.size) {
                        // iterating through the right diagonal direction
                        for (i in 0..3) {
                            if (matrix[y + i][x + i] == symbol) {
                                winSequence++
                            }
                        }
                        // checking the right diagonal direction for win sequence
                        if (winSequence == 4) {
                            println("Player $player won")
                            playerScoreNomination(player)
                            gameFinished = true
                            return
                        } else {
                            winSequence = 0
                        }
                    }
                    // checking row length for the left diagonal direction
                    if (x - 3 >= 0 && y + 3 < matrix.size) {
                        // iterating through the left diagonal direction
                        for (i in 0..3) {
                            if (matrix[y + i][x - i] == symbol) {
                                winSequence++
                            }
                        }
                        // checking the left diagonal direction for win sequence
                        if (winSequence == 4) {
                            println("Player $player won")
                            playerScoreNomination(player)
                            gameFinished = true
                            return
                        } else {
                            winSequence = 0
                        }
                    }
                    // checking row length for the right direction
                    if (x + 3 < matrix[0].size) {
                        // iterating through the right direction
                        for (i in 0..3) {
                            if (matrix[y][x + i] == symbol) {
                                winSequence++
                            }
                        }
                        // checking the right direction for win sequence
                        if (winSequence == 4) {
                            println("Player $player won")
                            playerScoreNomination(player)
                            gameFinished = true
                            return
                        } else {
                            winSequence = 0
                        }
                    }
                    // checking row length for the down direction
                    if (y + 3 < matrix.size) {
                        // iterating through the down direction
                        for (i in 0..3) {
                            if (matrix[y + i][x] == symbol) {
                                winSequence++
                            }
                        }
                        // checking the down direction for win sequence
                        if (winSequence == 4) {
                            println("Player $player won")
                            playerScoreNomination(player)
                            gameFinished = true
                            return
                        } else {
                            winSequence = 0
                        }
                    }
                }
            }
        }
    }
}

/**
 * Single game for two players
 */
fun singleGame() {
    var turnCount = 1
    var player: String
    var symbol: Char
    displayBoard(boardMatrix)
    do {
        // changing players' names and symbols for every turn
        if (turnCount % 2 != 0) {
            player = name1
            symbol = 'o'
        } else {
            player = name2
            symbol = '*'
        }
        gameMove(symbol, player, boardMatrix)
        gameStateCheck(symbol, player, boardMatrix, freeCellList)
        turnCount++
    } while(!gameFinished && !endCommand)
    println("Game over!")
}

/**
 * Multiple games for two players with counter
 */
fun multipleGames() {
    var turnCount = 1
    var player: String
    var symbol: Char
    val fixedGamesNumber = gamesNumber
    do {
        println("Game #${fixedGamesNumber - (gamesNumber - 1)}")
        displayBoard(boardMatrix)
        do {
            // changing players' names and symbols for every turn
            if (turnCount % 2 != 0) {
                player = name1
                symbol = 'o'
            } else {
                player = name2
                symbol = '*'
            }
            gameMove(symbol, player, boardMatrix)
            gameStateCheck(symbol, player, boardMatrix, freeCellList)
            turnCount++
        } while (!gameFinished && !endCommand)
        if (endCommand) {
            break
        }
        println("""
            Score
            $name1: $player1Score $name2: $player2Score
        """.trimIndent())
        gameFinished = false
        gamesNumber--
        boardMatrix = MutableList(boardSize1) { MutableList(boardSize2) { ' ' } }
        freeCellList = MutableList(boardSize2) { boardSize1 }
    } while (gamesNumber != 0 && !endCommand)
    println("Game over!")
}

fun main() {
    introduction()
    if (gamesNumber == 1) {
        singleGame()
    } else {
        multipleGames()
    }
}