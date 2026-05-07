import axios from './axiosAuth'

const BASE_URL = 'http://localhost:8080/admin/convocatorias'

export const crearConvocatoria = (cursoAcademico, fechaApertura, fechaCierre) =>
  axios.post(BASE_URL, { cursoAcademico, fechaApertura, fechaCierre })

export const obtenerConvocatorias = () =>
  axios.get(BASE_URL)