package co.touchlab.kampkit.core

import kotlinx.datetime.Clock

class AndroidDateHandler : DateHandler {
    override fun getCurrentDate(): String {
        return Clock.System.now().toString()
    }
}
