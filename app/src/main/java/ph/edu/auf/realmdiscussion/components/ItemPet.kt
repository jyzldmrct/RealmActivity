package ph.edu.auf.realmdiscussion.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPet(petModel: PetModel, onRemove: (PetModel) -> Unit){

    val currentItem by rememberUpdatedState(petModel)

    var dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd -> {
                    //DELETION PART
                    onRemove(currentItem)
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    //OTHER FUNCTION
                    return@rememberSwipeToDismissBoxState false
                }
                SwipeToDismissBoxValue.Settled -> {
                    //NOTHING
                    return@rememberSwipeToDismissBoxState false
                }
            }
            return@rememberSwipeToDismissBoxState true
        },
        //25% of the width of the card/box
        positionalThreshold = {it * .25f}
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState)},
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
                elevation =  CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = petModel.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${petModel.age} years old",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = petModel.petType,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    )
}