package de.erikspall.audiobookapp.data.dummy

import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.data.dummy.model.DummyAttribute
import de.erikspall.audiobookapp.data.dummy.model.DummyAudiobook
import de.erikspall.audiobookapp.data.model.Audiobook
import de.erikspall.audiobookapp.data.model.AudiobookWithAuthor
import de.erikspall.audiobookapp.data.model.Person

/**
 * An object to generate a dummy list of Audiobooks for testing purpose
 */
object DummyDataSource {
    val DUMMY_AUDIOBOOKSFORMEDIASESSION: List<AudiobookWithAuthor> = listOf(
        AudiobookWithAuthor(
            audiobook = Audiobook(
                0,
                "content://media/external/audio/media/7290",
                "file:///data/user/0/de.erikspall.audiobookapp/files/7290",
                "Känguru Chroniken",
                1,
                1,
                1,
                1
            ),
            author = Person(
                firstName = "Mark-Uwe",
                lastName = "Kling"
            )
        )
    )
    val DUMMY_AUDIOBOOKS: List<DummyAudiobook> = listOf(
        DummyAudiobook(
            R.drawable.k1,
            "Känguru Chroniken",
            24,
            mapOf(
                DummyAttribute.AUTHOR to listOf("Mark-Uwe Kling"),
                    DummyAttribute.GENRE to listOf("Comic", "Humorvolle Fikition"))
        ),
        DummyAudiobook(
            R.drawable.k2,
            "Känguru Manifest",
            99,
            mapOf(
                DummyAttribute.AUTHOR to listOf("Mark-Uwe Kling"),
                DummyAttribute.GENRE to listOf("Humor", "Comic", "Satire", "Humorvolle Fikition"))
        ),
        DummyAudiobook(
            R.drawable.k3,
            "Känguru Offenbarung",
            78,
            mapOf(
                DummyAttribute.AUTHOR to listOf("Mark-Uwe Kling"),
            DummyAttribute.GENRE to listOf("Humor", "Comic", "Humorvolle Fikition"))
        ),

        /* Add more dummies here */

        DummyAudiobook(
            R.drawable.h1,
            "Harry Potter und der Stein der Weisen",
            22,
            mapOf(DummyAttribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        DummyAudiobook(
            R.drawable.h2,
            "Harry Potter und die Kammer des Schreckens",
            42,
            mapOf(DummyAttribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        DummyAudiobook(
            R.drawable.h3,
            "Harry Potter und der Gefangene von Askaban",
            1,
            mapOf(DummyAttribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        DummyAudiobook(
            R.drawable.h4,
            "Harry Potter und der Feuerkelch",
            66,
            mapOf(DummyAttribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        DummyAudiobook(
            R.drawable.h5,
            "Harry Potter und der Orden des Phönix",
            89,
            mapOf(DummyAttribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        DummyAudiobook(
            R.drawable.h6,
            "Harry Potter und der Halbblutprinz",
            76,
            mapOf(DummyAttribute.AUTHOR to listOf("J.K. Rowling"))
        ),
        DummyAudiobook(
            R.drawable.h7,
            "Harry Potter und die Heiligtümer des Todes",
            34,
            mapOf(DummyAttribute.AUTHOR to listOf("Joanne K. Rowling"))
        )
    )
}