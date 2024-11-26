package ph.edu.auf.realmdiscussion.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.components.ItemPet
import androidx.compose.material3.Checkbox
import androidx.compose.ui.Alignment
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ph.edu.auf.realmdiscussion.ui.theme.Black
import ph.edu.auf.realmdiscussion.ui.theme.Blue
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel
import ph.edu.auf.realmdiscussion.ui.theme.Charleville
import ph.edu.auf.realmdiscussion.ui.theme.LightGray
import ph.edu.auf.realmdiscussion.ui.theme.LightOrange
import ph.edu.auf.realmdiscussion.ui.theme.Namaku
import ph.edu.auf.realmdiscussion.ui.theme.NavyBlue
import ph.edu.auf.realmdiscussion.ui.theme.Orange
import ph.edu.auf.realmdiscussion.ui.theme.SaintPeter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetScreen(petViewModel: PetViewModel = viewModel()) {

    val pets by petViewModel.pets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var snackbarShown by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredPets = pets.filter { it.name.contains(searchQuery, ignoreCase = true) }

    var showDialog by remember { mutableStateOf(false) }
    var newPetName by remember { mutableStateOf("") }
    var newPetAge by remember { mutableStateOf("") }
    var hasOwner by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedPetType by remember { mutableStateOf("") }
    val petTypes = listOf(
        "Dog", "Cat", "Duck", "Pig", "Bird", "Fish", "Hamster", "Rabbit", "Guinea Pig", "Turtle", "Snake"
    )

    var newOwnerName by remember { mutableStateOf("") }

    val nameError by petViewModel.nameError.collectAsState()
    val ageError by petViewModel.ageError.collectAsState()
    val ownerNameError by petViewModel.ownerNameError.collectAsState()

    LaunchedEffect(nameError, ageError, ownerNameError) {
        if (nameError != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(nameError!!)
            }
        } else if (ageError != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(ageError!!)
            }
        } else if (ownerNameError != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(ownerNameError!!)
            }
        }
    }

    LaunchedEffect(petViewModel.showSnackbar) {
        petViewModel.showSnackbar.collect { message: String ->
            if (!snackbarShown) {
                snackbarShown = true
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> {
                            Log.d("PetScreen", "Dismissed")
                            snackbarShown = false
                        }
                        SnackbarResult.ActionPerformed -> {
                            Log.d("PetScreen", "Action performed")
                            snackbarShown = false
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search Pets", fontFamily = Charleville, fontSize = 18.sp, color = Orange) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .height(40.dp)
                                .width(130.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Orange,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(25.dp),
                        ) {
                            Text(
                                text = "ADD PET ",
                                style = TextStyle(
                                    fontFamily = SaintPeter,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            )
                        }
                    }
                    LazyColumn {
                        items(
                            items = filteredPets,
                            key = { it.id }
                        ) { petContent ->
                            ItemPet(petContent, petViewModel, onRemove = petViewModel::deletePet)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add New Pet",
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
                        OutlinedTextField(
                            value = newPetName,
                            onValueChange = { newPetName = it },
                            label = { Text("Pet Name", fontFamily = Charleville) },
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
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        if (nameError != null) {
                            Text(
                                text = nameError!!,
                                color = Color.Red,
                                style = TextStyle(fontFamily = Charleville, fontSize = 12.sp),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = newPetAge,
                            onValueChange = { newPetAge = it },
                            label = { Text("Pet Age", fontFamily = Charleville) },
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
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        if (ageError != null) {
                            Text(
                                text = ageError!!,
                                color = Color.Red,
                                style = TextStyle(fontFamily = Charleville, fontSize = 12.sp),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Checkbox(
                                checked = hasOwner,
                                onCheckedChange = { hasOwner = it }
                            )
                            Text("Has Owner")
                        }

                        if (hasOwner) {
                            OutlinedTextField(
                                value = newOwnerName,
                                onValueChange = { newOwnerName = it },
                                label = { Text("Owner Name", fontFamily = Charleville) },
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
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                            if (ownerNameError != null) {
                                Text(
                                    text = ownerNameError!!,
                                    color = Color.Red,
                                    style = TextStyle(fontFamily = Charleville, fontSize = 12.sp),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedPetType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Pet Type", fontFamily = Charleville) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                textStyle = TextStyle(
                                    fontFamily = SaintPeter,
                                    fontSize = 24.sp,
                                    color = NavyBlue
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                petTypes.forEach { petType ->
                                    DropdownMenuItem(
                                        text = { Text(petType) },
                                        onClick = {
                                            selectedPetType = petType
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            petViewModel.addPet(newPetName, newPetAge, hasOwner, selectedPetType, newOwnerName)
                            showDialog = false
                            // Reset the form
                            newPetName = ""
                            newPetAge = ""
                            selectedPetType = ""
                            hasOwner = false
                            newOwnerName = ""
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Orange
                        )
                    ) {
                        Text(
                            text = "Add",
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
                        onClick = {
                            showDialog = false
                            // Reset the form
                            newPetName = ""
                            newPetAge = ""
                            selectedPetType = ""
                            hasOwner = false
                            newOwnerName = ""
                        },
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
}

@Preview(showBackground = true)
@Composable
fun PetScreenPreview() {
    PetScreen()
}