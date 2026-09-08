import axiosClient from './axiosClient';

export async function listerSalles() {
  const response = await axiosClient.get('/resources/salles');
  return response.data;
}

export async function creerSalle(payload) {
  const response = await axiosClient.post('/resources/salles', payload);
  return response.data;
}

export async function changerDisponibiliteSalle(id, disponible) {
  const response = await axiosClient.put(`/resources/salles/${id}/disponibilite`, null, { params: { disponible } });
  return response.data;
}

export async function listerLits() {
  const response = await axiosClient.get('/resources/lits');
  return response.data;
}

export async function creerLit(payload) {
  const response = await axiosClient.post('/resources/lits', payload);
  return response.data;
}