package com.oriolrg.firebasetutorial

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100

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
        //Coprobem si existeix sessió
        session()
    }
    private fun session(){
        //Recuperem si tenim guardat email i provider
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email:String? = prefs.getString("email", null)
        val provider:String? = prefs.getString("provider", null)
        //si es diferent que null significa que tenim iniciada sessio a la app
        if (email != null && provider != null){
            //authLayout.visivility = VIew.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }
    //Logica de la pantalla auhtactivity
    private fun setup(){
        val logInButton = findViewById<Button>(R.id.logInButton)
        val googleButton = findViewById<Button>(R.id.googleButton)
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
        googleButton.setOnClickListener {
            //Configuracio
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            //Creem el client d'autentifiacio amb google
            val googleClient:GoogleSignInClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val acount = task.getResult(ApiException::class.java)
                if (acount != null){
                    val credential = GoogleAuthProvider.getCredential(acount.idToken,null)
                    FirebaseAuth.getInstance()
                        .signInWithCredential(credential).addOnCompleteListener {
                            //Comprobo si hi ha errors en el registre
                            if(it.isSuccessful){
                                //Si registre ha estat correcte mostro nova pantalla HomeActivity
                                showHome(acount.email ?: "", ProviderType.GOOGLE)
                            }else{
                                //Sii hi ha error mostro missatge alerta
                                showAlert()
                            }
                        }

                }
            }catch (e: ApiException){
                showAlert()
            }

        }
    }
}