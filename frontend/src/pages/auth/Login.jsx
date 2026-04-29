import { useState } from 'react'
import { login } from '../../services/authService'
import { useNavigate } from 'react-router-dom'
import styles from './Login.module.css'
import logo from '../../assets/LogoGrande_FondoBlanco.png'

function Login({ rol }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const esEstudiante = rol === 'ESTUDIANTE'

  const handleSubmit = async () => {
    try {
      const response = await login({ email, password })
      const usuario = response.data
      localStorage.setItem('usuario', JSON.stringify(usuario))
  
      if (usuario.rol === 'ESTUDIANTE' || (usuario.rol === 'ADMIN' && esEstudiante)) {
        navigate('/estudiante/inicio')
      } else if (usuario.rol === 'UNIVERSIDAD' || (usuario.rol === 'ADMIN' && !esEstudiante)) {
        navigate('/universidad/inicio')
      }
    } catch (err) {
      setError('Usuario o contraseña incorrectos')
    }
  }

  return (
    <div
      className={styles.fondo}
      style={{
        backgroundImage: esEstudiante
          ? "url('https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=1600')"
          : "url('https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=1600')"
      }}>

      <div className={styles.card}>
        <img
          src={logo}
          alt="EduPlazas"
          className={styles.logoImg}
          onClick={() => navigate('/')}
        />

        <h2 className={styles.titulo}>Log in:</h2>
        <p className={styles.rol}>{esEstudiante ? 'Estudiantes' : 'Universidades'}</p>

        <input
          className={styles.input}
          type="email"
          placeholder="Usuario"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          className={styles.input}
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        {error && <p className={styles.error}>{error}</p>}

        <button className={styles.btnLogin} onClick={handleSubmit}>
          Log in
        </button>
        <button
          className={styles.btnSignin}
          onClick={() => navigate(esEstudiante ? '/registro/estudiante' : '/registro/universidad')}>
          Sign in
        </button>
      </div>
    </div>
  )
}

export default Login