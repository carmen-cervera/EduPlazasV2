import axios from 'axios'

const BASE_URL = 'http://localhost:8080/asignaciones'

export const obtenerResultados = () =>
  axios.get(⁠ ${BASE_URL} ⁠)

export const procesarAsignaciones = () =>
  axios.post(⁠ ${BASE_URL}/procesar ⁠)

export const obtenerMiAsignacion = (usuarioId) =>
  axios.get(⁠ ${BASE_URL}/estudiante/${usuarioId} ⁠)

export const obtenerResultadoEstudiante = (usuarioId) =>
  axios.get(⁠ ${BASE_URL}/estudiante/${usuarioId} ⁠)
