package com.example.homework

data class Notification(var title: String)

object Supplier {

    val notifications = listOf<Notification>(
        Notification("This"),
        Notification("is"),
        Notification("where"),
        Notification("the"),
        Notification("notifications"),
        Notification("go")
    )
}
