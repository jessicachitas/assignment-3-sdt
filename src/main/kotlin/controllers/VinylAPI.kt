package controllers

import models.Vinyl
import persistence.Serializer

class VinylAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType
    private var vinyls = ArrayList<Vinyl>()

    fun formatListString(vinylsToFormat : List<Vinyl>) : String =
        vinylsToFormat
            .joinToString (separator = "\n") { vinyl ->
                vinyls.indexOf(vinyl).toString() + ": " + vinyl.toString() }
    fun add(vinyl: Vinyl): Boolean {
        return vinyls.add(vinyl)
    }

    fun listAllVinyls(): String =
        if  (vinyls.isEmpty()) "No vinyls stored"
        else formatListString(vinyls)

    fun listActiveVinyls(): String =
        if  (numberOfActiveVinyls() == 0)  "No active vinyls stored"
        else formatListString(vinyls.filter { vinyl -> !vinyl.isVinylArchived})

    fun listArchivedVinyls(): String =
        if  (numberOfArchivedVinyls() == 0) "No archived vinyls stored"
        else formatListString(vinyls.filter { vinyl -> vinyl.isVinylArchived})

    fun numberOfVinyls(): Int {
        return vinyls.size
    }

    fun numberOfArchivedVinyls(): Int = vinyls.count { vinyl: Vinyl -> vinyl.isVinylArchived }

    fun numberOfActiveVinyls(): Int = vinyls.count { vinyl: Vinyl -> !vinyl.isVinylArchived }

    fun findVinyl(index: Int): Vinyl? {
        return if (isValidListIndex(index, vinyls)) {
            vinyls[index]
        } else null
    }

    fun searchByAlbumName (searchString : String) =
        formatListString(
            vinyls.filter { vinyl -> vinyl.albumName.contains(searchString, ignoreCase = true) })

    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }

    fun deleteVinyl(indexToDelete: Int): Vinyl? {
        return if (isValidListIndex(indexToDelete, vinyls)) {
            vinyls.removeAt(indexToDelete)
        } else null
    }

    fun updateVinyl(indexToUpdate: Int, vinyl: Vinyl?): Boolean {
        //find the vinyl object by the index number
        val foundVinyl = findVinyl(indexToUpdate)

        //if the vinyl exists, use the vinyl details passed as parameters to update the found vinyl in the ArrayList.
        if ((foundVinyl != null) && (vinyl != null)) {
            foundVinyl.albumName = vinyl.albumName
            foundVinyl.artist = vinyl.artist
            foundVinyl.genre = vinyl.genre
            foundVinyl.sizeInches = vinyl.sizeInches
            foundVinyl.colour = vinyl.colour
            return true
        }

        //if the vinyl was not found, return false, indicating that the update was not successful
        return false
    }

    fun isValidIndex(index: Int) :Boolean{
        return isValidListIndex(index, vinyls);
    }

    fun archiveVinyl(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val vinylToArchive = vinyls[indexToArchive]
            if (!vinylToArchive.isVinylArchived) {
                vinylToArchive.isVinylArchived = true
                return true
            }
        }
        return false
    }

    @Throws(Exception::class)
    fun load() {
        vinyls = serializer.read() as ArrayList<Vinyl>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(vinyls)
    }

}