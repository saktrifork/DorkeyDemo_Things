package dk.trifork.smartspy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_spy_mode.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SpyModeActivity : Activity() {

    // Get a handle of firestore
    private lateinit var firestore: FirebaseFirestore
    private lateinit var thing: ThingObject
    private lateinit var filePath: String

    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spy_mode)

        this.thing = gson.fromJson(intent.extras["thing"].toString(), ThingObject::class.java)
        this.firestore = FirebaseFirestore.getInstance()
    }

    override fun onResume() {
        super.onResume()

        spymode_owner.text = this.thing.claimedby

        spymode_capture.setOnClickListener {
            captureImage()
        }

        spymode_buzzin.setOnClickListener {
            buzzin()
        }

        spymode_deny.setOnClickListener {
            denyaccess()
        }
    }

    fun captureImage() {
        Timber.d("Capturing image...")

    }

    fun buzzin() {
        Timber.d("Buzzing in...")
    }

    fun denyaccess() {
        Timber.d("Denying access...")
    }

}
