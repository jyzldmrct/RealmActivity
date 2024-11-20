package ph.edu.auf.realmdiscussion.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel

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
        "Dog",
        "Cat",
        "Bird",
        "Fish",
        "Hamster",
        "Rabbit",
        "Guinea Pig",
        "Turtle",
        "Ferret",
        "Lizard",
        "Snake",
        "Parrot"
    )

    var newOwnerName by remember { mutableStateOf("") }


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

    Column(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Pets") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
                Button(onClick = { showDialog = true }, modifier = Modifier.padding(16.dp)) {
                    Text("Add Pet")
                }
                LazyColumn {
                    itemsIndexed(
                        items = filteredPets,
                        key = { _, item -> item.id }
                    ) { index: Int, petContent: PetModel ->
                        ItemPet(petContent, petViewModel, onRemove = petViewModel::deletePet)
                    }
                }
            }
        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Pet") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPetName,
                        onValueChange = { newPetName = it },
                        label = { Text("Pet Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = newPetAge,
                        onValueChange = { newPetAge = it },
                        label = { Text("Pet Age") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

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
                            label = { Text("Owner Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedPetType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pet Type") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
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
                        if (newPetName.isNotBlank() && selectedPetType.isNotBlank() && newPetAge.isNotBlank()) {
                            petViewModel.addPet(newPetName, newPetAge.toInt(), hasOwner, selectedPetType, newOwnerName)
                            showDialog = false
                            // Reset the form
                            newPetName = ""
                            newPetAge = ""
                            selectedPetType = ""
                            hasOwner = false
                            newOwnerName = ""
                        }
                    }
                ) {
                    Text("Add")
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
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}