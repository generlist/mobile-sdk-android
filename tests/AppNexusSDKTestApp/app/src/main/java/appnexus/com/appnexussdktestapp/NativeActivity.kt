package appnexus.com.appnexussdktestapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.squareup.picasso.Picasso

class NativeActivity : AppCompatActivity(), NativeAdRequestListener {

    lateinit var nativeAdRequest: NativeAdRequest
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Native Load Count", true)

    override fun onAdLoaded(nativeAdResponse: NativeAdResponse?) {
        Toast.makeText(this, "Native Ad Loaded", Toast.LENGTH_LONG).show()
        handleNativeResponse(nativeAdResponse)
    }

    override fun onAdFailed(errorcode: ResultCode?) {
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_native)
//        triggerAdLoad(
//            intent.getStringExtra("placement"),
//            useHttps = intent.getBooleanExtra("useHttps", true)
//        )
    }

    fun triggerAdLoad(placement: String?, useHttps: Boolean = true, creativeId: Int? = null) {
        Handler(Looper.getMainLooper()).post {

            nativeAdRequest = NativeAdRequest(this, if (placement == null) "13255429" else placement)
            nativeAdRequest.placementID = if (placement == null) "13255429" else placement
            SDKSettings.useHttps(useHttps)
            nativeAdRequest.listener = this
            if(creativeId != null) {
                val utils = Utils()
                utils.setForceCreativeId(creativeId, nativeAdRequest = nativeAdRequest);
            }
            nativeAdRequest.loadAd()
            idlingResource.increment()
        }
    }

    private fun handleNativeResponse(response: NativeAdResponse?) {

        val icon: ImageView = findViewById(R.id.icon)
        val image: ImageView = findViewById(R.id.image)
        val title: TextView = findViewById(R.id.title)
        val desc: TextView = findViewById(R.id.description)

        if (response?.iconUrl != null) Picasso.get().load(response.iconUrl).resize(40, 40).into(icon)
        if (response?.imageUrl != null) Picasso.get().load(response.imageUrl).into(image)
        title.setText(response?.title)
        desc.setText(response?.description)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()


    }
}
