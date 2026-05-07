import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerConvocatoriaAbierta } from '../../services/solicitudService'
import styles from './UniversidadInicio.module.css'
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png'

function UniversidadInicio() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))
  const [convocatoria, setConvocatoria] = useState(null)

  useEffect(() => {
    obtenerConvocatoriaAbierta()
      .then(res => setConvocatoria(res.data))
      .catch(() => setConvocatoria(null))
  }, [])

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src={logo} alt="EduPlazas" className={styles.logoImg} onClick={() => navigate('/')} />
        <h1 className={styles.tituloHeader}>Panel Universidad</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <svg
              className={styles.institutionIcon}
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path d="M12 3 3 8v2h18V8z" />
              <path d="M5 11h2v6H5zM9 11h2v6H9zM13 11h2v6h-2zM17 11h2v6h-2z" />
              <path d="M3 18h18v3H3z" />
            </svg>
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
            <button className={styles.button} onClick={() => navigate('/universidad/solicitudes/${usuario.universidad.id}')}>
              Solicitudes recibidas
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
                <p className={styles.mainText}>{convocatoria.cursoAcademico}</p>
                <p className={styles.subText}>
                  <strong>Inicio:</strong> {convocatoria.fechaApertura}
                </p>
                <p className={styles.subText}>
                  <strong>Fin:</strong> {convocatoria.fechaCierreConvocatoria}
                </p>
              </div>
            ) : (
              <p className={styles.subText}>No hay ninguna convocatoria abierta</p>
            )}
          </div>
        </main>
      </div>
    </div>
  )
}

export default UniversidadInicio