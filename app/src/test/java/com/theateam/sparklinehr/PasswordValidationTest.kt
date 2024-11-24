package com.theateam.sparklinehr

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class PasswordValidationTest {

    @Test
    fun `test valid passwords`() {
        assertTrue(isValidPassword("StrongPass123!"))
        assertTrue(isValidPassword("AnotherSecure@1"))
    }

    @Test
    fun `test invalid passwords`() {
        assertFalse(isValidPassword("Short1!"))
        assertFalse(isValidPassword("NoNumbers!"))
        assertFalse(isValidPassword("NoSpecialChar1"))
        assertFalse(isValidPassword("        "))
        assertFalse(isValidPassword(""))
    }


    private fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$")
        return regex.matches(password)
    }
}