package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.viewmodels.OwnerViewModel
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ph.edu.auf.realmdiscussion.ui.theme.Charleville
import ph.edu.auf.realmdiscussion.ui.theme.LightGray
import ph.edu.auf.realmdiscussion.ui.theme.LightOrange
import ph.edu.auf.realmdiscussion.ui.theme.Namaku
import ph.edu.auf.realmdiscussion.ui.theme.NavyBlue
import ph.edu.auf.realmdiscussion.ui.theme.Orange
import ph.edu.auf.realmdiscussion.ui.theme.SaintPeter

@Composable
fun OwnerScreen(
    ownerViewModel: OwnerViewModel = viewModel(),
    petViewModel: PetViewModel = viewModel()
) {
    val owners by ownerViewModel.owners.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Scaffold { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                itemsIndexed(
                    items = owners,
                    key = { _, item -> item.id }
                ) { _, ownerContent ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = LightOrange
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        ),
                        shape = RoundedCornerShape(25.dp)

                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OwnerItem(
                                owner = ownerContent,
                                ownerViewModel = ownerViewModel,
                                petViewModel = petViewModel
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Owns ${ownerContent.totalPets} pets",
                                fontFamily = SaintPeter,
                                fontSize = 16.sp,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerItem(
    owner: OwnerModel,
    ownerViewModel: OwnerViewModel,
    petViewModel: PetViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(owner.name) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Owner",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = Namaku,
                        color = Color.Blue,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete ${owner.name}?",
                    style = TextStyle(
                        fontFamily = SaintPeter,
                        fontSize = 16.sp,
                        color = NavyBlue
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        ownerViewModel.deleteOwner(owner)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Orange
                    )
                ) {
                    Text(
                        text = "Delete",
                        style = TextStyle(
                            fontFamily = SaintPeter,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = LightGray
                    )
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontFamily = SaintPeter,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        )
    }

    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = {
                Text(
                    text = "Update Owner Name",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = Namaku,
                        color = Color.Blue,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                )
            },
            text = {
                Column {
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Owner Name", fontFamily = Charleville) },
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
                        ownerViewModel.updateOwnerName(owner.id, newName, petViewModel)
                        showUpdateDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Orange
                    )
                ) {
                    Text(
                        text = "Update",
                        style = TextStyle(
                            fontFamily = SaintPeter,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUpdateDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = LightGray
                    )
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontFamily = SaintPeter,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDeleteDialog = true }
    ) {
        Text(
            text = owner.name,
            modifier = Modifier.clickable { showUpdateDialog = true },
            style = TextStyle(
                fontFamily = Namaku,
                fontSize = 24.sp,
                color = NavyBlue
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerScreenPreview() {
    val sampleOwnerViewModel = OwnerViewModel()
    val samplePetViewModel = PetViewModel()

    OwnerScreen(ownerViewModel = sampleOwnerViewModel, petViewModel = samplePetViewModel)
}
