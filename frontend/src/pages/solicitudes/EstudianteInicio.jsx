import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerConvocatoriaAbierta, obtenerVerSolicitud } from '../../services/solicitudService'
import styles from './EstudianteInicio.module.css'
import logo from '../../assets/LogoPequeño_FondoAzul_SinGorro.png'
import avatar from '../../assets/avatar.png'

function EstudianteInicio() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))
  const [convocatoria, setConvocatoria] = useState(null)
  const [solicitud, setSolicitud] = useState(null)

  useEffect(() => {
    cargarDatos()
  }, [])

  const cargarDatos = async () => {
    try {
      const resConvocatoria = await obtenerConvocatoriaAbierta()
      setConvocatoria(resConvocatoria.data)
    } catch {
      setConvocatoria(null)
    }

    try {
      const resSolicitud = await obtenerVerSolicitud(usuario.id)
      setSolicitud(resSolicitud.data)
    } catch {
      setSolicitud(null)
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
        <h1 className={styles.tituloHeader}>Bienvenid@</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <img src={avatar} alt="EduPlazas" className={styles.avatar} />
            <p className={styles.email}>{usuario?.email}</p>
          </div>

          <div className={styles.menu}>
            <button className={styles.button} onClick={() => navigate('/estudiante/grados')}>
              Explorar grados
            </button>
            <button className={styles.button} onClick={() => navigate('/estudiante/solicitud')}>
              Nueva solicitud
            </button>
            <button className={styles.button} onClick={() => navigate('/estudiante/ver-solicitud')}>
              Mis solicitudes
            </button>
          </div>

          <button className={styles.button} onClick={cerrarSesion}>
            Log out
          </button>
        </aside>

        <div className={styles.card}>
          <h2 className={styles.cardTitle}>Convocatoria abierta</h2>
          {convocatoria ? (
            <div className={styles.cardContent}>
              <p className={styles.mainText}>{convocatoria.cursoAcademico}</p>
              <div className={styles.datesRow}>
                <p className={styles.subText}>
                  <strong>Inicio:</strong> {convocatoria.fechaApertura}
                </p>
                <span className={styles.separator}>|</span>
                <p className={styles.subText}>
                  <strong>Fin:</strong> {convocatoria.fechaCierreConvocatoria}
                </p>
              </div>
            </div>
          ) : (
            <p className={styles.subText}>No hay ninguna convocatoria abierta</p>
          )}
        </div>
      </div>
    </div>
  )
}

export default EstudianteInicio