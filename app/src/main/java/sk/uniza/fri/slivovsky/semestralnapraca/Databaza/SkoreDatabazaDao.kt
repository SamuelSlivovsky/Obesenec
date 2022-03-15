package sk.uniza.fri.slivovsky.semestralnapraca.Databaza

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface  SkoreDatabazaDao {

    @Insert
    fun insert(skore: Skore) // vlozi udaje do databazy

    @Query("select * from skore where skoreID = :key") //vrati udaje zo skore na zaklade kluca
    fun get(key: Long): Skore ?

    @Query("select * from skore order by skore desc limit 10") // vrati udaje o najlepsich 10 hracoch
    fun getBest(): List<Skore> ?

    @Query("select * from skore where uid = :uid") // vrati historiu hier hraca
    fun getHistoriaHraca(uid : String): List<Skore> ?

}