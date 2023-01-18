package dev.tberghuis.swipit.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.tberghuis.swipit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
  navigateSubreddit: (subreddit: String) -> Unit,
) {
  val openDialog = rememberSaveable { mutableStateOf(false) }
  val closeDialog = { openDialog.value = false }
  Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    floatingActionButton = {
      FloatingActionButton(onClick = {
        openDialog.value = true
      }) {
        Icon(Icons.Filled.Add, contentDescription = "add subreddit")
      }
    }) { paddingValues ->
    HomeScreenContent(navigateSubreddit, paddingValues)
    if (openDialog.value) AddSubredditDialog(closeDialog)
  }
}

@Composable
fun HomeScreenContent(
  navigateSubreddit: (subreddit: String) -> Unit, paddingValues: PaddingValues
) {
  val vm = hiltViewModel<HomeViewModel>()
  val subredditList by vm.subredditListFlow.collectAsState(initial = listOf())

  // focus bug on Google TV if use LazyColumn, should i bother reporting???
  // not a real prod app, waste of time to report
  //  LazyColumn(
  //    contentPadding = PaddingValues(10.dp),
  //  ) {
  //    items(subredditList.size) { index ->
  //      val subreddit = subredditList[index]
  //    }
  //  }

  Column(
    modifier = Modifier
      .padding(paddingValues)
      .fillMaxSize(),
//    contentPadding = PaddingValues(10.dp),
    verticalArrangement = Arrangement.Center
  ) {

    subredditList.forEach { subreddit ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = {
          navigateSubreddit(subreddit)
        }) {
          Text(subreddit)
        }
        IconButton(onClick = {
          vm.delete(subreddit)
        }) {
          Icon(Icons.Filled.Delete, contentDescription = "delete")
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubredditDialog(closeDialog: () -> Unit) {
  val vm: HomeViewModel = hiltViewModel()
  var text by rememberSaveable { mutableStateOf("") }
  AlertDialog(onDismissRequest = closeDialog, confirmButton = {}, text = {
    TextField(value = text,
      onValueChange = { text = it },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onDone = {
        //todo try catch UNIQUE constraint...
        vm.addSubreddit(text)
        closeDialog()
      }),
      label = { Text("Add Subreddit") })
  })
}