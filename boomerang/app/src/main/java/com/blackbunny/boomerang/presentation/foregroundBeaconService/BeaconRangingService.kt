package com.blackbunny.boomerang.presentation.foregroundBeaconService

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.blackbunny.boomerang.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.altbeacon.beacon.*

/**
 * BeaconRangingService
 * Foreground Service that continuously scan and detects nearby beacons.
 * Considered as a Data Layer; Data Source, since it produces raw data from external source.
 */

// Dependency Injection using Hilt. (test)
@AndroidEntryPoint
class BeaconRangingService(
    private val beaconRangingCallback: BeaconServiceCallback
) : Service() {
    private val TAG = "BeaconRangingService"

    // Companion Objects
    companion object {
        val ACTION_APPROACH = "ACTION_APPROACH"
        val ACTION_ENTERED = "ACTION_ENTERED"
        val ACTION_SCANNING = "ACTION_SCANNING"
    }

    // Flow for returning result
    lateinit var resultFlow: Flow<Beacon>

    // Variables for Local Notification.
    private val mNotificationManager: NotificationManager by lazy {
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val channelID = "Beacon_Service_Channel"
    private val ONGOING_NOTIFICATION_ID = 1000
    private val RESULT_NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Beacon Service Started")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand Function Called")

        // Retrieve instance of BeaconServiceCallback from provided intent. (via Binder)
//        val localBinder = onBind(intent) as LocalBinder
//        val callback = localBinder.getServiceCallback()
//
//        if (callback != null) {
//            beaconRangingCallback = callback
//        } else {
//            Log.d(TAG, "Required ServiceCallback instance not found.\nService will be terminated.")
//            onDestroy()
//        }

        // For test and later user.
        beaconRangingCallback?.onBeaconRangingStarted()

        // Send out local notification for alerting the start of the service on the background.
        val initialNotification = sendNotification("Beacon Detection will be started.")
        startForeground(ONGOING_NOTIFICATION_ID, initialNotification)

        // Beacon Handling
        val mBeaconManager: BeaconManager = BeaconManager.getInstanceForApplication(this)
        // Add Beacon Parser (iBeacon)
        mBeaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(
            "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
        ))

        // Region Notifier. ; Could be located on the Data Layer (Repository)
        val mRangeNotifier = object : RangeNotifier {
            override fun didRangeBeaconsInRegion(
                beacons: MutableCollection<Beacon>?,
                region: Region?
            ) {
                if (beacons != null) {
                    addBeaconResultToFlow(beacons)

                    // For debug and later use.
                    beaconRangingCallback?.onBeaconResultReceived()
                }
            }
        }

        // Start Ranging
        mBeaconManager.addRangeNotifier(mRangeNotifier)
        mBeaconManager.backgroundBetweenScanPeriod = 0
        mBeaconManager.backgroundScanPeriod = 1500L

        mBeaconManager.setEnableScheduledScanJobs(false)
        mBeaconManager.startRangingBeacons(
            Region("wildcardRegion", null, null, null)
        )

        return START_STICKY
    }

    inner class LocalBinder(): Binder() {
        fun getServiceCallback(): BeaconServiceCallback? {
            return beaconRangingCallback
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return LocalBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")

//        // For debug and later use.
//        beaconRangingCallback.onBeaconRangingStopped()

    }

    private fun addBeaconResultToFlow(result: MutableCollection<Beacon>) {
        resultFlow = flow {
            for (beacon in result) {
                emit(beacon)
            }
        }
    }

    // Notification Handler
    private fun sendNotification(content: String): Notification {
        // Use PendingIntent (executed when notification is clicked.)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Create Notification Channel Group
        mNotificationManager.createNotificationChannelGroup(
            NotificationChannelGroup("Beacon_Service_Channel_Group", "Notification_Group")
        )

        // Notification Channel Instance.
        val channel = NotificationChannel(
            channelID, "Beacon_Service_Notification", NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableLights(false)
        channel.setSound(null, null)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        mNotificationManager.createNotificationChannel(channel)

        return Notification.Builder(this, channelID)
            .setContentTitle("Beacon Service Test")
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setTicker("Ticker Test")
            .build()
    }

}