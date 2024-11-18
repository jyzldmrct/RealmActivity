package ph.edu.auf.realmdiscussion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.database.RealmHelper
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel

class PetViewModel : ViewModel() {

    private val _pets = MutableStateFlow<List<PetModel>>(emptyList())
    val pets: StateFlow<List<PetModel>> get() = _pets.asStateFlow()

    private val _showSnackbar = MutableSharedFlow<String>()
    val showSnackbar: SharedFlow<String> = _showSnackbar

    init{
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            val results = realm.query(PetModel::class).find()
            _pets.value = results
        }
    }

    fun deletePet(model:PetModel){
        viewModelScope.launch(Dispatchers.IO){
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val pet = this.query<PetModel>("id == $0",model.id).first().find()
                if(pet != null){
                    delete(pet)
                    _pets.update {
                        val list = it.toMutableList()
                        list.remove(model)
                        list
                    }
                }
            }
            _showSnackbar.emit("Removed ${model.name}")
        }
    }

}