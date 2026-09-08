import axiosClient from './axiosClient';

export async function listerUtilisateurs(statut) {
  const response = await axiosClient.get('/admin/users', { params: statut ? { statut } : {} });
  return response.data;
}

export async function approuverUtilisateur(id) {
  const response = await axiosClient.put(`/admin/users/${id}/approuver`);
  return response.data;
}

export async function rejeterUtilisateur(id) {
  const response = await axiosClient.put(`/admin/users/${id}/rejeter`);
  return response.data;
}

export async function listerAudit(page = 0, taille = 20) {
  const response = await axiosClient.get('/audit', { params: { page, taille } });
  return response.data;
}