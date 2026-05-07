import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { crearConvocatoria, obtenerConvocatorias } from '../../services/convocatoriaService'
import styles from './AdminPanel.module.css'
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png'

function AdminPanel() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const [cursoAcademico, setCursoAcademico] = useState('')
  const [fechaApertura, setFechaApertura] = useState('')
  const [fechaCierre, setFechaCierre] = useState('')
  const [error, setError] = useState('')
  const [exito, setExito] = useState('')
  const [convocatorias, setConvocatorias] = useState([])

  useEffect(() => { cargarConvocatorias() }, [])

  const cargarConvocatorias = async () => {
    try {
      const res = await obtenerConvocatorias()
      setConvocatorias(res.data)
    } catch {}
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setExito('')

    if (!cursoAcademico.trim()) { setError('El curso académico es obligatorio'); return }
    if (!fechaApertura || !fechaCierre) { setError('Las fechas son obligatorias'); return }
    if (fechaCierre <= fechaApertura) { setError('La fecha de cierre debe ser posterior a la de apertura'); return }

    try {
      await crearConvocatoria(cursoAcademico.trim(), fechaApertura, fechaCierre)
      setExito('Convocatoria creada correctamente')
      setCursoAcademico('')
      setFechaApertura('')
      setFechaCierre('')
      cargarConvocatorias()
    } catch (err) {
      setError(err.response?.data || 'Error al crear la convocatoria')
    }
  }

  const cerrarSesion = () => { localStorage.removeItem('usuario'); localStorage.removeItem('token'); navigate('/') }

  const estadoBadge = (estado) => {
    const colores = { PENDIENTE: '#f59e0b', ABIERTA: '#22c55e', CERRADA: '#6b7280' }
    return (
      <span style={{ background: colores[estado] || '#6b7280', color: 'white',
        borderRadius: '999px', padding: '2px 10px', fontSize: '0.78rem', fontWeight: 600 }}>
        {estado}
      </span>
    )
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src={logo} alt="EduPlazas" className={styles.logoImg} onClick={() => navigate('/')} />
        <h1 className={styles.tituloHeader}>Panel de administración</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <p className={styles.email}>{usuario?.email}</p>
            <p className={styles.universidad}>Administrador</p>
          </div>
          <button className={styles.logoutBtn} onClick={cerrarSesion}>Log out</button>
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Nueva convocatoria</h2>
            <form onSubmit={handleSubmit} className={styles.form}>
              <div className={styles.field}>
                <label className={styles.label}>Curso académico</label>
                <input className={styles.input} type="text" placeholder="Ej: 2025-2026"
                  value={cursoAcademico} onChange={e => setCursoAcademico(e.target.value)} />
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Fecha de apertura</label>
                <input className={styles.input} type="date"
                  value={fechaApertura} onChange={e => setFechaApertura(e.target.value)} />
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Fecha de cierre</label>
                <input className={styles.input} type="date"
                  value={fechaCierre} onChange={e => setFechaCierre(e.target.value)} />
              </div>
              {error && <p className={styles.error}>{error}</p>}
              {exito && <p className={styles.exito}>{exito}</p>}
              <button type="submit" className={styles.buttonSubmit}>Crear convocatoria</button>
            </form>
          </div>

          {convocatorias.length > 0 && (
            <div className={styles.card} style={{ marginTop: '1.5rem' }}>
              <h2 className={styles.cardTitle}>Convocatorias</h2>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.9rem' }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid #e5e7eb', textAlign: 'left' }}>
                    <th style={{ padding: '8px 12px' }}>Curso</th>
                    <th style={{ padding: '8px 12px' }}>Apertura</th>
                    <th style={{ padding: '8px 12px' }}>Cierre</th>
                    <th style={{ padding: '8px 12px' }}>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {convocatorias.map(c => (
                    <tr key={c.id} style={{ borderBottom: '1px solid #f3f4f6' }}>
                      <td style={{ padding: '8px 12px' }}>{c.cursoAcademico}</td>
                      <td style={{ padding: '8px 12px' }}>{c.fechaApertura}</td>
                      <td style={{ padding: '8px 12px' }}>{c.fechaCierreConvocatoria}</td>
                      <td style={{ padding: '8px 12px' }}>{estadoBadge(c.estado)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </main>
      </div>
    </div>
  )
}

export default AdminPanel