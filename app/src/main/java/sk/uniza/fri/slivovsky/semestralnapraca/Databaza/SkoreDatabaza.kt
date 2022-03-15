package sk.uniza.fri.slivovsky.semestralnapraca.Databaza
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Skore::class], version = 3, exportSchema = false)

abstract class SkoreDatabaza: RoomDatabase() {

    abstract val SkoreDatabazaDao: SkoreDatabazaDao

    companion object {
        @Volatile
        private var INSTANCE: SkoreDatabaza? = null

        fun getInstance(context: Context): SkoreDatabaza {
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SkoreDatabaza::class.java,
                        "skore_databaza"
                    ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
