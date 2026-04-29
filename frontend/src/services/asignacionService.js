import axios from 'axios'

const BASE_URL = 'http://localhost:8080/asignaciones'

export const obtenerResultados = () =>
  axios.get(`${BASE_URL}`)

export const obtenerMiAsignacion = (estudianteId) =>
  axios.get(`${BASE_URL}/estudiante/${estudianteId}`)

export const obtenerResultadoEstudiante = (estudianteId) =>
  axios.get(`${BASE_URL}/estudiante/${estudianteId}`)

export const obtenerTablaOferta = (ofertaId) =>
  axios.get(`${BASE_URL}/tabla-oferta/${ofertaId}`)