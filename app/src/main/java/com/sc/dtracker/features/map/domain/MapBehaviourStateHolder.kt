package com.sc.dtracker.features.map.domain

import com.yandex.mapkit.map.CameraPosition

class MapBehaviourStateHolder {

    var globalRestoreCameraPos: CameraPosition? = null
    var followMap: Boolean = true
        private set

    fun onUserMovedMap() {
        followMap = false
    }

    fun onUserStartTrackRecording() {
        followMap = true
    }

    fun onUserJumpedToCurrent() {
        followMap = true
    }
}