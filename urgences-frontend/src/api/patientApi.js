import axiosClient from './axiosClient';

export async function rechercherPatients(q) {
  const response = await axiosClient.get('/patients', { params: q ? { q } : {} });
  return response.data;
}

export async function creerPatient(payload) {
  const response = await axiosClient.post('/patients', payload);
  return response.data;
}