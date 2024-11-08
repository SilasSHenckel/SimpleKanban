package com.sg.simplekanban.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card (
    @DocumentId
    var documentId : String? = null,

    var title: String? = null,
    var description: String? = null,
    var columnId : String? = null,
    val creationDate : String? = null,
    val startDate : String? = null,
    val endDate : String? = null,
    val priority : Int = 0,
    val ownerId : String? = null,
    var responsibleId : String? = null,
)  : Parcelable