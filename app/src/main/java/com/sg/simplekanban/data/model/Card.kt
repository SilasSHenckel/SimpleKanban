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
    var startDate : String? = null,
    var endDate : String? = null,
    var priority : Int = 0,
    val ownerId : String? = null,
    var responsibleId : String? = null,
)  : Parcelable