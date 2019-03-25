package dk.trifork.smartspy

import android.app.Application
import com.google.firebase.FirebaseApp
import timber.log.Timber

class SmartSpyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Timber.plant(Timber.DebugTree())
        Timber.i("Initializing app...")
    }

    companion object {
        val REQ_SPYMODE = 1
        val REQ_CAPTURE = 2
    }

}