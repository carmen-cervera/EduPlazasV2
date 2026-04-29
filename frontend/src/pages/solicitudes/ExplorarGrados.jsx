import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerConvocatoriaAbierta, obtenerOfertas } from '../../services/solicitudService'
import styles from './ExplorarGrados.module.css'
import logo from '../../assets/LogoPequeño_FondoAzul_SinGorro.png'
import avatar from '../../assets/avatar.png'

function ExplorarGrados() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const [convocatoria, setConvocatoria] = useState(null)
  const [ofertas, setOfertas] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    cargarDatos()
  }, [])

  const cargarDatos = async () => {
    try {
      setError('')
      const resConvocatoria = await obtenerConvocatoriaAbierta()
      setConvocatoria(resConvocatoria.data)

      const resOfertas = await obtenerOfertas(resConvocatoria.data.id)
      if (Array.isArray(resOfertas.data)) {
        setOfertas(resOfertas.data.map(o => ({
          id: o.id,
          grado: o.grado,
          totalPlazas: o.totalPlazas,
          universidadNombre: o.universidad?.nombre || ''
        })))
      } else {
        setOfertas([])
        setError('No se han podido cargar los grados')
      }
    } catch (err) {
      setError(err.response?.data || 'Error al cargar los grados')
      setOfertas([])
    }
  }

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src={logo} alt="EduPlazas" className={styles.logoImg} onClick={() => navigate('/')} />
        <h1 className={styles.tituloHeader}>Explorar grados</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <img src={avatar} alt="EduPlazas" className={styles.avatar} />
            <p className={styles.email}>{usuario?.email}</p>
          </div>

          <div className={styles.menu}>
            <button className={styles.button} onClick={() => navigate('/estudiante/solicitud')}>
              Nueva solicitud
            </button>
            <button className={styles.button} onClick={() => navigate('/estudiante/inicio')}>
              Volver
            </button>
          </div>

          <button className={styles.button} onClick={cerrarSesion}>
            Log out
          </button>
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            {convocatoria && (
              <p className={styles.convocatoria}>
                <strong>Convocatoria abierta:</strong> {convocatoria.cursoAcademico}
              </p>
            )}

            {error && <p className={styles.error}>{error}</p>}

            <div className={styles.tableContainer}>
              <table className={styles.table}>
                <thead>
                  <tr>
                    <th>Grado</th>
                    <th>Universidad</th>
                    <th>Plazas</th>
                  </tr>
                </thead>
                <tbody>
                  {ofertas.length > 0 ? (
                    ofertas.map(oferta => (
                      <tr key={oferta.id}>
                        <td>{oferta.grado}</td>
                        <td>{oferta.universidadNombre}</td>
                        <td>{oferta.totalPlazas}</td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="3">No hay grados disponibles.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}

export default ExplorarGrados