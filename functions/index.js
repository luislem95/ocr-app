const functions = require("firebase-functions");
const cors = require("cors")({ origin: true });
const vision = require("@google-cloud/vision");

const client = new vision.ImageAnnotatorClient();

exports.recognizeText = functions.https.onRequest(async (req, res) => {
  cors(req, res, async () => {
    try {
      const { imageUrl } = req.body;
      if (!imageUrl) {
        return res.status(400).json({ error: "Falta la URL de la imagen" });
      }

      // Llamar a Google Cloud Vision API
      const [result] = await client.textDetection(imageUrl);
      const detections = result.textAnnotations;

      if (!detections || detections.length === 0) {
        return res.status(200).json({ text: "No se detectó texto." });
      }

      return res.status(200).json({ text: detections[0].description });
    } catch (error) {
      console.error("Error en recognizeText:", error);
      return res.status(500).json({ error: "Error interno del servidor" });
    }
  });
});
