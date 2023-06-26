package voicerec;

import static android.provider.Settings.System.getString;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.justai.aimybox.Aimybox;
import com.justai.aimybox.assistant.api.DummyDialogApi;
import com.justai.aimybox.components.AimyboxAssistantViewModel;
import com.justai.aimybox.components.AimyboxProvider;
import com.justai.aimybox.core.Config;
import com.justai.aimybox.speechkit.google.platform.GooglePlatformTextToSpeech;
import com.justai.aimybox.speechkit.pocketsphinx.*;
//import com.justai.aimybox.speechkit.pocketsphinx.PocketsphinxAssets;
//import com.justai.aimybox.speechkit.pocketsphinx.PocketsphinxRecognizerProvider;
//import com.justai.aimybox.speechkit.pocketsphinx.PocketsphinxSpeechToText;
//import com.justai.aimybox.speechkit.pocketsphinx.PocketsphinxVoiceTrigger;

import java.util.Locale;

public class Recorder extends Application implements AimyboxProvider{
    @Override
    public Aimybox getAimybox() {
        return createAimybox(this);
    }
    private Aimybox createAimybox(Context context) {
        PocketsphinxAssets assets = PocketsphinxAssets.Companion.fromApkAssets(
                context,
                "model",
                "model" + "/dictionary.dict",
                "model" + "/grammar.gram",
                ""
        );
        PocketsphinxRecognizerProvider provider = new PocketsphinxRecognizerProvider(assets,16000, 1e-40f);

        GooglePlatformTextToSpeech textToSpeech = new GooglePlatformTextToSpeech(context, Locale.getDefault(), true);
        PocketsphinxSpeechToText speechToText = new PocketsphinxSpeechToText(provider, assets.getGrammarFilePath(), 5000, 5000L);
        String keyphrase = "one two three";
        PocketsphinxVoiceTrigger voiceTrigger = new PocketsphinxVoiceTrigger(provider, keyphrase);//getString(R.string.keyphrase));
        DummyDialogApi dialogApi = new DummyDialogApi();

        Config config = Config.Companion.create(speechToText, textToSpeech, dialogApi, builder -> {
            builder.setVoiceTrigger(voiceTrigger);
            return null;
        }
        );
//        config.Voice(voiceTrigger);

        return new Aimybox(config, context);
    }

    @NonNull
    @Override
    public AimyboxAssistantViewModel.Factory getViewModelFactory() {
        return null;
    }
}


