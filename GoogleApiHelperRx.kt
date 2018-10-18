package de.fintasys.jobcantouch.helper

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import io.reactivex.Observable
import io.reactivex.ObservableEmitter


class GoogleApiHelperRx {

    companion object {
        private val TAG = this::class.java.simpleName
    }

    fun build(context: Context, callbackRx: GoogleApiClientCallbackRx): Observable<GoogleApiClient> {
        return Observable.create<GoogleApiClient> { emitter: ObservableEmitter<GoogleApiClient> ->
            val builder = GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)

            val client = builder.build()

            val connCallback = object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {
                    Log.v(TAG, "Connected")
                    callbackRx.onConnected(client, emitter)
                }

                override fun onConnectionSuspended(status: Int) {
                    Log.e(TAG, "Connection suspended")
                    emitter.onError(RuntimeException("Connection failed"))
                }
            }

            val onConnectionFailedListener = GoogleApiClient.OnConnectionFailedListener { result ->
                Log.e(TAG, "Connection failed")
                emitter.onError(RuntimeException("Connection failed " + result.errorMessage))
            }

            client.registerConnectionCallbacks(connCallback)
            client.registerConnectionFailedListener(onConnectionFailedListener)
            client.connect()
        }
    }

    interface GoogleApiClientCallbackRx {
        fun onConnected(client: GoogleApiClient, emitter: ObservableEmitter<GoogleApiClient>)
    }
}