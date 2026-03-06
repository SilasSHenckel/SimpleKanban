package com.sg.simplekanban.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.sg.simplekanban.data.constants.Constants.Companion.CREATED_AT
import com.sg.simplekanban.data.constants.Constants.Companion.EMAIL
import com.sg.simplekanban.data.constants.Constants.Companion.NAME
import com.sg.simplekanban.data.constants.Constants.Companion.PHOTO_URL

fun FirebaseUser.toUser() = mapOf(
    NAME to displayName,
    EMAIL to email,
    PHOTO_URL to photoUrl?.toString(),
    CREATED_AT to serverTimestamp()
)
