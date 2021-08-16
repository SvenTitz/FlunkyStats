package com.example.flunkystats



class AppConfig {

    companion  object {
        const val EXTRA_MESSAGE_ENTRY_ID = "com.example.flunkystats.ENTRY_ID"

        const val RC_SIGN_IN = 1

        const val FLOAT_FORMAT_1 = "%.1f"
        const val FLOAT_FORMAT_0 = "%.0f"

        val Any.TAG: String
            get() {
                val tag = javaClass.simpleName +": Sven"
                return tag;
                //return if (tag.length <= 23) tag else tag.substring(0, 23)
            }
    }

}