import { useState, useEffect } from 'react'
import { registrarUniversidad, obtenerUniversidades } from '../../services/authService'
import { useNavigate } from 'react-router-dom'
import styles from './Registro.module.css'
import logo from '../../assets/LogoGrande_FondoBlanco.png'

function RegistroUniversidad() {
  const [form, setForm] = useState({
    nombre: '', apellidos: '',
    emailInstitucional: '', password: '', confirmPassword: '',
    dni: '', universidadId: ''
  })
  const [universidades, setUniversidades] = useState([])
  const [universidadesFiltradas, setUniversidadesFiltradas] = useState([])
  const [errorEmail, setErrorEmail] = useState('')
  const [error, setError] = useState('')
  const [exito, setExito] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    obtenerUniversidades()
      .then(res => {
        setUniversidades(res.data)
        setUniversidadesFiltradas(res.data)
      })
      .catch(() => setError('Error al cargar las universidades'))
  }, [])

  const handleEmailChange = (e) => {
    const email = e.target.value
    setForm({ ...form, emailInstitucional: email, universidadId: '' })
    setErrorEmail('')

    if (!email.includes('@')) {
      setUniversidadesFiltradas(universidades)
      return
    }

    const dominio = email.split('@')[1]
    if (!dominio) {
      setUniversidadesFiltradas(universidades)
      return
    }

    const coincidentes = universidades.filter(u => dominio === u.extensionEmail)

    if (coincidentes.length === 1) {
      // autorellena la universidad
      setForm(prev => ({ ...prev, emailInstitucional: email, universidadId: String(coincidentes[0].id) }))
      setUniversidadesFiltradas(coincidentes)
      setErrorEmail('')
    } else if (coincidentes.length === 0 && dominio.includes('.')) {
      setUniversidadesFiltradas([])
      setErrorEmail('Este email no corresponde a ninguna universidad registrada')
    } else {
      setUniversidadesFiltradas(universidades)
    }
  }

  const handleUniversidadChange = (e) => {
    const universidadId = e.target.value
    setForm({ ...form, universidadId })
    setErrorEmail('')

    if (!universidadId) return

    const universidad = universidades.find(u => String(u.id) === universidadId)
    if (!universidad) return

    const email = form.emailInstitucional
    if (email.includes('@')) {
      const dominio = email.split('@')[1]
      if (dominio && dominio !== universidad.extensionEmail) {
        setErrorEmail(`El email debe terminar en @${universidad.extensionEmail}`)
      }
    }
  }

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async () => {
    setError('')
    if (form.password !== form.confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }
    if (errorEmail) {
      setError('Corrige el email antes de continuar')
      return
    }
  
    // Validar que email y universidad coinciden
    if (form.universidadId && form.emailInstitucional.includes('@')) {
      const universidad = universidades.find(u => String(u.id) === String(form.universidadId))
      if (universidad) {
        const dominio = form.emailInstitucional.split('@')[1]
        if (dominio !== universidad.extensionEmail) {
          setError(`El email debe terminar en @${universidad.extensionEmail}`)
          return
        }
      }
    }
  
    try {
      await registrarUniversidad({
        nombre: form.nombre,
        apellidos: form.apellidos,
        emailInstitucional: form.emailInstitucional,
        password: form.password,
        dni: form.dni,
        universidadId: form.universidadId
      })
      setExito(true)
      setTimeout(() => navigate('/universidades/login'), 2000)
    } catch (err) {
      setError(err.response?.data || 'Error al registrarse')
    }
  }

  if (exito) return (
    <div className={styles.fondo} style={{ backgroundImage: "url('https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=1600')" }}>
      <div className={styles.card}>
        <p className={styles.exito}>Registro completado. Redirigiendo...</p>
      </div>
    </div>
  )

  return (
    <div className={styles.fondo} style={{ backgroundImage: "url('https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=1600')" }}>
      <div className={styles.card}>
        <img src={logo} alt="EduPlazas" className={styles.logoImg} onClick={() => navigate('/')} />

        <h2 className={styles.titulo}>Sign in:</h2>
        <p className={styles.rol}>Universidades</p>

        <div className={styles.grid}>
          <div>
            <label className={styles.label}>Nombre</label>
            <input className={styles.input} type="text" name="nombre" value={form.nombre} onChange={handleChange} />
          </div>
          <div>
            <label className={styles.label}>Apellidos</label>
            <input className={styles.input} type="text" name="apellidos" value={form.apellidos} onChange={handleChange} />
          </div>
          <div>
            <label className={styles.label}>DNI</label>
            <input className={styles.input} type="text" name="dni" value={form.dni} onChange={handleChange} />
          </div>
          <div>
            <label className={styles.label}>Email universitario</label>
            <input
              className={styles.input}
              type="email"
              name="emailInstitucional"
              value={form.emailInstitucional}
              onChange={handleEmailChange}
            />
            {errorEmail && <p className={styles.error}>{errorEmail}</p>}
          </div>
          <div>
            <label className={styles.label}>Universidad</label>
            <select
              className={styles.input}
              name="universidadId"
              value={form.universidadId}
              onChange={handleUniversidadChange}
            >
              <option value="">Selecciona una universidad</option>
              {universidadesFiltradas.map(u => (
                <option key={u.id} value={u.id}>{u.nombre}</option>
              ))}
            </select>
          </div>
          <div>
            <label className={styles.label}>Contraseña</label>
            <input className={styles.input} type="password" name="password" value={form.password} onChange={handleChange} />
          </div>
          <div>
            <label className={styles.label}>Repetir contraseña</label>
            <input className={styles.input} type="password" name="confirmPassword" value={form.confirmPassword} onChange={handleChange} />
          </div>
        </div>

        {error && <p className={styles.error}>{error}</p>}

        <button className={styles.btnPrimario} onClick={handleSubmit}>Registrarse</button>
        <button className={styles.btnSecundario} onClick={() => navigate('/universidades/login')}>Volver al login</button>
      </div>
    </div>
  )
}

export default RegistroUniversidad