package ph.edu.auf.realmdiscussion.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
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
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel


@Composable
fun PetScreen(petViewModel: PetViewModel = viewModel()){

    val pets by petViewModel.pets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState()}
    val coroutineScope = rememberCoroutineScope()

    var snackbarShown by remember { mutableStateOf(false) }

    LaunchedEffect(petViewModel.showSnackbar)
    {
        petViewModel.showSnackbar.collect{ message ->
            if(!snackbarShown){
                snackbarShown = true
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                    when(result){
                        SnackbarResult.Dismissed ->{
                            Log.d("PetScreen", "Dismissed")
                            snackbarShown = false
                        }
                        SnackbarResult.ActionPerformed ->{
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState)}
        ) { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                itemsIndexed(
                    items = pets,
                    key = {_,item -> item.id}
                ){_, petContent->
                    ItemPet(petContent, onRemove = petViewModel::deletePet)
                }
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PetScreenPreview(){
    PetScreen()
}