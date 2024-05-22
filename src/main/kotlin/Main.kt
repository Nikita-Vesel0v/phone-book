package phonebook

import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.forEachLine
import kotlin.io.path.readText
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

var startTimeQuickSort = System.currentTimeMillis()
var timeLimit = 1000000L

fun linearSearch(book: Map<String, String>, peopleFile: Path): Int {
    var count = 0
    peopleFile.forEachLine { if (book.containsKey(it)) count++  }
    return count
}

fun operationTime(operationTime: Long) =
    "${operationTime / 1000 / 60} min. ${ operationTime / 1000 % 60 } sec. ${operationTime % 1000} ms."

fun resultOfSearch(countFoundPeople: Int, countPeople: Int, timeTotal: Long) =
    println("Found ${countFoundPeople} / ${countPeople + 1} entries. Time taken: ${operationTime(timeTotal)}")

fun jumpSearch(book: Map<String, String>, peopleFile: Path): Int {
    var count = 0
    if (book.isEmpty()) return -1

    val last = book.size - 1
    val step = floor(sqrt(last.toDouble()))

    val bookValues = book.keys.toList()

    peopleFile.forEachLine {
        var curr = 0
        var prev = 0

        while (bookValues[curr] < it) {
            prev = curr
            if (bookValues[curr] == it) {
                count++
                break
            }
            curr = min(curr + step, last.toDouble()).toInt()
        }

        while (curr < prev) {
            curr--
            if (curr <= prev) return -1
            if (bookValues[curr] == it) {
                count++
                break
            }
        }
    }
    return count
}

fun bubbleSort(book: MutableList<Pair<String, String>>): Map<String, String> {
    val startTime = System.currentTimeMillis()
    var timeInProcess: Long
    var swapped = true
    var lastIndex = book.lastIndex
    while (swapped) {
        swapped = false
        for (i in 0 until lastIndex) {
            if (book[i].first > book[i + 1].first) {
                swapped = true
                book[i + 1] = book[i].apply { book[i] = book[i + 1] }
            }
        }
        lastIndex--
        timeInProcess = System.currentTimeMillis() - startTime
        if (timeInProcess > timeLimit  * 10)  return emptyMap()
    }
    return book.toMap()
}

fun quickSort(list: MutableList<Pair<String, String>>, l: Int = 0, r: Int = list.lastIndex): Map<String, String> {
    fun partition(l: Int, r: Int): Int {
        val pivot = list[r].first
        var i = l - 1
        for (j in l until r) {
            if (list[j].first < pivot) {
                i++
                list[i] = list[j].apply { list[j] = list[i] }
            }
            if (System.currentTimeMillis() - startTimeQuickSort > timeLimit *  10) return  -1
        }
        list[r] = list[i + 1].apply { list[i + 1] = list[r] }
        return i + 1
    }
    if (l < r) {
        val pivot = partition(l, r)
        if (pivot == -1) return emptyMap()
        quickSort(list, l, pivot - 1)
        quickSort(list, pivot + 1, r)
    }
    if (System.currentTimeMillis() - startTimeQuickSort > timeLimit *  10) return  emptyMap()
    return list.toMap()
}

fun binarySearch(book: Map<String, String>, peopleFile: Path): Int {
    var count = 0
    if (book.isEmpty()) return -1

    val bookValues = book.keys.toList()

    var left: Int
    var right: Int
    var middle: Int

    peopleFile.forEachLine {
        left = 0
        right = bookValues.lastIndex
        while(left <= right) {
            middle = (left + right) / 2
            when {
                bookValues[middle] == it -> {
                    count++
                    break
                }
                bookValues[middle] > it -> right = middle - 1
                else -> left = middle + 1
            }
        }
    }
    return count
}

fun hashMapSearch(book: HashMap<String, String>, peopleFile: Path): Int {
    var count = 0
    peopleFile.forEachLine { if (it in book.keys) count++  }
    return count
}


fun main() {
    val workingDirectory = System.getProperty ("user.dir")
    val separator = File.separator

    val bookFileName = "directory.txt"
    val peopleFileName = "find.txt"

    val bookFile = Path("${workingDirectory}${separator}src${separator}main${separator}resources${separator}$bookFileName")
    val peopleFile = Path("${workingDirectory}${separator}src${separator}main${separator}resources${separator}$peopleFileName")

    val phoneBook = mutableMapOf<String, String>()
    bookFile.forEachLine {
        val mobileNumber = it.substring(0, (it.indexOfFirst { char -> char == ' '}))
        val name = it.substring((it.indexOfFirst { char -> char == ' '} + 1), it.lastIndex + 1)
        phoneBook[name] = mobileNumber
    }
    val countPeople = peopleFile.readText().count { it == '\n'}

    //Linear Searching
    val linearSearchTime: Long
    run {
        println("Start searching (linear search)...")
        val startTime = System.currentTimeMillis()
        val countFoundPeople = linearSearch(phoneBook, peopleFile)
        linearSearchTime = System.currentTimeMillis() - startTime
        resultOfSearch(countFoundPeople, countPeople, linearSearchTime)
    }
    timeLimit = linearSearchTime
    //Bubble sort and jump searching
    val bubbleJumpSearchTime: Long
    run {
        val phoneBookList = mutableListOf<Pair<String, String>>()
        phoneBook.entries.forEach { phoneBookList.add(Pair(it.key, it.value)) }
        println("\nStart searching (bubble sort + jump search)...")
        val startTime = System.currentTimeMillis()
        val sortedPhoneBook = bubbleSort(phoneBookList)
        val bubbleSortTime = System.currentTimeMillis() - startTime
        var wasBubbleSort = false
        val countFoundPeople =
            if (sortedPhoneBook.isEmpty()) {
                linearSearch(phoneBook, peopleFile)
            }
            else {
                wasBubbleSort = true
                jumpSearch(sortedPhoneBook, peopleFile)
            }
        val searchTime = System.currentTimeMillis() - (startTime + bubbleSortTime)
        bubbleJumpSearchTime = bubbleSortTime + searchTime

        resultOfSearch(countFoundPeople, countPeople, bubbleJumpSearchTime)

        print("Sorting time: ${operationTime(bubbleSortTime)}")
        if (!wasBubbleSort) {
            print(" - STOPPED, moved to linear search")
        }
        println("\nSearching time: ${operationTime(searchTime)}")

    }

    // Quick sort and Binary searching
    val quickBinarySearchTime: Long
    run {
        val phoneBookList = mutableListOf<Pair<String, String>>()
        phoneBook.entries.forEach { phoneBookList.add(Pair(it.key, it.value)) }
        println("\nStart searching (quick sort + binary search)...")
        val startTime = System.currentTimeMillis()
        startTimeQuickSort = System.currentTimeMillis()
        val sortedPhoneBook = quickSort(phoneBookList)
        val quickSortTime = System.currentTimeMillis() - startTime
        var wasQuickSort = false
        val countFoundPeople =
            if (sortedPhoneBook.isEmpty()) {
                linearSearch(phoneBook, peopleFile)
            }
            else {
                wasQuickSort = true
                binarySearch(sortedPhoneBook, peopleFile)
            }

        val searchTime = System.currentTimeMillis() - (startTime + quickSortTime)
        quickBinarySearchTime = quickSortTime + searchTime

        resultOfSearch(countFoundPeople, countPeople, quickBinarySearchTime)

        print("Sorting time: ${operationTime(quickSortTime)}")
        if (!wasQuickSort) {
            print(" - STOPPED, moved to linear search")
        }
        println("\nSearching time: ${operationTime(searchTime)}")
    }

    // HashMap
    val hashMapSearchTime: Long
    run {
        println("\nStart searching (hash table)...")
        val startTime = System.currentTimeMillis()
        val phoneBookHash = HashMap(phoneBook)
        val createHashMapTime = System.currentTimeMillis() - startTime
        val countFoundPeople = hashMapSearch(phoneBookHash, peopleFile)
        val searchTime = System.currentTimeMillis() - (startTime + createHashMapTime)
        hashMapSearchTime = System.currentTimeMillis() - startTime
        resultOfSearch(countFoundPeople, countPeople, hashMapSearchTime)
        println("Creating time: ${operationTime(createHashMapTime)}")
        println("Searching time: ${operationTime(searchTime)}")

    }

}