import { auth } from "../firebase";
import { signInWithEmailAndPassword, signOut } from "firebase/auth";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";

async function getToken() {
  const user = auth.currentUser;
  if (user) {
    return await user.getIdToken();
  }
  return null;
}

export function getStoredUser() {
  const token = sessionStorage.getItem("nlams_token");
  if (!token) return null;
  try {
    const payload = token.split(".")[1];
    const json = JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
    return {
      id: json.userId ?? null,
      email: json.sub ?? "",
      name: json.name ?? "Authenticated User",
      role: json.role ?? "",
    };
  } catch {
    return null;
  }
}

async function request(path, options = {}) {
  const headers = new Headers(options.headers || {});
  if (options.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const token = await getToken();
  if (token) headers.set("Authorization", `Bearer ${token}`);

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });

  const text = await response.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }

  if (!response.ok) {
    const message =
      data?.message || data?.error || (typeof data === "string" ? data : null) ||
      `Request failed with status ${response.status}`;
    // Note: We no longer remove sessionStorage items as we rely on Firebase auth state.
    throw new Error(message);
  }

  return data;
}

export const api = {
  auth: {
    login: (email, password) => request("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    }),
    register: (payload) => request("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(payload),
    }),
  },

  dashboard: {
    summary: () => request("/api/dashboard/summary"),
  },

  projects: {
    list: () => request("/api/projects"),
    byId: (id) => request(`/api/projects/$${id}`),
    byState: (state) => request(`/api/projects/state/${encodeURIComponent(state)}`),
    byDistrict: (district) => request(`/api/projects/district/${encodeURIComponent(district)}`),
    byStatus: (status) => request(`/api/projects/status/${encodeURIComponent(status)}`),
    create: (payload, createdById) => request(`/api/projects?createdById=${encodeURIComponent(createdById)}`, {
      method: "POST",
      body: JSON.stringify(payload),
    }),
    update: (id, payload) => request(`/api/projects/$${id}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    }),
    remove: (id) => request(`/api/projects/$${id}`, { method: "DELETE" }),
  },

  parcels: {
    list: () => request("/api/parcels"),
    byId: (id) => request(`/api/parcels/$${id}`),
    byProject: (projectId) => request(`/api/parcels/project/${projectId}`),
    create: (payload) => request("/api/parcels", { method: "POST", body: JSON.stringify(payload) }),
    update: (id, payload) => request(`/api/parcels/${id}`, { method: "PUT", body: JSON.stringify(payload) }),
    remove: (id) => request(`/api/parcels/${id}`, { method: "DELETE" }),
  },

  acquisitionStages: {
    list: () => request("/api/acquisition-stages"),
    byId: (id) => request(`/api/acquisition-stages/$${id}`),
    byProject: (projectId) => request(`/api/acquisition-stages/project/${projectId}`),
    create: (payload) => request("/api/acquisition-stages", { method: "POST", body: JSON.stringify(payload) }),
    update: (id, payload) => request(`/api/acquisition-stages/$${id}`, { method: "PUT", body: JSON.stringify(payload) }),
    remove: (id) => request(`/api/acquisition-stages/$${id}`, { method: "DELETE" }),
    advance: (projectId, updatedById, remarks = "") => request(`/api/acquisition-stages/project/${projectId}/advance?updatedById=${encodeURIComponent(updatedById)}&remarks=${encodeURIComponent(remarks)}`, { method: "POST" }),
  },

  compensations: {
    list: () => request("/api/compensations"),
    byId: (id) => request(`/api/compensations/$${id}`),
    byParcel: (parcelId) => request(`/api/compensations/parcel/${parcelId}`),
    create: (payload) => request("/api/compensations", { method: "POST", body: JSON.stringify(payload) }),
    update: (id, payload) => request(`/api/compensations/$${id}`, { method: "PUT", body: JSON.stringify(payload) }),
    remove: (id) => request(`/api/compensations/$${id}`, { method: "DELETE" }),
  },

  affectedFamilies: {
    list: () => request("/api/affected-families"),
    byId: (id) => request(`/api/affected-families/$${id}`),
    byParcel: (parcelId) => request(`/api/affected-families/parcel/${parcelId}`),
    byProject: (projectId) => request(`/api/affected-families/project/${projectId}`),
    create: (payload) => request("/api/affected-families", { method: "POST", body: JSON.stringify(payload) }),
    update: (id, payload) => request(`/api/affected-families/$${id}`, { method: "PUT", body: JSON.stringify(payload) }),
    remove: (id) => request(`/api/affected-families/$${id}`, { method: "DELETE" }),
  },
};

export async function loginWithBackend(email, password) {
  // Use Firebase for authentication
  await signInWithEmailAndPassword(auth, email, password);
  const user = auth.currentUser;
  if (user) {
    const token = await user.getIdToken();
    return { token };
  }
  throw new Error("Failed to authenticate with Firebase");
}

export function logoutFromBackend() {
  signOut(auth);
}