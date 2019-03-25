package dk.trifork.smartspy


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing Firebase Authentication
        this.auth = FirebaseAuth.getInstance()

        // Initializing Firebase Cloud Firestore
        // The settings are to add timestamps in the snapshots
        // see https://developers.google.com/android/reference/com/google/firebase/firestore/FirebaseFirestoreSettings.Builder
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        this.firestore = FirebaseFirestore.getInstance()
        this.firestore.firestoreSettings = settings

    }

    /**
     * Listening for changes on this things node (uid from login, which is hardcoded and thus scaling like s...).
     * If the thing does not exist in the data base, it will create it self.
     * If the thing exists, but is unclaimed it will present the activation key on the screen.s
     * If the thing exists and it is claimed, the application will proceed to the next activity
     */
    override fun onResume() {
        super.onResume()
        // Checking if we have a logged in user
        val uid = this.auth.currentUser?.uid

        // The user is logged in (the first time you use the app or if the app data was reset)
        if (uid != null) {
            Timber.d("Already logged in...${uid}")

            // Creating a listener for cloud firestore
            val ref = this.firestore.collection("Things").document(uid)
            ref.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
                // Our friend, Gson. Googles Serialization / Deserialization tool for java (and kotlin)
                // Be sure to check out the file "ThingObject.kt" where we use annotation for this to work
                val gson = Gson()

                // If there was an error, log it as warning and return
                if (e != null) {
                    Timber.w("Could not setup listener")
                    return@EventListener
                }

                // Does the thing exist in the database?
                if (snapshot != null && snapshot.exists()) {

                    // Make the data a bit more edible
                    val data = gson.toJson(snapshot.data)
                    Timber.d("Data: ${data}")

                    // Serialize to a ThingObject
                    val t = gson.fromJson(data, ThingObject::class.java)

                    // Check if the thing is claimed
                    if (!t.isclaimed) {

                        // Thing was not claimed
                        main_keytext.text = t.activationkey.toString()
                    } else {

                        // Thing was claimed! Lets proceed
                        // Create an intent to open the SpyModeActivity activity and put the data as payload
                        val spymodeIntent = Intent(this, SpyModeActivity::class.java).apply {
                            putExtra("thing", data)
                        }
                        // We give this transaction a code so that we can keep track of the flow
                        startActivityForResult(spymodeIntent, SmartSpyApp.REQ_SPYMODE)

                    }
                // The thing did not exist in the data base
                } else {
                    Timber.d("Data: null")
                    // Lets create it
                    val t = ThingObject(uid, _genKey(), false, null)
                    ref.set(t)
                }
            })
        // The user is not logged in
        } else {
            Timber.d("Logging in...")
            // This method signs in an existing user
            this.auth.signInWithEmailAndPassword("dev1@trifork.com", "dev1234")
        }
    }

    /**
     * We override this method to catch callbacks when an activity has a result. We can then check the request code and act
     * react based on that
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If the activity was started by us (the request code tells us that) and it was closed with an accepable result
        // We can finish this activity - if we dont, we will loop back to the spymode activity
        if (requestCode == SmartSpyApp.REQ_SPYMODE && resultCode == RESULT_OK) {
            finish()
        }
    }

    /**
     * Creates a random 4 digit number
     */
    private fun _genKey(): Number {
        return ((Math.random()  * 9000.0) + 1000).toInt()
    }
}
