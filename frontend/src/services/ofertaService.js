import axios from 'axios'

const BASE_URL = 'http://localhost:8080/ofertas'

export const publicarOferta = (usuarioId, grado, plazas) =>
  axios.post(BASE_URL, { usuarioId, grado, plazas })

