package com.example.propscape

import java.security.MessageDigest

class Hashed {

    fun hash(pwd: String): String {

        val bytes = pwd.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold(StringBuilder()) {

                sb, it -> sb.append("%02x".format(it))
        }.toString()
    }
}