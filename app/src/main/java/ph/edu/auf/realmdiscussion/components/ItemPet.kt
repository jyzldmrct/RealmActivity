package ph.edu.auf.realmdiscussion.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPet(petModel: PetModel, petViewModel: PetViewModel, onRemove: (PetModel) -> Unit) {

    val currentItem by rememberUpdatedState(petModel)

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    // Deletion part
                    onRemove(currentItem)
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    // Add pet owner
                    petViewModel.addOwner(petModel)
                    return@rememberSwipeToDismissBoxState false
                }
                SwipeToDismissBoxValue.Settled -> {
                    // Nothing
                    return@rememberSwipeToDismissBoxState false
                }
            }
            return@rememberSwipeToDismissBoxState true
        },
        // 25% of the width of the card/box
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState) },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
                elevation = CardDefaults.cardElevation(
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