package ph.edu.auf.realmdiscussion.database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import java.util.UUID

object RealmHelper {
    private lateinit var realmInstance: Realm

    fun initializeRealm() {
        val config = RealmConfiguration.Builder(schema = setOf(PetModel::class, OwnerModel::class))
            .name("petrealm.realm")
            .schemaVersion(2)
            .deleteRealmIfMigrationNeeded()
            .initialData {
                // Adding PetModel
                if (query(PetModel::class, "name == 'Browny'").find().isEmpty()) {
                    copyToRealm(PetModel().apply {
                        id = UUID.randomUUID().toString() // Unique ID
                        name = "Browny"
                        age = 5
                        petType = "Aspin"
                    })
                }

                // Adding OwnerModel
                if (query(OwnerModel::class, "name == 'Angelo'").find().isEmpty()) {
                    copyToRealm(OwnerModel().apply {
                        name = "Angelo" // Owner's name
                        pets.addAll(
                            listOf(
                                copyToRealm(PetModel().apply { // Add the pet as a managed Realm object
                                    id = UUID.randomUUID().toString()
                                    name = "Choco"
                                    age = 5
                                    petType = "Aspin"
                                })
                            )
                        )
                    })
                }
            }
            .build()
        realmInstance = Realm.open(config)
    }

    fun getRealmInstance(): Realm {
        if (!::realmInstance.isInitialized) {
            throw IllegalStateException("Realm is not initialized. Call initializeRealm() first")
        }
        return realmInstance
    }

    fun closeRealm() {
        if (::realmInstance.isInitialized) {
            realmInstance.close()
        }
    }
}