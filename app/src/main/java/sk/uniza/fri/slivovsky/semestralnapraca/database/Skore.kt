package sk.uniza.fri.slivovsky.semestralnapraca.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "skore")
/**
 * Databazova trieda ktora obsahuje udaje a konstuktor
 */
data class Skore (
    @PrimaryKey(autoGenerate = true)
    val skoreID : Long = 0L,

    val menoHraca: String,

    val uid: String,

    val skore: Int,

    val datum: String

 ){constructor(menoHraca: String,skore: Int,uid: String) : this(0L,menoHraca,uid,skore,
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Calendar.getInstance().time)) }


