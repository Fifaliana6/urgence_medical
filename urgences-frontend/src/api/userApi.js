import axiosClient from './axiosClient';

export async function getMonProfil() {
  const response = await axiosClient.get('/users/me');
  return response.data;
}

export async function modifierMonProfil(payload) {
  const response = await axiosClient.put('/users/me', payload);
  return response.data;
}

export async function changerMaDisponibilite(disponible) {
  const response = await axiosClient.put('/users/me/disponibilite', { disponible });
  return response.data;
}

export async function getMedecinsDisponibles() {
  const response = await axiosClient.get('/users/medecins-disponibles');
  return response.data;
}