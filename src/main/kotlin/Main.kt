import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.forEachLine
import kotlin.io.path.readText
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

fun linearSearch(book: List<String>, peopleFile: Path): Int {
    var count = 0
    peopleFile.forEachLine { if (it in book) count++  }
    return count
}

fun operationTime(operationTime: Long) = "${operationTime / 1000 / 60} min. ${ operationTime / 1000 % 60 } sec. ${operationTime % 1000} ms."

fun resultOfSearch(countFoundPeople: Int, countPeople: Int, linearSearchTime: Long) {
    println("Found $countFoundPeople / $countPeople entries. Time taken: ${operationTime(linearSearchTime)}")
}

fun jumpSearch(book: List<String>, peopleFile: Path): Int {
    var count = 0
    if (book.isNullOrEmpty()) return -1

    val last = book.lastIndex
    val step = floor(sqrt(last.toDouble()))

    peopleFile.forEachLine {
        var curr = 0
        var prev = 0

        while (book[curr] < it) {
            prev = curr
            if (book[curr] == it) {
                count++
                break
            }
            curr = min(curr + step, last.toDouble()).toInt()
        }

        while (curr < prev) {
            curr--
            if (curr <= prev) return -1
            if (book[curr] == it) {
                count++
                break
            }
        }
    }
    return count
}

fun bubbleSort(phoneBook: MutableList<String> ): List<String> {
    var swapped = true
    var lastIndex = phoneBook.lastIndex
    while (swapped) {
        swapped = false
        for (i in 0 until lastIndex) {
            if (phoneBook[i] > phoneBook[i + 1]) {
                swapped = true
                phoneBook[i + 1] = phoneBook[i].apply { phoneBook[i] = phoneBook[i + 1] }
            }
        }
        lastIndex--
    }
    return phoneBook
}

fun main() {
    val workingDirectory = System.getProperty ("user.dir")
    val separator = File.separator

    val bookFileName = "small_directory.txt"
    val peopleFileName = "small_find.txt"

    val bookFile = Path("${workingDirectory}${separator}src${separator}main${separator}resources${separator}$bookFileName")
    val peopleFile = Path("${workingDirectory}${separator}src${separator}main${separator}resources${separator}$peopleFileName")

    val phoneBook = mutableListOf<String>()
    bookFile.forEachLine { phoneBook.add(it.substring((it.indexOfFirst {char -> char == ' '} + 1), it.lastIndex + 1)) }

    val countPeople = peopleFile.readText().count { it == '\n'}

    println("Start searching (linear search)...")
    var startTime = System.currentTimeMillis()
    var countFoundPeople = linearSearch(phoneBook, peopleFile)
    val linearSearchTime = System.currentTimeMillis() - startTime
    resultOfSearch(countFoundPeople, countPeople, linearSearchTime)

    println("\nStart searching (bubble sort + jump search)...")
    startTime = System.currentTimeMillis()
    val sortedPhoneBook = bubbleSort(phoneBook)
    val bubbleSortTime = System.currentTimeMillis() - startTime
    var wasJumpSearch = false
    countFoundPeople =
        if (bubbleSortTime * 10 > linearSearchTime) {
            linearSearch(sortedPhoneBook, peopleFile)
        }
        else {
            wasJumpSearch = true
            jumpSearch(sortedPhoneBook, peopleFile)
        }
    val searchTime = System.currentTimeMillis() - startTime + bubbleSortTime
    val bubbleJumpSearchTime = bubbleSortTime + searchTime

    resultOfSearch(countFoundPeople, countPeople, bubbleJumpSearchTime)

    print("Sorting time: ${operationTime(bubbleSortTime)}")
    if (!wasJumpSearch) {
        println(" - STOPPED, moved to linear search")
    }
    println("Searching time: ${operationTime(searchTime)}")
}