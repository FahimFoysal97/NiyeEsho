import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

export const sendPushRelay = functions.https.onRequest(async (req, res) => {
  res.set("Access-Control-Allow-Origin", "*");
  res.set("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    res.status(204).send("");
    return;
  }

  if (req.method !== "POST") {
    res.status(405).json({ error: "Method Not Allowed" });
    return;
  }

  try {
    const { targetToken, payload } = req.body;
    if (!targetToken || !payload) {
      res.status(400).json({ error: "Missing targetToken or payload" });
      return;
    }

    const message: admin.messaging.Message = {
      token: targetToken,
      data: {
        type: payload.type || "",
        senderId: payload.senderId || "",
        senderName: payload.senderName || "",
        listId: payload.listId || "",
        timestamp: payload.timestamp || "",
        data: typeof payload.data === "string" ? payload.data : JSON.stringify(payload.data),
      },
      android: {
        priority: "high",
      },
    };

    const response = await admin.messaging().send(message);
    console.log("Successfully sent FCM message:", response);
    res.status(200).json({ success: true, messageId: response });
  } catch (error: any) {
    console.error("Error sending FCM message:", error);
    res.status(500).json({ error: error.message || "Failed to send message" });
  }
});
