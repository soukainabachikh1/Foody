package com.imadev.foody.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.imadev.foody.R
import com.imadev.foody.model.Category
import com.imadev.foody.model.Meal
import com.imadev.foody.utils.Constants.CATEGORY_COLLECTION
import com.imadev.foody.utils.Constants.CATEGORY_DRINKS
import com.imadev.foody.utils.Constants.CATEGORY_FOODS
import com.imadev.foody.utils.Constants.CATEGORY_SNACKS
import com.imadev.foody.utils.Constants.MEALS_COLLECTION
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*


private const val TAG = "GenerateFoodViewModel"

class GenerateFoodViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()


    fun uploadCategories() = viewModelScope.launch {

        val categories = listOf(
            Category(name = CATEGORY_FOODS),
            Category(name = CATEGORY_DRINKS),
            Category(name = CATEGORY_SNACKS)
        )

        categories.map {
            async {
                db.collection(CATEGORY_COLLECTION).add(it)
            }
        }
    }


    val categoryFlow = MutableSharedFlow<String>()


    suspend fun getCategories() {
        val task = db.collection(CATEGORY_COLLECTION).get().await()
        task.documents.map {
            delay(1000)
            categoryFlow.emit(it.id)
        }
    }




     fun insertMeal() {
        val img = "https://firebasestorage.googleapis.com/v0/b/foody-341522.appspot.com/o/images%2Fimages%2Ffish%20borcolli.jpg?alt=media&token=9959ca08-72f8-4f18-a670-412698f1978d"
        val name = "Broccoli fish"
        val ingredient = listOf(
            "375g Sweet Potatoes",
            "2 Tbsps Parmesan Cheese",
            "2 Cod Fillets",
            "A Few Sprigs Mint",
        )

        val catID = "ewc1tpKhDqtZWppueb4w"
        val price = 80.0

        val meal = Meal(
            name = name,
            price = price,
            ingredient = ingredient,
            image = img,
            categoryId = catID,
        )


        db.collection(MEALS_COLLECTION).add(meal).addOnSuccessListener {
            Log.d(TAG, "insertMeal: success")
        }.addOnFailureListener {
            Log.d(TAG, "insertMeal: ${it.message}")
        }
    }


}