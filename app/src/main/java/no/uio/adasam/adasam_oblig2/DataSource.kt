package no.uio.adasam.adasam_oblig2

import android.util.Log
import com.google.gson.Gson
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import kotlinx.coroutines.*
import java.io.InputStream
import kotlin.math.roundToInt


class DataSource (val vei:String) {
    private val gson = Gson()
    val road1 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v22/obligatoriske-oppgaver/district1.json"
    val road2 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v22/obligatoriske-oppgaver/district2.json"
    val road3 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v22/obligatoriske-oppgaver/district3.xml"

    suspend fun hentAlpaca(): List<AlpacaParty>? {

        try {
            val listeObjekt = gson.fromJson(Fuel.get(vei).awaitString(), Party::class.java)

            return listeObjekt.parties
        } catch (exception: Exception) {
            println("A network request exception was thrown: ${exception.message}")
            return null
        }
    }

    suspend fun hentStemmerDistriktEn(listeAlpaca: List<AlpacaParty>) : List<AlpacaParty> {
        val listeDistriktEn: List<Vote> = gson.fromJson(Fuel.get(road1).awaitString(), Array<Vote>::class.java).toList()

        for (alpaca:AlpacaParty in listeAlpaca){
            val stemmer:Int = listeDistriktEn.filter{ it.id == alpaca.id}.size
            alpaca.votes = stemmer

            val pros = (stemmer.toDouble())*(100)/ (listeDistriktEn.size.toDouble())
            alpaca.prosent = pros.roundToInt()
        }
        return listeAlpaca
    }

    suspend fun hentStemmerDistriktTo(listeAlpaca: List<AlpacaParty>): List<AlpacaParty>{
        val listeDistriktTo: List<Vote> = gson.fromJson(Fuel.get(road2).awaitString(), Array<Vote>::class.java).toList()

        for (alpaca:AlpacaParty in listeAlpaca){
            val stemmer:Int = listeDistriktTo.filter{ it.id == alpaca.id}.size
            alpaca.votes = stemmer

            val pros = (stemmer.toDouble())*(100)/ (listeDistriktTo.size.toDouble())
            alpaca.prosent = pros.roundToInt()
        }

        //Regn ut prosent:

        return listeAlpaca

    }

    suspend fun hentStemmerDistriktTre(listeAlpaca: List<AlpacaParty>): List<AlpacaParty>{
        fun getData(): String {
            val requestUrl = road3
            return khttp.get(requestUrl).text
        }

        runBlocking{
            val response = getData()

            var totalt = 0

            val inputStream: InputStream = response.byteInputStream()
            val listOfDistrictThree = AlpacaParser().parse(inputStream)


            //Legger stemmene inn i de forksjellige alpaca partiene
            for (dist:DistrictThree in listOfDistrictThree){
                for (alpaca:AlpacaParty in listeAlpaca){
                    val id = Integer.parseInt(alpaca.id)
                    if (dist.id == id){
                        alpaca.votes = dist.antStemmer!!
                        totalt += dist.antStemmer
                    }
                }
            }

            for (dist:DistrictThree in listOfDistrictThree){
                for (alpaca:AlpacaParty in listeAlpaca){
                    val id = Integer.parseInt(alpaca.id)
                    if (dist.id == id){
                        val pros = (alpaca.votes.toDouble())*(100)/ (totalt.toDouble())
                        alpaca.prosent = pros.roundToInt()
                    }
                }
            }

        }
        return listeAlpaca
    }

}
