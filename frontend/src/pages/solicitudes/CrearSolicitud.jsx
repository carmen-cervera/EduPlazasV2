import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { obtenerConvocatoriaAbierta, obtenerOfertas, crearSolicitud, guardarNotas, guardarBorradorSolicitud, obtenerVerSolicitud, obtenerNotas } from '../../services/solicitudService'
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

      try {
        const resSolicitud = await obtenerVerSolicitud(usuario.id)
        const solicitudExistente = resSolicitud.data

        if (solicitudExistente.estado === 'BORRADOR' && Array.isArray(solicitudExistente.preferencias)) {
          const preferenciasOrdenadas = [...solicitudExistente.preferencias]
            .sort((a, b) => a.ordenPreferencia - b.ordenPreferencia)

          setPrioridad1(preferenciasOrdenadas[0]?.oferta?.id ? String(preferenciasOrdenadas[0].oferta.id) : '')
          setPrioridad2(preferenciasOrdenadas[1]?.oferta?.id ? String(preferenciasOrdenadas[1].oferta.id) : '')
          setPrioridad3(preferenciasOrdenadas[2]?.oferta?.id ? String(preferenciasOrdenadas[2].oferta.id) : '')
        }

        if (solicitudExistente.estado !== 'BORRADOR') {
          navigate('/estudiante/ver-solicitud')
        }
      } catch {
        // Si no tiene solicitud, no hacemos nada
      }

      try {
        const resNotas = await obtenerNotas(usuario.id)
        const notasGuardadas = resNotas.data

        const nuevasNotas = notas.map(notaActual => {
          const notaGuardada = notasGuardadas.find(n => n.asignatura === notaActual.asignatura)
          return {
            ...notaActual,
            nota: notaGuardada ? String(notaGuardada.nota) : ''
          }
        })

        setNotas(nuevasNotas)

        const notasEspecificas = notasGuardadas.filter(n =>
          n.asignatura !== 'Bachillerato' &&
          n.asignatura !== 'Lengua Castellana' &&
          n.asignatura !== 'Historia de España' &&
          n.asignatura !== 'Inglés' &&
          n.asignatura !== 'Matemáticas'
        )

        if (notasEspecificas[0]) {
          setEspecifica1({
            asignatura: notasEspecificas[0].asignatura,
            nota: String(notasEspecificas[0].nota)
          })
        }

        if (notasEspecificas[1]) {
          setEspecifica2({
            asignatura: notasEspecificas[1].asignatura,
            nota: String(notasEspecificas[1].nota)
          })
        }

      } catch {
        // Si no hay notas guardadas, dejamos los campos vacíos.
      }

    } catch (err) {
      setError(err.response?.data || 'Error al cargar los datos')
      setOfertas([])
    }
  }


  const obtenerIdsSeleccionados = () => {
    const idsSeleccionados = [prioridad1, prioridad2, prioridad3].filter(id => id !== '')

    if (idsSeleccionados.length === 0) {
      setError('Debes seleccionar al menos una opción')
      return null
    }

    if (new Set(idsSeleccionados).size !== idsSeleccionados.length) {
      setError('No puedes repetir el mismo grado en varias prioridades')
      return null
    }

    return idsSeleccionados.map(id => Number(id))
  }

const handleGuardarBorrador = async () => {
  try {
    setError('')
    setMensaje('')

    const idsSeleccionados = obtenerIdsSeleccionados()
    if (!idsSeleccionados) return

    const todasLasNotas = [
      ...notas
        .filter(n => n.nota !== '')
        .map(n => ({ asignatura: n.asignatura, nota: Number(n.nota) })),
    ]

    if (especifica1.asignatura && especifica1.nota !== '') {
      todasLasNotas.push({
        asignatura: especifica1.asignatura,
        nota: Number(especifica1.nota)
      })
    }

    if (especifica2.asignatura && especifica2.nota !== '') {
      todasLasNotas.push({
        asignatura: especifica2.asignatura,
        nota: Number(especifica2.nota)
      })
    }

    if (todasLasNotas.length > 0) {
      await guardarNotas(usuario.id, todasLasNotas)
    }

    await guardarBorradorSolicitud(
      usuario.id,
      convocatoria.id,
      idsSeleccionados
    )

    setMensaje('Borrador guardado correctamente')

    setTimeout(() => {
      navigate('/estudiante/inicio')
    }, 1000)

  } catch (err) {
    setError(err.response?.data || 'Error al guardar el borrador')
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

      const notasInvalidas = notas.some(n =>
        n.nota === '' ||
        isNaN(n.nota) ||
        Number(n.nota) < 0 ||
        Number(n.nota) > 10
      )

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

      const idsSeleccionados = obtenerIdsSeleccionados()
      if (!idsSeleccionados) return

      const todasLasNotas = [
        ...notas.map(n => ({ asignatura: n.asignatura, nota: Number(n.nota) })),
        { asignatura: especifica1.asignatura, nota: Number(especifica1.nota) },
        { asignatura: especifica2.asignatura, nota: Number(especifica2.nota) },
      ]

      await guardarNotas(usuario.id, todasLasNotas)

      await crearSolicitud(
        usuario.id,
        convocatoria.id,
        idsSeleccionados
      )

      setMensaje('Solicitud enviada correctamente')

      setTimeout(() => {
        navigate('/estudiante/inicio')
      }, 1000)

    } catch (err) {
      setError(err.response?.data || 'Error al enviar la solicitud')
    }
  }


  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  const obtenerTextoOferta = (idOferta) => {
    const oferta = ofertas.find(o => String(o.id) === String(idOferta))
    return oferta ? `${oferta.grado} (${oferta.universidadNombre})` : ''
  }

  const BuscadorOferta = ({ label, value, onChange }) => {
    const [textoBusqueda, setTextoBusqueda] = useState(obtenerTextoOferta(value))
    const [mostrarOpciones, setMostrarOpciones] = useState(false)

    useEffect(() => {
      setTextoBusqueda(obtenerTextoOferta(value))
    }, [value, ofertas])

    const ofertasFiltradas = ofertas.filter(oferta => {
      const texto = `${oferta.grado} ${oferta.universidadNombre}`.toLowerCase()
      return texto.includes(textoBusqueda.toLowerCase())
    })

    const seleccionarOferta = (oferta) => {
      onChange(String(oferta.id))
      setTextoBusqueda(`${oferta.grado} (${oferta.universidadNombre})`)
      setMostrarOpciones(false)
    }

    const limpiarSeleccion = () => {
      onChange('')
      setTextoBusqueda('')
      setMostrarOpciones(false)
    }


    return (
      <div className={styles.buscadorContainer}>
        <label className={styles.label}>{label}</label>

        <div className={styles.inputWrapper}>
          <input
            className={styles.select}
            type="text"
            placeholder="Selecciona o escribe un grado"
            value={textoBusqueda}
            onChange={(e) => {
              setTextoBusqueda(e.target.value)
              onChange('')
              setMostrarOpciones(true)
            }}
            onFocus={() => setMostrarOpciones(true)}
          />

          {textoBusqueda && (
            <button
              type="button"
              className={styles.clearButton}
              onClick={limpiarSeleccion}
            >
              ×
            </button>
          )}
        </div>

        {mostrarOpciones && (
          <div className={styles.opcionesBuscador}>
            {ofertasFiltradas.length > 0 ? (
              ofertasFiltradas.map(oferta => (
                <button
                  type="button"
                  key={oferta.id}
                  className={styles.opcionBuscador}
                  onClick={() => seleccionarOferta(oferta)}
                >
                  <strong>{oferta.grado}</strong>
                  <span>{oferta.universidadNombre}</span>
                </button>
              ))
            ) : (
              <p className={styles.sinResultados}>No se encontraron grados</p>
            )}
          </div>
        )}
      </div>
    )
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
        <h1 className={styles.tituloHeader}>Nueva solicitud</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <img src={avatar} alt="EduPlazas" className={styles.avatar} />
            <p className={styles.email}>{usuario?.email}</p>
          </div>

          <button className={styles.button} onClick={() => navigate('/estudiante/inicio')}>
            Volver
          </button>

          <button className={styles.button} onClick={cerrarSesion}>
            Log out
          </button>
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
                    type="number"
                    min="0"
                    max="10"
                    step="0.01"
                    placeholder="0 - 10"
                    value={nota.nota}
                    onChange={(e) => handleNotaChange(index, e.target.value)}
                  />
                </div>
              ))}

              <div>
                <label className={styles.label}>Materia específica 1:</label>
                <select
                  className={styles.select}
                  value={especifica1.asignatura}
                  onChange={(e) => setEspecifica1({ ...especifica1, asignatura: e.target.value })}
                >
                  <option value="">Selecciona asignatura</option>
                  {ASIGNATURAS_ESPECIFICAS.map(a => (
                    <option key={a} value={a}>{a}</option>
                  ))}
                </select>

                <input
                  className={styles.select}
                  type="number"
                  min="0"
                  max="10"
                  step="0.01"
                  placeholder="0 - 10"
                  value={especifica1.nota}
                  onChange={(e) => setEspecifica1({ ...especifica1, nota: e.target.value })}
                />
              </div>

              <div>
                <label className={styles.label}>Materia específica 2:</label>
                <select
                  className={styles.select}
                  value={especifica2.asignatura}
                  onChange={(e) => setEspecifica2({ ...especifica2, asignatura: e.target.value })}
                >
                  <option value="">Selecciona asignatura</option>
                  {ASIGNATURAS_ESPECIFICAS.map(a => (
                    <option key={a} value={a}>{a}</option>
                  ))}
                </select>

                <input
                  className={styles.select}
                  type="number"
                  min="0"
                  max="10"
                  step="0.01"
                  placeholder="0 - 10"
                  value={especifica2.nota}
                  onChange={(e) => setEspecifica2({ ...especifica2, nota: e.target.value })}
                />
              </div>

              <BuscadorOferta
                label="Grado de prioridad 1:"
                value={prioridad1}
                onChange={setPrioridad1}
              />

              <BuscadorOferta
                label="Grado de prioridad 2:"
                value={prioridad2}
                onChange={setPrioridad2}
              />

              <BuscadorOferta
                label="Grado de prioridad 3:"
                value={prioridad3}
                onChange={setPrioridad3}
              />
            </div>

            <div className={styles.footerButtons}>
              <button className={styles.primaryButton} onClick={handleGuardarBorrador}>
                Guardar como borrador
              </button>
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