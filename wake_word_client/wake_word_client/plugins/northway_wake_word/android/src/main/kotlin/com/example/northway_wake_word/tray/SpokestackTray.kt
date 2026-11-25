@file:JvmName("SpokestackTray")

package io.spokestack.tray

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.northway_wake_word.R
import com.example.northway_wake_word.databinding.TrayFragmentBinding
import com.example.northway_wake_word.tray.*
import io.spokestack.spokestack.SpeechContext
import io.spokestack.spokestack.Spokestack
import io.spokestack.spokestack.SpokestackAdapter
import io.spokestack.spokestack.SpokestackModule
import io.spokestack.spokestack.dialogue.DialogueEvent
import io.spokestack.spokestack.nlu.NLUResult
import io.spokestack.spokestack.tts.SynthesisRequest
import io.spokestack.spokestack.tts.TTSEvent
import io.spokestack.spokestack.util.EventTracer
import com.example.northway_wake_word.tray.message.Message
import com.example.northway_wake_word.tray.message.MessageAdapter

/**
 * A Fragment that exposes the primary functionality of the Spokestack tray.
 *
 * The tray wraps an instance of the
 * [`Spokestack`](https://github.com/spokestack/spokestack-android) library that is used to
 * capture user voice input, convert it to text, display that text, and classify it using an NLU.
 * An application can register a component to listen to tray events, return responses to NLU
 * classifications, and have those responses automatically synthesized via Spokestack TTS to be
 * read to the user.
 *
 * To include the tray in your `Activity`'s `layout.xml`:
 *
 *
 * ```xml
 * <!-- nested in the main layout, after other views/sublayouts -->
 *
 *     <include
 *          android:id="@+id/tray_fragment"
 *          layout="@layout/spokestack_tray_fragment"
 *      />
 * ```
 *
 * Then in the `Activity` itself:
 *
 * ```kotlin
 * import io.spokestack.tray.*
 *
 * class MyActivity : AppCompatActivity(), SpokestackTrayListener {
 *
 *     lateinit var tray: SpokestackTray
 *
 *     // ...
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *
 *         val config = TrayConfig.Builder()
 *             // credentials from your Spokestack account
 *             .credentials("spokestack-client-id", "spokestack-secret-key")
 *             .wakewordModelURL("https://path-to-wakeword-models")
 *             .nluURL("https://path-to-nlu-files")
 *             .withListener(this)
 *             // optional builder customization; see the documentation for more details...
 *             .build()
 *         supportFragmentManager.fragmentFactory = SpokestackTrayFactory(config)
 *
 *         // note that the factory is instantiated and set on the manager BEFORE calling
 *         // `super.onCreate()`
 *         super.onCreate(savedInstanceState)
 *     }
 *
 *     override fun onStart() {
 *         // set the value of the lateinit `tray` var
 *         tray = SpokestackTray.getInstance(config)
 *         super.onStart()
 *     }
 * ```
 *
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class SpokestackTray constructor(
    config: TrayConfig,
)  {
    private lateinit var spokestack: Spokestack

    var listener: SpokestackTrayListener? = config.listener

    /**
     * @return The Spokestack instance in use by the tray.
     */
    fun spokestack(): Spokestack {
        return spokestack
    }

    /**
     * Explicitly start the underlying [Spokestack] instance and its speech pipeline.
     *
     * This method should not be required under normal usage, but is necessary after an
     * explicit call to [stop] or [close] to re-enable voice control without destroying the tray
     * fragment.
     */
    fun start() {
        spokestack.start()
    }

    /**
     * Release resources associated with the Tray and the underlying Spokestack instance.
     * Note that `spokestack.close()`/`.stop()` are not called automatically because in the normal
     * case, we want Spokestack to continue listening even if the fragment is killed (i.e., on
     * device rotation).
     *
     * A convenience method that renames [close] and provides parallelism for [start].
     */
    fun stop() {
//        close()
    }

    /**
     * Release resources associated with the Tray and the underlying Spokestack instance.
     * Note that `spokestack.close()`/`.stop()` are not called automatically because in the normal
     * case, we want Spokestack to continue listening even if the fragment is killed (i.e., on
     * device rotation).
     *
     * **Note**: This method is **not** for visually sliding the tray's UI closed;
     * use [setOpen] for that. This method's name is inherited from
     * [AutoCloseable].
     */
//    override fun close() {
//        spokestack.close()
//    }

    /**
     * Clear the tray's internal conversation history, making the next time it opens act like
     * the first without resetting any user modifications like muting TTS or changing the tray
     * size.
     */

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        lifecycleScope.launchWhenCreated {
//            spokestackListener = SpokestackListener()
//            spokestack =
//                SpokestackFactory.getConfigured(
//                    config,
//                    requireContext(),
//                    spokestackListener
//                )
//            if (checkMicPermission()) {
//                spokestack.start()
//            }
//            setReady()
//        }
//    }






}
