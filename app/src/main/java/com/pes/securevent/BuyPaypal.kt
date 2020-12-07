package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.paypal.android.sdk.payments.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal


class BuyPaypal : AppCompatActivity() {
    val PAYPAL_REQUEST_CODE = 123

    val config = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .clientId(BuildConfig.keyPayPal)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_paypal)

        val actionBar : ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val extras = intent.extras
        val roomName = extras?.getString("roomName")
        actionBar.title = roomName

        val cadires = extras?.getString("matrix")

        var prematrix : List<String>?=null

        if (cadires != null) {
            prematrix = cadires.split('\n')
        }

        // GET THE MATRIX DIMENSIONS
        val rows = prematrix!!.size // Utilizar el put.extra para conseguir filas y columnas de la room
        var columns = prematrix.get(0).filter{it!= '\t'}.count()


        var sala = ArrayList<String>()

        for (i in 0 until columns){
            sala.add(prematrix.get(i).filter{it!= '\t'})
        }


        // INITIALISE YOUR GRID
        val grid = findViewById<View>(R.id.grid) as GridView
        grid.numColumns = columns

        // CREATE A LIST OF MATRIX OBJECT
        val salaList: MutableList<Seat> = ArrayList()

        // ADD SOME CONTENTS TO EACH ITEM
        for (i in 0 until rows) {
            for (j in 0 until columns) {

                if (sala.get(i).get(j) == 'T')
                    salaList.add(Seat(i, j, 'T'))
                else if (sala.get(i).get(j) == 'F')
                    salaList.add(Seat(i, j, 'F'))
                else
                    salaList.add(Seat(i, j, 'C'))


            }
        }

        // CREATE AN ADAPTER  (MATRIX ADAPTER)
        val adapter = SeatAdapter(applicationContext, salaList)

        // ATTACH THE ADAPTER TO GRID
        grid.adapter = adapter

        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    override fun onDestroy() {
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == RESULT_OK) {
                //Getting the payment confirmation
                val confirm: PaymentConfirmation? =
                    data?.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        val paymentDetails = confirm.toJSONObject().toString(4)
                        Log.i("paymentExample", paymentDetails)
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                getResources().getString(R.string.MessageInscripcioEvent),
                                Snackbar.LENGTH_LONG
                        )
                            .setAction("Action", null).show()
                    } catch (e: JSONException) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e)
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "paymentExample",
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
                )
            }
        }
    }

    fun increment(view: View) {
        val edit_text_tickets = findViewById<EditText>(R.id.numTickets)
        val numTickets = edit_text_tickets.text.toString().toInt()

        edit_text_tickets.setText((numTickets + 1).toString())

    }

    fun decrement(view: View) {
        val edit_text_tickets = findViewById<EditText>(R.id.numTickets)
        val numTickets = edit_text_tickets.text.toString().toInt()

        if (numTickets > 0)
            edit_text_tickets.setText((numTickets - 1).toString())
    }

    fun buy(view: View) {

        val edit_text_tickets = findViewById<EditText>(R.id.numTickets)
        val numTickets = edit_text_tickets.text.toString().toInt()

        if (numTickets > 0) {

            // Get extras in the Esdeveniment.kt
            val extras = intent.extras

            //Creating a paypalpayment
            val payment = PayPalPayment(
                    BigDecimal(extras?.getString("qtt")).toInt().toBigDecimal() * numTickets.toBigDecimal(),
                    "USD",
                    "Simplified Coding Fee",
                    PayPalPayment.PAYMENT_INTENT_SALE
            )

            //Creating Paypal Payment activity intent
            val intent = Intent(this, PaymentActivity::class.java)

            //putting the paypal configuration to the intent
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

            //Puting paypal payment to the intent
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

            //Starting the intent activity for result
            //the request code will be used on the method onActivityResult

            startActivityForResult(intent, PAYPAL_REQUEST_CODE)

            postEvent(extras?.getString("eventID"))
        }
    }

    fun postEvent(eventID: String?) {

        val url = "https://securevent.herokuapp.com/reservations"

        //creem objecte JSON per fer la crida POST
        val params = JSONObject()
        params.put("id_event", eventID)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var tokenMongoPost :String
        pref.apply{
            tokenMongoPost = (getString("TOKEN", "").toString())
        }
        // Volley post request with parameters
        val request = object : JsonObjectRequest(Method.POST, url, params,
                { response ->
                    // Process the json
                    try {
                        Log.i("Registration", "Response $response")
                    } catch (e: Exception) {
                        Log.e("Registration", "Response $e")
                    }
                }, {
            // Error in request -- ja estem registrats
            println("Volley error: $it")

        }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Token $tokenMongoPost")
                return headers
            }
        }
        // Volley request policy, only one time request to avoid duplicate transaction
        request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                1f
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}