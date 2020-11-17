package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.pes.securevent.MainActivity.Companion.UsuariActiu
import com.pes.securevent.MainActivity.Companion.usuari
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
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
        //mGoogleSignInClient = GoogleSignIn.getClient(this.context, gso)

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
        val url = "https://securevent.herokuapp.com/clients"
        /*val params = HashMap<String,String>()
        params["username"] = usuari.firstName
        params["email"] = usuari.email
        params["password"] = usuari.token
        params["first_name"] = usuari.firstName
        params["last_name"] = usuari.lastName
        params["is_manager"] = "false"*/


/*
     val params = JSONObject()
        val username = usuari.email.split("@")
        println(username[0])
        println(usuari.token)
     params.put("username", username[0]);
     params.put("email", usuari.email);
     params.put("password",  username[0]);
     params.put("first_name",  usuari.firstName);
     params.put("last_name",  usuari.lastName);
     params.put("is_manager",  false);





 // Volley post request with parameters
 val request = JsonObjectRequest(com.android.volley.Request.Method.POST,url,params,
         { response ->
             // Process the json
             try {
                 Log.i("Registration", "Response $response")
             }catch (e:Exception){
                 Log.e("Registration", "Response $e")

             }

         }, {
     // Error in request
    println( "Volley error: $it")

 })


 // Volley request policy, only one time request to avoid duplicate transaction
 request.retryPolicy = DefaultRetryPolicy(
         DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
         // 0 means no retry
         0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
         1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
 )

*/

//getToken
        val username = usuari.email.split("@")
        val urltoken = "https://securevent.herokuapp.com/auth/login"
        val paramsToken = JSONObject()
        paramsToken.put("username", username[0]);
        paramsToken.put("password",  username[0]);

        // Volley post request with parameters
        val requestToken= JsonObjectRequest(com.android.volley.Request.Method.POST,urltoken,paramsToken,
                { response ->
                    // Process the json
                    try {
                        Log.i("Registration", "Response $response")
                    }catch (e:Exception){
                        Log.e("Registration", "Response $e")

                    }

                }, {
            // Error in request
            println( "Volley error: $it")

        })


        // Volley request policy, only one time request to avoid duplicate transaction
        requestToken.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                // 0 means no retry
                0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

 // Add the volley post request to the request queue

 //getActivity()?.getApplicationContext()?.let { VolleySingleton.getInstance(it).addToRequestQueue(request)}
 getActivity()?.getApplicationContext()?.let { VolleySingleton.getInstance(it).addToRequestQueue(requestToken)
 }


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
 println(MainActivity.UsuariActiu)
 //Header


 //var navigationView: NavigationView

 //navigationView = (NavigationView()) findViewById(R.id.nav_view);
 //nav_view.getMenu().clear(); //clear old inflated items.
 //nav_view.inflateMenu(R.layout.header_log); //inflate new items
// (activity as AppCompatActivity).header?.title = "Example 1"
 //Picasso.get().load(userGoogle.image).into(findViewById);
 //imageUserName.text = userGoogle.firstName
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