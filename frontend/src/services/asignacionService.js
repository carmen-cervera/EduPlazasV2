import axios from 'axios'

const BASE_URL = 'http://localhost:8080/asignaciones'

export const obtenerResultados = () =>
  axios.get(`${BASE_URL}`)

export const procesarAsignaciones = () =>
  axios.post(`${BASE_URL}/procesar`)
