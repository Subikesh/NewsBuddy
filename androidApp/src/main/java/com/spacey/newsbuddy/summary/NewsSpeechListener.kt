package com.spacey.newsbuddy.summary

import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

class NewsSpeechListener(
    private val setFabIcon: (ImageVector) -> Unit,
    private val onSpeaking: (Int) -> Unit
) : UtteranceProgressListener() {

    override fun onStart(utteranceId: String?) {
        setFabIcon(Icons.Outlined.Pause)
        if (utteranceId != null) {
            onSpeaking(utteranceId.toInt())
        }
    }

    override fun onBeginSynthesis(
        utteranceId: String?,
        sampleRateInHz: Int,
        audioFormat: Int,
        channelCount: Int
    ) {
        super.onBeginSynthesis(utteranceId, sampleRateInHz, audioFormat, channelCount)
    }

    override fun onDone(utteranceId: String?) {
        setFabIcon(Icons.Outlined.PlayArrow)
        onSpeaking(-1)
    }

    override fun onStop(utteranceId: String?, interrupted: Boolean) {
        setFabIcon(Icons.Outlined.PlayArrow)
        onSpeaking(-1)
    }

    override fun onError(utteranceId: String?) {
        TODO("Not yet implemented")
    }

    override fun onError(utteranceId: String?, code: Int) {
        onSpeaking(-1)
        setFabIcon(Icons.Outlined.PlayArrow)
        Log.d("TextToSpeech", "Error in uttering conversation: `$utteranceId` with code: `$code`")
    }
}