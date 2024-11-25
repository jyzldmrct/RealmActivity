package ph.edu.auf.realmdiscussion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.edu.auf.realmdiscussion.database.RealmHelper
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel


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


fun updateOwnerName(ownerId: String, newName: String, petViewModel: PetViewModel) {
    viewModelScope.launch(Dispatchers.IO) {
        val realm = RealmHelper.getRealmInstance()

        try {
            // Query for the owner and observe changes
            val ownerFlow = realm.query(OwnerModel::class, "id == $0", ownerId)
                .first()
                .asFlow()
                .map { it.obj }

            // Collect changes in real-time
            ownerFlow.collect { owner ->
                if (owner != null) {
                    // Update the name
                    realm.write {
                        findLatest(owner)?.let { latestOwner ->
                            latestOwner.name = newName

                            // Update pets with the new owner name
                            val pets = query(PetModel::class, "ownerName == $0", owner.name).find()
                            pets.forEach { pet ->
                                pet.ownerName = newName
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        loadOwners()
                        petViewModel.loadPets() // Reload pets to reflect the updated owner name
                        _showSnackbar.emit("Updated owner name to $newName")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _showSnackbar.emit("Owner not found")
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
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
                    if (existingOwner.totalPets == 0) {
                        delete(existingOwner)
                        _owners.value = _owners.value.toMutableList().apply { remove(owner) }
                        viewModelScope.launch(Dispatchers.Main) {
                            _showSnackbar.emit("Removed owner ${owner.name}")
                        }
                    } else {
                        viewModelScope.launch(Dispatchers.Main) {
                            _showSnackbar.emit("Cannot delete owner ${owner.name} because they have pets")
                        }
                    }
                }
            }
        }
    }

}
