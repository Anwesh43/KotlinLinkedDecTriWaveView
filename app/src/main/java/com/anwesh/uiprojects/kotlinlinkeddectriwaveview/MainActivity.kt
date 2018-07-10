package com.anwesh.uiprojects.kotlinlinkeddectriwaveview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linkeddectriwaveview.LinkedDecTriWaveView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinkedDecTriWaveView.create(this, true)
    }
}
