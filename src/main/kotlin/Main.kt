import kotlin.io.path.Path

import kotlin.io.path.forEachLine
import kotlin.io.path.readLines
import kotlin.io.path.readText

fun main() {
    val phoneBook = Path("src/main/resources/directory.txt")
    val peopleFind = Path("src/main/resources/find.txt")
    val listPhoneBook = phoneBook.readText()
    var count = 0
    println("Start searching...")
    val startTime = System.currentTimeMillis()
    peopleFind.forEachLine { if (it in listPhoneBook) count++  }
    val timeToSearch = System.currentTimeMillis() - startTime
    println("Found $count / 500 entries. Time taken: ${timeToSearch / 1000 / 60} min. ${ timeToSearch / 1000 % 60 } sec. ${timeToSearch % 1000} ms.")
}