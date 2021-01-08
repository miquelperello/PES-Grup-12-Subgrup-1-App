package com.pes.securevent

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_eventdetails.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONException
import org.json.JSONObject


class EventDetails : AppCompatActivity() {

    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 123

    private lateinit var paymentsClient: PaymentsClient
    private var fullRoom: Boolean = true
    private var avail: Int = 0
    lateinit var MyEvents: MyEvents


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventdetails)

        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val extras = intent.extras

        val roomName = extras?.getString("roomName")
        val user_id = extras?.getString("user_id")
        val event_id = extras?.getString("eventID")
        val cadires = extras?.getString("matrix")


        actionBar.title = roomName


        var prematrix: List<String>? = null

        if (cadires != null) {
            prematrix = cadires.split('\n')
        }

        val rows = prematrix!!.size
        val columns = prematrix[0].split('\t').count()


        val sala = ArrayList<ArrayList<String>>()

        for (i in 0 until rows) {
            sala.add(prematrix[i].split('\t') as ArrayList<String>)
        }


        val grid = findViewById<View>(R.id.grid) as GridView
        grid.numColumns = columns

        val salaList: MutableList<Seat> = ArrayList()

        for (i in 0 until rows) {
            for (j in 0 until columns) {

                if (sala[i][j] == "T") {
                    salaList.add(Seat(i, j, 'T'))
                    ++avail
                    fullRoom = false
                } else if (sala[i][j] == "F")
                    salaList.add(Seat(i, j, 'C'))
                else
                    if (sala[i][j] == user_id)
                        salaList.add(Seat(i, j, 'U'))
                    else
                        salaList.add(Seat(i, j, 'F'))
            }
        }

        val adapter = SeatAdapter(applicationContext, salaList)

        grid.adapter = adapter

        val url = "https://securevent.herokuapp.com/reservations/" + event_id + "_" + user_id

        val requestQueue: RequestQueue? = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, url, null, {
            renderSeeSeats()
        }, { renderSeeDisponibility() })

        requestQueue?.add(request)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun renderSeeDisponibility(){
        paymentsClient = PaymentsUtil.createPaymentsClient(this)
        possiblyShowGooglePayButton()

        val editText = findViewById<EditText>(R.id.numTickets)
        editText!!.showSoftInputOnFocus = false
        val buyButton = findViewById<Button>(R.id.googlePayButton)
        buyButton.isEnabled = false

        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                buyButton.isEnabled = s.toString().toInt() != 0
                if (editText.text.toString() == "")
                    editText.setText("0")
            }
        })
        findViewById<GridLayout>(R.id.legend).visibility = View.GONE
    }

    private fun renderSeeSeats(){
        findViewById<Button>(R.id.googlePayButton).visibility = View.GONE
        findViewById<GridLayout>(R.id.numSeatsWrapper).visibility = View.GONE
    }

    private fun possiblyShowGooglePayButton() {
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
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
                    Toast.LENGTH_LONG).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val extras = intent.extras
        when (requestCode) {
            // Value passed in AutoResolveHelper
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        val editText = findViewById<EditText>(R.id.numTickets)
                        val numTickets = editText.text.toString().toInt()
                        postEvent(extras?.getString("eventID"), numTickets.toString())
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }!!

                        startActivity(Intent(this, MainActivity::class.java))

                        Toast.makeText(
                            this,
                            resources.getString(R.string.MessageInscripcioEvent),
                            Toast.LENGTH_LONG).show()
                    }

                    RESULT_CANCELED -> {
                        // The user cancelled the payment attempt
                        println("User cancelled payment attempt")

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
            Log.e("handlePaymentSuccess", "Error: $e")
        }

    }
    private fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }


    fun increment(view: View) {
        val editText = findViewById<EditText>(R.id.numTickets)
        val numTickets = editText.text.toString().toInt()
        if (numTickets < 4) {
            editText.setText((numTickets + 1).toString())
        }

    }

    fun decrement(view: View) {
        val editText = findViewById<EditText>(R.id.numTickets)
        val numTickets = editText.text.toString().toInt()

        if (numTickets > 0)
            editText.setText((numTickets - 1).toString())

    }

    fun buy(view: View) {

        val editText = findViewById<EditText>(R.id.numTickets)
        val numTickets = editText.text.toString().toInt()


        if (numTickets > 0) {
            if (fullRoom || avail < numTickets) {
                Snackbar.make(view, resources.getString(R.string.FullRoom), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
            else {
                val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(0)
                if (paymentDataRequestJson == null) {
                    Log.e("RequestPayment", "Can't fetch payment data request")
                    return
                }
                val paymentDataRequest = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

                paymentsClient.loadPaymentData(paymentDataRequest)?.let { task ->
                    AutoResolveHelper.resolveTask(task, this, LOAD_PAYMENT_DATA_REQUEST_CODE)
                }
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
                headers["Authorization"] = "Token $tokenMongoPost"
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

