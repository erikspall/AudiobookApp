package de.erikspall.audiobookapp.data.dummy

import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.model.Attribute
import de.erikspall.audiobookapp.model.AudioBook

/**
 * An object to generate a dummy list of Audiobooks for testing purpose
 */
object DummyDataSource {
    val audioBooks: List<AudioBook> = listOf(
        AudioBook(
            R.drawable.k1,
            "Känguru Chroniken",
            24,
            mapOf(Attribute.AUTHOR to listOf("Mark-Uwe Kling"),
                    Attribute.GENRE to listOf("Comic", "Humorvolle Fikition"))
        ),
        AudioBook(
            R.drawable.k2,
            "Känguru Manifest",
            99,
            mapOf(Attribute.AUTHOR to listOf("Mark-Uwe Kling"),
                Attribute.GENRE to listOf("Humor", "Comic", "Satire", "Humorvolle Fikition"))
        ),
        AudioBook(
            R.drawable.k3,
            "Känguru Offenbarung",
            78,
            mapOf(Attribute.AUTHOR to listOf("Mark-Uwe Kling"),
            Attribute.GENRE to listOf("Humor", "Comic", "Humorvolle Fikition"))
        ),

        /* Add more dummies here */

        AudioBook(
            R.drawable.h1,
            "Harry Potter und der Stein der Weisen",
            22,
            mapOf(Attribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        AudioBook(
            R.drawable.h2,
            "Harry Potter und die Kammer des Schreckens",
            42,
            mapOf(Attribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        AudioBook(
            R.drawable.h3,
            "Harry Potter und der Gefangene von Askaban",
            1,
            mapOf(Attribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        AudioBook(
            R.drawable.h4,
            "Harry Potter und der Feuerkelch",
            66,
            mapOf(Attribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        AudioBook(
            R.drawable.h5,
            "Harry Potter und der Orden des Phönix",
            89,
            mapOf(Attribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        AudioBook(
            R.drawable.h6,
            "Harry Potter und der Halbblutprinz",
            76,
            mapOf(Attribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        AudioBook(
            R.drawable.h7,
            "Harry Potter und die Heiligtümer des Todes",
            34,
            mapOf(Attribute.AUTHOR to listOf("Joanne K. Rowling"))
        )
    )
}