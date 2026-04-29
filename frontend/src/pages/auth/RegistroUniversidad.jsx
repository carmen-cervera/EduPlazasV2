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
  const [error, setError] = useState('')
  const [exito, setExito] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    obtenerUniversidades()
      .then(res => setUniversidades(res.data))
      .catch(() => setError('Error al cargar las universidades'))
  }, [])

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async () => {
    if (form.password !== form.confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
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
        <p className={styles.exito}>✅ Registro completado. Redirigiendo...</p>
      </div>
    </div>
  )

  return (
    <div className={styles.fondo} style={{ backgroundImage: "url('https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=1600')" }}>
      <div className={styles.card}>
        <img
          src={logo}
          alt="EduPlazas"
          className={styles.logoImg}
          onClick={() => navigate('/')}
        />

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
            <input className={styles.input} type="email" name="emailInstitucional" value={form.emailInstitucional} onChange={handleChange} />
          </div>
          <div>
            <label className={styles.label}>Universidad</label>
            <select className={styles.input} name="universidadId" value={form.universidadId} onChange={handleChange}>
              <option value="">Selecciona una universidad</option>
              {universidades.map(u => (
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