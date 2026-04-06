import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerResultadoEstudiante } from '../../services/asignacionService'
import styles from './VerSolicitud.module.css'

function VerResultados() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const [asignacion, setAsignacion] = useState(undefined)
  const [error, setError] = useState('')

  useEffect(() => {
    obtenerResultadoEstudiante(usuario.id)
      .then(res => setAsignacion(res.data))
      .catch(err => {
        setError(err.response?.data || 'Error al cargar el resultado')
        setAsignacion(null)
      })
  }, [])

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h1 className={styles.tituloHeader}>Mis resultados</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <p className={styles.email}>{usuario?.nombre || usuario?.email}</p>
          </div>

          <div className={styles.menu}>
            <button className={styles.button} onClick={() => navigate('/estudiante/grados')}>
              Explorar grados
            </button>
            <button className={styles.button} onClick={() => navigate('/estudiante/ver-solicitud')}>
              Mi solicitud
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
            <h2 className={styles.cardTitle}>Resultado de la asignación</h2>

            {error && <p className={styles.error}>{error}</p>}
            {asignacion === undefined && <p className={styles.subText}>Cargando...</p>}
            {asignacion === null && !error && (
              <p className={styles.mainText}>Las asignaciones aún no han sido procesadas.</p>
            )}

            {asignacion && (
              <div className={styles.resultadoBox}>
                <p><strong>Estado:</strong> {asignacion.estado}</p>
                <p><strong>Grado:</strong> {asignacion.oferta?.grado}</p>
                <p><strong>Universidad:</strong> {asignacion.oferta?.universidad?.nombre}</p>
                <p><strong>Nota final:</strong> {asignacion.notaFinal?.toFixed(3)}</p>
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  )
}

export default VerResultados
