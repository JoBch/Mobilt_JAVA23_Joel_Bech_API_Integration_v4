package com.example.jb_apiintegrationv4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class API2Fragment : Fragment() {

    private lateinit var inputET: EditText
    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner
    private lateinit var resultTV: TextView
    private lateinit var convertButton: Button

    private val API_KEY = "fxr_live_d5f1077e50fd90488194d72dd6b82e52bc98"

    //Currencies to choose from considering they exists in the API
    private val fromCurrencies = arrayOf("From Currency", "SEK", "USD", "EUR", "GBP", "RUB", "INR", "AUD")
    private val toCurrencies = arrayOf("To Currency", "SEK", "USD", "EUR", "GBP", "RUB", "INR", "AUD")

    private var savedInput: String? = null
    private var savedResult: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Restore saved state when rotating screen if it exists
        savedInstanceState?.let {
            savedInput = it.getString("input")
            savedResult = it.getString("result")
        }

        return inflater.inflate(R.layout.fragment_a_p_i2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputET = view.findViewById(R.id.inputEditText)
        fromCurrencySpinner = view.findViewById(R.id.fromCurrencySpinner)
        toCurrencySpinner = view.findViewById(R.id.toCurrencySpinner)
        resultTV = view.findViewById(R.id.resultTextView)
        convertButton = view.findViewById(R.id.convertButton)

        //Set up the "From Currency" spinner
        val fromCurrencyAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fromCurrencies)
        fromCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromCurrencySpinner.adapter = fromCurrencyAdapter

        //Set up the "To Currency" spinner
        val toCurrencyAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, toCurrencies)
        toCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toCurrencySpinner.adapter = toCurrencyAdapter

        //Making some checks to see all the fields are correctly filled in, if they are call fetchExchangeRate()
        convertButton.setOnClickListener {
            val amount = inputET.text.toString().toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT)
                    .show()
            }

            val fromCurrency = fromCurrencySpinner.selectedItem.toString()
            val toCurrency = toCurrencySpinner.selectedItem.toString()

            if (fromCurrency == "From Currency" || toCurrency == "To Currency") {
                Toast.makeText(
                    requireContext(),
                    "Please select both currencies",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (amount == null) {
                Toast.makeText(
                    requireContext(),
                    "Please enter an amount to exhange",
                    Toast.LENGTH_SHORT
                ).show()
            } else fetchExchangeRate(fromCurrency, toCurrency, amount)
        }
    }

    //Setting a savedstate when changing fragment view
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("input", inputET.text.toString())
        outState.putString("result", resultTV.text.toString())
    }

    //Fetching and calculating the exchange rate
    private fun fetchExchangeRate(fromCurrency: String, toCurrency: String, amount: Double) {
        val url = "https://api.exchangerate-api.com/v4/latest/$fromCurrency?api_key=$API_KEY"

        val queue: RequestQueue = Volley.newRequestQueue(requireContext())

        //API request to fetch exchange rate
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val rates: JSONObject = response.getJSONObject("rates")
                    val exchangeRate = rates.getDouble(toCurrency)
                    val convertedAmount = amount * exchangeRate
                    resultTV.text = "$amount $fromCurrency = $convertedAmount $toCurrency"
                } catch (e: Exception) {
                    resultTV.text = "Error fetching exchange rate"
                }
            },
            { error ->
                resultTV.text = "Request failed: ${error.message}"
            })

        queue.add(request)
    }
}
