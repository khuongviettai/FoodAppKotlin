package com.khuongviettai.foodorder.model

import java.io.Serializable

class Image : Serializable {
    private var url: String? = null

    constructor() {}
    constructor(url: String?) {
        this.url = url
    }

    fun getUrl(): String? {
        return url
    }

    fun setUrl(url: String?) {
        this.url = url
    }
}