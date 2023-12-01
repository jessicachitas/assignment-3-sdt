import controllers.VinylAPI
import models.Vinyl
import models.Collection
import mu.KotlinLogging
import persistence.JSONSerializer
import java.lang.System.exit
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import java.io.File

private val logger = KotlinLogging.logger {}
//private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
private val vinylAPI = VinylAPI(JSONSerializer(File("vinyls.json")))

fun main(args: Array<String>) {
    println("Vinyl Collection App V1.0")
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
            1  -> addVinyl()
            2  -> listVinyls()
            3  -> updateVinyl()
            4  -> deleteVinyl()
            5 -> archiveVinyl()
            6 -> searchVinyls()
            7 -> save()
            8 -> load()
            0  -> exitApp()
            else -> println("Invalid option entered: $option")
        }
    } while (true)
}

fun addVinyl(){
    val albumName = readNextLine("Please the album name for the vinyl: ")
    val artist = readNextInt("Please enter the artist that created the vinyl: ")
    val genre = readNextLine("Please enter the genre of the album: ")
    val sizeInches = readNextLine("What size is the vinyl? (7in, 10in, 12in): ")
    val colour = readNextLine("Please enter the colour of the vinyl: ")
    val isAdded = vinylAPI.add(Vinyl(albumName, artist, genre, sizeInches, colour,false))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}

fun listVinyls() {
    if (vinylAPI.numberOfVinyls() > 0) {
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
            1 -> listAllVinyls();
            2 -> listActiveVinyls();
            3 -> listArchivedVinyls();
            else -> println("Invalid option entered: " + option);
        }
    } else {
        println("Option Invalid - No notes stored");
    }
}

fun listAllVinyls() {
    println(vinylAPI.listAllVinyls())
}

fun listArchivedVinyls() {
    println(vinylAPI.listArchivedVinyls())
}

fun updateVinyl() {
    listVinyls()
    if (vinylAPI.numberOfVinyls() > 0) {
        val indexToUpdate = readNextInt("Enter the index of the vinyl to update: ")
        if (vinylAPI.isValidIndex(indexToUpdate)) {
            val albumName = readNextLine("Please the album name for the vinyl: ")
            val artist = readNextInt("Please enter the artist that created the vinyl: ")
            val genre = readNextLine("Please enter the genre of the album: ")
            val sizeInches = readNextLine("What size is the vinyl? (7in, 10in, 12in): ")
            val colour = readNextLine("Please enter the colour of the vinyl: ")

            if (vinylAPI.updateVinyl(indexToUpdate, Vinyl(albumName, artist, genre, sizeInches, colour,false))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no notes for this index number")
        }
    }
}


fun deleteVinyl(){
    listVinyls()
    if (vinylAPI.numberOfVinyls() > 0) {
        val indexToDelete = readNextInt("Enter the index of the note to delete: ")
        val vinylToDelete = vinylAPI.deleteVinyl(indexToDelete)
        if (vinylToDelete != null) {
            println("Delete Successful! Deleted note: ${vinylToDelete.albumName} - ${vinylToDelete.artist}")
        } else {
            println("Delete NOT Successful")
        }
    }
}

fun listActiveVinyls() {
    println(vinylAPI.listActiveVinyls())
}

fun archiveVinyl() {
    listActiveVinyls()
    if (vinylAPI.numberOfActiveVinyls() > 0) {
        //only ask the user to choose the note to archive if active notes exist
        val indexToArchive = readNextInt("Enter the index of the note to archive: ")
        //pass the index of the note to NoteAPI for archiving and check for success.
        if (vinylAPI.archiveVinyl(indexToArchive)) {
            println("Archive Successful!")
        } else {
            println("Archive NOT Successful")
        }
    }
}

fun searchVinyls() {
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = vinylAPI.searchByAlbumName(searchTitle)
    if (searchResults.isEmpty()) {
        println("No vinyls found")
    } else {
        println(searchResults)
    }
}

fun save() {
    try {
        vinylAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        vinylAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

fun exitApp(){
    println("Exiting...bye")
    exit(0)
}

