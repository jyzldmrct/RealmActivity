package ph.edu.auf.realmdiscussion.database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel

object RealmHelper {
    private lateinit var realmInstance : Realm

    fun initializeRealm(){
        val config = RealmConfiguration.Builder(schema = setOf(PetModel::class, OwnerModel::class))
            .name("petrealm.realm")
            .schemaVersion(2)
            .initialData{
                copyToRealm(PetModel().apply { name = "Browny"; age = 5; petType = "Aspin" })
                copyToRealm(OwnerModel().apply { name = "Angelo"; pets.addAll(
                    listOf(
                        PetModel().apply { name = "Choco"; age = 5; petType = "Aspin" }
                    )
                ) })
            }
            .build()
        realmInstance = Realm.open(config)
    }

    fun getRealmInstance(): Realm{
        if(!::realmInstance.isInitialized){
            throw IllegalStateException("Realm is not initialized. Call initializeRealm() first")
        }
        return  realmInstance
    }

    fun closeRealm(){
        if(::realmInstance.isInitialized && !realmInstance.isClosed()){
            realmInstance.close()
        }
    }
}