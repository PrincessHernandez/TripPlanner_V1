package travelers.tripplanner.register;

import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;
import android.widget.Toast;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import travelers.tripplanner.MainActivity;
import travelers.tripplanner.R;

import static org.junit.Assert.*;

public class signUpTest {

    @Rule
    public ActivityTestRule<signUp> mainActivityActivityTestRule = new ActivityTestRule<signUp>(signUp.class);
    private signUp signUpActivity = null;
    private EditText email, password, confirmPassword, name;

    @Before
    public void setUp() throws Exception {
        signUpActivity = mainActivityActivityTestRule.getActivity();
        name = (EditText) signUpActivity.findViewById(R.id.ET0);
        email = (EditText) signUpActivity.findViewById(R.id.ET1);
        password = (EditText) signUpActivity.findViewById(R.id.ET2);
        confirmPassword = (EditText) signUpActivity.findViewById(R.id.ET3);
    }

    @Test
    public void TestLaunch(){
        //validPassword
        Boolean success = signUpActivity.validPassword("123456");
        Boolean fail = signUpActivity.validPassword("12345");
        assertEquals(success, true);
        assertEquals(fail, false);

        //checkEmpty
        Boolean checkEmpty = signUpActivity.checkEmpty(name, email, password, confirmPassword);
        assertEquals(checkEmpty, true);

        //valid
        Boolean valid = signUpActivity.valid(password);
        assertEquals(valid, true);

        //email
        Boolean email = signUpActivity.validEmail(this.email);
        assertEquals(email, false);
    }

    @After
    public void tearDown() throws Exception {
    }

}