import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
// Asegúrate de que el archivo se llame exactamente así
import styles from './UniversidadInicio.module.css' 
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png'

function SolicitudesRecibidas() {
    const navigate = useNavigate()
    const [solicitudes, setSolicitudes] = useState([])
    
    const usuarioString = localStorage.getItem('usuario')
    const usuario = usuarioString ? JSON.parse(usuarioString) : null
    const univId = usuario?.universidad?.id

    useEffect(() => {
        if (univId) {
            fetch(`http://localhost:8080/solicitudes/universidad/recibidas/${univId}`)
                .then(res => res.json())
                .then(data => setSolicitudes(data))
                .catch(err => console.error("Error al cargar:", err))
        }
    }, [univId])

    const cerrarSesion = () => {
        localStorage.removeItem('usuario')
        navigate('/')
    }

    if (!usuario) return null

    return (
        <div className={styles.page}>
            {/* Cabecera gris con el título azul central */}
            <header className={styles.header}>
                <img
                    src={logo}
                    alt="EduPlazas"
                    className={styles.logoImg}
                    onClick={() => navigate('/universidad/inicio')}
                    style={{ cursor: 'pointer' }}
                />
                <h1 className={styles.tituloHeader}>Solicitudes recibidas</h1>
            </header>

            <div className={styles.content}>
                {/* Barra lateral azul */}
                <aside className={styles.sidebar}>
                    <div className={styles.userBox}>
                        <p className={styles.email}>{usuario.email}</p>
                        <p className={styles.universidad}>{usuario.universidad?.nombre}</p>
                    </div>

                    <div className={styles.menu}>
                        <button className={styles.button} onClick={() => navigate('/universidad/publicar-offer')}>
                            Publicar oferta
                        </button>
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

                {/* Contenido principal con la tabla tipo Card */}
                <main className={styles.main}>
                    <div className={styles.tableCard}>
                        <h2 className={styles.cardTitle}>Listado de Estudiantes</h2>
                        <table className={styles.table}>
                            <thead>
                                <tr>
                                    <th>Estudiante</th>
                                    <th>Grado</th>
                                    <th>Preferencia</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                {solicitudes.length > 0 ? (
                                    solicitudes.map((s) => (
                                        <tr key={s.idSolicitud}>
                                            <td>{s.nombreEstudiante}</td>
                                            <td>{s.nombreGrado}</td>
                                            <td style={{ fontWeight: 'bold' }}>{s.ordenPreferencia}º</td>
                                            <td>{s.estado}</td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="4" style={{ textAlign: 'center', padding: '40px' }}>
                                            No se han recibido solicitudes todavía.
                                        </td>
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

export default SolicitudesRecibidas