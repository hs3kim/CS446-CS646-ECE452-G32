package voicerec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

public class Recorder {
    public void start_recording(Context context) {
        Configuration configuration = new Configuration();

        // Set the path to the configuration file
        configuration.setAcousticModelPath("assets/model");
        configuration.setDictionaryPath("assets/model/dictionary.dict");
        configuration.setLanguageModelPath("assets/model/en-70k-0.1.lm");

        LiveSpeechRecognizer recognizer = null;
        try {
            recognizer = new LiveSpeechRecognizer(configuration);
            recognizer.startRecognition(true);

            System.out.println("Listening for speech...");

            SpeechResult result;
            while ((result = recognizer.getResult()) != null) {
                // Process each word result
                for (WordResult wordResult : result.getWords()) {
                    String word = wordResult.getWord().getSpelling();
                    double startTime = wordResult.getTimeFrame().getStart();
                    double endTime = wordResult.getTimeFrame().getEnd();

                    // Filter only the recognized words during voice activity
                    if (isSpeechActive(startTime, endTime)) {
                        System.out.println("Recognized: " + word);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (recognizer != null) {
                recognizer.stopRecognition();
            }
        }
    }

    private static boolean isSpeechActive(double startTime, double endTime) {
        // Implement your voice activity detection logic here
        // Check if the duration between startTime and endTime exceeds a threshold
        // to consider it as an active speech segment
        double threshold = 0.5; // Adjust this threshold as needed
        return (endTime - startTime) > threshold;
    }
}



