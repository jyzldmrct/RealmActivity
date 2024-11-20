package ph.edu.auf.realmdiscussion.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.edu.auf.realmdiscussion.database.RealmHelper
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import java.util.UUID

class PetViewModel : ViewModel() {

    private val _pets = MutableStateFlow<List<PetModel>>(emptyList())
    val pets: StateFlow<List<PetModel>> get() = _pets.asStateFlow()

    private val _showSnackbar = MutableSharedFlow<String>()
    val showSnackbar: SharedFlow<String> = _showSnackbar

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            val results: RealmResults<PetModel> = realm.query(PetModel::class).find()
            _pets.value = results
        }
    }

    fun deletePet(model: PetModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val pet: PetModel? =
                    this.query(PetModel::class, "id == $0", model.id).first().find()
                if (pet != null) {
                    delete(pet)
                    _pets.value = _pets.value.toMutableList().apply { remove(model) }
                }
            }
            viewModelScope.launch {
                _showSnackbar.emit("Removed ${model.name}")
            }
        }
    }

fun addPet(name: String, age: Int, hasOwner: Boolean, petType: String, ownerName: String) {
    viewModelScope.launch(Dispatchers.IO) {
        val realm = RealmHelper.getRealmInstance()
        realm.write {
            val existingPet: PetModel? =
                this.query(PetModel::class, "name == $0", name).first().find()
            if (existingPet == null) {
                val newPet = PetModel().apply {
                    this.id = UUID.randomUUID().toString() // Generate a unique ID
                    this.name = name
                    this.age = age
                    this.petType = petType
                    this.ownerName = if (hasOwner) ownerName else ""
                }
                copyToRealm(newPet)
                _pets.value = _pets.value.toMutableList().apply { add(newPet) }

                if (hasOwner) {
                    val owner = OwnerModel().apply {
                        this.name = ownerName
                        this.petId = newPet.id
                    }
                    copyToRealm(owner)
                }
                viewModelScope.launch {
                    _showSnackbar.emit("Added $name")
                }
            } else {
                viewModelScope.launch {
                    _showSnackbar.emit("Pet with name $name already exists")
                }
            }
        }
    }
}

    fun addOwner(pet: PetModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val owner = OwnerModel().apply {
                    this.petId = pet.id
                    // Set other owner properties
                }
                copyToRealm(owner)
            }
            viewModelScope.launch {
                _showSnackbar.emit("Owner added for ${pet.name}")
            }
        }
    }

    fun updatePet(pet: PetModel, newName: String, newAge: Int, newPetType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            try {
                var existingPet: PetModel? = null
                realm.write {
                    // Query for the existing pet by its ID
                    existingPet = query(PetModel::class, "id == $0", pet.id).first().find()

                    if (existingPet != null) {
                        // Update pet details
                        existingPet!!.name = newName
                        existingPet!!.age = newAge
                        existingPet!!.petType = newPetType
                    }
                }

                // Trigger UI update after write transaction completes
                launch(Dispatchers.Main) {
                    // Reload pets to ensure UI reflects changes
                    loadPets()

                    _showSnackbar.emit(
                        if (existingPet != null)
                            "Updated $newName"
                        else
                            "Pet not found"
                    )
                }

            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    _showSnackbar.emit("Error updating pet: ${e.message}")
                }
            }
            // No need to manually close realm
        }
    }






}