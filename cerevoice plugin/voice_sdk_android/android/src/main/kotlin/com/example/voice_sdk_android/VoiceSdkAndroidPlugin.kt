package com.example.voice_sdk_android

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import androidx.annotation.NonNull
import com.cereproc.cerevoice_eng.CPRC_VOICE_LOAD_TYPE
import com.cereproc.cerevoice_eng.SWIGTYPE_p_CPRCEN_engine
import com.cereproc.cerevoice_eng.cerevoice_eng
import com.example.platform_channel.utils.DemoCallback

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.io.UnsupportedEncodingException

/** VoiceSdkAndroidPlugin */
class VoiceSdkAndroidPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  private var context: Context? = null
  private var channel: MethodChannel? = null

  private var eng: SWIGTYPE_p_CPRCEN_engine? = null
  private var channelHandle = 0
  private val MESSAGE_CHANNEL = "platform_channel/sendTextToAudioRepro"
  private var message: String = "It's ok!!"
  private var notVoice = ""
  private var notLicense = ""
  private var notRootCertificate = ""
  private var notClientCertificate = ""
  private var notClientKey = ""

  private val license = "99C93CDC-5505-479D-A5ED-E08AFD8C7542_CJI_license.lic"
  private val root = "root_certificate.pem"
  private val cert = "99C93CDC-5505-479D-A5ED-E08AFD8C7542_CJI_client.crt"
  private val key = "99C93CDC-5505-479D-A5ED-E08AFD8C7542_CJI_client.key"
  private val voice = "cerevoice-adam-16k-cerewave.voice"

  companion object {
    var TAG = "CEREVOICE_LOG"

    init {
      System.loadLibrary("cerevoice_eng")
    }
  }

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  //private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "voice_sdk_android")
    channel?.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "messageSent") {
      val textToRepro = call.arguments.toString()
      startingVoiceSdk(textToRepro)
      result.success(message)
    } else {
      result.notImplemented()
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
        message = context!!.getString(R.string.err_chan)
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

  /** Load voice file into the engine  */
  private fun loadVoice(): String {

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
  private fun synthText(txtIn: String): Boolean {
    // The synthesis process can be done in a separate thread

    // The flush value should be set to 1 / 0 and not true / false because of the underlining C code
    // playing of the audio is handled in the callback
    val utf8bytes: ByteArray = try {
      txtIn.toByteArray(charset("UTF-8"))
    } catch (e: UnsupportedEncodingException) {
      message = "UTF-8 conversion failed" + e.message
      return false
    }
    cerevoice_eng.CPRCEN_engine_channel_speak(eng, channelHandle, txtIn, utf8bytes.size, 1)
    return true
  }

  /** Note: onDestroy can flush the buffer and close the channel but must delete the engine  */
  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    // Always delete the engine when finished running
    if (eng != null) {
      cerevoice_eng.CPRCEN_engine_delete(eng)
    }
    channel?.setMethodCallHandler(null)
    channel = null
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
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
