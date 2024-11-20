package ph.edu.auf.realmdiscussion.database.realmodel

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class OwnerModel : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name: String = ""
    var petId: String = ""
    var pets: RealmList<PetModel> = realmListOf()
}