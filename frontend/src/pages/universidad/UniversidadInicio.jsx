import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerConvocatoriaAbierta } from '../../services/solicitudService'
import { procesarAsignaciones } from '../../services/asignacionService'
import styles from './UniversidadInicio.module.css'

function UniversidadInicio() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))
  const [convocatoria, setConvocatoria] = useState(null)
  const [mensajeProcesar, setMensajeProcesar] = useState('')

  useEffect(() => {
    obtenerConvocatoriaAbierta()
      .then(res => setConvocatoria(res.data))
      .catch(() => setConvocatoria(null))
  }, [])

  const handleProcesarAsignaciones = async () => {
    try {
      await procesarAsignaciones(usuario?.universidad?.id)
      setMensajeProcesar('Asignaciones procesadas correctamente')
    } catch (err) {
      setMensajeProcesar(err.response?.data || 'Error al procesar las asignaciones')
    }
  }

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h1 className={styles.tituloHeader}>Panel Universidad</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <p className={styles.email}>{usuario?.email}</p>
            <p className={styles.universidad}>{usuario?.universidad?.nombre}</p>
          </div>

          <div className={styles.menu}>
            <button className={styles.button} onClick={() => navigate('/universidad/publicar-oferta')}>
              Publicar oferta
            </button>
            <button className={styles.button} onClick={() => navigate('/universidad/mis-ofertas')}>
              Mis ofertas
            </button>
          </div>

          <button className={styles.logoutBtn} onClick={cerrarSesion}>
            Log out
          </button>
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Convocatoria abierta</h2>
            {convocatoria ? (
              <div>
                <p className={styles.mainText}>{convocatoria.nombre}</p>
                <p className={styles.subText}><strong>Inicio:</strong> {convocatoria.fechaInicio}</p>
                <p className={styles.subText}><strong>Fin:</strong> {convocatoria.fechaFin}</p>
              </div>
            ) : (
              <p className={styles.subText}>No hay ninguna convocatoria abierta</p>
            )}
          </div>

          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Procesar asignaciones</h2>
            <p className={styles.subText}>
              Lanza el proceso de asignación de plazas a los estudiantes según sus solicitudes y notas.
            </p>
            <button className={styles.buttonAccion} onClick={handleProcesarAsignaciones}>
              Procesar asignaciones
            </button>
            {mensajeProcesar && <p className={styles.mensaje}>{mensajeProcesar}</p>}
          </div>
        </main>
      </div>
    </div>
  )
}

export default UniversidadInicio
