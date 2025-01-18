package com.sg.simplekanban.commom.util

class AdmobUtil {
    companion object{

        private const val TEST = "TEST"
        private const val RELEASE = "RELEASE"

        private val TEST_OR_RELEASE: String = TEST
//        private val TEST_OR_RELEASE: String = RELEASE

        private const val BANNER_TEST = "ca-app-pub-3940256099942544/6300978111"
        private const val INTERSTICIAL_TEST = "ca-app-pub-3940256099942544/1033173712"

        private const val BANNER = "ca-app-pub-7405337249837850/1475498428"
        private const val INTERSTICIAL = "ca-app-pub-7405337249837850/5750581512"

        fun getBannerId(): String{
            return if (TEST_OR_RELEASE === RELEASE) BANNER else BANNER_TEST
        }

        fun getIntersticialId(): String{
            return if (TEST_OR_RELEASE === RELEASE) INTERSTICIAL else INTERSTICIAL_TEST
        }
    }

}