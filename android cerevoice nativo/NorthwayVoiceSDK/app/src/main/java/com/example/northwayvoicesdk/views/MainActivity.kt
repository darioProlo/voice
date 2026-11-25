package com.example.northwayvoicesdk.views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cereproc.cerevoice_eng.CPRCEN_AUDIO_FORMAT
import com.cereproc.cerevoice_eng.CPRC_VOICE_LOAD_TYPE
import com.cereproc.cerevoice_eng.SWIGTYPE_p_CPRCEN_engine
import com.cereproc.cerevoice_eng.cerevoice_eng
import com.example.northwayvoicesdk.R
import com.example.northwayvoicesdk.databinding.ActivityMainBinding
import com.example.northwayvoicesdk.domain.SomethingUseCase
import com.example.northwayvoicesdk.utils.DemoCallback
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.UnsupportedEncodingException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var useCase: SomethingUseCase

    private lateinit var binding: ActivityMainBinding
    private var eng: SWIGTYPE_p_CPRCEN_engine? = null
    private var chan_handle = 0
    private var audioTrack: AudioTrack? = null
    private var channelHandle = 0

    private var notVoice = ""
    private var notLicense = ""
    private var notRootCertificate = ""
    private var notClientCertificate = ""
    private var notClientKey = ""
    private var message: String = "It's ok!!"

    private val license = "99C93CDC-5505-479D-A5ED-E08AFD8C7542_CJI_license.lic"
    private val root = "root_certificate.pem"
    private val cert = "99C93CDC-5505-479D-A5ED-E08AFD8C7542_CJI_client.crt"
    private val key = "99C93CDC-5505-479D-A5ED-E08AFD8C7542_CJI_client.key"
    private val voice = "cerevoice-adam-16k-cerewave.voice"



    companion object {
        var TAG = "CEREVOICE_LOG"
        var REQUEST_PERMISSION = 1

        init {
            System.loadLibrary("cerevoice_eng")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Setup the AudioTrack interface for playing
        val minSize = AudioTrack.getMinBufferSize(
            16000,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            16000,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minSize,
            AudioTrack.MODE_STREAM
        )

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1
                )
            }
        } else {
            startingVoiceSdk("")
        }

    }

    private fun startingVoiceSdk(textToReproduce: String) {

        val srate = 0

        val callback: DemoCallback?
        val audioTrack: AudioTrack?
        val minSize = AudioTrack.getMinBufferSize(
            16000,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            16000,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minSize,
            AudioTrack.MODE_STREAM
        )


        // Instance the synthesis engine
        eng = cerevoice_eng.CPRCEN_engine_new()

        // check the voice was loaded
        if (loadVoice().isNotEmpty()) {
            message
        } else {
            // before synthesis can take place a channel must be opened
            channelHandle = cerevoice_eng.CPRCEN_engine_open_default_channel(eng)
            // check the channel was opened
            if (channelHandle == 0) {
                message = getString(R.string.err_chan)
            } else {
                // set the callback on the engine
                callback = DemoCallback(audioTrack)
                callback.SetCallback(eng, channelHandle)

                // Ready for synthesis
                val sr =
                    cerevoice_eng.CPRCEN_channel_get_voice_info(eng, channelHandle, "SAMPLE_RATE")
                        .toInt()

                //toIntSome information that may be useful
                Log.i(TAG, "sample rate: $sr")
                Log.i(
                    TAG, "opened channel: $channelHandle"
                )

                // Make sure the audioTrack has the correct sample rate
                if (srate != sr) {
                    audioTrack.stop()
                    audioTrack.flush()
                    audioTrack.playbackRate = sr
                    audioTrack.play()
                }

                synthText(textToReproduce)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {

            startingVoiceSdk(useCase.invoke())

        }
    }

    /** Note: onDestroy can flush the buffer and close the channel but must delete the engine  */
    override fun onDestroy() {
        super.onDestroy()

        // Always delete the engine when finished running
        if (eng != null) {
            cerevoice_eng.CPRCEN_engine_delete(eng)
        }
    }

    /** Load voice file into the engine  */
    fun loadVoice(): String {

        var context = this.applicationContext

        getFileFromAssets(context!!, voice)
        getFileFromAssets(context!!, license)
        getFileFromAssets(context!!, root)
        getFileFromAssets(context!!, cert)
        getFileFromAssets(context!!, key)

        val vFile = getFileFromAssets(context!!, voice)
        val lFile = getFileFromAssets(context!!, license)
        val rFile = getFileFromAssets(context!!, root)
        val cFile = getFileFromAssets(context!!, cert)
        val kFile = getFileFromAssets(context!!, key)

        //Check that the voice and license files exist
        if (!vFile.isFile) notVoice = context!!.getString(R.string.err_no_voice)
        if (!lFile.isFile) notLicense = context!!.getString(R.string.err_no_license)
        if (!rFile.isFile) notRootCertificate = context!!.getString(R.string.err_no_root_ca_certificate)
        if (!cFile.isFile) notClientCertificate = context!!.getString(R.string.err_no_client_certificate)
        if (!kFile.isFile) notClientKey = context!!.getString(R.string.err_no_client_key)
        if (!vFile.isFile
            || !lFile.isFile || !rFile.isFile || !cFile.isFile || !kFile.isFile
        ) {
            return message.apply {
                message =
                    notVoice + notLicense + notRootCertificate + notClientCertificate + notClientKey
            }
        }
        // Check they can be read

        if (!vFile.canRead()) notVoice = context!!.getString(R.string.err_read_voice)
        if (!lFile.canRead()) notLicense = context!!.getString(R.string.err_read_license)
        if (!rFile.canRead()) notRootCertificate = context!!.getString(R.string.err_read_root_ca_certificate)
        if (!cFile.canRead()) notClientCertificate = context!!.getString(R.string.err_read_client_certificate)
        if (!kFile.canRead()) notClientKey = context!!.getString(R.string.err_read_client_key)

        if (
            !vFile.canRead()
            || !lFile.canRead()
            || !rFile.canRead()
            || !cFile.canRead()
            || !kFile.canRead()
        ) {
            return message.apply {
                message =
                    notVoice + notLicense + notRootCertificate + notClientCertificate + notClientKey
            }
        }
        //  If there is a problem it might be useful to check these are right
        Log.d(TAG, "VOICE Size: " + vFile.length())
        Log.d(TAG, "LICENSE Size: " + lFile.length())
        Log.d(TAG, "ROOT CA CERTIFICATE Size: " + rFile.length())
        Log.d(TAG, "CLIENT CERTIFICATE Size: " + cFile.length())
        Log.d(TAG, "CLIENT KEY Size: " + kFile.length())

        // Load the voice file
        val voiceIndex = cerevoice_eng.CPRCEN_engine_load_voice(
            eng,
            vFile.absolutePath,
            "",
            CPRC_VOICE_LOAD_TYPE.CPRC_VOICE_LOAD_EMB,
            lFile.absolutePath,
            rFile.absolutePath,
            cFile.absolutePath,
            kFile.absolutePath // Load as voice for an embedded device
        )

        // valid voice indices start at 1
        val result: String = if (voiceIndex == 0) {
            voiceIndex.toString();
        } else {
            ""
        }

        return result
    }

    /** Actual synthesis function  */
    fun synthText(txtIn: String): Boolean {
        // The synthesis process can be done in a separate thread

        // The flush value should be set to 1 / 0 and not true / false because of the underlining C code
        // playing of the audio is handled in the callback
        val utf8bytes: ByteArray = try {
            txtIn.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            message = "UTF-8 conversion failed" + e.message
            return false
        }

        val fileFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)

//        val wavOut = File(fileFolder, "1" + ".wav")
//
//
//        cerevoice_eng.CPRCEN_engine_channel_to_file(eng, channelHandle, wavOut.absolutePath,
//            CPRCEN_AUDIO_FORMAT.CPRCEN_RIFF
//        )
        cerevoice_eng.CPRCEN_engine_channel_speak(eng, channelHandle, txtIn, utf8bytes.size, 1)
        return true
    }

    /** Simple function for updating the message box  */
    fun updateMessage(msg: String) {
        val txtOut = binding.userMessage
        val newTxt = """
             $msg
             ${txtOut.text}
             """.trimIndent()
        txtOut.text = newTxt
    }

    /** Called when the user clicks the synthesise button  */
    fun onSynth(view: View?) {

        var text = useCase.invoke()
        val editText = binding.userInput
        val txtIn = text //editText.text.toString()
        updateMessage(getString(R.string.inf_synth) + " " + txtIn)
        // check that synthesis was performed correctly or report an error
        if (!synthText(txtIn)) {
            Log.w(TAG, "Synthesis failed")
            updateMessage(getString(R.string.err_synth) + " " + txtIn)
        }
    }

    fun getFileFromAssets(context: Context, fileName: String): File = File(context.cacheDir, fileName)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(fileName).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }


}