package sk.uniza.fri.slivovsky.semestralnapraca.Hra


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import sk.uniza.fri.slivovsky.semestralnapraca.R


class TitulkaActivity:AppCompatActivity() {
    var titulkaFragment : TitulkaFragment = TitulkaFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        supportFragmentManager.beginTransaction().add(R.id.container,titulkaFragment)
            .commit()
    }


}