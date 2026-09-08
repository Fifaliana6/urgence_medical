import axiosClient from './axiosClient';

export async function loginRequest(email, password) {
  const response = await axiosClient.post('/auth/login', { email, password });
  return response.data;
}

export async function registerRequest(payload) {
  const response = await axiosClient.post('/auth/register', payload);
  return response.data;
}