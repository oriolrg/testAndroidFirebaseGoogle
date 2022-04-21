package com.oriolrg.firebasetutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC
}
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        //Setup
        //recuperem les dades de l'Intent email i provider
        //TODO Mirar que es Bundle i Intent
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        //?:"" si no existeix envia string buit
        setup(email ?: "", provider ?:"")
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
            //retorna  a la pantalla anterior
            onBackPressed()
        }


    }
}