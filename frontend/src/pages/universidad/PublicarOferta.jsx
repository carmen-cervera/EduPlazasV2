import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { publicarOferta } from '../../services/ofertaService'
import styles from './PublicarOferta.module.css'
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png'

function PublicarOferta() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const [grado, setGrado] = useState('')
  const [totalPlazas, setTotalPlazas] = useState('')
  const [error, setError] = useState('')
  const [exito, setExito] = useState('')
  const [criterios, setCriterios] = useState([{ asignatura: '', peso: '' }])

  const ASIGNATURAS_ESPECIFICAS = [
    'Análisis Musical II', 'Artes Escénicas II', 'Biología', 'Ciencias Generales',
    'Coro y Técnica Vocal II', 'Dibujo Artístico II', 'Dibujo Técnico II',
    'Dibujo Técn. Aplicado a las Artes Plásticas y al Diseño II', 'Diseño',
    'Empresa y Diseño de Modelos de Negocio', 'Física', 'Fundamentos Artísticos',
    'Geografía', 'Geología y CC. Ambientales', 'Griego II', 'Historia de España',
    'Historia de la Filosofía', 'Historia de la Música y de la Danza',
    'Historia del Arte', 'Latín II', 'Literatura Dramática',
    'Matemáticas II', 'Matemáticas Apl. CC. Soc. II', 'Movimientos Culturales y Artísticos',
    'Química', 'Técnicas de Expresión Gráfico-Plástica', 'Tecnología e Ingeniería II',
  ]

  const handleCriterioChange = (index, field, value) => {
    const nuevos = [...criterios]
    nuevos[index][field] = value
    setCriterios(nuevos)
  }

  const añadirCriterio = () => {
    setCriterios([...criterios, { asignatura: '', peso: '' }])
  }

  const eliminarCriterio = (index) => {
    setCriterios(criterios.filter((_, i) => i !== index))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setExito('')

    if (!grado.trim()) {
      setError('El nombre del grado es obligatorio')
      return
    }
    if (!totalPlazas || parseInt(totalPlazas) <= 0) {
      setError('El número de plazas debe ser mayor que 0')
      return
    }

    const criteriosValidos = criterios.filter(c => c.asignatura && c.peso !== '')
    for (const c of criteriosValidos) {
      if (Number(c.peso) !== 0.1 && Number(c.peso) !== 0.2) {
        setError('El peso de cada asignatura debe ser 0.1 o 0.2')
        return
      }
    }

    try {
      await publicarOferta(
        usuario.id,
        grado.trim(),
        parseInt(totalPlazas),
        criteriosValidos.map(c => ({ asignatura: c.asignatura, peso: Number(c.peso) }))
      )
      setExito('Oferta publicada correctamente')
      setGrado('')
      setTotalPlazas('')
      setCriterios([{ asignatura: '', peso: '' }])
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
        <img src={logo} alt="EduPlazas" className={styles.logoImg} onClick={() => navigate('/')} />
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

          <button className={styles.logoutBtn} onClick={cerrarSesion}>
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
                  value={totalPlazas}
                  onChange={e => setTotalPlazas(e.target.value)}
                />
              </div>

              <div className={styles.field}>
                <label className={styles.label}>Asignaturas que ponderan</label>
                {criterios.map((criterio, index) => (
                  <div key={index} className={styles.criterioRow}>
                    <select
                      className={styles.input}
                      value={criterio.asignatura}
                      onChange={e => handleCriterioChange(index, 'asignatura', e.target.value)}
                    >
                      <option value="">Selecciona asignatura</option>
                      {ASIGNATURAS_ESPECIFICAS.map(a => <option key={a} value={a}>{a}</option>)}
                    </select>
                    <select
                      className={styles.input}
                      value={criterio.peso}
                      onChange={e => handleCriterioChange(index, 'peso', e.target.value)}
                    >
                      <option value="">Peso</option>
                      <option value="0.1">0.1</option>
                      <option value="0.2">0.2</option>
                    </select>
                    {criterios.length > 1 && (
                      <button type="button" className={styles.btnEliminar}
                        onClick={() => eliminarCriterio(index)}>✕</button>
                    )}
                  </div>
                ))}
                <button type="button" className={styles.btnAñadir} onClick={añadirCriterio}>
                  + Añadir asignatura
                </button>
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