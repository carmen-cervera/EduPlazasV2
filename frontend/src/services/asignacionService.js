import axios from 'axios'

const BASE_URL = 'http://localhost:8080/asignaciones'

export const obtenerResultados = () =>
  axios.get(`${BASE_URL}`)

export const procesarAsignaciones = (universidadId) =>
  axios.post(`${BASE_URL}/procesar`, null, { params: { universidadId } })

export const obtenerMiAsignacion = (usuarioId) =>
  axios.get(`${BASE_URL}/estudiante/${usuarioId}`)

export const obtenerResultadoEstudiante = (usuarioId) =>
  axios.get(`${BASE_URL}/estudiante/${usuarioId}`)
