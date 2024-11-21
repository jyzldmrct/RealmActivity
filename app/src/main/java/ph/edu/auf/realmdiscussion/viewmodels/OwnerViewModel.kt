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


    fun updateOwnerName(ownerId: String, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            try {
                var existingOwner: OwnerModel? = null
                realm.write {
                    existingOwner = query(OwnerModel::class, "id == $0", ownerId).first().find()

                    if (existingOwner != null) {
                        existingOwner!!.name = newName
                        copyToRealm(existingOwner!!)
                    }
                }

                launch(Dispatchers.Main) {
                    _showSnackbar.emit(
                        if (existingOwner != null)
                            "Updated owner name to $newName"
                        else
                            "Owner not found"
                    )
                }

            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    _showSnackbar.emit("Error updating owner name: ${e.message}")
                }
            }
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