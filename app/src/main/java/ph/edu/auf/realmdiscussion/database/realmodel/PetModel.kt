package ph.edu.auf.realmdiscussion.database.realmodel

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class PetModel : RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var age: Int = 0
    var petType: String = ""
    var ownerName: String = ""
}