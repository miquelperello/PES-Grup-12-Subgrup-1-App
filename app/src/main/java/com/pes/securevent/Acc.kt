package com.pes.securevent

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.preference.PreferenceManager.*
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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignIn.newInstance] factory method to
 * create an instance of this fragment.
 */
class Acc : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("188171199785-ffb2q48q1kjvvgf6lekq226028diam9a.apps.googleusercontent.com")
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity().getApplicationContext(), gso);


    }

    private fun signIn() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
                signInIntent, RC_SIGN_IN
        )
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
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

        val pref = PreferenceManager.getDefaultSharedPreferences(getActivity()?.getApplicationContext())
        var check: Boolean
        pref.apply{
            if (getString("EMAIL", "") != "") check = true
            else check = false
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
            google_login_btn.setVisibility(View.GONE); //amaguem el botó
            imageE.setVisibility(View.VISIBLE)
            NomUser.setVisibility(View.VISIBLE)
            MailUser.setVisibility(View.VISIBLE)
            NomUser.text = usuari.firstName
            MailUser.text = usuari.email
            Picasso.get().load(usuari.image).into(imageE);
            google_logout_btn.setVisibility(View.VISIBLE)
        }



        return view;
    }

    private fun signOut() {

        getActivity()?.let {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(it) {
                        UsuariActiu = false
                        google_login_btn.setVisibility(View.VISIBLE);
                        google_logout_btn.setVisibility(View.GONE);
                        imageE.setVisibility(View.GONE)
                        NomUser.setVisibility(View.GONE)
                        MailUser.setVisibility(View.GONE)
                    }
        }
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
            //println("Google ID" + googleId)

            val googleFirstName = account?.givenName ?: ""
            //println("Google First Name" + googleFirstName)

            val googleLastName = account?.familyName ?: ""
            //println("Google Last Name" + googleLastName)

            val googleEmail = account?.email ?: ""
            //println("Google Email" + googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            //println("Google Profile Pic URL" + googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            //println("Google ID Token" + googleIdToken)

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
        val urltoken = "https://securevent.herokuapp.com/auth/login"
        val paramsToken = JSONObject()
        paramsToken.put("username", username[0]);
        paramsToken.put("password", username[0]);

        // Volley post request with parameters
        val requestToken= JsonObjectRequest(com.android.volley.Request.Method.POST, urltoken, paramsToken,
                { response ->
                    // Process the json
                    try {

                        val event = response.getString("token")
                        println(event)
                        println( "Response $response")
                        token_mongo = event
                        println(token_mongo)

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

            getActivity()?.getApplicationContext()?.let { VolleySingleton.getInstance(it).addToRequestQueue(request)}

        })


        // Volley request policy, only one time request to avoid duplicate transaction
        requestToken.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                1f
        )

        // Add the volley post request to the request queue

        getActivity()?.getApplicationContext()?.let { VolleySingleton.getInstance(it).addToRequestQueue(requestToken) }

//Un cop fetes les crides i amb la informació, guardem al local storage l'user

        val pref = PreferenceManager.getDefaultSharedPreferences(getActivity()?.getApplicationContext())
        pref.apply{
            println(getString("NAME", ""))
            println(getString("EMAIL", ""))
            println(getString("IMAGE", ""))
        }
        val editor = pref.edit()

        editor
                .putString("NAME", usuari.firstName)
                .putString("SURNAME", usuari.lastName)
                .putString("EMAIL", usuari.email)
                .putString("IMAGE", usuari.image)
                .putString("TOKEN", token_mongo)
                .apply()


        //Fragment Sign In
        google_login_btn.setVisibility(View.GONE); //amaguem el botó
        imageE.setVisibility(View.VISIBLE)
        NomUser.setVisibility(View.VISIBLE)
        MailUser.setVisibility(View.VISIBLE)
        NomUser.text = usuari.firstName
        MailUser.text = usuari.email
        Picasso.get().load(usuari.image).into(imageE);
        google_logout_btn.setVisibility(View.VISIBLE)
        MainActivity.UsuariActiu = true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignIn.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                Acc().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}