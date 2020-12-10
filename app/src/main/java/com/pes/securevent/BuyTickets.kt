package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import kotlinx.android.synthetic.main.activity_buy_paypal.*


class BuyTickets : AppCompatActivity() {

    val LOAD_PAYMENT_DATA_REQUEST_CODE = 123

    private lateinit var paymentsClient: PaymentsClient
    private var fullRoom: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_paypal)


        // Initialize a Google Pay API client for an environment suitable for testing.
        // It's recommended to create the PaymentsClient object inside of the onCreate method.
        paymentsClient = PaymentsUtil.createPaymentsClient(this)
        possiblyShowGooglePayButton()


        val actionBar : ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val extras = intent.extras
        val roomName = extras?.getString("roomName")
        val user_id = extras?.getString("user_id")

        actionBar.title = roomName

        val cadires = extras?.getString("matrix")

        var prematrix : List<String>?=null

        if (cadires != null) {
            prematrix = cadires.split('\n')
        }

        // GET THE MATRIX DIMENSIONS
        val rows = prematrix!!.size // Utilizar el put.extra para conseguir filas y columnas de la room
        val columns = prematrix[0].split('\t').count()
        //var columns = prematrix.get(0).filter{it!= '\t'}.count()


        val sala = ArrayList<ArrayList<String>>()

        for (i in 0 until rows ){
            sala.add(prematrix[i].split('\t') as ArrayList<String>)
        }


        // INITIALISE YOUR GRID
        val grid = findViewById<View>(R.id.grid) as GridView
        grid.numColumns = columns

        // CREATE A LIST OF MATRIX OBJECT
        val salaList: MutableList<Seat> = ArrayList()

        // ADD SOME CONTENTS TO EACH ITEM
        for (i in 0 until rows) {
            for (j in 0 until columns) {

                if (sala[i][j] == "T") {
                    salaList.add(Seat(i, j, 'T'))
                    fullRoom = false;
                }
                else if (sala[i][j] == "F")
                    salaList.add(Seat(i, j, 'C'))
                else
                    if (sala[i][j] == user_id)
                        salaList.add(Seat(i, j, 'U'))
                    else
                        salaList.add(Seat(i, j, 'F'))
            }
        }

        // CREATE AN ADAPTER  (MATRIX ADAPTER)
        val adapter = SeatAdapter(applicationContext, salaList)

        // ATTACH THE ADAPTER TO GRID
        grid.adapter = adapter


    }

    private fun possiblyShowGooglePayButton() {
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay failed", exception)
            }
        }

    }

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            googlePayButton.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                this,
                "Unfortunately, Google Pay is not available on this device",
                Toast.LENGTH_LONG).show();
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            // Value passed in AutoResolveHelper
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }!!

                    RESULT_CANCELED -> {
                        // The user cancelled the payment attempt
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }!!
                    }
                }

                // Re-enables the Google Pay payment button.
                googlePayButton.isClickable = true
            }
        }
    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)

            //Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show()

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token"))

        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }

    }
    private fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
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

        val extras = intent.extras
        val edit_text_tickets = findViewById<EditText>(R.id.numTickets)
        val numTickets = edit_text_tickets.text.toString().toInt()

        if (numTickets > 0) {
            if (fullRoom)
                Snackbar.make(view, resources.getString(R.string.FullRoom), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            else {
                val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(0)
                if (paymentDataRequestJson == null) {
                    Log.e("RequestPayment", "Can't fetch payment data request")
                    return
                }
                val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

                // Since loadPaymentData may show the UI asking the user to select a payment method, we use
                // AutoResolveHelper to wait for the user interacting with it. Once completed,
                // onActivityResult will be called with the result.
                if (request != null) {
                    AutoResolveHelper.resolveTask(
                            paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE)
                }
                //Starting the intent activity for result
                //the request code will be used on the method onActivityResult

                postEvent(extras?.getString("eventID"), numTickets.toString())
            }
        }
    }

    fun postEvent(eventID: String?, numTickets: String) {

        val url = "https://securevent.herokuapp.com/reservations?n=$numTickets"

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