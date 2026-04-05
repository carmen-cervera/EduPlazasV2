import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { publicarOferta } from '../../services/ofertaService'
import styles from './PublicarOferta.module.css'

function PublicarOferta() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const [grado, setGrado] = useState('')
  const [plazas, setPlazas] = useState('')
  const [error, setError] = useState('')
  const [exito, setExito] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setExito('')

    if (!grado.trim()) {
      setError('El nombre del grado es obligatorio')
      return
    }
    if (!plazas || parseInt(plazas) <= 0) {
      setError('El número de plazas debe ser mayor que 0')
      return
    }

    try {
      await publicarOferta(usuario.id, grado.trim(), parseInt(plazas))
      setExito('Oferta publicada correctamente')
      setGrado('')
      setPlazas('')
    } catch (err) {
      setError(err.response?.data || 'Error al publicar la oferta')
    }
  }

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h1 className={styles.tituloHeader}>Publicar oferta</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <p className={styles.email}>{usuario?.email}</p>
            <p className={styles.universidad}>{usuario?.universidad?.nombre}</p>
          </div>

          <div className={styles.menu}>
            <button className={styles.button} onClick={() => navigate('/universidad/mis-ofertas')}>
              Mis ofertas
            </button>
            <button className={styles.button} onClick={() => navigate('/universidad/inicio')}>
              Volver
            </button>
          </div>

          <button className={styles.button} onClick={cerrarSesion}>
            Log out
          </button>
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Nueva oferta de plaza</h2>
            <p className={styles.subText}>
              Universidad: <strong>{usuario?.universidad?.nombre}</strong>
            </p>

            <form onSubmit={handleSubmit} className={styles.form}>
              <div className={styles.field}>
                <label className={styles.label}>Nombre del grado</label>
                <input
                  className={styles.input}
                  type="text"
                  placeholder="Ej: Ingeniería Informática"
                  value={grado}
                  onChange={e => setGrado(e.target.value)}
                />
              </div>

              <div className={styles.field}>
                <label className={styles.label}>Número de plazas</label>
                <input
                  className={styles.input}
                  type="number"
                  placeholder="Ej: 120"
                  min="1"
                  value={plazas}
                  onChange={e => setPlazas(e.target.value)}
                />
              </div>

              {error && <p className={styles.error}>{error}</p>}
              {exito && <p className={styles.exito}>{exito}</p>}

              <button type="submit" className={styles.buttonSubmit}>
                Publicar oferta
              </button>
            </form>
          </div>
        </main>
      </div>
    </div>
  )
}

export default PublicarOferta
