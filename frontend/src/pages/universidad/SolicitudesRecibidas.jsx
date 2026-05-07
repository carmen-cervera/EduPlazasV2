import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import styles from './SolicitudesRecibidas.module.css'
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png'

function SolicitudesRecibidas() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))
  const [panel, setPanel] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    if (usuario?.id) {
      fetch(`http://localhost:8080/ofertas/panel-universidad?representanteId=${usuario.id}`)
        .then(res => res.json())
        .then(data => Array.isArray(data) ? setPanel(data) : setError('Error al cargar el panel'))
        .catch(() => setError('Error de conexión'))
    }
  }, [])

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  if (!usuario) return null

  const totalSolicitudes = panel.reduce((acc, o) => acc + o.numSolicitudes, 0)
  const gradosLlenos = panel.filter(o => o.numSolicitudes >= o.totalPlazas).length

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src={logo} alt="EduPlazas" className={styles.logoImg}
          onClick={() => navigate('/universidad/inicio')} />
        <h1 className={styles.tituloHeader}>Panel de seguimiento</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div>
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
              <p className={styles.email}>{usuario.email}</p>
              <p className={styles.universidad}>{usuario.universidad?.nombre}</p>
            </div>
            <div className={styles.menu}>
              <button className={styles.button} onClick={() => navigate('/universidad/publicar-oferta')}>
                Publicar oferta
              </button>
              <button className={styles.button} onClick={() => navigate('/universidad/mis-ofertas')}>
                Mis ofertas
              </button>
              <button className={styles.button} onClick={() => navigate('/universidad/inicio')}>
                Volver
              </button>
            </div>
          </div>
          <button className={styles.logoutBtn} onClick={cerrarSesion}>
            Log out
          </button>
        </aside>

        <main className={styles.main}>

          {error && <p className={styles.error}>{error}</p>}

          {/* Resumen global */}
          <div className={styles.resumen}>
            <div className={styles.resumenCard}>
              <span className={styles.resumenNum}>{panel.length}</span>
              <span className={styles.resumenLabel}>Grados publicados</span>
            </div>
            <div className={styles.resumenCard}>
              <span className={styles.resumenNum}>{totalSolicitudes}</span>
              <span className={styles.resumenLabel}>Solicitudes recibidas</span>
            </div>
            <div className={styles.resumenCard}>
              <span className={`${styles.resumenNum} ${gradosLlenos > 0 ? styles.rojo : styles.verde}`}>
                {gradosLlenos}
              </span>
              <span className={styles.resumenLabel}>Grados al 100%</span>
            </div>
          </div>

          {/* Grid de tarjetas */}
          {panel.length === 0 && !error ? (
            <p className={styles.vacio}>No hay ofertas publicadas todavía.</p>
          ) : (
            <div className={styles.grid}>
              {panel.map(oferta => {
                const ocupacion = Math.min(
                  Math.round((oferta.numSolicitudes / oferta.totalPlazas) * 100), 100
                )
                const plazasLibres = Math.max(oferta.totalPlazas - oferta.numSolicitudes, 0)
                const colorBarra = ocupacion >= 100 ? '#e74c3c' : ocupacion >= 75 ? '#f39c12' : '#2ecc71'

                return (
                  <div key={oferta.id} className={styles.card}>
                    <div className={styles.cardHeader}>
                      <h3 className={styles.cardGrado}>{oferta.grado}</h3>
                      {oferta.rama && (
                        <span className={`${styles.ramaBadge} ${styles['rama_' + oferta.rama.replace(/ /g, '_')]}`}>
                          {oferta.rama}
                        </span>
                      )}
                    </div>

                    <div className={styles.metricas}>
                      <div className={styles.metrica}>
                        <span className={styles.metricaNum}>{oferta.totalPlazas}</span>
                        <span className={styles.metricaLabel}>Plazas</span>
                      </div>
                      <div className={styles.metricaDivider} />
                      <div className={styles.metrica}>
                        <span className={styles.metricaNum}>{oferta.numSolicitudes}</span>
                        <span className={styles.metricaLabel}>Solicitudes</span>
                      </div>
                      <div className={styles.metricaDivider} />
                      <div className={styles.metrica}>
                        <span className={styles.metricaNum}>
                          {oferta.notaCorteProvisional > 0 ? oferta.notaCorteProvisional : '—'}
                        </span>
                        <span className={styles.metricaLabel}>Nota de corte<br />  provisional</span>
                      </div>
                    </div>

                    <div className={styles.barraContainer}>
                      <div className={styles.barraHeader}>
                        <span className={styles.barraLabel}>Ocupación</span>
                        <span className={styles.barraPorc}>{ocupacion}%</span>
                      </div>
                      <div className={styles.barraFondo}>
                        <div
                          className={styles.barraRelleno}
                          style={{ width: `${ocupacion}%`, backgroundColor: colorBarra }}
                        />
                      </div>
                      <span className={styles.barraEstado} style={{ color: colorBarra }}>
                        {ocupacion >= 100 ? 'Sin plazas disponibles' : `${plazasLibres} plaza${plazasLibres !== 1 ? 's' : ''} libre${plazasLibres !== 1 ? 's' : ''}`}
                      </span>
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </main>
      </div>
    </div>
  )
}

export default SolicitudesRecibidas