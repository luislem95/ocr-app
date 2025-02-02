package com.tuproyecto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import java.io.IOException;

@CapacitorPlugin(name = "MLKitOCR")
public class MLKitOCR extends BridgeActivity {

    @PluginMethod
    public void recognizeText(String imagePath) {
        try {
            // Convertir imagen en Bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            InputImage image = InputImage.fromBitmap(bitmap, 0);

            // Crear el recognizer de ML Kit
            TextRecognizer recognizer = TextRecognition.getClient();
            recognizer.process(image)
                .addOnSuccessListener(result -> {
                    StringBuilder textoReconocido = new StringBuilder();
                    for (Text.TextBlock block : result.getTextBlocks()) {
                        textoReconocido.append(block.getText()).append("\n");
                    }

                    // Enviar resultado a la app
                    JSObject response = new JSObject();
                    response.put("text", textoReconocido.toString());
                    notifyListeners("ocrResult", response);
                })
                .addOnFailureListener(e -> Log.e("MLKitOCR", "Error en OCR: " + e.getMessage()));
        } catch (IOException e) {
            Log.e("MLKitOCR", "No se pudo leer la imagen: " + e.getMessage());
        }
    }
}
