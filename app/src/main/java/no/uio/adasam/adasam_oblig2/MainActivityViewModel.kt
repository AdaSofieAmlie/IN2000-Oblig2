package no.uio.adasam.adasam_oblig2


import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivityViewModel: ViewModel() {

    private val vei = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v22/obligatoriske-oppgaver/alpacaparties.json"
    private val data = DataSource(vei)
    private lateinit var spinner: Spinner

    private val alpacaAlpacaPartyLiveData: MutableLiveData<List<AlpacaParty>?> by lazy {
        MutableLiveData<List<AlpacaParty>?>().also {
            loadAlpacaParty()
        }
    }


    fun getAlpacaLiveData(): MutableLiveData<List<AlpacaParty>?> {
        return alpacaAlpacaPartyLiveData
    }

    private fun loadAlpacaParty() {
        // Do an asynchronous operation to fetch users.

        viewModelScope.launch (Dispatchers.IO){
            var liste = data.hentAlpaca()
            if (liste != null) {
                data.hentStemmerDistriktEn(liste)
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    // An item was selected. You can retrieve the selected item using
                    if (liste != null) {
                        viewModelScope.launch (Dispatchers.IO) {
                            when (pos) {
                                0 -> liste = data.hentStemmerDistriktEn(liste!!)
                                1 -> liste = data.hentStemmerDistriktTo(liste!!)
                                2 -> liste = data.hentStemmerDistriktTre(liste!!)
                            }
                            alpacaAlpacaPartyLiveData.postValue(liste)
                        }
                    }

                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                }
            }

            alpacaAlpacaPartyLiveData.postValue(liste)
        }
    }


    fun faaSpinner(sp: Spinner){
        spinner = sp
    }


}


