<!-- <template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>OCR en Tiempo Real</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content>
      <div id="camera-preview"></div>
      <ion-button @click="iniciarCamara">Iniciar Cámara</ion-button>
      <ion-button @click="detenerCamara">Detener Cámara</ion-button>
      <ion-textarea v-model="texto" placeholder="Texto detectado aparecerá aquí"></ion-textarea>
    </ion-content>
  </ion-page>
</template>

<script>
import { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonTextarea } from '@ionic/vue';
import { CameraPreview } from '@capacitor-community/camera-preview';
import { TextDetector } from '@capacitor-mlkit/text-recognition';
import { Capacitor } from '@capacitor/core';

// ✅ **Detectar si está en móvil o en la web**
const esMovil = () => Capacitor.getPlatform() !== 'web';

export default {
  components: { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonTextarea },
  data() {
    return {
      texto: '',
      interval: null, // Intervalo para capturar frames en tiempo real
    };
  },
  methods: {
    async iniciarCamara() {
      try {
        await CameraPreview.start({
          parent: "camera-preview",
          position: "rear",
          toBack: true
        });

        console.log("📷 Cámara iniciada");

        // Capturar frames cada 2 segundos y hacer OCR
        this.interval = setInterval(this.capturarFrame, 2000);
      } catch (error) {
        console.error("❌ Error al iniciar la cámara:", error);
      }
    },

    async capturarFrame() {
      try {
        console.log("📸 Capturando frame...");
        const photo = await CameraPreview.capture({ quality: 90 });

        if (!photo.value) {
          console.error("❌ No se obtuvo una imagen válida.");
          return;
        }

        const base64Image = `data:image/jpeg;base64,${photo.value}`;
        console.log("🖼 Imagen capturada en Base64");

        // ✅ **Hacer OCR directamente en el dispositivo**
        this.detectarTextoLocal(base64Image);
      } catch (error) {
        console.error("❌ Error al capturar frame:", error);
      }
    },

    async detectarTextoLocal(imageBase64) {
      try {
        console.log("🔍 Procesando OCR en local...");
        const result = await TextDetector.recognizeText({ image: imageBase64 });

        if (result.text && result.text.length > 0) {
          this.texto = result.text;
          console.log("✅ Texto detectado:", this.texto);
        } else {
          console.warn("⚠️ No se detectó texto en la imagen.");
          this.texto = "No se detectó texto.";
        }
      } catch (error) {
        console.error("❌ Error en OCR local:", error);
        this.texto = "Error al reconocer el texto.";
      }
    },

    detenerCamara() {
      try {
        console.log("🛑 Deteniendo cámara...");
        clearInterval(this.interval);
        CameraPreview.stop();
        console.log("✅ Cámara detenida");
      } catch (error) {
        console.error("❌ Error al detener la cámara:", error);
      }
    }
  }
};
</script>

<style scoped>
#camera-preview {
  width: 100%;
  height: 300px;
  background: black;
}
ion-textarea {
  margin-top: 20px;
  width: 100%;
  height: 150px;
}
</style> -->
