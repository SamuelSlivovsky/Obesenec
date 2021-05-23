package sk.uniza.fri.slivovsky.semestralnapraca.Databaza

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Entity(tableName = "skore")
/**
 * Databazova trieda ktora obsahuje udaje a konstuktor
 */
data class Skore (
    @PrimaryKey(autoGenerate = true)
    val skoreID : Long = 0L,

    val menoHraca: String,

    val skore: Int,

    val datum: String

 ){constructor(menoHraca: String,skore: Int) : this(0L,menoHraca,skore,
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Calendar.getInstance().time)) }


