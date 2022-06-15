package com.example.practiceset2.util

sealed class UIComponentType{

    data class Snackbar(val description: String): UIComponentType()

    object Dialog: UIComponentType()

    object None: UIComponentType()
}
