package com.example.farmwise;

import static android.widget.Toast.makeText;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class PocketSphinxActivity extends Activity implements
        RecognitionListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String DIGITS_SEARCH = "digits";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "farm wise";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        // Prepare the data for UI
        captions = new HashMap<>();
        captions.put(KWS_SEARCH, R.string.collect_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        setContentView(R.layout.sphinx);
        ((TextView) findViewById(R.id.caption_text))
                .setText("Preparing the recognizer");

        // Check if user has given permission to record audio

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new SetupTask(this).execute();
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<PocketSphinxActivity> activityReference;
        SetupTask(PocketSphinxActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }
        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                ((TextView) activityReference.get().findViewById(R.id.caption_text))
                        .setText("Failed to init recognizer " + result);
            } else {
                activityReference.get().switchSearch(KWS_SEARCH);
            }
        }
    }

    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
    public void onRequestPermissionsResult(int requestCode, String[] permissions,   int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("here");
        System.out.println(grantResults);
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            System.out.println("AUDIO");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                new SetupTask(this).execute();
            } else {
                System.out.println("denied audio");
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE))
            switchSearch(DIGITS_SEARCH);
        else
            ((TextView) findViewById(R.id.result_text)).setText(text);
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH)) {
            Hypothesis hypothesis = recognizer.getDecoder().hyp();
            if (hypothesis != null) {
                String recognizedWords = hypothesis.getHypstr();
                // save words to file
                saveRecognizedWordsToFile(recognizedWords);
            } else {
                System.out.println("No words recognized.");
            }
            switchSearch(KWS_SEARCH);
        }
    }
    private void saveRecognizedWordsToFile(String recognizedWords) {
        try {
            recognizedWords = "collected " + recognizedWords;
            File externalFilesDir = getExternalFilesDir(null); // Get the app-specific external directory
            if (externalFilesDir != null) {
                File file = new File(externalFilesDir, "recognized_crops.txt");

                FileOutputStream fos = new FileOutputStream(file, true); // Pass "true" for append mode
                fos.write(recognizedWords.getBytes());
                fos.write("\n".getBytes()); // Add a new line after each set of recognized words
                fos.close();

                System.out.println("Recognized words appended to file: " + file.getAbsolutePath());
            } else {
                System.out.println("External storage not available or accessible.");
            }
            // used to check the words inside recognized.txt
            printRecognizedWords();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void printRecognizedWords() {
        String filename = "recognized_crops.txt";
        File file = new File(getExternalFilesDir(null), filename);

        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line); // Print each line to the console
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("recognized_words.txt does not exist.");
        }
    }
    private void switchSearch(String searchName) {
        recognizer.stop();

        // if the activate keyword is said, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

        String caption = getResources().getString(captions.get(searchName));
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
    }

    private void setupRecognizer(File assetsDir) throws IOException {

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        // keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // number and crop recognition
        File digitsGrammar = new File(assetsDir, "digits.gram");
        // reset the grammar if changed
//        if (digitsGrammar.exists()) {
//            digitsGrammar.delete();
//        }
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
    }

    @Override
    public void onError(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }
}
