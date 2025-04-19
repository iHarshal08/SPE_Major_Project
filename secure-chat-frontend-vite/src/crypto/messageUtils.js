export const encryptMessage = async (key, message) => {
    const iv = window.crypto.getRandomValues(new Uint8Array(12));
    const encoded = new TextEncoder().encode(message);
  
    const encrypted = await window.crypto.subtle.encrypt(
      { name: "AES-GCM", iv },
      key,
      encoded
    );
  
    const ivBase64 = btoa(String.fromCharCode(...iv));
    const cipherBase64 = btoa(String.fromCharCode(...new Uint8Array(encrypted)));
    return `${ivBase64}:${cipherBase64}`;
  };
  
  export const decryptMessage = async (key, encodedMessage) => {
    try {
      const [ivBase64, cipherBase64] = encodedMessage.split(":");
      const iv = Uint8Array.from(atob(ivBase64), c => c.charCodeAt(0));
      const cipher = Uint8Array.from(atob(cipherBase64), c => c.charCodeAt(0));
  
      const decryptedBuffer = await window.crypto.subtle.decrypt(
        { name: "AES-GCM", iv },
        key,
        cipher
      );
  
      return new TextDecoder().decode(decryptedBuffer);
    } catch (e) {
      console.error("Decryption failed:", e);
      return "⚠️ [Encrypted message]";
    }
  };
  
  