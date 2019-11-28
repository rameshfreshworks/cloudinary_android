package cloudinary.android.sample;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.cloudinary.android.sample.R;
import com.cloudinary.android.sample.app.MainActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class UploadWidgetTest {

    private static File assetFile;
    private static final String TEST_IMAGE = "image.png";

    @BeforeClass
    public static void setup() throws IOException {
        assetFile = assetToFile(TEST_IMAGE);
    }

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void testUploadWidget() {
        Intent intent = new Intent();
        intent.setData(Uri.fromFile(assetFile));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

        intending(hasComponent(MockNativePickerActivity.class.getName())).respondWith(result);
        intentsTestRule.getActivity().startActivityForResult(new Intent(intentsTestRule.getActivity(), MockNativePickerActivity.class), MainActivity.CHOOSE_IMAGE_REQUEST_CODE);

        onView(withId(R.id.crop_action)).perform(click());
        onView(withId(R.id.doneButton)).perform(click());
        onView(withId(R.id.uploadFab)).perform(click());
    }

    private static File assetToFile(String testImage) throws IOException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File file = new File(context.getCacheDir() + "/tempFile_" + System.currentTimeMillis());

        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        InputStream is = getAssetStream(testImage);

        byte[] buffer = new byte[16276];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }

        fos.flush();
        is.close();

        return file;
    }

    private static InputStream getAssetStream(String filename) throws IOException {
        return InstrumentationRegistry.getContext().getAssets().open(filename);
    }
}