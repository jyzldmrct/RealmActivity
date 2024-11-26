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
import kotlinx.coroutines.withContext
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

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> get() = _nameError.asStateFlow()

    private val _ageError = MutableStateFlow<String?>(null)
    val ageError: StateFlow<String?> get() = _ageError.asStateFlow()

    private val _ownerNameError = MutableStateFlow<String?>(null)
    val ownerNameError: StateFlow<String?> get() = _ownerNameError.asStateFlow()

    init {
        loadPets()
    }

    fun loadPets() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val realm = RealmHelper.getRealmInstance()
                val results: RealmResults<PetModel> = realm.query(PetModel::class).find()

                withContext(Dispatchers.Main) {
                    _pets.value = results.toList().also {
                        if (it.isEmpty()) {
                            _showSnackbar.emit("No pets found")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _showSnackbar.emit("Error loading pets: ${e.message}")
                }
            }
        }
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _showSnackbar.emit(message)
        }
    }

    fun updateOwnerName(pet: PetModel, newOwnerName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val existingPet: PetModel? = query(PetModel::class, "id == $0", pet.id).first().find()
                if (existingPet != null) {
                    existingPet.ownerName = newOwnerName
                }
            }
            loadPets()
        }
    }

fun addPet(name: String, age: String, hasOwner: Boolean, petType: String, ownerName: String) {
    viewModelScope.launch(Dispatchers.IO) {
        val realm = RealmHelper.getRealmInstance()

        try {
            // Reset error messages
            withContext(Dispatchers.Main) {
                _nameError.value = null
                _ageError.value = null
                _ownerNameError.value = null
            }

            // Validation checks
            if (name.isBlank()) {
                withContext(Dispatchers.Main) {
                    _nameError.value = "Pet name cannot be empty"
                }
                return@launch
            }

            if (name.any { it.isDigit() }) {
                withContext(Dispatchers.Main) {
                    _nameError.value = "Pet name cannot contain numbers"
                }
                return@launch
            }

            val ageInt = age.toIntOrNull()
            if (ageInt == null || ageInt <= 0) {
                withContext(Dispatchers.Main) {
                    _ageError.value = "Pet age must be a positive number"
                }
                return@launch
            }

            if (hasOwner && ownerName.isBlank()) {
                withContext(Dispatchers.Main) {
                    _ownerNameError.value = "Owner name cannot be empty if the pet has an owner"
                }
                return@launch
            }

            var success = false
            var attemptCount = 0
            val maxAttempts = 3

            while (!success && attemptCount < maxAttempts) {
                try {
                    realm.write {
                        // Check for existing pet with same name
                        val existingPet = query(PetModel::class, "name == $0", name)
                            .first()
                            .find()

                        if (existingPet == null) {
                            val newId = UUID.randomUUID().toString()

                            // Verify the ID doesn't exist
                            val idExists = query(PetModel::class, "id == $0", newId)
                                .first()
                                .find() != null

                            if (!idExists) {
                                // Create new unmanaged pet object
                                val newPet = PetModel().apply {
                                    id = newId
                                    this.name = name
                                    this.age = ageInt
                                    this.petType = petType
                                    this.ownerName = if (hasOwner) ownerName else ""
                                }

                                // Add the pet to Realm
                                val managedPet = copyToRealm(newPet)

                                // Handle owner creation/update if needed
                                if (hasOwner && managedPet != null) {
                                    val owner = query(OwnerModel::class, "name == $0", ownerName)
                                        .first()
                                        .find()

                                    if (owner == null) {
                                        // Create new owner
                                        copyToRealm(OwnerModel().apply {
                                            id = UUID.randomUUID().toString()
                                            this.name = ownerName
                                            totalPets = 1
                                            pets.add(managedPet)
                                        })
                                    } else {
                                        // Update existing owner
                                        owner.apply {
                                            totalPets += 1
                                            pets.add(managedPet)
                                        }
                                    }
                                }

                                success = true
                                launch(Dispatchers.Main) {
                                    _showSnackbar.emit("Pet Added: $name")
                                }
                            }
                        } else {
                            success = true // Exit the loop if pet name exists
                            launch(Dispatchers.Main) {
                                _showSnackbar.emit("Pet with name $name already exists")
                            }
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    attemptCount++
                    if (attemptCount >= maxAttempts) {
                        launch(Dispatchers.Main) {
                            _showSnackbar.emit("Failed to add pet after multiple attempts")
                        }
                    }
                    kotlinx.coroutines.delay(100)
                }
            }
        } catch (e: Exception) {
            launch(Dispatchers.Main) {
                _showSnackbar.emit("Error adding pet: ${e.message}")
            }
        } finally {
            loadPets()
        }
    }
}



    fun deletePet(model: PetModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val pet: PetModel? = this.query(PetModel::class, "id == $0", model.id).first().find()
                if (pet != null) {

                    if (pet.ownerName.isNotEmpty()) {
                        viewModelScope.launch {
                            _showSnackbar.emit("Cannot delete ${model.name} because it has an owner")
                        }
                    } else {
                        delete(pet)
                        _pets.value = _pets.value.toMutableList().apply { remove(model) }
                        viewModelScope.launch {
                            _showSnackbar.emit("Removed ${model.name}")
                        }
                    }
                }
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

    fun adoptPet(pet: PetModel, newOwnerName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val existingPet: PetModel? = query(PetModel::class, "id == $0", pet.id).first().find()
                if (existingPet != null) {
                    val originalOwner: OwnerModel? = query(OwnerModel::class, "name == $0", existingPet.ownerName).first().find()
                    if (originalOwner != null) {
                        originalOwner.totalPets -= 1
                        originalOwner.pets.remove(existingPet)
                        copyToRealm(originalOwner)
                    }

                    existingPet.ownerName = newOwnerName

                    val newOwner: OwnerModel? = query(OwnerModel::class, "name == $0", newOwnerName).first().find()
                    if (newOwner != null) {
                        newOwner.totalPets += 1
                        newOwner.adoptedPetsCount += 1
                        newOwner.pets.add(existingPet)
                        copyToRealm(newOwner)
                    } else {
                        val newOwnerModel = OwnerModel().apply {
                            this.id = UUID.randomUUID().toString()
                            this.name = newOwnerName
                            this.totalPets = 1
                            this.adoptedPetsCount = 1
                            this.pets.add(existingPet)
                        }
                        copyToRealm(newOwnerModel)
                    }
                }
            }
            loadPets()
            viewModelScope.launch {
                _showSnackbar.emit("$newOwnerName adopted ${pet.name}")
            }
        }
    }
}
