package sk.uniza.fri.slivovsky.semestralnapraca

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import sk.uniza.fri.slivovsky.semestralnapraca.Hra.HraFragment

/**
 * Aplikacia Obesenec implementuje klasicku hru obesenca
 * kde hrac hlada nejake nahodne slovo.
 *
 * @author  Samuel Slivovský
 * @version 1.0
 * @since   2021.05.22
 */
class MainActivity : AppCompatActivity() {

    /**
     * Funkcia onCreate ktora vytvori layout mainActivity
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}