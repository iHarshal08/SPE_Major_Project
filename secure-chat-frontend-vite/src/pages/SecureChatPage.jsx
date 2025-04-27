// 📁 src/components/SecureChatPage.jsx
import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import {
  generateKeyPair,
  importOtherPublicKey,
  deriveSharedKey
} from "../crypto/keyUtils";
import {
  encryptMessage,
  decryptMessage
} from "../crypto/messageUtils";
import {
  sendPublicKey,
  pollForOtherKey,
  fetchChatMessages,
  postMessage
} from "../api/chatApi";
import '../styles/SecureChatPage.css'

const SecureChatPage = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [jwtToken, setJwtToken] = useState("");
  const [sharedKey, setSharedKey] = useState(null);
  const [myUserId, setMyUserId] = useState("");
  const [recipientEmail, setRecipientEmail] = useState("");
  const [recipientEntered, setRecipientEntered] = useState(false);
  const [status, setStatus] = useState("Enter recipient email to begin");
  const [isKeyGenerating, setIsKeyGenerating] = useState(false);
  const [myKeyPair, setMyKeyPair] = useState(null);
  const [myPublicKeyBase64, setMyPublicKeyBase64] = useState("");
  const myPublicKeyRef = useRef("");
  const pollIntervalRef = useRef(null);

  useEffect(() => {
    const token = sessionStorage.getItem("jwt_token");
console.log("🔐 [KeyExchange] JWT from sessionStorage:", token);

if (!token || token === "undefined") {
  console.error("❌ JWT missing or invalid");
  setStatus("Missing auth token. Please log in.");
  return;
}

    setJwtToken(token);
    try {
      const decoded = jwtDecode(token);
      setMyUserId(decoded.sub || decoded.userId || decoded.username);
      initializeKeys();
    } catch (error) {
      setStatus("Invalid JWT token");
    }
    return () => {
      if (pollIntervalRef.current) clearInterval(pollIntervalRef.current);
    };
  }, []);

  const initializeKeys = async () => {
    setIsKeyGenerating(true);
    setStatus("Generating cryptographic keys...");
    try {
      const { keyPair, pubB64 } = await generateKeyPair();
      console.log("🔑 Key pair generated:", keyPair);
      console.log("📡 Public Key (Base64):", pubB64);
  
      setMyKeyPair(keyPair);
      setMyPublicKeyBase64(pubB64);
      myPublicKeyRef.current = pubB64;
      setStatus("Keys generated successfully. Ready to chat!");
    } catch {
      setStatus("Failed to generate keys. Please refresh the page.");
    } finally {
      setIsKeyGenerating(false);
    }
  };

  const handleStartChat = async () => {
    console.log("⏯️ handleStartChat triggered");
    if (!recipientEmail) return setStatus("Please enter recipient's email");
    if (!myPublicKeyRef.current) {
  await initializeKeys();
  
  // After generating, check again. Don't call recursively.
  if (!myPublicKeyRef.current) {
    setStatus("Key generation failed. Please refresh.");
    return;
  }
}

    const token = sessionStorage.getItem("jwt_token").trim();
    if (!token) return setStatus("Missing auth token. Please log in.");

    setStatus("Initiating secure connection...");
    setRecipientEntered(true);

    try {
      await sendPublicKey(token, recipientEmail, myPublicKeyRef.current);
      console.log("✅ Public key POST sent successfully");

      setStatus("Waiting for recipient to respond...");
      pollIntervalRef.current = setInterval(async () => {
        console.log("📡 Polling for other user's key...");
      
        try {
          const res = await pollForOtherKey(token, recipientEmail);
          console.log("✅ Poll response:", res);
      
          const data = res.data; // <- Axios returns { data, status, headers... }
          if (data?.otherPublicKey) {
            console.log("🔐 Received other public key. Stopping polling.");
            clearInterval(pollIntervalRef.current);
            await establishSharedSecret(data.otherPublicKey);
          }
        } catch (e) {
          console.error("❌ Polling error", e);
        }
      }, 3000);
      
    } catch (e) {
      console.error("❌ Exchange failed", e);
      if (e.response?.status === 401) {
        setStatus("Authentication failed. Please log in.");
      } else if (e.response?.status === 400) {
        setStatus("Invalid request format. Check recipient email.");
      } else {
        setStatus(`Server error: ${e.response?.status || "Network error"}`);
      }
    }
    
  };

  const establishSharedSecret = async (otherPubKeyB64) => {
    try {
      setStatus("Establishing secure channel...");
      const imported = await importOtherPublicKey(otherPubKeyB64);
      const derived = await deriveSharedKey(myKeyPair.privateKey, imported);
      setSharedKey(derived);
      setStatus("Secure connection established! You can now chat.");
      await loadMessages(derived);
      pollIntervalRef.current = setInterval(() => loadMessages(derived), 5000);
    } catch (e) {
      console.error("Key derivation failed", e);
      setStatus("Failed to establish secure connection.");
    }
  };

  const loadMessages = async (key) => {
    try {
      const res = await fetchChatMessages(jwtToken, recipientEmail);
      const decrypted = await Promise.all(res.data.map(async msg => {
        const text = await decryptMessage(key, msg);
        console.log(text);
        return `${msg.sender === myUserId ? "You" : recipientEmail}: ${text}`;
      }));
      setMessages(decrypted);
    } catch (e) {
      console.error("Fetch messages failed", e);
    }
  };

  const sendMsg = async () => {
    if (!sharedKey || !input.trim()) return;
    try {
      setStatus("Sending message...");
      const encrypted = await encryptMessage(sharedKey, input);
      await postMessage(jwtToken, recipientEmail, encrypted);
      setMessages(prev => [...prev, `You: ${input}`]);
      setInput("");
      setStatus("Message sent");
    } catch (e) {
      console.error("Send failed", e);
      setStatus("Failed to send message");
    }
  };

  return (
    <div className="App" style={{ padding: 20, maxWidth: 600, margin: "0 auto" }}>
      <h2>Secure Chat: {myUserId}</h2>
      <div style={{ marginBottom: 10, color: "#666" }}>{status}</div>

      {!recipientEntered ? (
        <div style={{ display: "flex", gap: 10 }}>
          <input
            type="email"
            placeholder="Enter recipient's email"
            value={recipientEmail}
            onChange={e => setRecipientEmail(e.target.value)}
            style={{ flexGrow: 1, padding: 8 }}
            disabled={isKeyGenerating}
          />
          <button
            onClick={handleStartChat}
            disabled={isKeyGenerating}
            style={{ padding: "8px 16px" }}>
            {isKeyGenerating ? "Generating Keys..." : "Start Chat"}
          </button>
        </div>
      ) : !sharedKey ? (
        <div>
          <p>Waiting for {recipientEmail} to respond...</p>
          <button onClick={() => setRecipientEntered(false)}>Cancel</button>
        </div>
      ) : (
        <>
          <div className="chat-box" style={{ padding: 15, border: "1px solid #ddd", borderRadius: 8, height: 300, overflowY: "auto", marginBottom: 10, backgroundColor: "#f9f9f9" }}>
            {messages.length === 0 ? (
              <p style={{ color: "#999" }}>No messages yet. Start the conversation!</p>
            ) : (
              messages.map((msg, idx) => (
                <div key={idx} style={{ margin: "5px 0", padding: 8, borderRadius: 4, backgroundColor: msg.startsWith("You:") ? "#e3f2fd" : "#f5f5f5" }}>{msg}</div>
              ))
            )}
          </div>
          <div style={{ display: "flex", gap: 10 }}>
            <input
              type="text"
              value={input}
              onChange={e => setInput(e.target.value)}
              onKeyDown={e => e.key === "Enter" && sendMsg()}
              placeholder="Type your message"
              style={{ flexGrow: 1, padding: 8 }}
            />
            <button onClick={sendMsg} disabled={!input.trim()} style={{ padding: "8px 16px" }}>Send</button>
          </div>
        </>
      )}
    </div>
  );
};

export default SecureChatPage;
