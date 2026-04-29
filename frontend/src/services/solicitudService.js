import axios from 'axios'

const BASE_URL = 'http://localhost:8080/solicitudes'

export const obtenerConvocatoriaAbierta = () =>
  axios.get(`${BASE_URL}/convocatoria-abierta`)

export const obtenerOfertas = (convocatoriaId) =>
  axios.get(`${BASE_URL}/ofertas`, { params: { convocatoriaId } })

export const obtenerVerSolicitud = (estudianteId) =>
  axios.get(`${BASE_URL}/ver-solicitud/${estudianteId}`)

export const crearSolicitud = (estudianteId, convocatoriaId, ofertaIds) =>
  axios.post(`${BASE_URL}`, { estudianteId, convocatoriaId, ofertaIds })

export const guardarNotas = (estudianteId, notas) =>
  axios.put(`${BASE_URL}/estudiante/${estudianteId}/notas`, notas)