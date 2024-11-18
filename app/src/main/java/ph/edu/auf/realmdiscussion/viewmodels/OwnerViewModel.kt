package ph.edu.auf.realmdiscussion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.database.RealmHelper
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel

class OwnerViewModel : ViewModel() {
    private val _owners = MutableStateFlow<List<OwnerModel>>(emptyList())
    val owners : StateFlow<List<OwnerModel>> get() = _owners

    init {
        loadOwners()
    }

    private fun loadOwners() {
        viewModelScope.launch(Dispatchers.IO){
            val realm = RealmHelper.getRealmInstance()
            val results = realm.query(OwnerModel::class).find()
            _owners.value = results
        }
    }
}