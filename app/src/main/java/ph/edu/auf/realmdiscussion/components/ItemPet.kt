package ph.edu.auf.realmdiscussion.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ph.edu.auf.realmdiscussion.R
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPet(petModel: PetModel, petViewModel: PetViewModel, onRemove: (PetModel) -> Unit) {

    val currentItem by rememberUpdatedState(petModel)
    var newName by remember { mutableStateOf(petModel.name) }
    var newAge by remember { mutableStateOf(petModel.age.toString()) }
    var newPetType by remember { mutableStateOf(petModel.petType) }
    var showDialog by remember { mutableStateOf(false) }
    var showAdoptDialog by remember { mutableStateOf(false) }
    var ownerName by remember { mutableStateOf("") }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onRemove(currentItem)
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    showDialog = true
                    return@rememberSwipeToDismissBoxState false
                }
                SwipeToDismissBoxValue.Settled -> {
                    return@rememberSwipeToDismissBoxState false
                }
            }
            return@rememberSwipeToDismissBoxState true
        },
        positionalThreshold = { it * .25f }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Update Pet") },
            text = {
                Column {
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Pet Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = newAge,
                        onValueChange = { newAge = it },
                        label = { Text("Pet Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = newPetType,
                        onValueChange = { newPetType = it },
                        label = { Text("Pet Type") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val age = newAge.toIntOrNull()
                        if (age != null) {
                            petViewModel.updatePet(
                                pet = currentItem,
                                newName = newName,
                                newAge = age,
                                newPetType = newPetType
                            )
                            showDialog = false
                        } else {
                            Log.e("PetUpdate", "Invalid age input")
                        }
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAdoptDialog) {
        AlertDialog(
            onDismissRequest = { showAdoptDialog = false },
            title = { Text("Adopt Pet") },
            text = {
                Column {
                    TextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Owner Name") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (ownerName.isNotBlank()) {
                            petViewModel.adoptPet(currentItem, ownerName)
                            showAdoptDialog = false
                        }
                    }
                ) {
                    Text("Adopt")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdoptDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                    val imageResId = petViewModel.petTypeToImageRes[petModel.petType] ?: R.drawable.default_image
                    val painter: Painter = painterResource(id = imageResId)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showAdoptDialog = true }) {
                        Text("Adopt")
                    }
                }
            }
        }
    )
}