import axiosClient from './axiosClient';

export async function getExamsEnAttente() {
  const response = await axiosClient.get('/exams/attente');
  return response.data;
}

export async function saisirResultatExamen(examId, resultat) {
  const response = await axiosClient.put(`/exams/${examId}/resultat`, { resultat });
  return response.data;
}

export async function telechargerRapportExamen(examId) {
  const response = await axiosClient.get(`/exams/${examId}/rapport`, { responseType: 'blob' });
  return response.data;
}