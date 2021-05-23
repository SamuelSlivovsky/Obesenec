package sk.uniza.fri.slivovsky.semestralnapraca.ViewModels

import androidx.lifecycle.ViewModel

/**
 * Viewmodel ktori v sebe drzi vsetky obtiaznosti slov.
 *
 */
class SlovaViewModel : ViewModel() {

    val slovaLahke = mutableListOf<String>(
        "SKOLA", "POCITAC", "KNIHA", "KAMZIK", "JAVA", "FLASA","TELEFON","PES","MACKA","KAMEN","HORA","KARTA","PAPIER","LOPTA","POHAR","KRAVA"
    )
    val slovaStredneTazke = mutableListOf<String>(
        "MYS A KLAVESNICA", "XYLOFON", "POSILNOVNA", "STAROBINEC","PODLOZKA","SPRCHOVY GEL","NAUSNICE","PREHLIADAC","OBCIANSKY PREUKAZ","ZOOLOGICKA ZAHRADA","NEMOCNICA","DZUNGLA"
    )
    val slovaTazke = mutableListOf<String>(
        "STRC PRST SKRZ KRK", "DVE MUCHY JEDNOU RANOU", "RAZ ZA UHORSKY ROK","STASTNY NOVY ROK","XYLOFON","ISIC KARTA","PREUKAZ POISTENCA","UNIVERZITA V ZILINE","MATERSKA SKOLA"
    )

    var druhSlova = ""
}