import axios from "axios";

//const API = "http://chat-app.local:32540";
//const API2 = "http://keyexchange:8081";
//const API3 = "http://messaging:8082";
export const sendPublicKey = async (jwt, userId, publicKey) => {
    console.log(jwt);
    const jwtSanitized = jwt.replace(/\s/g, ''); // remove all whitespace (including inside)

    return await axios.post(`/keyexchange/exchange`, { userId, publicKey }, {
    headers: {
      Authorization: `Bearer ${jwtSanitized}`,
      "Content-Type": "application/json"
    }
  });
};

export const pollForOtherKey = async (jwt, email) => {
  return await axios.get(`/keyexchange/exchange`, {
    headers: { Authorization: `Bearer ${jwt}` },
    params: { email }
  });
};

export const fetchChatMessages = async (jwt, recipient) => {
    console.log("JWT token being sent:", jwt);
    return await axios.get(`/messaging/messages`, {
    headers: { Authorization: `Bearer ${jwt}` },
    
  });
};

export const postMessage = async (jwt, to, encryptedMessage) => {
  return await axios.post(`/messaging/send`, { to, message: encryptedMessage }, {
    headers: { Authorization: `Bearer ${jwt}` }
  });
};

export async function loginUser(email, password) {
  const response = await fetch(`/login/login`, {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password })
  });

  if (!response.ok) {
      throw new Error('Login failed');
  }

  const data = await response.json();
  return data.token; // expects { token: "JWT_TOKEN" }
}
