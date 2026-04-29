import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerOfertasMiUniversidad } from '../../services/ofertaService'
import styles from './UniversidadInicio.module.css'
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png'

function MisOfertas() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const [ofertas, setOfertas] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    obtenerOfertasMiUniversidad(usuario.id)
      .then(res => setOfertas(res.data))
      .catch(err => setError(err.response?.data || 'Error al cargar las ofertas'))
  }, [])

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src={logo} alt="EduPlazas" className={styles.logoImg} onClick={() => navigate('/')} />
        <h1 className={styles.tituloHeader}>Mis ofertas publicadas</h1>
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
            <button className={styles.button} onClick={() => navigate('/universidad/inicio')}>
              Volver
            </button>
          </div>

          <button className={styles.logoutBtn} onClick={cerrarSesion}>
            Log out
          </button>
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            {error && <p className={styles.error}>{error}</p>}
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Grado</th>
                  <th>Plazas</th>
                  <th>Convocatoria</th>
                </tr>
              </thead>
              <tbody>
                {ofertas.length > 0 ? (
                  ofertas.map(oferta => (
                    <tr key={oferta.id}>
                      <td>{oferta.grado}</td>
                      <td>{oferta.totalPlazas}</td>
                      <td>{oferta.convocatoria?.cursoAcademico}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="3">No hay ofertas publicadas todavía.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </main>
      </div>
    </div>
  )
}

export default MisOfertas