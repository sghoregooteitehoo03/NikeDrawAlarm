package com.nikealarm.nikedrawalarm.data

import com.google.gson.annotations.SerializedName

data class Nodes(
    val nodes: List<ImageNodes>,
    val properties: ShoesProperties
)
