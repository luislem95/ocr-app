<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>OCR con Firebase ML Kit</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content>
      <ion-button @click="tomarFoto">Tomar Foto</ion-button>
      <img v-if="imagen" :src="imagen" alt="Imagen capturada" />
      <ion-textarea v-model="texto" placeholder="Texto detectado aparecerá aquí"></ion-textarea>
    </ion-content>
  </ion-page>
</template>

<script>
import { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonTextarea } from '@ionic/vue';
import { Camera, CameraResultType } from '@capacitor/camera';
import { Capacitor } from '@capacitor/core';
import { initializeApp } from "firebase/app";
import { getStorage, ref, uploadBytes, getDownloadURL } from "firebase/storage";

// ✅ **Configuración de Firebase**
const firebaseConfig = {
  apiKey: "AIzaSyBJmdGpEiVAf7lGi_o-kMYOGG8ydS98hd0",
  authDomain: "claro-image-to-text.firebaseapp.com",
  projectId: "claro-image-to-text",
  storageBucket: "claro-image-to-text.firebasestorage.app",
  messagingSenderId: "68456990321",
  appId: "1:68456990321:web:43625b4884924b7e45fdd0",
  measurementId: "G-XMPHJHJETW"
};

// ✅ **Inicializar Firebase**
const app = initializeApp(firebaseConfig);
const storage = getStorage(app);

// ✅ **URL de la función de OCR en Firebase Functions**
const OCR_FUNCTION_URL = "https://us-central1-claro-image-to-text.cloudfunctions.net/recognizeText";

export default {
  components: { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonTextarea },
  data() {
    return {
      imagen: null,
      texto: '',
    };
  },
  methods: {
    // ✅ **Detectar si está en móvil o en la web**
    esMovil() {
      return Capacitor.getPlatform() !== 'web';
    },

    // ✅ **Solicitar permisos de cámara en móvil**
    async pedirPermisoCamara() {
      try {
        const permiso = await Camera.requestPermissions();
        if (permiso.camera !== 'granted') {
          console.error("Permiso de cámara denegado");
          this.texto = "Se requiere permiso para acceder a la cámara.";
          return false;
        }
        return true;
      } catch (error) {
        console.error("Error solicitando permisos:", error);
        this.texto = "No se pudo solicitar permisos.";
        return false;
      }
    },

    // ✅ **Tomar foto en móvil o abrir input en web**
    async tomarFoto() {
      if (!this.esMovil()) {
        return this.tomarFotoWeb();
      }

      const permisoConcedido = await this.pedirPermisoCamara();
      if (!permisoConcedido) return;

      try {
        const foto = await Camera.getPhoto({
          quality: 90,
          allowEditing: false,
          resultType: CameraResultType.Uri
        });

        if (!foto.webPath) {
          console.error("No se obtuvo una imagen válida.");
          return;
        }

        this.imagen = foto.webPath;
        await this.subirImagenYReconocerTexto(foto.webPath);
      } catch (error) {
        console.error("Error al tomar la foto:", error);
        this.texto = "Error al tomar la foto.";
      }
    },

    // ✅ **Método para subir imagen en la web**
    tomarFotoWeb() {
      const input = document.createElement("input");
      input.type = "file";
      input.accept = "image/*";
      input.onchange = async (event) => {
        const file = event.target.files[0];
        if (file) {
          this.imagen = URL.createObjectURL(file);
          await this.subirImagenYReconocerTexto(URL.createObjectURL(file));
        }
      };
      input.click();
    },

    // ✅ **Subir imagen a Firebase Storage y hacer OCR**
    async subirImagenYReconocerTexto(imagePath) {
      try {
        // Verificar si la URL es válida antes de hacer `fetch`
        const response = await fetch(imagePath);
        if (!response.ok) throw new Error("No se pudo obtener la imagen.");

        const blob = await response.blob();

        // ✅ **Subir imagen a Firebase Storage**
        const storageRef = ref(storage, `ocr_images/${new Date().toISOString()}.jpg`);
        await uploadBytes(storageRef, blob);
        const imageUrl = await getDownloadURL(storageRef);

        console.log("Imagen subida a Firebase Storage:", imageUrl);

        // ✅ **Llamar a la función en Firebase para procesar la imagen con ML Kit**
        const ocrText = await this.enviarImagenARecognizeText(imageUrl);
        this.texto = ocrText || "No se detectó texto.";
      } catch (error) {
        console.error("Error en OCR:", error);
        this.texto = "Error al reconocer el texto.";
      }
    },

    // ✅ **Enviar la imagen a Firebase Function para OCR**
    async enviarImagenARecognizeText(imageUrl) {
      try {
        const response = await fetch(OCR_FUNCTION_URL, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({ imageUrl })
        });

        const data = await response.json();
        return data.text || "No se detectó texto.";
      } catch (error) {
        console.error("Error llamando a la función recognizeText:", error);
        return "Error al procesar la imagen.";
      }
    }
  }
};
</script>

<style scoped>
img {
  width: 100%;
  max-height: 300px;
  margin-top: 10px;
}
ion-textarea {
  margin-top: 20px;
  margin-left: 20px;
  width: 100%;
  height: 150px;
}
</style>
