package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.pes.securevent.MainActivity.Companion.UsuariActiu
import com.pes.securevent.MainActivity.Companion.usuari
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.*
import org.json.JSONObject

class Acc : Fragment() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.keyGCM)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity().applicationContext, gso);

    }

    private fun signIn() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
                signInIntent, RC_SIGN_IN
        )
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        super.onViewCreated(view, savedInstanceState)
        val google_login_btn : Button = view.findViewById<Button>(R.id.google_login_btn)
        val google_logout_btn : Button = view.findViewById<Button>(R.id.google_logout_btn)
        google_login_btn.setOnClickListener{
            signIn()
        }
        google_logout_btn.setOnClickListener{
            signOut()
        }
        //handle login - hay que hacerlo así, ya que al fragment no le gustan las referencias xd.

        val NomUser : TextView = view.findViewById<TextView>(R.id.NomUser)
        val MailUser : TextView = view.findViewById<TextView>(R.id.MailUser)
        val imageE: ImageView = view.findViewById<ImageView>(R.id.imageE)
        val imageViewGoogle : ImageView = view.findViewById<ImageView>(R.id.imageViewGoogle)

        val pref = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        var check: Boolean
        pref.apply{
            check = getString("EMAIL", "") != ""
        }

        if (check) {
            UsuariActiu = true
            pref.apply {
                val firstname = getString("NAME", "") ?: ""
                val secondname = getString("SURNAME", "") ?: ""
                val email_ = getString("EMAIL", "") ?: ""
                val url_ = getString("IMAGE", "") ?: ""

                usuari = UserG(firstname, secondname, email_, "token", url_)
                NomUser.text = firstname
                MailUser.text = email_
                Picasso.get().load(url_).into(imageE);
            }
        }

        if (UsuariActiu) {
            google_login_btn.visibility = View.GONE; //amaguem el botó
            imageViewGoogle.visibility = View.GONE;
            imageE.visibility = View.VISIBLE
            NomUser.visibility = View.VISIBLE
            MailUser.visibility = View.VISIBLE
            NomUser.text = usuari.firstName
            MailUser.text = usuari.email
            Picasso.get().load(usuari.image).into(imageE);
            google_logout_btn.visibility = View.VISIBLE
        }

        return view;
    }

    private fun signOut() {

        activity?.let {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(it) {
                        UsuariActiu = false
                        google_login_btn.visibility = View.VISIBLE;
                        imageViewGoogle.visibility = View.VISIBLE;
                        google_logout_btn.visibility = View.GONE;
                        imageE.visibility = View.GONE
                        NomUser.visibility = View.GONE
                        MailUser.visibility = View.GONE
                    }
        }
        val pref = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        val editor = pref.edit()
        editor
                .putString("NAME", "")
                .putString("SURNAME", "")
                .putString("USERNAME", "")
                .putString("EMAIL", "")
                .putString("IMAGE", "")
                .putString("TOKEN", "")
                .putString("ID", "")
                .apply()

        UsuariActiu = false;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                    ApiException::class.java
            )
            // Signed in successfully
            val googleId = account?.id ?: ""

            val googleFirstName = account?.givenName ?: ""

            val googleLastName = account?.familyName ?: ""

            val googleEmail = account?.email ?: ""

            val googleProfilePicURL = account?.photoUrl.toString()

            val googleIdToken = account?.idToken ?: ""

            usuari = UserG(googleFirstName, googleLastName, googleEmail, googleIdToken, googleProfilePicURL)

            loadUserInfo()

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                    "failed code=", e.statusCode.toString()
            )
        }
    }


    private fun loadUserInfo() {

        //Fem el post a clients. SI ja estem registrats ens torna un error 400.

        val username = usuari.email.split("@")
        val url = "https://securevent.herokuapp.com/clients"

        //creem objecte JSON per fer la crida POST
        val params = JSONObject()
        params.put("username", username[0]);
        params.put("email", usuari.email);
        params.put("password",  username[0]);
        params.put("first_name",  usuari.firstName);
        params.put("last_name",  usuari.lastName);
        params.put("is_manager",  false);

        //un cop ja hem fet el registre, sol.licitem el token

        //getToken

        var token_mongo = ""
        var user_id = -1
        val urltoken = "https://securevent.herokuapp.com/auth/login"
        val paramsToken = JSONObject()
        paramsToken.put("username", username[0]);
        paramsToken.put("password", username[0]);

        // Volley post request with parameters
        val requestToken= JsonObjectRequest(com.android.volley.Request.Method.POST, urltoken, paramsToken,
                { response ->
                    // Process the json
                    try {
                        user_id = response.getJSONObject("user").getInt("id")
                        token_mongo = response.getString("token")
                        guardaCache(token_mongo, user_id.toString(), username[0])

                    } catch (e: Exception) {
                       println( "Response $e")

                    }

                }, {
            // Error in request
            //POST a /clients
            // Volley post request with parameters
            val request = JsonObjectRequest(com.android.volley.Request.Method.POST,url,params,
                    { response ->
                        // Process the json
                        try {
                            println( "Response $response")
                        }catch (e:Exception){
                            println("Response $e")

                        }

                    }, {
                // Error in request -- ja estem registrats
                println( "Volley error: $it")

            })


            // Volley request policy, only one time request to avoid duplicate transaction
            request.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    0,
                    1f
            )

            activity?.applicationContext?.let { VolleySingleton.getInstance(it).addToRequestQueue(request)}

        })


        // Volley request policy, only one time request to avoid duplicate transaction
        requestToken.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                1f
        )

        // Add the volley post request to the request queue

        activity?.applicationContext?.let { VolleySingleton.getInstance(it).addToRequestQueue(requestToken) }

        //Un cop fetes les crides i amb la informació, guardem al local storage l'user

        //Fragment Sign In
        google_login_btn.visibility = View.GONE; //amaguem el botó
        imageViewGoogle.visibility = View.GONE;
        imageE.visibility = View.VISIBLE
        NomUser.visibility = View.VISIBLE
        MailUser.visibility = View.VISIBLE
        NomUser.text = usuari.firstName
        MailUser.text = usuari.email
        Picasso.get().load(usuari.image).into(imageE);
        google_logout_btn.visibility = View.VISIBLE
        UsuariActiu = true
    }

    private fun guardaCache(token_mongo: String, user_id: String, username: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        val editor = pref.edit()
        editor
            .putString("NAME", usuari.firstName)
            .putString("SURNAME", usuari.lastName)
            .putString("USERNAME", username)
            .putString("EMAIL", usuari.email)
            .putString("IMAGE", usuari.image)
            .putString("TOKEN", token_mongo)
            .putString("ID", user_id)
            .apply()
    }

}