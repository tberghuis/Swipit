package dev.tberghuis.swipit.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tberghuis.swipit.db.Subreddit
import dev.tberghuis.swipit.db.SubredditDao
import dev.tberghuis.swipit.util.logd
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle, private val subredditDao: SubredditDao
) : ViewModel() {
  val subredditListFlow = subredditDao.getAll().map { subredditList ->
    subredditList.map {
      it.subreddit
    }
  }
  init {
    logd("HomeViewModel subredditDao $subredditDao")
  }

  fun addSubreddit(subreddit: String) {
    viewModelScope.launch(Dispatchers.IO) {
      subredditDao.insertAll(Subreddit(subreddit))
    }
  }

  fun delete(subreddit: String) {
    viewModelScope.launch(Dispatchers.IO) {
      subredditDao.delete(Subreddit(subreddit))
    }
  }
}