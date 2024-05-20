import kotlin.io.path.Path
import kotlin.io.path.forEachLine
import kotlin.io.path.readText

fun countPeopleInPhoneBook(pathToBook: String, pathToListPeople: String) {
    val phoneBook = Path(pathToBook)
    val peopleFind = Path(pathToListPeople)
    val listPhoneBook = phoneBook.readText()
    var count = 0
    println("Start searching...")
    val startTime = System.currentTimeMillis()
    peopleFind.forEachLine { if (it in listPhoneBook) count++  }
    val timeToSearch = System.currentTimeMillis() - startTime
    println("Found $count / 500 entries. Time taken: ${timeToSearch / 1000 / 60} min. ${ timeToSearch / 1000 % 60 } sec. ${timeToSearch % 1000} ms.")
}

fun main() {
    val pathToBook = "src/main/resources/directory.txt"
    val pathToListPeople = "src/main/resources/find.txt"
    countPeopleInPhoneBook(pathToBook, pathToListPeople)
}