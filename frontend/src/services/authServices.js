import axios from 'axios';

const BASE_URL = 'http://localhost:8080/auth';

export const registrarEstudiante = (datos) =>
    axios.post(`${BASE_URL}/registro/estudiante`, datos);

export const registrarUniversidad = (datos) =>
    axios.post(`${BASE_URL}/registro/universidad`, datos);

export const login = (datos) =>
    axios.post(`${BASE_URL}/login`, datos);

export const obtenerUniversidades = () =>
    axios.get(`${BASE_URL}/universidades`);