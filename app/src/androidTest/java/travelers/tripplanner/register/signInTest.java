package travelers.tripplanner.register;

import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import travelers.tripplanner.R;

import static org.junit.Assert.*;

public class signInTest {

    @Rule
    public ActivityTestRule<signIn> SignInTestRule = new ActivityTestRule<signIn>(signIn.class);
    private signIn signInActivity = null;
    private EditText username, password;

    @Before
    public void setUp() throws Exception {
        signInActivity = SignInTestRule.getActivity();
        username =  signInActivity.findViewById(R.id.ET1);
        password =  signInActivity.findViewById(R.id.ET2);
    }

    @Test
    public void TestLaunch(){
        //validPassword
        Boolean success = signInActivity.checkEmpty(username, password);
        assertEquals(success, true);
    }

    @After
    public void tearDown() throws Exception {
    }

}