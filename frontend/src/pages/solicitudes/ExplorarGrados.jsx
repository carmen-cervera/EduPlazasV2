import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerConvocatoriaAbierta, obtenerOfertas, obtenerVerSolicitud } from '../../services/solicitudService'
import styles from './ExplorarGrados.module.css'
import logo from '../../assets/LogoPequeño_FondoAzul_SinGorro.png'
import avatar from '../../assets/avatar.png'

const RAMAS = [
  'Arte y Humanidades',
  'Ciencias',
  'Ciencias de la Salud',
  'Ciencias Sociales',
  'Ingeniería y Arquitectura',
]

function ExplorarGrados() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))
  const esInvitado = !usuario

  const [convocatoria, setConvocatoria] = useState(null)
  const [ofertas, setOfertas] = useState([])
  const [error, setError] = useState('')
  const [solicitud, setSolicitud] = useState(null)

  const [filtroGrado, setFiltroGrado] = useState('')
  const [filtroRama, setFiltroRama] = useState('')
  const [filtroUniversidad, setFiltroUniversidad] = useState('')
  const [filtroPlazasMin, setFiltroPlazasMin] = useState('')
  const [filtroPlazasMax, setFiltroPlazasMax] = useState('')

  useEffect(() => {
    cargarDatos()
    if (!esInvitado) comprobarSolicitud()
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
          rama: o.rama || '',
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

  const comprobarSolicitud = async () => {
    try {
      const resSolicitud = await obtenerVerSolicitud(usuario.id)
      setSolicitud(resSolicitud.data)
    } catch {
      setSolicitud(null)
    }
  }

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    localStorage.removeItem('token')
    navigate('/')
  }

  const universidades = [...new Set(ofertas.map(o => o.universidadNombre).filter(Boolean))]

  const ofertasFiltradas = ofertas.filter(o => {
    const gradoOk = o.grado.toLowerCase().includes(filtroGrado.toLowerCase())
    const ramaOk = !filtroRama || o.rama === filtroRama
    const uniOk = !filtroUniversidad || o.universidadNombre === filtroUniversidad
    const minOk = filtroPlazasMin === '' || o.totalPlazas >= Number(filtroPlazasMin)
    const maxOk = filtroPlazasMax === '' || o.totalPlazas <= Number(filtroPlazasMax)
    return gradoOk && ramaOk && uniOk && minOk && maxOk
  })

  const filtrosActivos = [
    filtroGrado      && { label: `Grado: "${filtroGrado}"`,              limpiar: () => setFiltroGrado('') },
    filtroRama       && { label: `Rama: ${filtroRama}`,                  limpiar: () => setFiltroRama('') },
    filtroUniversidad && { label: `Universidad: ${filtroUniversidad}`,   limpiar: () => setFiltroUniversidad('') },
    filtroPlazasMin  && { label: `Plazas mín: ${filtroPlazasMin}`,       limpiar: () => setFiltroPlazasMin('') },
    filtroPlazasMax  && { label: `Plazas máx: ${filtroPlazasMax}`,       limpiar: () => setFiltroPlazasMax('') },
  ].filter(Boolean)

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
            <p className={styles.email}>
              {esInvitado ? 'Usuario invitado' : usuario.email}
            </p>
          </div>

          <div className={styles.menu}>
            {esInvitado ? (
              <button className={styles.button} onClick={() => navigate('/')}>
                Volver al inicio
              </button>
            ) : (
              <>
                {!solicitud && (
                  <button className={styles.button} onClick={() => navigate('/estudiante/solicitud')}>
                    Nueva solicitud
                  </button>
                )}
                {solicitud?.estado === 'BORRADOR' && (
                  <button className={styles.button} onClick={() => navigate('/estudiante/borradores')}>
                    Mi borrador
                  </button>
                )}
                <button className={styles.button} onClick={() => navigate('/estudiante/inicio')}>
                  Volver
                </button>
              </>
            )}
          </div>

          {!esInvitado && (
            <button className={styles.button} onClick={cerrarSesion}>
              Log out
            </button>
          )}
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            {esInvitado && (
              <p className={styles.infoInvitado}>
                Estás consultando los grados publicados como usuario invitado.
              </p>
            )}

            {convocatoria && (
              <p className={styles.convocatoria}>
                <strong>Convocatoria abierta:</strong> {convocatoria.cursoAcademico}
              </p>
            )}

            {error && <p className={styles.error}>{error}</p>}

            {filtrosActivos.length > 0 && (
              <div className={styles.chipsContainer}>
                {filtrosActivos.map((f, i) => (
                  <span key={i} className={styles.chip}>
                    {f.label}
                    <button className={styles.chipX} onClick={f.limpiar}>×</button>
                  </span>
                ))}
              </div>
            )}

            <div className={styles.tableContainer}>
              <table className={styles.table}>
                <thead>
                  <tr>
                    <th>
                      <div className={styles.thLabel}>Grado</div>
                      <input
                        className={styles.filtroTexto}
                        type="text"
                        placeholder="Buscar..."
                        value={filtroGrado}
                        onChange={e => setFiltroGrado(e.target.value)}
                      />
                    </th>
                    <th>
                      <div className={styles.thLabel}>Rama</div>
                      <select
                        className={styles.filtroSelect}
                        value={filtroRama}
                        onChange={e => setFiltroRama(e.target.value)}
                      >
                        <option value="">Todas</option>
                        {RAMAS.map(r => <option key={r} value={r}>{r}</option>)}
                      </select>
                    </th>
                    <th>
                      <div className={styles.thLabel}>Universidad</div>
                      <select
                        className={styles.filtroSelect}
                        value={filtroUniversidad}
                        onChange={e => setFiltroUniversidad(e.target.value)}
                      >
                        <option value="">Todas</option>
                        {universidades.map(u => <option key={u} value={u}>{u}</option>)}
                      </select>
                    </th>
                    <th>
                      <div className={styles.thLabel}>Plazas</div>
                      <div className={styles.filtroRango}>
                        <input
                          className={styles.filtroTexto}
                          type="number"
                          placeholder="Mín"
                          min="0"
                          value={filtroPlazasMin}
                          onChange={e => setFiltroPlazasMin(e.target.value)}
                        />
                        <input
                          className={styles.filtroTexto}
                          type="number"
                          placeholder="Máx"
                          min="0"
                          value={filtroPlazasMax}
                          onChange={e => setFiltroPlazasMax(e.target.value)}
                        />
                      </div>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {ofertasFiltradas.length > 0 ? (
                    ofertasFiltradas.map(oferta => (
                      <tr key={oferta.id}>
                        <td>{oferta.grado}</td>
                        <td>
                          <span className={`${styles.ramaBadge} ${styles['rama_' + oferta.rama.replace(/ /g, '_')]}`}>
                            {oferta.rama}
                          </span>
                        </td>
                        <td>{oferta.universidadNombre}</td>
                        <td>{oferta.totalPlazas}</td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="4">No se encontraron grados.</td>
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