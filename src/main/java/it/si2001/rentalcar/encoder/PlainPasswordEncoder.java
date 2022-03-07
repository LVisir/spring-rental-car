package it.si2001.rentalcar.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * No Encoder for passwords
 */
public class PlainPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        return charSequence.toString();
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return charSequence.toString().equals(s);
    }
}
