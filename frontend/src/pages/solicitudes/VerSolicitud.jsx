import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerVerSolicitud } from '../../services/solicitudService'
import styles from './VerSolicitud.module.css'
import logo from '../../assets/LogoPequeño_FondoAzul_SinGorro.png'
import avatar from '../../assets/avatar.png'
import { obtenerMiAsignacion } from '../../services/asignacionService'


function VerSolicitud() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const [asignacion, setAsignacion] = useState(null)
  const [solicitud, setSolicitud] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    cargarSolicitud()
  }, [])

  const cargarSolicitud = async () => {
    try {
      setError('')
      const res = await obtenerVerSolicitud(usuario.id)
      setSolicitud(res.data)
      const resAsignacion = await obtenerMiAsignacion(usuario.id)
      setAsignacion(resAsignacion.data)
    } catch (err) {
      setSolicitud(null)
      setError(err.response?.data || 'No se ha podido cargar la solicitud')
    }
  }

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img
          src={logo}
          alt="EduPlazas"
          className={styles.logoImg}
          onClick={() => navigate('/')}
        />
        <h1 className={styles.tituloHeader}>Mi solicitud</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <img
              src={avatar}
              alt="EduPlazas"
              className={styles.avatar}
            />
            <p className={styles.user}>{usuario?.nombre || usuario?.email}</p>
          </div>

          <div className={styles.menu}>
            <button
              className={styles.button}
              onClick={() => navigate('/estudiante/grados')}
            >
              Explorar grados
            </button>

            <button
              className={styles.button}
              onClick={() => navigate('/estudiante/solicitud')}
            >
              Nueva solicitud
            </button>

            <button 
              className={styles.button} 
              onClick={() => navigate('/estudiante/inicio')}
            >
              Volver
            </button>

          </div>

          <button className={styles.button} onClick={cerrarSesion}>
            Log out
          </button>
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            {error && <p className={styles.error}>{error}</p>}

            {!error && solicitud && (
              <>
                <div className={styles.estadoBox}>
                  <p>
                    <strong>Estado actual:</strong>{' '}
                    <span className={styles.estado}>{solicitud.estado}</span>
                  </p>
                  <p>
                    <strong>Convocatoria:</strong> {solicitud.convocatoria?.nombre}
                  </p>
                </div>

                <h2 className={styles.sectionTitle}>Preferencias enviadas</h2>

                <div className={styles.listaPreferencias}>
                  {solicitud.preferencias?.map((oferta, index) => (
                    <div key={oferta.id} className={styles.preferenciaItem}>
                      <p className={styles.prioridad}>Prioridad {index + 1}</p>
                      <p className={styles.grado}>{oferta.grado}</p>
                      <p className={styles.universidad}>
                        {oferta.universidad?.nombre}
                      </p>
                    </div>
                  ))}
                </div>

                <h2 className={styles.sectionTitle}>Resultado de asignación</h2>
                <div className={styles.resultadoBox}>
                  {!asignacion && (
                    <p>Tu solicitud está siendo procesada, los resultados estarán disponibles próximamente.</p>
                  )}
                  {asignacion && asignacion.estado === 'ASIGNADA' && (
                    <p>Has sido admitido en el Grado en <strong>{asignacion.oferta?.grado}</strong> de la <strong>{asignacion.oferta?.universidad?.nombre}</strong>.</p>
                  )}
                  {asignacion && asignacion.estado !== 'ASIGNADA' && (
                    <p>Lo sentimos, no has obtenido plaza en esta convocatoria.</p>
                  )}
                </div>

              </>
            )}
          </div>
        </main>
      </div>
    </div>
  )
}

export default VerSolicitud