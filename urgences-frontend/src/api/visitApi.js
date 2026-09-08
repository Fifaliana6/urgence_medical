import axiosClient from './axiosClient';

export async function creerVisite(payload) {
  const response = await axiosClient.post('/visits', payload);
  return response.data;
}

export async function getListeAttente() {
  const response = await axiosClient.get('/visits/attente');
  return response.data;
}

export async function listerToutesVisites(statut) {
  const response = await axiosClient.get('/visits', { params: statut ? { statut } : {} });
  return response.data;
}

export async function getVisiteDetail(id) {
  const response = await axiosClient.get(`/visits/${id}`);
  return response.data;
}

export async function sortirPatient(id, payload) {
  const response = await axiosClient.put(`/visits/${id}/sortie`, payload);
  return response.data;
}

export async function hospitaliserPatient(id, payload) {
  const response = await axiosClient.put(`/visits/${id}/hospitalisation`, payload);
  return response.data;
}

export async function finHospitalisation(id) {
  const response = await axiosClient.put(`/visits/${id}/fin-hospitalisation`);
  return response.data;
}