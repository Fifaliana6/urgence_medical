import axiosClient from './axiosClient';

export async function ouvrirConsultation(visitId, payload) {
  const response = await axiosClient.post(`/visits/${visitId}/consultations`, payload);
  return response.data;
}

export async function ajouterPrescription(consultationId, payload) {
  const response = await axiosClient.post(`/consultations/${consultationId}/prescriptions`, payload);
  return response.data;
}

export async function demanderExamen(consultationId, payload) {
  const response = await axiosClient.post(`/consultations/${consultationId}/exams`, payload);
  return response.data;
}