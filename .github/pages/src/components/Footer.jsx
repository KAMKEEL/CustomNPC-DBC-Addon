import styles from './Footer.module.css'
import { REPO_OWNER, REPO_NAME } from '../useDocs'

export default function Footer({ source }) {
  const now = new Intl.DateTimeFormat('en-GB', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit', timeZoneName: 'short'
  }).format(new Date())

  return (
    <footer className={styles.footer}>
      <span>
        <span className={styles.dot} />
        Auto-generated · {source === 'manifest' ? 'manifest.json' : 'GitHub API'}
      </span>
      <span>{now}</span>
      <a
        href={`https://github.com/${REPO_OWNER}/${REPO_NAME}`}
        target="_blank"
        rel="noopener noreferrer"
        className={styles.link}
      >
        github.com/{REPO_OWNER}/{REPO_NAME}
      </a>
    </footer>
  )
}
