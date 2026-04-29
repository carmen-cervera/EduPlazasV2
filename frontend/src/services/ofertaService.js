import axios from 'axios'

const BASE_URL = 'http://localhost:8080/ofertas'

export const publicarOferta = (representanteId, grado, totalPlazas, criterios) =>
  axios.post(BASE_URL, { representanteId, grado, totalPlazas, criterios })

export const obtenerOfertasMiUniversidad = (representanteId) =>
  axios.get(`${BASE_URL}/mi-universidad`, { params: { representanteId } })

export const obtenerTodasOfertas = () =>
  axios.get(BASE_URL)