package ph.edu.auf.realmdiscussion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.database.RealmHelper
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import java.util.UUID
import ph.edu.auf.realmdiscussion.R

class PetViewModel : ViewModel() {

    val petTypeToImageRes: Map<String, Int> = mapOf(
        "Dog" to R.drawable.dog,
        "Cat" to R.drawable.cat,
        "Duck" to R.drawable.duck,
        "Pig" to R.drawable.pig,
        "Bird" to R.drawable.bird,
        "Fish" to R.drawable.fish,
        "Hamster" to R.drawable.hamster,
        "Rabbit" to R.drawable.rabbit,
        "Guinea Pig" to R.drawable.guinea_pig,
        "Turtle" to R.drawable.turtle,
        "Snake" to R.drawable.snake
    )

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
                        val existingOwner: OwnerModel? =
                            this.query(OwnerModel::class, "name == $0", ownerName).first().find()
                        if (existingOwner == null) {
                            val owner = OwnerModel().apply {
                                this.name = ownerName
                                this.petId = newPet.id
                                this.totalPets = 1
                            }
                            copyToRealm(owner)
                        } else {
                            existingOwner.pets.add(newPet)
                            existingOwner.totalPets += 1
                            copyToRealm(existingOwner)
                        }
                    }
                    viewModelScope.launch {
                        _showSnackbar.emit("Pet Added: $name")
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
                    existingPet = query(PetModel::class, "id == $0", pet.id).first().find()

                    if (existingPet != null) {
                        //updates pets
                        existingPet!!.name = newName
                        existingPet!!.age = newAge
                        existingPet!!.petType = newPetType
                    }
                }

                launch(Dispatchers.Main) {
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
        }
    }

    fun adoptPet(pet: PetModel, ownerName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val existingPet: PetModel? = query(PetModel::class, "id == $0", pet.id).first().find()
                if (existingPet != null) {
                    existingPet.ownerName = ownerName

                    val owner: OwnerModel? = query(OwnerModel::class, "name == $0", ownerName).first().find()
                    if (owner != null) {
                        owner.totalPets += 1
                        copyToRealm(owner)
                    } else {
                        val newOwner = OwnerModel().apply {
                            this.id = UUID.randomUUID().toString()
                            this.name = ownerName
                            this.totalPets = 1
                        }
                        copyToRealm(newOwner)
                    }
                }
            }
            loadPets()
            viewModelScope.launch {
                _showSnackbar.emit("$ownerName adopted ${pet.name}")
            }
        }
    }

}