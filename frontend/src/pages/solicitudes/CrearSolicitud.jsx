import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerConvocatoriaAbierta, obtenerOfertas, crearSolicitud, guardarNotas } from '../../services/solicitudService'
import styles from './CrearSolicitud.module.css'
import logo from '../../assets/LogoPequeño_FondoAzul_SinGorro.png'
import avatar from '../../assets/avatar.png'

function CrearSolicitud() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const [convocatoria, setConvocatoria] = useState(null)
  const [ofertas, setOfertas] = useState([])
  const [prioridad1, setPrioridad1] = useState('')
  const [prioridad2, setPrioridad2] = useState('')
  const [prioridad3, setPrioridad3] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')
  const [notas, setNotas] = useState([
    { asignatura: 'Bachillerato', nota: '' },
    { asignatura: 'Lengua Castellana', nota: '' },
    { asignatura: 'Historia de España', nota: '' },
    { asignatura: 'Inglés', nota: '' },
    { asignatura: 'Matemáticas', nota: '' },
  ])
  const [especifica1, setEspecifica1] = useState({ asignatura: '', nota: '' })
  const [especifica2, setEspecifica2] = useState({ asignatura: '', nota: '' })

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

  useEffect(() => {
    cargarDatos()
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
          universidadNombre: o.universidad?.nombre || ''
        })))
      } else {
        setOfertas([])
        setError('No se han podido cargar las ofertas correctamente')
      }
    } catch (err) {
      setError(err.response?.data || 'Error al cargar los datos')
      setOfertas([])
    }
  }

  const handleNotaChange = (index, value) => {
    const nuevasNotas = [...notas]
    nuevasNotas[index].nota = value
    setNotas(nuevasNotas)
  }

  const handleEnviarSolicitud = async () => {
    try {
      setError('')
      setMensaje('')

      const notasInvalidas = notas.some(n => n.nota === '' || isNaN(n.nota) || Number(n.nota) < 0 || Number(n.nota) > 10)
      if (notasInvalidas) {
        setError('Introduce todas las notas (entre 0 y 10)')
        return
      }
      if (!especifica1.asignatura || especifica1.nota === '' || Number(especifica1.nota) < 0 || Number(especifica1.nota) > 10) {
        setError('Introduce la asignatura y nota de Materia específica 1 (entre 0 y 10)')
        return
      }
      if (!especifica2.asignatura || especifica2.nota === '' || Number(especifica2.nota) < 0 || Number(especifica2.nota) > 10) {
        setError('Introduce la asignatura y nota de Materia específica 2 (entre 0 y 10)')
        return
      }

      const idsSeleccionados = [prioridad1, prioridad2, prioridad3].filter(id => id !== '')
      if (idsSeleccionados.length === 0) {
        setError('Debes seleccionar al menos una opción')
        return
      }
      if (new Set(idsSeleccionados).size !== idsSeleccionados.length) {
        setError('No puedes repetir el mismo grado en varias prioridades')
        return
      }

      const todasLasNotas = [
        ...notas.map(n => ({ asignatura: n.asignatura, nota: Number(n.nota) })),
        { asignatura: especifica1.asignatura, nota: Number(especifica1.nota) },
        { asignatura: especifica2.asignatura, nota: Number(especifica2.nota) },
      ]
      await guardarNotas(usuario.id, todasLasNotas)

      await crearSolicitud(
        usuario.id,
        convocatoria.id,
        idsSeleccionados.map(id => Number(id))
      )

      setMensaje('Solicitud enviada correctamente')
      setPrioridad1('')
      setPrioridad2('')
      setPrioridad3('')
    } catch (err) {
      setError(err.response?.data || 'Error al enviar la solicitud')
    }
  }

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  const renderOpciones = () => (
    <>
      <option value="">Selecciona un grado</option>
      {ofertas.map(oferta => (
        <option key={oferta.id} value={oferta.id}>
          {oferta.grado} ({oferta.universidadNombre})
        </option>
      ))}
    </>
  )

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src={logo} alt="EduPlazas" className={styles.logoImg} onClick={() => navigate('/')} />
        <h1 className={styles.tituloHeader}>Nueva solicitud</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <img src={avatar} alt="EduPlazas" className={styles.avatar} />
            <p className={styles.email}>{usuario?.email}</p>
          </div>
          <button className={styles.button} onClick={() => navigate('/estudiante/inicio')}>Volver</button>
          <button className={styles.button} onClick={cerrarSesion}>Log out</button>
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            <h2 className={styles.sectionTitle}>Selección</h2>

            {error && <p className={styles.error}>{error}</p>}
            {mensaje && <p className={styles.success}>{mensaje}</p>}

            <div className={styles.formulario}>
              {convocatoria && (
                <p className={styles.convocatoria}>
                  <strong>Convocatoria abierta:</strong> {convocatoria.cursoAcademico}
                </p>
              )}

              <h3 className={styles.label}>Notas EvAU</h3>
              {notas.map((nota, index) => (
                <div key={index}>
                  <label className={styles.label}>{nota.asignatura}:</label>
                  <input
                    className={styles.select}
                    type="number" min="0" max="10" step="0.01"
                    placeholder="0 - 10"
                    value={nota.nota}
                    onChange={(e) => handleNotaChange(index, e.target.value)}
                  />
                </div>
              ))}

              <div>
                <label className={styles.label}>Materia específica 1:</label>
                <select className={styles.select} value={especifica1.asignatura}
                  onChange={(e) => setEspecifica1({ ...especifica1, asignatura: e.target.value })}>
                  <option value="">Selecciona asignatura</option>
                  {ASIGNATURAS_ESPECIFICAS.map(a => <option key={a} value={a}>{a}</option>)}
                </select>
                <input className={styles.select} type="number" min="0" max="10" step="0.01"
                  placeholder="0 - 10" value={especifica1.nota}
                  onChange={(e) => setEspecifica1({ ...especifica1, nota: e.target.value })} />
              </div>

              <div>
                <label className={styles.label}>Materia específica 2:</label>
                <select className={styles.select} value={especifica2.asignatura}
                  onChange={(e) => setEspecifica2({ ...especifica2, asignatura: e.target.value })}>
                  <option value="">Selecciona asignatura</option>
                  {ASIGNATURAS_ESPECIFICAS.map(a => <option key={a} value={a}>{a}</option>)}
                </select>
                <input className={styles.select} type="number" min="0" max="10" step="0.01"
                  placeholder="0 - 10" value={especifica2.nota}
                  onChange={(e) => setEspecifica2({ ...especifica2, nota: e.target.value })} />
              </div>

              <label className={styles.label}>Grado de prioridad 1:</label>
              <select className={styles.select} value={prioridad1} onChange={(e) => setPrioridad1(e.target.value)}>
                {renderOpciones()}
              </select>

              <label className={styles.label}>Grado de prioridad 2:</label>
              <select className={styles.select} value={prioridad2} onChange={(e) => setPrioridad2(e.target.value)}>
                {renderOpciones()}
              </select>

              <label className={styles.label}>Grado de prioridad 3:</label>
              <select className={styles.select} value={prioridad3} onChange={(e) => setPrioridad3(e.target.value)}>
                {renderOpciones()}
              </select>
            </div>

            <div className={styles.footerButtons}>
              <button className={styles.primaryButton} onClick={handleEnviarSolicitud}>
                Enviar
              </button>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}

export default CrearSolicitud