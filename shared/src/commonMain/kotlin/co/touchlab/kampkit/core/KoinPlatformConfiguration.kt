package co.touchlab.kampkit.core

object KoinPlatformConfiguration {
    private lateinit var dateHandler: DateHandler
    fun init(dateHandler: DateHandler) {
        this.dateHandler = dateHandler
    }
    fun provideDateHandler(): DateHandler = dateHandler
}
