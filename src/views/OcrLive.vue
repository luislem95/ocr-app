<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>OCR Offline con ML Kit</ion-title>
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
import { Plugins } from '@capacitor/core';

const { MLKitOCR } = Plugins;

export default {
  components: { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonTextarea },
  data() {
    return {
      imagen: null,
      texto: '',
    };
  },
  methods: {
    async tomarFoto() {
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

        // 📌 **Llamar al OCR nativo en Android**
        const resultado = await MLKitOCR.recognizeText({ imagePath: foto.path });
        this.texto = resultado.text || "No se detectó texto.";
      } catch (error) {
        console.error("Error al tomar la foto:", error);
        this.texto = "Error al tomar la foto.";
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
