package ph.edu.auf.realmdiscussion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.database.RealmHelper
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel

class OwnerViewModel : ViewModel() {
    private val _owners = MutableStateFlow<List<OwnerModel>>(emptyList())
    val owners: StateFlow<List<OwnerModel>> get() = _owners

    private val _showSnackbar = MutableSharedFlow<String>()
    val showSnackbar = _showSnackbar

    init {
        loadOwners()
    }

    private fun loadOwners() {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            val results = realm.query(OwnerModel::class).find()
            _owners.value = results
        }
    }

   fun deleteOwner(owner: OwnerModel) {
    viewModelScope.launch(Dispatchers.IO) {
        val realm = RealmHelper.getRealmInstance()
        realm.write {
            val existingOwner: OwnerModel? = query(OwnerModel::class, "id == $0", owner.id).first().find()
            if (existingOwner != null) {
                if (existingOwner.pets.isEmpty()) {
                    delete(existingOwner)
                    _owners.value = _owners.value.toMutableList().apply { remove(owner) }
                    viewModelScope.launch {
                        _showSnackbar.emit("Removed owner ${owner.name}")
                    }
                } else {
                    viewModelScope.launch {
                        _showSnackbar.emit("Cannot delete owner ${owner.name} because they have pets")
                    }
                }
            }
        }
    }
}
}