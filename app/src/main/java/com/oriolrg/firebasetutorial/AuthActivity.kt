package com.oriolrg.firebasetutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        //Googleanalitics events
        var analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        var bundle = Bundle()
        bundle.putString("message", "Integració de firebase completa")
        analytics.logEvent("InitScrean", bundle)

        //Setup
        setup()
    }
    //Logica de la pantalla auhtactivity
    private fun setup(){
        val logInButton = findViewById<Button>(R.id.logInButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        title = "Autentificacio"
        //Logica botó registrar
        signUpButton.setOnClickListener {
            //Comprobar que les dades són correctes
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                //registrem usuari a firebase
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailEditText.text.toString(),
                        passwordEditText.text.toString()).addOnCompleteListener {
                            //Comprobo si hi ha errors en el registre
                            if(it.isSuccessful){
                                //Si registre ha estat correcte mostro nova pantalla HomeActivity
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            }else{
                                //Sii hi ha error mostro missatge alerta
                                showAlert()
                            }
                }
            }
        }
        //Logica botó accedir
        logInButton.setOnClickListener {
            //Comprobar que les dades són correctes
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                //registrem usuari a firebase
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailEditText.text.toString(),
                        passwordEditText.text.toString()).addOnCompleteListener {
                            //Comprobo si hi ha errors en el registre
                            if(it.isSuccessful){
                                //Si registre ha estat correcte mostro nova pantalla HomeActivity
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            }else{
                                //Sii hi ha error mostro missatge alerta
                                showAlert()
                            }
                        }
            }
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("S'ha produit un error notificant l'usuari")
        builder.setPositiveButton("Acceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email:String, provider: ProviderType){
        //Mostrarem la pantalla
        val homeInetent = Intent(this, HomeActivity::class.java).apply {
            //Pasem a la nova pantalla email i proveidor
            putExtra("email", email)
            putExtra("provider", provider)
        }
        //Naveguem a la nova pantalla un cop tenim apunt l'Intent
        startActivity(homeInetent)
    }
}