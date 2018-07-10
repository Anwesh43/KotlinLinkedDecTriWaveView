package com.anwesh.uiprojects.kotlinlinkeddectriwaveview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.anwesh.uiprojects.linkeddectriwaveview.LinkedDecTriWaveView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : LinkedDecTriWaveView = LinkedDecTriWaveView.create(this, true)
        view.setOnCompletionListener {
            Toast.makeText(this, "Animation number ${it + 1} completed", Toast.LENGTH_SHORT).show()
        }
    }
}
