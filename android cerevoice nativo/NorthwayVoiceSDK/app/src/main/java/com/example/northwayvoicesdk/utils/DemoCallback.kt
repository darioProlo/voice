package com.example.northwayvoicesdk.utils

import android.media.AudioTrack
import android.util.Log
import com.cereproc.cerevoice_eng.*

class DemoCallback(a: AudioTrack?) : TtsEngineCallback() {

    val TAG = "DemoCallback" // Tag for logger

    var audioTrack: AudioTrack? = a

    /** The Callback function is fired periodically throughout the synthesis.
     *
     * In CereVoice, a callback can be specified to receive data every
     * time a phrase has been synthesised. The user application can
     * process that data safely, as the engine does not continue until the
     * user returns from the callback.  Ideally, the processing done in
     * the callback itself should be kept simple, such as sending audio
     * data to the audio player, or sending timed phonetic information to
     * a separate thread for lip animation on a talking head.
     *
     * @param abuf Cereproc Audio Buffer
     */
    override fun Callback(abuf: SWIGTYPE_p_CPRC_abuf?) {
        Log.i(TAG, "Firing engine callback")
        // an example use is to get information about the progress
        val transMk = cerevoice_eng.CPRC_abuf_trans_mk(abuf)
        val transDone = cerevoice_eng.CPRC_abuf_trans_done(abuf)
        for (i in transMk until transDone) {
            var transType: CPRC_ABUF_TRANS_TYPE
            var start: Float
            var end: Float
            var name: String?
            val trans: SWIGTYPE_p_CPRC_abuf_trans? = cerevoice_eng.CPRC_abuf_get_trans(abuf, i)
            transType = cerevoice_eng.CPRC_abuf_trans_type(trans)
            start = cerevoice_eng.CPRC_abuf_trans_start(trans)
            end = cerevoice_eng.CPRC_abuf_trans_end(trans)
            name = cerevoice_eng.CPRC_abuf_trans_name(trans)
            when (transType) {
                CPRC_ABUF_TRANS_TYPE.CPRC_ABUF_TRANS_PHONE -> {
                    Log.i(TAG, String.format("phoneme: %.3f %.3f %s\n", start, end, name))
                }
                CPRC_ABUF_TRANS_TYPE.CPRC_ABUF_TRANS_WORD -> {
                    Log.i(TAG, String.format("word: %.3f %.3f %s\n", start, end, name))
                }
                CPRC_ABUF_TRANS_TYPE.CPRC_ABUF_TRANS_MARK -> {
                    Log.i(TAG, String.format("marker: %.3f %.3f %s\n", start, end, name))
                }
                CPRC_ABUF_TRANS_TYPE.CPRC_ABUF_TRANS_ERROR -> {
                    Log.i(TAG, String.format("could not retrieve transcription at '%d'", i))
                }
            }
        }

        // Another example usage of the callback is to play the audio
        // this is the recommended approach as it is safe to use the audio in the callback
        // Copy the audio buffer into a byte array for Android to then pass to the audio track
        val wavAdded = cerevoice_eng.CPRC_abuf_added_wav_sz(abuf)
        val wavMk = cerevoice_eng.CPRC_abuf_wav_mk(abuf)
        val outBuffer = ShortArray(wavAdded)
        for (i in 0 until wavAdded) {
            outBuffer[i] = cerevoice_eng.CPRC_abuf_wav(abuf, wavMk + i)
        }
        audioTrack!!.write(outBuffer, 0, outBuffer.size)
        Log.d(TAG, "TAMAÑO DE BUFFER" + outBuffer.size.toString())

    }
}
