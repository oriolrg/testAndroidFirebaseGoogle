package com.oriolrg.firebasetutorial

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType{
    BASIC,
    GOOGLE
}
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        //Setup
        //recuperem les dades de l'Intent email i provider
        //Bundle es un objecte per passar dades entre processos o activitats diferents
        //Un Intent es un objeto que proporciona vinculación en tiempo de ejecución entre componentes separados, como dos actividades
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        var firestore: FirebaseFirestore
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("tracks").document("correr").collection("track")
            .get()
            .addOnSuccessListener {documents->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.getData('btt')}")
                }

            }
            .addOnFailureListener{
                Log.d(TAG, "get failed with ", it)
            }
        //?:"" si no existeix envia string buit
        setup(email ?: "", provider ?:"")

        //Guardadr dades de l'usuaria autenticat a nivell de sessio

        val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs?.putString("email", email)
        prefs?.putString("provider", provider)
        prefs?.apply()
    }
    private fun setup(email: String, provider: String){
        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val providerTextView = findViewById<TextView>(R.id.providerTextView)
        val logOutButton = findViewById<Button>(R.id.logOutButton)
        title = "Inici"
        emailTextView.text = email
        providerTextView.text = provider
        //Accions al clicar el boto
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //Borrat de dades
            val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs?.clear()
            prefs?.apply()
            //retorna  a la pantalla anterior
            onBackPressed()
        }


    }
}