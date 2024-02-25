/*
 * MIT License
 *
 * Copyright (c) 2024 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.testing

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

/**
 * Unit tests for validating text input using the TextValidator class. The tests cover various
 * scenarios for validating names, emails, and passwords.
 */
class TextValidatorTest {

    @Test
    fun textValidator_CorrectNameSimple_ReturnsTrue() {
        assertThat(TextValidator.isValidName("Name"), `is`(true))
    }

    @Test
    fun textValidator_CorrectFullName_ReturnsTrue() {
        assertThat(TextValidator.isValidName("Name Surname"), `is`(true))
    }

    @Test
    fun textValidator_CorrectShortName_ReturnsTrue() {
        assertThat(TextValidator.isValidName("Ram"), `is`(true))
    }

    @Test
    fun textValidator_InvalidNameInitialSpace_ReturnsFalse() {
        assertThat(TextValidator.isValidName(" Name"), `is`(false))
    }

    @Test
    fun textValidator_InvalidNameFinalSpace_ReturnsFalse() {
        assertThat(TextValidator.isValidName("Name "), `is`(false))
    }

    @Test
    fun textValidator_InvalidNameLowerCase_ReturnsFalse() {
        assertThat(TextValidator.isValidName("name"), `is`(false))
    }

    @Test
    fun textValidator_InvalidShortName_ReturnsFalse() {
        assertThat(TextValidator.isValidName("Jo"), `is`(false))
    }

    @Test
    fun textValidator_EmptyNameString_ReturnsFalse() {
        assertThat(TextValidator.isValidName(""), `is`(false))
    }

    @Test
    fun textValidator_CorrectEmailSimple_ReturnsTrue() {
        assertThat(TextValidator.isValidEmail("name@itu.dk"), `is`(true))
    }

    @Test
    fun textValidator_CorrectEmailSubDomain_ReturnsTrue() {
        assertThat(TextValidator.isValidEmail("name@email.co.uk"), `is`(true))
    }

    @Test
    fun textValidator_InvalidEmailNoTld_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail("name@itu"), `is`(false))
    }

    @Test
    fun textValidator_InvalidEmailDoubleDot_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail("name@itu..dk"), `is`(false))
    }

    @Test
    fun textValidator_InvalidEmailNoUsername_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail("@itu.dk"), `is`(false))
    }

    @Test
    fun textValidator_InvalidEmailNoDomain_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail("name@"), `is`(false))
    }

    @Test
    fun textValidator_EmptyEmailString_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail(""), `is`(false))
    }

    @Test
    fun textValidator_CorrectPasswordSixChars_ReturnsTrue() {
        assertThat(TextValidator.isValidPassword("ABC123"), `is`(true))
    }

    @Test
    fun textValidator_InvalidPasswordFiveChars_ReturnsFalse() {
        assertThat(TextValidator.isValidPassword("ABC12"), `is`(false))
    }

    @Test
    fun textValidator_InvalidPasswordFourChars_ReturnsFalse() {
        assertThat(TextValidator.isValidPassword("AB12"), `is`(false))
    }

    @Test
    fun textValidator_InvalidPasswordThreeChars_ReturnsFalse() {
        assertThat(TextValidator.isValidPassword("AB1"), `is`(false))
    }

    @Test
    fun textValidator_InvalidPasswordTwoChars_ReturnsFalse() {
        assertThat(TextValidator.isValidPassword("A1"), `is`(false))
    }

    @Test
    fun textValidator_EmptyPasswordString_ReturnsFalse() {
        assertThat(TextValidator.isValidPassword(""), `is`(false))
    }

}
