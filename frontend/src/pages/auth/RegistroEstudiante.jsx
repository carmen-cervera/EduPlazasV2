import { useState } from 'react'
import { registrarEstudiante } from '../../services/authService'
import { useNavigate } from 'react-router-dom'
import styles from './Registro.module.css'
import logo from '../../assets/LogoGrande_FondoBlanco.png'

function RegistroEstudiante() {
  const [form, setForm] = useState({
    nombre: '', apellidos: '', email: '',
    password: '', confirmPassword: '', dni: '', idEvau: ''
  })
  const [error, setError] = useState('')
  const [exito, setExito] = useState(false)
  const navigate = useNavigate()

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async () => {
    if (form.password !== form.confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }
    try {
      await registrarEstudiante({
        nombre: form.nombre, apellidos: form.apellidos,
        email: form.email, password: form.password,
        dni: form.dni, idEvau: form.idEvau
      })
      setExito(true)
      setTimeout(() => navigate('/estudiantes/login'), 2000)
    } catch (err) {
      setError(err.response?.data || 'Error al registrarse')
    }
  }

  if (exito) return (
    <div className={styles.fondo} style={{ backgroundImage: "url('https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=1600')" }}>
      <div className={styles.card}>
        <p className={styles.exito}>Registro completado. Redirigiendo...</p>
      </div>
    </div>
  )

  return (
    <div className={styles.fondo} style={{ backgroundImage: "url('https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=1600')" }}>
      <div className={styles.card}>
      <img
        src={logo}
        alt="EduPlazas"
        className={styles.logoImg}
        onClick={() => navigate('/')}
        />

        <h2 className={styles.titulo}>Sign in:</h2>
        <p className={styles.rol}>Estudiantes</p>

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
            <label className={styles.label}>Email</label>
            <input className={styles.input} type="email" name="email" value={form.email} onChange={handleChange} />
          </div>
          <div>
            <label className={styles.label}>DNI</label>
            <input className={styles.input} type="text" name="dni" value={form.dni} onChange={handleChange} />
          </div>
          <div>
            <label className={styles.label}>Contraseña</label>
            <input className={styles.input} type="password" name="password" value={form.password} onChange={handleChange} />
          </div>
          <div>
            <label className={styles.label}>Repetir contraseña</label>
            <input className={styles.input} type="password" name="confirmPassword" value={form.confirmPassword} onChange={handleChange} />
          </div>
          <div className={styles.fullWidth}>
            <label className={styles.label}>ID EvAU</label>
            <input className={styles.input} type="text" name="idEvau" value={form.idEvau} onChange={handleChange} />
          </div>
        </div>

        {error && <p className={styles.error}>{error}</p>}

        <button className={styles.btnPrimario} onClick={handleSubmit}>Registrarse</button>
        <button className={styles.btnSecundario} onClick={() => navigate('/estudiantes/login')}>Volver al login</button>
      </div>
    </div>
  )
}

export default RegistroEstudiante