export const generateKeyPair = async () => {
    const keyPair = await window.crypto.subtle.generateKey(
      { name: "ECDH", namedCurve: "P-256" },
      true,
      ["deriveKey"]
    );
  
    const rawPub = await window.crypto.subtle.exportKey("raw", keyPair.publicKey);
    const pubB64 = btoa(String.fromCharCode(...new Uint8Array(rawPub)));
  
    return { keyPair, pubB64 };
  };
  
  export const importOtherPublicKey = async (base64) => {
    const binary = Uint8Array.from(atob(base64), c => c.charCodeAt(0));
    return await window.crypto.subtle.importKey(
      "raw",
      binary,
      { name: "ECDH", namedCurve: "P-256" },
      false,
      []
    );
  };
  
  export const deriveSharedKey = async (myPrivateKey, theirPublicKey) => {
    return await window.crypto.subtle.deriveKey(
      { name: "ECDH", public: theirPublicKey },
      myPrivateKey,
      { name: "AES-GCM", length: 256 },
      false,
      ["encrypt", "decrypt"]
    );
  };
  