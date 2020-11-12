package com.pes.securevent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.pes.securevent.MainActivity.Companion.usuari
import com.pes.securevent.UserG
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.header.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignIn.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignIn : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        super.onViewCreated(view, savedInstanceState)
        val button : Button = view.findViewById<Button>(R.id.google_login_btn)
        button.setOnClickListener{
            signIn()
        }
        return view;
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

             usuari = UserG( googleFirstName, googleLastName, googleEmail, googleIdToken, googleProfilePicURL )

            loadUserInfo()

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                    "failed code=", e.statusCode.toString()
            )
        }
    }

    private fun loadUserInfo() {
        //Fragment Sign In
        google_login_btn.setVisibility(View.GONE); //amaguem el botó
        titleE.text = usuari.firstName
        descE.text = usuari.email
        Picasso.get().load(usuari.image).into(imageE);
        MainActivity.UsuariActiu = true
        println (MainActivity.UsuariActiu)
        //Header

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
            SignIn().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}