package jpushdemo.example.com.jpush;

import android.app.Application;
import android.text.TextUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(4, 2 + 2);
        String cc = "AA:B9:C0:F2:4P:89:AA";
        System.out.println(cc);
        String aa = address2CentralId(cc);
        System.out.println(aa);
        String bb = centralId2Address(aa);
        System.out.println(bb);

    }


    /**
     * BluetoothDevice address Convert to centralId
     *
     * @param address
     * @return
     */
    public static String address2CentralId(String address) {

        String[] addressArray = address.split(":");

        StringBuilder convert = new StringBuilder();
        for (int i = addressArray.length - 1; i >= 0; i--) {
            convert.append(addressArray[i]);
        }

        return convert.toString();
    }

    public static String centralId2Address(String address) {

        StringBuilder convert = new StringBuilder();

        for (int i = address.length() - 1; i >= 0; i--) {

            if (i % 2 == 0) {
                convert.append(address.charAt(i));
                convert.append(address.charAt(i + 1));
                if (i != 0) {
                    convert.append(":");
                }
            }
        }

        return convert.toString();
    }
}