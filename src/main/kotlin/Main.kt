var score_player = 0
var score_computer = 0
var cards_player = 0
var cards_computer = 0
var cards_win_player = mutableListOf<String>()
var cards_win_computer = mutableListOf<String>()
var playerWinLast = false


fun main() {
    var cards = mutableListOf<String>("0")
    var pcFirst: Boolean? = null
    var exit = false
    var cardsForPlayer = mutableListOf<String>()
    var cardsForPC = mutableListOf<String>()
    var cardsForTable = mutableListOf<String>("")

    println("Indigo Card Game")
    println("Play first?")

    while (pcFirst == null) {
        when(readln()) {
            "yes" -> pcFirst = false
            "no" -> pcFirst = true
            else -> println("Play first?")
        }
    }

// раздача карт
    cards = reset()
    cards = toShuffle(cards)
    cardsForTable = getCards(cards, 4)

    println("Initial cards on the table: ${cardsForTable.joinToString(" ")}")
    println()

// игровой процесс
   loop@ while (cards_player + cards_computer != 52) {
        // проверка карт на столе
       infoTableCards(cardsForTable)


       if (cardsForPlayer.isEmpty() && cards.isNotEmpty()) {
           cardsForPlayer = getCards(cards, 6)
       } else if (cardsForPlayer.isEmpty() && cards.isEmpty() ) {
          // println(cardsForPlayer.isEmpty())
           break
       }
       if (cardsForPC.isEmpty() && cards.isNotEmpty()) {
           cardsForPC = getCards(cards, 6)
       } else if (cardsForPC.isEmpty() && cards.isEmpty() ){
           break
       }
       // первый ход игрока
       if (pcFirst == false) {
           exit = stepByPlayer(cardsForPlayer, cardsForTable) // , cardsForPC)
           if (exit) {
               break@loop
           }
            // проверка карт на столе
           infoTableCards(cardsForTable)

            // ход компа
           stepByComputer(cardsForPC, cardsForTable, cardsForPlayer)
       } else {
           // ход компа
           stepByComputer(cardsForPC, cardsForTable, cardsForPlayer)
           // проверка карт на столе
           infoTableCards(cardsForTable)
           // ход игрока
           exit = stepByPlayer(cardsForPlayer, cardsForTable) // , cardsForPC)
           if (exit) {
               break@loop
           }
       }

    }
    if (exit) {
        println("Game Over")
    } else {
        var score_player = 0
        var score_computer = 0
        // определяем, кому отдать карты со стола в конце игры
        if (playerWinLast) {
            cards_player += cardsForTable.size
            cards_win_player += cardsForTable
        }
        if (playerWinLast == false) {
            cards_computer += cardsForTable.size
            cards_win_computer += cardsForTable
        }
        // определяем, кому отдать 3 очка в конце игры
        when {
            cards_player > cards_computer -> score_player = 3
            cards_computer > cards_player -> score_computer = 3
            cards_player == cards_computer && pcFirst == false -> score_player = 3
            cards_player == cards_computer && pcFirst == true -> score_computer = 3
        }

        for (i in cards_win_player) {
            score_player += toCountPoints2(i)
        }
        for (i in cards_win_computer) {
            score_computer += toCountPoints2(i)
        }

        println("Score: Player $score_player - Computer $score_computer")
        println("Cards: Player $cards_player - Computer $cards_computer")
        println("Game Over")
    }

    /*
    while (true) {
        if (exit == false) {
            println("Choose an action (reset, shuffle, get, exit):")
            var str = readln()
            if (cards.size == 1) {
                when (str) {
                    "reset" -> cards = reset()
                    "get" -> get(cards)
                    "exit" -> exit = true
                    else -> println("Wrong action.")
                }
            } else {
                when (str) {
                "reset" -> cards = reset()
                "shuffle" -> cards = toShuffle(cards)
                "get" -> get(cards)
                "exit" -> exit = true
                else -> println("Wrong action.")
                }
            }
        } else {
            println("Bye")
            break
        }
    }
    */
    // "Wrong action."
    // println(cards.joinToString(" "))
}

// подсчет очков
fun toCountPoints2(cardsForPlayer: String): Int {
    if (cardsForPlayer[0] == '1' || cardsForPlayer[0] == 'J' || cardsForPlayer[0] == 'Q' || cardsForPlayer[0] == 'K'
        || cardsForPlayer[0] == 'A'  ) {
        return 1
    }
    return 0
}

fun whoWins(str1: String, str2: String): Boolean {
    var win = false
    try {
        when {
            str1[str1.lastIndex] == str2[str2.lastIndex] -> win = true
            str1.first()  == str2.first() -> win = true
        }
    } catch (e: IndexOutOfBoundsException) {
        return win
    }
    return win
}

fun stepByPlayer(cardsForPlayer: MutableList<String>, cardsForTable: MutableList<String>): Boolean {
    var exit = false

    print("Cards in hand: ")
    for (i in cardsForPlayer.indices) {
        print("${i + 1})${cardsForPlayer[i]} ")
    }
    println()
    println("Choose a card to play (1-${cardsForPlayer.size}):")
    // ввод карты
    while (true) {
        var card = readln()
        if (card == "exit") {
            exit = true
            break
        } else if (card[0].isDigit() && card.toInt() in 1..cardsForPlayer.size && cardsForTable.isNotEmpty()) {
            if (whoWins(cardsForPlayer[card.toInt() - 1], cardsForTable[cardsForTable.lastIndex])) {
                cards_player += cardsForTable.size + 1
                cards_win_player += cardsForTable
                cards_win_player.add(cardsForPlayer[card.toInt() - 1])
                score_player += toCountPoints2(cardsForPlayer[card.toInt() - 1])
                for (i in cardsForTable) {
                    score_player += toCountPoints2(i)
                }
                cardsForPlayer.removeAt(card.toInt() - 1)
                cardsForTable.clear()
                playerWinLast = true
                println("Player wins cards")
                println("Score: Player $score_player - Computer $score_computer")
                println("Cards: Player $cards_player - Computer $cards_computer")
                println()
                break
            } else {
                cardsForTable.add(cardsForPlayer[card.toInt() - 1])
                cardsForPlayer.removeAt(card.toInt() - 1)
                break
            }
        } else if (cardsForTable.isEmpty()){
            cardsForTable.add(cardsForPlayer[card.toInt() - 1])
            cardsForPlayer.removeAt(card.toInt() - 1)
            break
        } else {
            println("Choose a card to play (1-${cardsForPlayer.size}):")
        }
    }
    return exit
}

fun stepByComputer(cardsForPC:  MutableList<String>, cardsForTable: MutableList<String>, cardsForPlayer: MutableList<String>) {
    // var cardOfPC = (0 until cardsForPC.size).random()
    var str = ""
    if (cardsForTable.isNotEmpty()) {
        str = choose_card(cardsForPC, cardsForTable.last(), true)
    } else {
        str = choose_card(cardsForPC, tableHaveCards = false)
    }
    var cardOfPC = cardsForPC.indexOf(str)
    println(cardsForPC.joinToString(" "))
    println("Computer plays ${cardsForPC[cardOfPC]}")
    if (cardsForTable.isNotEmpty()) {

        if (whoWins(cardsForPC[cardOfPC], cardsForTable[cardsForTable.lastIndex])) {
            cards_computer += cardsForTable.size + 1
            cards_win_computer += cardsForTable
            cards_win_computer.add(cardsForPC[cardOfPC])
            score_computer += toCountPoints2(cardsForPC[cardOfPC])
            for (i in cardsForTable) {
                score_computer += toCountPoints2(i)
            }
            cardsForPC.removeAt(cardOfPC)
            cardsForTable.clear()
            playerWinLast = false
            println("Computer wins cards")
            println("Score: Player $score_player - Computer $score_computer")
            println("Cards: Player $cards_player - Computer $cards_computer")
            println()
        } else {
            cardsForTable.add(cardsForPC[cardOfPC])
            cardsForPC.removeAt(cardOfPC)
            println()
        }
    } else {
        cardsForTable.add(cardsForPC[cardOfPC])
        cardsForPC.removeAt(cardOfPC)
        println()
    }
}

// проверка карт на столе
fun infoTableCards(cardsForTable: MutableList<String>) {
    if (cardsForTable.size == 0) {
        println("No cards on the table")
    } else {
        println("${cardsForTable.size} cards on the table, and the top card is ${cardsForTable[cardsForTable.lastIndex]}")
    }
}

// метод для второго шага
fun getCards(cards: MutableList<String>, num: Int): MutableList<String>  {
    var cards2 = mutableListOf<String>()
    repeat(num) {
        cards2.add(cards.removeLast())
    }
    return cards2
}

// метод для первого шага
fun get(cards: MutableList<String>) {
    println("Number of cards:")
      try {
          val n = readln().toInt()
          if (n <= cards.size && n != 0) {
              repeat(n) {
                  print("${cards.removeLast()} ")
              }
              println()
          } else if (n == 0 || n > 52) {
              println("Invalid number of cards.")
          } else {
              println("The remaining cards are insufficient to meet the request.")
          }
      } catch (e: NumberFormatException) {
          println("Invalid number of cards.")
      }
}


fun reset(): MutableList<String> {
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    val suits = listOf<Char>('\u2666', '\u2665', '\u2660', '\u2663')
    val cards = mutableListOf<String>()

    for (i in suits) {
        for (j in ranks) {
            cards.add(j+i)
        }
    }
    //println("Card deck is reset.")
    return cards
}

fun toShuffle(cards: MutableList<String>): MutableList<String> {
    var i = cards.size
    while (i > 1) {
        i = i - 1
        var j = (0..cards.size-1).random()    // 0 <= j <= i-1
        var item = cards[j]
        cards[j] = cards[i]
        cards[i] = item
    }
    return cards
}

// карта-кандидат - карта, которая выигрывает карты на столе
fun choose_card(cardsForPC:  MutableList<String>, topCard: String = "", tableHaveCards: Boolean): String {
    val cards_candidates = mutableListOf<String>()
    var str = ""
    var cardsForTurn = mutableListOf<String>()
    // проверить, есть ли карты на столе
    if (tableHaveCards) {  // карты на столе есть
        for (i in cardsForPC) {
            if (whoWins(i, topCard)) {
                cards_candidates.add(i)
            }
        }
    }

    when {
        cardsForPC.size == 1 ->  return cardsForPC[0]
        cards_candidates.size == 1 -> return cards_candidates[0]
        filterCards(cards_candidates, "suits") != "none" -> cardsForTurn = filterCards(cards_candidates, "suits").split(" ").toMutableList()
        filterCards(cards_candidates, "ranks") != "none" -> cardsForTurn = filterCards(cards_candidates, "ranks").split(" ").toMutableList()
        cards_candidates.size >= 2 -> cardsForTurn = cards_candidates
        // если нет карт на столе и, соответственно, не может быть карт-кандидатов
        filterCards(cardsForPC, "suits") != "none" -> cardsForTurn = filterCards(cardsForPC, "suits").split(" ").toMutableList()
        filterCards(cardsForPC, "ranks") != "none" -> cardsForTurn = filterCards(cardsForPC, "ranks").split(" ").toMutableList()
        cardsForTurn.isEmpty() -> cardsForTurn = cardsForPC
    }
    var index = (0 until cardsForTurn.size).random()  // индекс отобранной карты
    str = cardsForTurn[index]

    return str
}

// функция для подсчета карт по масти или по номиналу
fun filterCards(cardsForPC:  MutableList<String>, index: String): String {
    val count_suits = mutableListOf(mutableListOf<String>(), mutableListOf(), mutableListOf(), mutableListOf())
    val count_ranks = mutableListOf(mutableListOf<String>())

    if (index == "suits" && cardsForPC.isNotEmpty()) {
        for (i in cardsForPC) {
            when {
                i[i.lastIndex] == '♥' -> count_suits[0].add(i)
                i[i.lastIndex] == '♣' -> count_suits[1].add(i)
                i[i.lastIndex] == '♦' -> count_suits[2].add(i)
                i[i.lastIndex] == '♠' -> count_suits[3].add(i)
            }
        }
        var count_suits_sorted = count_suits.sortedBy { it.size }
        when {
            count_suits_sorted.last().size >= 2 -> return (count_suits_sorted.last()).joinToString(" ")
            else -> return "none"
        }
    }
    // проверка по номиналу
    // 7♦ 7♥ 4♠ K♣, throw one of 7♦ 7♥ at random.
    if (index == "ranks" && cardsForPC.isNotEmpty()) {
        var n = 0 // номер списка
        for (i in cardsForPC.sorted()) {
            if (count_ranks[n].isEmpty() == true) {
                count_ranks[n].add(i)
            } else if ( count_ranks[n][0][0].equals(i[0]) ) {
                count_ranks[n].add(i)
            } else {
                count_ranks.add(mutableListOf<String>())
                count_ranks[n+1].add(i)
                n++
            }
        }
         // count_ranks  = [[4♠], [7♥, 7♦], [K♣]]
        var count_ranks_sorted = count_ranks.sortedBy { it.size }
        if ( count_ranks_sorted[count_ranks_sorted.lastIndex].size >= 2) {
            return count_ranks_sorted[count_ranks_sorted.lastIndex].joinToString(" ")
        }
    }
     return "none"
}