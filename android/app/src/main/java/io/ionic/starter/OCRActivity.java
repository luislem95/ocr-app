package io.ionic.starter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.graphics.Bitmap;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.common.InputImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OCRActivity extends AppCompatActivity {
    private TextRecognizer recognizer;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Asegúrate de que este layout existe

        recognizer = TextRecognition.getClient();
        cameraExecutor = Executors.newSingleThreadExecutor();
        
        iniciarOCRTiempoReal();
    }

    private void iniciarOCRTiempoReal() {
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, image -> {
            processImage(image);
        });
    }

    private void processImage(ImageProxy image) {
        Bitmap bitmap = imageToBitmap(image);
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

        recognizer.process(inputImage)
            .addOnSuccessListener(text -> {
                Log.d("OCR", "Texto detectado: " + text.getText());
                runOnUiThread(() -> {
                    TextView textoDetectado = findViewById(R.id.texto_detectado);
                    textoDetectado.setText(text.getText());
                });
            })
            .addOnFailureListener(e -> Log.e("OCR", "Error en OCR: " + e.getMessage()))
            .addOnCompleteListener(task -> image.close());
    }

    private Bitmap imageToBitmap(ImageProxy image) {
        return Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
    }
}
