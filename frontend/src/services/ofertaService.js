import axios from 'axios'

const BASE_URL = 'http://localhost:8080/ofertas'

export const publicarOferta = (usuarioId, grado, plazas, criterios) =>
  axios.post(BASE_URL, { usuarioId, grado, plazas, criterios })

export const obtenerOfertasMiUniversidad = (usuarioId) =>
  axios.get(`${BASE_URL}/mi-universidad`, { params: { usuarioId } })
