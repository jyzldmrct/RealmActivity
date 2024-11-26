package ph.edu.auf.realmdiscussion.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.auf.realmdiscussion.R
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import ph.edu.auf.realmdiscussion.ui.theme.Charleville
import ph.edu.auf.realmdiscussion.ui.theme.LightGray
import ph.edu.auf.realmdiscussion.ui.theme.LightOrange
import ph.edu.auf.realmdiscussion.ui.theme.Namaku
import ph.edu.auf.realmdiscussion.ui.theme.NavyBlue
import ph.edu.auf.realmdiscussion.ui.theme.Orange
import ph.edu.auf.realmdiscussion.ui.theme.SaintPeter
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPet(petModel: PetModel, petViewModel: PetViewModel, onRemove: (PetModel) -> Unit) {
    key(petModel.id) {
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
                        if (currentItem.ownerName.isEmpty()) {
                            onRemove(currentItem)
                            return@rememberSwipeToDismissBoxState true
                        } else {
                            petViewModel.showSnackbar("Cannot delete pet with owner")
                            return@rememberSwipeToDismissBoxState false
                        }
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
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Adopt Pet",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = Namaku,
                            color = Color.Blue,
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            },
            text = {
                Column {
                    TextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Owner Name",
                            fontFamily = Charleville) },

                        textStyle = TextStyle(
                            fontFamily = SaintPeter,
                            fontSize = 24.sp,
                            color = NavyBlue
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedTextColor = NavyBlue,
                            containerColor = LightOrange,
                            focusedIndicatorColor = Orange,
                            unfocusedIndicatorColor = Color.LightGray
                        )
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
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Orange
                    )
                ) {
                    Text(
                        text = "Adopt",
                        style = TextStyle(
                            fontFamily = Namaku,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAdoptDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = LightGray
                    )
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontFamily = Namaku,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightOrange
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(160.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val imageResId = petViewModel.petTypeToImageRes[petModel.petType]
                            ?: R.drawable.default_image
                        val painter: Painter = painterResource(id = imageResId)
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showAdoptDialog = true },
                                modifier = Modifier
                                    .height(30.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NavyBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp),
                            ) {
                                Text(
                                    text = "Adopt Me!",
                                    style = TextStyle(
                                        fontFamily = SaintPeter,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        color = Orange
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = petModel.name,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = Namaku,
                                    color = Color.Blue,
                                    fontSize = 50.sp
                                )
                            )
                            Text(
                                text = petModel.petType,
                                style = MaterialTheme.typography.labelSmall
                                .copy(
                                    fontFamily = Namaku,
                                    color = Orange,
                                    fontSize = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${petModel.age} years old",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = SaintPeter,
                                fontSize = 16.sp,
                                color = NavyBlue
                            )
                            if (petModel.ownerName.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Owner: ${petModel.ownerName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = SaintPeter,
                                    fontSize = 16.sp,
                                    color = NavyBlue
                                )
                            }
                        }
                    }
                }
            }
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ItemPetPreview() {
    val samplePetModel = PetModel().apply {
        id = "1"
        name = "Buddy"
        age = 3
        petType = "Dog"
        ownerName = "John Doe"
    }
    val samplePetViewModel = PetViewModel()

    ItemPet(petModel = samplePetModel, petViewModel = samplePetViewModel, onRemove = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AdoptDialogPreview() {
    var showAdoptDialog by remember { mutableStateOf(true) }
    var ownerName by remember { mutableStateOf("") }
    val samplePetModel = PetModel().apply {
        id = "1"
        name = "Buddy"
        age = 3
        petType = "Dog"
        ownerName = ""
    }
    val samplePetViewModel = PetViewModel()

    if (showAdoptDialog) {
        AlertDialog(
            onDismissRequest = { showAdoptDialog = false },
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Adopt Pet",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = Namaku,
                            color = Color.Blue,
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            },
            text = {
                Column {
                    TextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Owner Name",
                            fontFamily = Charleville) },

                        textStyle = TextStyle(
                            fontFamily = SaintPeter,
                            fontSize = 16.sp,
                            color = NavyBlue
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedTextColor = NavyBlue,
                            containerColor = LightOrange,
                            focusedIndicatorColor = Orange,
                            unfocusedIndicatorColor = Color.LightGray
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (ownerName.isNotBlank()) {
                            samplePetViewModel.adoptPet(samplePetModel, ownerName)
                            showAdoptDialog = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Orange
                    )
                ) {
                    Text(
                        text = "Adopt",
                        style = TextStyle(
                            fontFamily = Namaku,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAdoptDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = LightGray
                    )
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontFamily = Namaku,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        )
    }
}


