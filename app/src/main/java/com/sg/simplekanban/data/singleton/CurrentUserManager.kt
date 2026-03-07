package com.sg.simplekanban.data.singleton

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserManager @Inject constructor(){

    var currentKanbanUserId: String? = null
    var userId :String? = null

}