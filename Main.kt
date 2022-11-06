package wordsvirtuoso

import java.io.File

const val LENGTH = 5

fun invalid(s: String) = s.length != LENGTH || !s.all { it in 'A'..'Z' } || !s.all { c -> s.count { it == c } == 1 }

fun green(c: Char) = "\u001B[48:5:10m$c\u001B[0m"

fun gs(s: String) = s.map { green(it) }.joinToString("")

fun yellow(c: Char) = "\u001B[48:5:11m$c\u001B[0m"

fun grey(c: Char) = "\u001B[48:5:7m$c\u001B[0m"

fun azure(s:String) = "\u001B[48:5:14m$s\u001B[0m"

fun correct(guesses: Int, time: Long, tries: List<String>) {
    println(tries.joinToString("\n"))
    println("\nCorrect!")
    if (guesses == 1) {
        println("Amazing luck! The solution was found at once.")
    } else {
        println("The solution was found after $guesses tries in ${time / 1000} seconds.")
    }
}

fun play(wordsList: List<String>, candidates: List<String>) {
    println("Words Virtuoso")
    var guesses = 0
    val tries = mutableListOf("")
    val wrong = emptySet<Char>().toMutableSet()
    val word = candidates.random()
    val startTime = System.currentTimeMillis()
    while (true) {
        println("\nInput a 5-letter word:")
        val guess = readln().uppercase()
        guesses++
        when {
            guess == "EXIT" -> { println("The game is over."); break }
            guess == word -> { correct(guesses, System.currentTimeMillis() - startTime, tries.apply { this.add(gs(word)) }); break }
            guess.length != LENGTH -> println("The input isn't a $LENGTH-letter word.")
            !guess.all { it in 'A'..'Z' } -> println("One or more letters of the input aren't valid.")
            guess.toSet().size != LENGTH -> println("The input has duplicated letters.")
            guess !in wordsList -> println("The input word isn't included in my words list.")
            else -> {
                tries.add(guess.mapIndexed { i, c -> if (c == word[i]) green(c) else if (c in word) yellow(c) else { wrong.add(c); grey(c) } }.joinToString(""))
                println(tries.joinToString("\n") + "\n")
                println(azure(wrong.sorted().joinToString("")))
            }
        }
    }
}

fun main(args: Array<String>) {
    when {
        args.size != 2 -> println("Error: Wrong number of arguments.")
        !File(args.first()).exists() -> println("Error: The words file ${args.first()} doesn't exist.")
        !File(args.last()).exists() -> println("Error: The candidate words file ${args.last()} doesn't exist.")
        else -> {
            val (words, invalidWords) = File(args.first()).readLines().map { it.trim().uppercase() }.let { Pair(it, it.count(::invalid)) }
            val (cands, invalidCands) = File(args.last()).readLines().map { it.trim().uppercase() }.let { Pair(it, it.count(::invalid)) }
            when {
                invalidWords != 0 -> println("Error: $invalidWords invalid words were found in the ${args.first()} file.")
                invalidCands != 0 -> println("Error: $invalidCands invalid words were found in the ${args.last()} file.")
                !cands.all { it in words } -> println("Error: ${cands.count { it !in words }} candidate words are not included in the ${args.first()} file.")
                else -> play(words, cands)
            }
        }
   }
}