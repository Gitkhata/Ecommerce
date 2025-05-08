package np.com.jp.app.ecommerce.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncoderTest {
    @Test
    public void testPasswordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String plainPassword = "jp2082";
        String encodedPassword = bCryptPasswordEncoder.encode(plainPassword);

        System.out.println(encodedPassword);

       boolean match=  bCryptPasswordEncoder.matches(plainPassword, encodedPassword);

       assertThat(match).isTrue();

    }
}
