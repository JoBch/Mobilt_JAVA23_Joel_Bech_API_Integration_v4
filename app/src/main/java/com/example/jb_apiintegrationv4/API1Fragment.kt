package com.example.jb_apiintegrationv4

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class API1Fragment : Fragment() {

    private lateinit var resultTV: TextView
    private lateinit var searchET: EditText
    private lateinit var submitBtn: Button

    private var savedInput: String? = null
    private var savedResult: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_a_p_i1, container, false)
        //Restore saved state when rotating screen if it exists
        savedInstanceState?.let {
            savedInput = it.getString("input")
            savedResult = it.getString("result")
        }

        submitBtn = view.findViewById(R.id.submitBtn)
        searchET = view.findViewById(R.id.searchEditText)
        submitBtn.setOnClickListener {
            if (searchET.text.isNotEmpty()) {
                fetchWordDetails(searchET.text.toString())
                searchET.setText("")
            } else Toast.makeText(requireContext(), "Please enter a word!", Toast.LENGTH_SHORT)
                .show()
        }

        resultTV = view.findViewById(R.id.resultTextView)
        resultTV.movementMethod = ScrollingMovementMethod()
        return view
    }

    private fun fetchWordDetails(word: String) {
        val url = "https://wordsapiv1.p.rapidapi.com/words/$word/definitions"
        val queue: RequestQueue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            { response -> displayWordDetails(response) },
            { error -> resultTV.text = "Error: ${error.message}" }
        ) {
            override fun getHeaders(): MutableMap<String, String> = mutableMapOf(
                "X-RapidAPI-Key" to "616c058a97msh574e89f5c3480dcp16ccdejsn74e4fc7a50f7",
                "X-RapidAPI-Host" to "wordsapiv1.p.rapidapi.com"
            )
        }
        queue.add(jsonObjectRequest)
    }

    //Setting a savedstate when changing fragment view
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("input", searchET.text.toString())
        outState.putString("result", resultTV.text.toString())
    }

    private fun displayWordDetails(response: JSONObject) {
        val word = response.optString("word", "No word")
        val definitionsArray = response.optJSONArray("definitions")

        //Checks if there are definitions
        if (definitionsArray != null && definitionsArray.length() > 0) {
            val definitionsList = mutableListOf<String>()

            //Loops through the definitions and populates JSONObject with them
            for (i in 0 until definitionsArray.length()) {
                val definitionObj = definitionsArray.optJSONObject(i)
                val definition = definitionObj?.optString("definition", "No definition")
                definitionsList.add("$definition")
            }

            //Join the list of definitions into a string with line breaks
            val definitionsText = definitionsList.joinToString(".\n\n")
            resultTV.text = "Word: $word\nDefinitions:\n$definitionsText"

        } else {
            resultTV.text = "No definitions found for the word: $word"
        }
    }

}