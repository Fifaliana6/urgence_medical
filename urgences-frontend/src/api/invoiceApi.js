import axiosClient from './axiosClient';

export async function getFactureParVisite(visitId) {
  const response = await axiosClient.get(`/invoices/visit/${visitId}`);
  return response.data;
}

export async function marquerFacturePayee(invoiceId) {
  const response = await axiosClient.put(`/invoices/${invoiceId}/paiement`);
  return response.data;
}

export async function telechargerFacturePdf(invoiceId) {
  const response = await axiosClient.get(`/invoices/${invoiceId}/pdf`, { responseType: 'blob' });
  return response.data;
}