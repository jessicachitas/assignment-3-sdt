import controllers.NoteAPI
import models.Note
import mu.KotlinLogging
import persistence.JSONSerializer
import persistence.XMLSerializer
import java.lang.System.exit
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import java.io.File

private val logger = KotlinLogging.logger {}
//private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
private val noteAPI = NoteAPI(JSONSerializer(File("notes.json")))

fun main(args: Array<String>) {
    println("Notes App V4.0")
    runMenu()
}

fun mainMenu() : Int {
    return ScannerInput.readNextInt(""" 
         > -------------------------------------------------------------
         > |        _   __      __               ___                   |
         > |       / | / /___  / /____  _____   /   |  ____  ____      |
         > |      /  |/ / __ \/ __/ _ \/ ___/  / /| | / __ \/ __ \     |
         > |     / /|  / /_/ / /_/  __(__  )  / ___ |/ /_/ / /_/ /     |
         > |    /_/ |_/\____/\__/\___/____/  /_/  |_/ .___/ .___/      |
         > |                                       /_/   /_/           |
         > -------------------------------------------------------------
         > |                    __  ___                                |
         > |                   /  |/  /__ ___  __  __                  |
         > |                  / /|_/ / -_) _ \/ /_/ /                  |
         > |                 /_/  /_/\__/_//_/\_,__/                   |
         > |                                                           |
         > |                 1) Add a note                             |
         > |                 2) List all notes                         |
         > |                 3) Update a note                          |
         > |                 4) Delete a note                          |
         > |                 5) Archive a note                         |
         > |                 6) Search for a note                      |
         > |                                                           |
         > -------------------------------------------------------------
         > |                 7) Save notes                             |
         > |                 8) Load notes                             |
         > -------------------------------------------------------------
         > |                 0) Exit                                   |
         > -------------------------------------------------------------
         > ==>> """.trimMargin(">"))
}

fun runMenu() {
    do {
        val option = mainMenu()
        when (option) {
            1  -> addNote()
            2  -> listNotes()
            3  -> updateNote()
            4  -> deleteNote()
            5 -> archiveNote()
            6 -> searchNotes()
            7 -> save()
            8 -> load()
            0  -> exitApp()
            else -> println("Invalid option entered: $option")
        }
    } while (true)
}

fun addNote(){
    val noteTitle = readNextLine("Enter a title for the note: ")
    val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val noteCategory = readNextLine("Enter a category for the note: ")
    val isAdded = noteAPI.add(Note(noteTitle, notePriority, noteCategory, false))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}

fun listNotes() {
    if (noteAPI.numberOfNotes() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) View ALL notes          |
                  > |   2) View ACTIVE notes       |
                  > |   3) View ARCHIVED notes     |
                  > |   4) View notes by PRIORITY  |
                  > --------------------------------
         > ==>> """.trimMargin(">"))

        when (option) {
            1 -> listAllNotes();
            2 -> listActiveNotes();
            3 -> listArchivedNotes();
            4 -> listNotesByPriority();
            else -> println("Invalid option entered: " + option);
        }
    } else {
        println("Option Invalid - No notes stored");
    }
}

fun listAllNotes() {
    println(noteAPI.listAllNotes())
}

fun listArchivedNotes() {
    println(noteAPI.listArchivedNotes())
}

//fun updateNote() {
//    listNotes()
//    if (noteAPI.numberOfNotes() > 0) {
//        val indexToUpdate = readNextInt("Enter the index of the note to update: ")
//        if (noteAPI.isValidIndex(indexToUpdate)) {
//            val noteSelection = readNextInt("What would you like to update? 1. Title, 2. Priority, 3. Category : ")
//            if (noteSelection == 1) {
//            val noteTitle = readNextLine("Enter a title for the note: ")
//            }
//            else if (noteSelection == 2) {
//            val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
//            }
//            else if (noteSelection == 3) {
//            val noteCategory = readNextLine("Enter a category for the note: ")
//            }
//            else {
//                println("Invalid choice")
//                return
//            }
//
//            if (noteAPI.updateNote(indexToUpdate, Note(noteTitle, notePriority, noteCategory, false))){
//                println("Update Successful")
//            } else {
//                println("Update Failed")
//            }
//        } else {
//            println("There are no notes for this index number")
//        }
//    }
//}

fun updateNote() {
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        val indexToUpdate = readNextInt("Enter the index of the note to update: ")
        if (noteAPI.isValidIndex(indexToUpdate)) {
            val noteTitle = readNextLine("Enter a title for the note: ")
            val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val noteCategory = readNextLine("Enter a category for the note: ")

            if (noteAPI.updateNote(indexToUpdate, Note(noteTitle, notePriority, noteCategory, false))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no notes for this index number")
        }
    }
}


fun deleteNote(){
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        val indexToDelete = readNextInt("Enter the index of the note to delete: ")
        val noteToDelete = noteAPI.deleteNote(indexToDelete)
        if (noteToDelete != null) {
            println("Delete Successful! Deleted note: ${noteToDelete.noteTitle}")
        } else {
            println("Delete NOT Successful")
        }
    }
}

fun listActiveNotes() {
    println(noteAPI.listActiveNotes())
}

fun listNotesByPriority() {
    println(noteAPI.listNotesBySelectedPriority(readNextInt("Enter priority: ")))
}

fun archiveNote() {
    listActiveNotes()
    if (noteAPI.numberOfActiveNotes() > 0) {
        //only ask the user to choose the note to archive if active notes exist
        val indexToArchive = readNextInt("Enter the index of the note to archive: ")
        //pass the index of the note to NoteAPI for archiving and check for success.
        if (noteAPI.archiveNote(indexToArchive)) {
            println("Archive Successful!")
        } else {
            println("Archive NOT Successful")
        }
    }
}

fun searchNotes() {
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = noteAPI.searchByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No notes found")
    } else {
        println(searchResults)
    }
}

fun save() {
    try {
        noteAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        noteAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

fun exitApp(){
    println("Exiting...bye")
    exit(0)
}

