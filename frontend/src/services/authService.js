import axios from './axiosAuth'

const BASE_URL = 'http://localhost:8080/auth';

export const registrarEstudiante = (datos) =>
    axios.post(`${BASE_URL}/registro/estudiante`, datos);

export const registrarUniversidad = (datos) =>
    axios.post(`${BASE_URL}/registro/universidad`, datos);

export const login = async (datos) => {
    const res = await axios.post(`${BASE_URL}/login`, datos);
    if (res.data.token) {
        localStorage.setItem('token', res.data.token);
    }
    return res;
};

export const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
};

export const obtenerUniversidades = () =>
    axios.get(`${BASE_URL}/universidades`);