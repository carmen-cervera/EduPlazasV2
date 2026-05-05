import { useNavigate } from 'react-router-dom'
import styles from './Home.module.css'
import logo from '../assets/LogoGrande_FondoAzul.png'

function Home() {
  const navigate = useNavigate()

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <img src={logo} alt="EduPlazas" className={styles.logo} />
      </header>

      <div className={styles.fondo}>
        <div className={styles.botones}>
          <button className={styles.btnEstudiantes} onClick={() => navigate('/estudiantes/login')}>
            ESTUDIANTES
          </button>
          <button className={styles.btnUniversidades} onClick={() => navigate('/universidades/login')}>
            UNIVERSIDADES
          </button>
          <button className={styles.btnInvitado} onClick={() => navigate('/grados-publicos')}>
            EXPLORAR GRADOS
          </button>
        </div>
      </div>
    </div>
  )
}

export default Home